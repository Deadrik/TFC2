//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Cubic curve-related utility methods.
 */
public class CubicCurves
{
    public static double flatnessSq (double x1, double y1, double ctrlx1, double ctrly1,
                                       double ctrlx2, double ctrly2, double x2, double y2) {
        return Math.max(Lines.pointSegDistSq(ctrlx1, ctrly1, x1, y1, x2, y2),
                        Lines.pointSegDistSq(ctrlx2, ctrly2, x1, y1, x2, y2));
    }

    public static double flatnessSq (double[] coords, int offset) {
        return flatnessSq(coords[offset + 0], coords[offset + 1], coords[offset + 2],
                             coords[offset + 3], coords[offset + 4], coords[offset + 5],
                             coords[offset + 6], coords[offset + 7]);
    }

    public static double flatness (double x1, double y1, double ctrlx1, double ctrly1,
                                     double ctrlx2, double ctrly2, double x2, double y2) {
        return Math.sqrt(flatnessSq(x1, y1, ctrlx1, ctrly1, ctrlx2, ctrly2, x2, y2));
    }

    public static double flatness (double[] coords, int offset) {
        return flatness(coords[offset + 0], coords[offset + 1], coords[offset + 2],
                           coords[offset + 3], coords[offset + 4], coords[offset + 5],
                           coords[offset + 6], coords[offset + 7]);
    }

    public static void subdivide (ICubicCurve src, CubicCurve left, CubicCurve right) {
        double x1 = src.x1(), y1 = src.y1();
        double cx1 = src.ctrlX1(), cy1 = src.ctrlY1();
        double cx2 = src.ctrlX2(), cy2 = src.ctrlY2();
        double x2 = src.x2(), y2 = src.y2();
        double cx = (cx1 + cx2) / 2f, cy = (cy1 + cy2) / 2f;
        cx1 = (x1 + cx1) / 2f;
        cy1 = (y1 + cy1) / 2f;
        cx2 = (x2 + cx2) / 2f;
        cy2 = (y2 + cy2) / 2f;
        double ax = (cx1 + cx) / 2f, ay = (cy1 + cy) / 2f;
        double bx = (cx2 + cx) / 2f, by = (cy2 + cy) / 2f;
        cx = (ax + bx) / 2f;
        cy = (ay + by) / 2f;
        if (left != null) {
            left.setCurve(x1, y1, cx1, cy1, ax, ay, cx, cy);
        }
        if (right != null) {
            right.setCurve(cx, cy, bx, by, cx2, cy2, x2, y2);
        }
    }

    public static void subdivide (double[] src, int srcOff, double left[], int leftOff,
                                  double[] right, int rightOff) {
        double x1 = src[srcOff + 0], y1 = src[srcOff + 1];
        double cx1 = src[srcOff + 2], cy1 = src[srcOff + 3];
        double cx2 = src[srcOff + 4], cy2 = src[srcOff + 5];
        double x2 = src[srcOff + 6], y2 = src[srcOff + 7];
        double cx = (cx1 + cx2) / 2f, cy = (cy1 + cy2) / 2f;
        cx1 = (x1 + cx1) / 2f;
        cy1 = (y1 + cy1) / 2f;
        cx2 = (x2 + cx2) / 2f;
        cy2 = (y2 + cy2) / 2f;
        double ax = (cx1 + cx) / 2f, ay = (cy1 + cy) / 2f;
        double bx = (cx2 + cx) / 2f, by = (cy2 + cy) / 2f;
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

    public static int solveCubic (double[] eqn) {
        return solveCubic(eqn, eqn);
    }

    public static int solveCubic (double[] eqn, double[] res) {
        return Crossing.solveCubic(eqn, res);
    }
}
