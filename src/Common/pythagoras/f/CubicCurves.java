//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Cubic curve-related utility methods.
 */
public class CubicCurves
{
    public static float flatnessSq (float x1, float y1, float ctrlx1, float ctrly1,
                                       float ctrlx2, float ctrly2, float x2, float y2) {
        return Math.max(Lines.pointSegDistSq(ctrlx1, ctrly1, x1, y1, x2, y2),
                        Lines.pointSegDistSq(ctrlx2, ctrly2, x1, y1, x2, y2));
    }

    public static float flatnessSq (float[] coords, int offset) {
        return flatnessSq(coords[offset + 0], coords[offset + 1], coords[offset + 2],
                             coords[offset + 3], coords[offset + 4], coords[offset + 5],
                             coords[offset + 6], coords[offset + 7]);
    }

    public static float flatness (float x1, float y1, float ctrlx1, float ctrly1,
                                     float ctrlx2, float ctrly2, float x2, float y2) {
        return FloatMath.sqrt(flatnessSq(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
    }

    public static float flatness (float[] coords, int offset) {
        return flatness(coords[offset + 0], coords[offset + 1], coords[offset + 2],
                           coords[offset + 3], coords[offset + 4], coords[offset + 5],
                           coords[offset + 6], coords[offset + 7]);
    }

    public static void subdivide (ICubicCurve src, CubicCurve left, CubicCurve right) {
        float x1 = src.x1(), y1 = src.y1();
        float cx1 = src.ctrlX1(), cy1 = src.ctrlY1();
        float cx2 = src.ctrlX2(), cy2 = src.ctrlY2();
        float x2 = src.x2(), y2 = src.y2();
        float cx = (cx1 + cx2) / 2f, cy = (cy1 + cy2) / 2f;
        cx1 = (x1 + cx1) / 2f;
        cy1 = (y1 + cy1) / 2f;
        cx2 = (x2 + cx2) / 2f;
        cy2 = (y2 + cy2) / 2f;
        float ax = (cx1 + cx) / 2f, ay = (cy1 + cy) / 2f;
        float bx = (cx2 + cx) / 2f, by = (cy2 + cy) / 2f;
        cx = (ax + bx) / 2f;
        cy = (ay + by) / 2f;
        if (left != null) {
            left.setCurve(x1, y1, cx1, cy1, ax, ay, cx, cy);
        }
        if (right != null) {
            right.setCurve(cx, cy, bx, by, cx2, cy2, x2, y2);
        }
    }

    public static void subdivide (float[] src, int srcOff, float left[], int leftOff,
                                  float[] right, int rightOff) {
        float x1 = src[srcOff + 0], y1 = src[srcOff + 1];
        float cx1 = src[srcOff + 2], cy1 = src[srcOff + 3];
        float cx2 = src[srcOff + 4], cy2 = src[srcOff + 5];
        float x2 = src[srcOff + 6], y2 = src[srcOff + 7];
        float cx = (cx1 + cx2) / 2f, cy = (cy1 + cy2) / 2f;
        cx1 = (x1 + cx1) / 2f;
        cy1 = (y1 + cy1) / 2f;
        cx2 = (x2 + cx2) / 2f;
        cy2 = (y2 + cy2) / 2f;
        float ax = (cx1 + cx) / 2f, ay = (cy1 + cy) / 2f;
        float bx = (cx2 + cx) / 2f, by = (cy2 + cy) / 2f;
        cx = (ax + bx) / 2f;
        cy = (ay + by) / 2f;
        if (left != null) {
            left[leftOff + 0] = x1;
            left[leftOff + 1] = y1;
            left[leftOff + 2] = cx1;
            left[leftOff + 3] = cy1;
            left[leftOff + 4] = ax;
            left[leftOff + 5] = ay;
            left[leftOff + 6] = cx;
            left[leftOff + 7] = cy;
        }
        if (right != null) {
            right[rightOff + 0] = cx;
            right[rightOff + 1] = cy;
            right[rightOff + 2] = bx;
            right[rightOff + 3] = by;
            right[rightOff + 4] = cx2;
            right[rightOff + 5] = cy2;
            right[rightOff + 6] = x2;
            right[rightOff + 7] = y2;
        }
    }

    public static int solveCubic (float[] eqn) {
        return solveCubic(eqn, eqn);
    }

    public static int solveCubic (float[] eqn, float[] res) {
        return Crossing.solveCubic(eqn, res);
    }
}
