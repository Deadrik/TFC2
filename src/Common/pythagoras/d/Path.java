//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.util.NoSuchElementException;

import pythagoras.util.Platform;

/**
 * Represents a path constructed from lines and curves and which can contain subpaths.
 */
public final class Path implements IShape, Cloneable
{
    /** Specifies the even/odd rule for determining the interior of a path. */
    public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;

    /** Specifies the non-zero rule for determining the interior of a path. */
    public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;

    public Path () {
        this(WIND_NON_ZERO, BUFFER_SIZE);
    }

    public Path (int rule) {
        this(rule, BUFFER_SIZE);
    }

    public Path (int rule, int initialCapacity) {
        setWindingRule(rule);
        types = new byte[initialCapacity];
        points = new double[initialCapacity * 2];
    }

    public Path (IShape shape) {
        this(WIND_NON_ZERO, BUFFER_SIZE);
        PathIterator p = shape.pathIterator(null);
        setWindingRule(p.windingRule());
        append(p, false);
    }

    public void setWindingRule (int rule) {
        if (rule != WIND_EVEN_ODD && rule != WIND_NON_ZERO) {
            throw new IllegalArgumentException("Invalid winding rule value");
        }
        this.rule = rule;
    }

    public int windingRule () {
        return rule;
    }

    public void moveTo (double x, double y) {
        if (typeSize > 0 && types[typeSize - 1] == PathIterator.SEG_MOVETO) {
            points[pointSize - 2] = x;
            points[pointSize - 1] = y;
        } else {
            checkBuf(2, false);
            types[typeSize++] = PathIterator.SEG_MOVETO;
            points[pointSize++] = x;
            points[pointSize++] = y;
        }
    }

    public void lineTo (double x, double y) {
        checkBuf(2, true);
        types[typeSize++] = PathIterator.SEG_LINETO;
        points[pointSize++] = x;
        points[pointSize++] = y;
    }

    public void quadTo (double x1, double y1, double x2, double y2) {
        checkBuf(4, true);
        types[typeSize++] = PathIterator.SEG_QUADTO;
        points[pointSize++] = x1;
        points[pointSize++] = y1;
        points[pointSize++] = x2;
        points[pointSize++] = y2;
    }

    public void curveTo (double x1, double y1, double x2, double y2, double x3, double y3) {
        checkBuf(6, true);
        types[typeSize++] = PathIterator.SEG_CUBICTO;
        points[pointSize++] = x1;
        points[pointSize++] = y1;
        points[pointSize++] = x2;
        points[pointSize++] = y2;
        points[pointSize++] = x3;
        points[pointSize++] = y3;
    }

    public void closePath () {
        if (typeSize == 0 || types[typeSize - 1] != PathIterator.SEG_CLOSE) {
            checkBuf(0, true);
            types[typeSize++] = PathIterator.SEG_CLOSE;
        }
    }

    public void append (IShape shape, boolean connect) {
        PathIterator p = shape.pathIterator(null);
        append(p, connect);
    }

    public void append (PathIterator path, boolean connect) {
        while (!path.isDone()) {
            double[] coords = new double[6];
            switch (path.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO:
                if (!connect || typeSize == 0) {
                    moveTo(coords[0], coords[1]);
                } else if (types[typeSize - 1] != PathIterator.SEG_CLOSE &&
                           points[pointSize - 2] == coords[0] &&
                           points[pointSize - 1] == coords[1]) {
                    // we're already here
                } else {
                    lineTo(coords[0], coords[1]);
                }
                break;
            case PathIterator.SEG_LINETO:
                lineTo(coords[0], coords[1]);
                break;
            case PathIterator.SEG_QUADTO:
                quadTo(coords[0], coords[1], coords[2], coords[3]);
                break;
            case PathIterator.SEG_CUBICTO:
                curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                break;
            case PathIterator.SEG_CLOSE:
                closePath();
                break;
            }
            path.next();
            connect = false;
        }
    }

    public Point currentPoint () {
        if (typeSize == 0) {
            return null;
        }
        int j = pointSize - 2;
        if (types[typeSize - 1] == PathIterator.SEG_CLOSE) {
            for (int i = typeSize - 2; i > 0; i--) {
                int type = types[i];
                if (type == PathIterator.SEG_MOVETO) {
                    break;
                }
                j -= pointShift[type];
            }
        }
        return new Point(points[j], points[j + 1]);
    }

    public void reset () {
        typeSize = 0;
        pointSize = 0;
    }

    public void transform (Transform t) {
        t.transform(points, 0, points, 0, pointSize / 2);
    }

    public IShape createTransformedShape (Transform t) {
        Path p = clone();
        if (t != null) {
            p.transform(t);
        }
        return p;
    }

    @Override // from interface IShape
    public Rectangle bounds () {
        return bounds(new Rectangle());
    }

    @Override // from interface IShape
    public Rectangle bounds (Rectangle target) {
        double rx1, ry1, rx2, ry2;
        if (pointSize == 0) {
            rx1 = ry1 = rx2 = ry2 = 0f;
        } else {
            int i = pointSize - 1;
            ry1 = ry2 = points[i--];
            rx1 = rx2 = points[i--];
            while (i > 0) {
                double y = points[i--];
                double x = points[i--];
                if (x < rx1) {
                    rx1 = x;
                } else if (x > rx2) {
                    rx2 = x;
                }
                if (y < ry1) {
                    ry1 = y;
                } else if (y > ry2) {
                    ry2 = y;
                }
            }
        }
        target.setBounds(rx1, ry1, rx2 - rx1, ry2 - ry1);
        return target;
    }

    @Override // from interface IShape
    public boolean isEmpty () {
        // TODO: will this be insanely difficult to do correctly?
        return bounds().isEmpty();
    }

    @Override // from interface IShape
    public boolean contains (double px, double py) {
        return isInside(Crossing.crossShape(this, px, py));
    }

    @Override // from interface IShape
    public boolean contains (double rx, double ry, double rw, double rh) {
        int cross = Crossing.intersectShape(this, rx, ry, rw, rh);
        return cross != Crossing.CROSSING && isInside(cross);
    }

    @Override // from interface IShape
    public boolean intersects (double rx, double ry, double rw, double rh) {
        int cross = Crossing.intersectShape(this, rx, ry, rw, rh);
        return cross == Crossing.CROSSING || isInside(cross);
    }

    @Override // from interface IShape
    public boolean contains (IPoint p) {
        return contains(p.x(), p.y());
    }

    @Override // from interface IShape
    public boolean contains (IRectangle r) {
        return contains(r.x(), r.y(), r.width(), r.height());
    }

    @Override // from interface IShape
    public boolean intersects (IRectangle r) {
        return intersects(r.x(), r.y(), r.width(), r.height());
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t) {
        return new Iterator(this, t);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t, double flatness) {
        return new FlatteningPathIterator(pathIterator(t), flatness);
    }

    // @Override // can't declare @Override due to GWT
    public Path clone () {
        return new Path(rule, Platform.clone(types), Platform.clone(points), typeSize, pointSize);
    }

    /**
     * Checks points and types buffer size to add pointCount points. If necessary realloc buffers
     * to enlarge size.
     *
     * @param pointCount the point count to be added in buffer
     */
    protected void checkBuf (int pointCount, boolean checkMove) {
        if (checkMove && typeSize == 0) {
            throw new IllegalPathStateException("First segment must be a SEG_MOVETO");
        }
        if (typeSize == types.length) {
            byte[] tmp = new byte[typeSize + BUFFER_CAPACITY];
            System.arraycopy(types, 0, tmp, 0, typeSize);
            types = tmp;
        }
        if (pointSize + pointCount > points.length) {
            double[] tmp = new double[pointSize + Math.max(BUFFER_CAPACITY * 2, pointCount)];
            System.arraycopy(points, 0, tmp, 0, pointSize);
            points = tmp;
        }
    }

    /**
     * Checks cross count according to path rule to define is it point inside shape or not.
     *
     * @param cross the point cross count.
     * @return true if point is inside path, or false otherwise.
     */
    protected boolean isInside (int cross) {
        return (rule == WIND_NON_ZERO) ? Crossing.isInsideNonZero(cross) :
            Crossing.isInsideEvenOdd(cross);
    }

    private Path (int rule, byte[] types, double[] points, int typeSize, int pointSize) {
        this.rule = rule;
        this.types = types;
        this.points = points;
        this.typeSize = typeSize;
        this.pointSize = pointSize;
    }

    /** An iterator over a {@link Path}. */
    protected static class Iterator implements PathIterator
    {
        /** The current cursor position in types buffer. */
        private int typeIndex;

        /** The current cursor position in points buffer. */
        private int pointIndex;

        /** The source Path object. */
        private Path p;

        /** The path iterator transformation. */
        private Transform t;

        Iterator (Path path) {
            this(path, null);
        }

        Iterator (Path path, Transform at) {
            this.p = path;
            this.t = at;
        }

        @Override public int windingRule () {
            return p.windingRule();
        }

        @Override public boolean isDone () {
            return typeIndex >= p.typeSize;
        }

        @Override public void next () {
            typeIndex++;
        }

        @Override public int currentSegment (double[] coords) {
            if (isDone()) {
                throw new NoSuchElementException("Iterator out of bounds");
            }
            int type = p.types[typeIndex];
            int count = Path.pointShift[type];
            System.arraycopy(p.points, pointIndex, coords, 0, count);
            if (t != null) {
                t.transform(coords, 0, coords, 0, count / 2);
            }
            pointIndex += count;
            return type;
        }
    }

    /** The point's types buffer. */
    protected byte[] types;

    /** The points buffer. */
    protected double[] points;

    /** The point's type buffer size. */
    protected int typeSize;

    /** The points buffer size. */
    protected int pointSize;

    /* The path rule. */
    protected int rule;

    /** The space required in points buffer for different segment types. */
    protected static int[] pointShift = { 2, // MOVETO
                                          2, // LINETO
                                          4, // QUADTO
                                          6, // CUBICTO
                                          0 }; // CLOSE

    /** The default initial buffer size. */
    protected static final int BUFFER_SIZE = 10;

    /** The amount by which to expand the buffer capacity. */
    protected static final int BUFFER_CAPACITY = 10;
}
