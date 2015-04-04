//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Quad curve-related utility methods.
 */
public class QuadCurves
{
    public static float flatnessSq (float x1, float y1, float ctrlx, float ctrly,
                                       float x2, float y2) {
        return Lines.pointSegDistSq(ctrlx, ctrly, x1, y1, x2, y2);
    }

    public static float flatnessSq (float[] coords, int offset) {
        return Lines.pointSegDistSq(coords[offset + 2], coords[offset + 3],
                                    coords[offset + 0], coords[offset + 1],
                                    coords[offset + 4], coords[offset + 5]);
    }

    public static float flatness (float x1, float y1, float ctrlx, float ctrly,
                                     float x2, float y2) {
        return Lines.pointSegDist(ctrlx, ctrly, x1, y1, x2, y2);
    }

    public static float flatness (float[] coords, int offset) {
        return Lines.pointSegDist(coords[offset + 2], coords[offset + 3],
                                  coords[offset + 0], coords[offset + 1],
                                  coords[offset + 4], coords[offset + 5]);
    }

    public static void subdivide (IQuadCurve src, QuadCurve left, QuadCurve right) {
        float x1 = src.x1();
        float y1 = src.y1();
        float cx = src.ctrlX();
        float cy = src.ctrlY();
        float x2 = src.x2();
        float y2 = src.y2();
        float cx1 = (x1 + cx) / 2f;
        float cy1 = (y1 + cy) / 2f;
        float cx2 = (x2 + cx) / 2f;
        float cy2 = (y2 + cy) / 2f;
        cx = (cx1 + cx2) / 2f;
        cy = (cy1 + cy2) / 2f;
        if (left != null) {
            left.setCurve(x1, y1, cx1, cy1, cx, cy);
        }
        if (right != null) {
            right.setCurve(cx, cy, cx2, cy2, x2, y2);
        }
    }

    public static void subdivide (float[] src, int srcoff,
                                  float[] left, int leftOff, float[] right, int rightOff) {
        float x1 = src[srcoff + 0];
        float y1 = src[srcoff + 1];
        float cx = src[srcoff + 2];
        float cy = src[srcoff + 3];
        float x2 = src[srcoff + 4];
        float y2 = src[srcoff + 5];
        float cx1 = (x1 + cx) / 2f;
        float cy1 = (y1 + cy) / 2f;
        float cx2 = (x2 + cx) / 2f;
        float cy2 = (y2 + cy) / 2f;
        cx = (cx1 + cx2) / 2f;
        cy = (cy1 + cy2) / 2f;
        if (left != null) {
            left[leftOff + 0] = x1;
            left[leftOff + 1] = y1;
            left[leftOff + 2] = cx1;
            left[leftOff + 3] = cy1;
            left[leftOff + 4] = cx;
            left[leftOff + 5] = cy;
        }
        if (right != null) {
            right[rightOff + 0] = cx;
            right[rightOff + 1] = cy;
            right[rightOff + 2] = cx2;
            right[rightOff + 3] = cy2;
            right[rightOff + 4] = x2;
            right[rightOff + 5] = y2;
        }
    }

    public static int solveQuadratic (float[] eqn) {
        return solveQuadratic(eqn, eqn);
    }

    public static int solveQuadratic (float[] eqn, float[] res) {
        return Crossing.solveQuad(eqn, res);
    }
}
