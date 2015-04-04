//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.util.NoSuchElementException;

/**
 * A path iterator that flattens curves.
 */
class FlatteningPathIterator implements PathIterator
{
    public FlatteningPathIterator (PathIterator path, float flatness) {
        this(path, flatness, BUFFER_LIMIT);
    }

    public FlatteningPathIterator (PathIterator path, float flatness, int limit) {
        if (flatness < 0) {
            throw new IllegalArgumentException("Flatness is less then zero");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("Limit is less then zero");
        }
        if (path == null) {
            throw new NullPointerException("Path is null");
        }
        this.p = path;
        this.flatness = flatness;
        this.flatness2 = flatness * flatness;
        this.bufLimit = limit;
        this.bufSize = Math.min(bufLimit, BUFFER_SIZE);
        this.buf = new float[bufSize];
        this.bufIndex = bufSize;
    }

    public float flatness () {
        return flatness;
    }

    public int recursionLimit () {
        return bufLimit;
    }

    @Override
    // from interface PathIterator
    public int windingRule () {
        return p.windingRule();
    }

    @Override
    // from interface PathIterator
    public boolean isDone () {
        return bufEmpty && p.isDone();
    }

    @Override
    // from interface PathIterator
    public void next () {
        if (bufEmpty) {
            p.next();
        }
    }

    @Override
    // from interface PathIterator
    public int currentSegment (float[] coords) {
        if (isDone()) {
            throw new NoSuchElementException("Iterator out of bounds");
        }
        evaluate();
        int type = bufType;
        if (type != SEG_CLOSE) {
            coords[0] = px;
            coords[1] = py;
            if (type != SEG_MOVETO) {
                type = SEG_LINETO;
            }
        }
        return type;
    }

    /** Calculates flat path points for the current segment of the source shape. Line segment is
     * flat by itself. Flatness of quad and cubic curves are evaluated by the flatnessSq()
     * method. Curves are subdivided until current flatness is bigger than user defined value and
     * subdivision limit isn't exhausted. Single source segments are translated to a series of
     * buffer points. The smaller the flatness the bigger the series. Every currentSegment() call
     * extracts one point from the buffer. When a series is completed, evaluate() takes the next
     * source shape segment. */
    protected void evaluate () {
        if (bufEmpty) {
            bufType = p.currentSegment(coords);
        }

        switch (bufType) {
        case SEG_MOVETO:
        case SEG_LINETO:
            px = coords[0];
            py = coords[1];
            break;

        case SEG_QUADTO:
            if (bufEmpty) {
                bufIndex -= 6;
                buf[bufIndex + 0] = px;
                buf[bufIndex + 1] = py;
                System.arraycopy(coords, 0, buf, bufIndex + 2, 4);
                bufSubdiv = 0;
            }

            while (bufSubdiv < bufLimit) {
                if (QuadCurves.flatnessSq(buf, bufIndex) < flatness2) {
                    break;
                }

                // Realloc buffer
                if (bufIndex <= 4) {
                    float[] tmp = new float[bufSize + BUFFER_CAPACITY];
                    System.arraycopy(buf, bufIndex, tmp, bufIndex + BUFFER_CAPACITY, bufSize
                            - bufIndex);
                    buf = tmp;
                    bufSize += BUFFER_CAPACITY;
                    bufIndex += BUFFER_CAPACITY;
                }

                QuadCurves.subdivide(buf, bufIndex, buf, bufIndex - 4, buf, bufIndex);

                bufIndex -= 4;
                bufSubdiv++;
            }

            bufIndex += 4;
            px = buf[bufIndex];
            py = buf[bufIndex + 1];

            bufEmpty = (bufIndex == bufSize - 2);
            if (bufEmpty) {
                bufIndex = bufSize;
                bufType = SEG_LINETO;
            }
            break;

        case SEG_CUBICTO:
            if (bufEmpty) {
                bufIndex -= 8;
                buf[bufIndex + 0] = px;
                buf[bufIndex + 1] = py;
                System.arraycopy(coords, 0, buf, bufIndex + 2, 6);
                bufSubdiv = 0;
            }

            while (bufSubdiv < bufLimit) {
                if (CubicCurves.flatnessSq(buf, bufIndex) < flatness2) {
                    break;
                }

                // Realloc buffer
                if (bufIndex <= 6) {
                    float[] tmp = new float[bufSize + BUFFER_CAPACITY];
                    System.arraycopy(buf, bufIndex, tmp, bufIndex + BUFFER_CAPACITY, bufSize
                            - bufIndex);
                    buf = tmp;
                    bufSize += BUFFER_CAPACITY;
                    bufIndex += BUFFER_CAPACITY;
                }

                CubicCurves.subdivide(buf, bufIndex, buf, bufIndex - 6, buf, bufIndex);

                bufIndex -= 6;
                bufSubdiv++;
            }

            bufIndex += 6;
            px = buf[bufIndex];
            py = buf[bufIndex + 1];

            bufEmpty = (bufIndex == bufSize - 2);
            if (bufEmpty) {
                bufIndex = bufSize;
                bufType = SEG_LINETO;
            }
            break;
        }
    }

    /** The type of current segment to be flat */
    private int bufType;

    /** The curve subdivision limit */
    private int bufLimit;

    /** The current points buffer size */
    private int bufSize;

    /** The inner cursor position in points buffer */
    private int bufIndex;

    /** The current subdivision count */
    private int bufSubdiv;

    /** The points buffer */
    private float[] buf;

    /** The indicator of empty points buffer */
    private boolean bufEmpty = true;

    /** The source PathIterator */
    private PathIterator p;

    /** The flatness of new path */
    private float flatness;

    /** The square of flatness */
    private float flatness2;

    /** The x coordinate of previous path segment */
    private float px;

    /** The y coordinate of previous path segment */
    private float py;

    /** The tamporary buffer for getting points from PathIterator */
    private float[] coords = new float[6];

    /** The default points buffer size */
    private static final int BUFFER_SIZE = 16;

    /** The default curve subdivision limit */
    private static final int BUFFER_LIMIT = 16;

    /** The points buffer capacity */
    private static final int BUFFER_CAPACITY = 16;
}
