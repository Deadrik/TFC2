//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Various geometry utility methods.
 */
public class GeometryUtil
{
    public static final float EPSILON = FloatMath.pow(10, -14);

    public static int intersectLinesWithParams (float x1, float y1, float x2, float y2,
                                                float x3, float y3, float x4, float y4,
                                                float[] params) {
        float dx = x4 - x3;
        float dy = y4 - y3;
        float d = dx * (y2 - y1) - dy * (x2 - x1);
        // float comparison
        if (Math.abs(d) < EPSILON) {
            return 0;
        }

        params[0] = (-dx * (y1 - y3) + dy * (x1 - x3)) / d;

        if (dx != 0) {
            params[1] = (line(params[0], x1, x2) - x3) / dx;
        } else if (dy != 0) {
            params[1] = (line(params[0], y1, y2) - y3) / dy;
        } else {
            params[1] = 0f;
        }

        if (params[0] >= 0 && params[0] <= 1 && params[1] >= 0 && params[1] <= 1) {
            return 1;
        }

        return 0;
    }

    /**
     * Checks whether line (x1, y1) - (x2, y2) and line (x3, y3) - (x4, y4) intersect. If lines
     * intersect then the result parameters are saved to point array. The size of {@code point}
     * must be at least 2.
     *
     * @return 1 if two lines intersect in the defined interval, otherwise 0.
     */
    public static int intersectLines (float x1, float y1, float x2, float y2, float x3, float y3,
                                      float x4, float y4, float[] point) {
        float A1 = -(y2 - y1);
        float B1 = (x2 - x1);
        float C1 = x1 * y2 - x2 * y1;
        float A2 = -(y4 - y3);
        float B2 = (x4 - x3);
        float C2 = x3 * y4 - x4 * y3;
        float coefParallel = A1 * B2 - A2 * B1;
        // float comparison
        if (x3 == x4 && y3 == y4 && (A1 * x3 + B1 * y3 + C1 == 0) && (x3 >= Math.min(x1, x2)) &&
            (x3 <= Math.max(x1, x2)) && (y3 >= Math.min(y1, y2)) && (y3 <= Math.max(y1, y2))) {
            return 1;
        }
        if (Math.abs(coefParallel) < EPSILON) {
            return 0;
        }
        point[0] = (B1 * C2 - B2 * C1) / coefParallel;
        point[1] = (A2 * C1 - A1 * C2) / coefParallel;
        if (point[0] >= Math.min(x1, x2) && point[0] >= Math.min(x3, x4) &&
            point[0] <= Math.max(x1, x2) && point[0] <= Math.max(x3, x4) &&
            point[1] >= Math.min(y1, y2) && point[1] >= Math.min(y3, y4) &&
            point[1] <= Math.max(y1, y2) && point[1] <= Math.max(y3, y4)) {
            return 1;
        }
        return 0;
    }

    /**
     * Checks whether there is intersection of the line (x1, y1) - (x2, y2) and the quad curve
     * (qx1, qy1) - (qx2, qy2) - (qx3, qy3). The parameters of the intersection area saved to
     * {@code params}. Therefore {@code params} must be of length at least 4.
     *
     * @return the number of roots that lie in the defined interval.
     */
    public static int intersectLineAndQuad (float x1, float y1, float x2, float y2,
                                            float qx1, float qy1, float qx2, float qy2,
                                            float qx3, float qy3, float[] params) {
        float[] eqn = new float[3];
        float[] t = new float[2];
        float[] s = new float[2];
        float dy = y2 - y1;
        float dx = x2 - x1;
        int quantity = 0;
        int count = 0;

        eqn[0] = dy * (qx1 - x1) - dx * (qy1 - y1);
        eqn[1] = 2 * dy * (qx2 - qx1) - 2 * dx * (qy2 - qy1);
        eqn[2] = dy * (qx1 - 2 * qx2 + qx3) - dx * (qy1 - 2 * qy2 + qy3);

        if ((count = Crossing.solveQuad(eqn, t)) == 0) {
            return 0;
        }

        for (int i = 0; i < count; i++) {
            if (dx != 0) {
                s[i] = (quad(t[i], qx1, qx2, qx3) - x1) / dx;
            } else if (dy != 0) {
                s[i] = (quad(t[i], qy1, qy2, qy3) - y1) / dy;
            } else {
                s[i] = 0f;
            }
            if (t[i] >= 0 && t[i] <= 1 && s[i] >= 0 && s[i] <= 1) {
                params[2 * quantity] = t[i];
                params[2 * quantity + 1] = s[i];
                ++quantity;
            }
        }

        return quantity;
    }

    /**
     * Checks whether the line (x1, y1) - (x2, y2) and the cubic curve (cx1, cy1) - (cx2, cy2) -
     * (cx3, cy3) - (cx4, cy4) intersect. The points of intersection are saved to {@code points}.
     * Therefore {@code points} must be of length at least 6.
     *
     * @return the numbers of roots that lie in the defined interval.
     */
    public static int intersectLineAndCubic (float x1, float y1, float x2, float y2,
                                             float cx1, float cy1, float cx2, float cy2,
                                             float cx3, float cy3, float cx4, float cy4,
                                             float[] params) {
        float[] eqn = new float[4];
        float[] t = new float[3];
        float[] s = new float[3];
        float dy = y2 - y1;
        float dx = x2 - x1;
        int quantity = 0;
        int count = 0;

        eqn[0] = (cy1 - y1) * dx + (x1 - cx1) * dy;
        eqn[1] = -3 * (cy1 - cy2) * dx + 3 * (cx1 - cx2) * dy;
        eqn[2] = (3 * cy1 - 6 * cy2 + 3 * cy3) * dx - (3 * cx1 - 6 * cx2 + 3 * cx3) * dy;
        eqn[3] = (-3 * cy1 + 3 * cy2 - 3 * cy3 + cy4) * dx +
            (3 * cx1 - 3 * cx2 + 3 * cx3 - cx4) * dy;

        if ((count = Crossing.solveCubic(eqn, t)) == 0) {
            return 0;
        }

        for (int i = 0; i < count; i++) {
            if (dx != 0) {
                s[i] = (cubic(t[i], cx1, cx2, cx3, cx4) - x1) / dx;
            } else if (dy != 0) {
                s[i] = (cubic(t[i], cy1, cy2, cy3, cy4) - y1) / dy;
            } else {
                s[i] = 0f;
            }
            if (t[i] >= 0 && t[i] <= 1 && s[i] >= 0 && s[i] <= 1) {
                params[2 * quantity] = t[i];
                params[2 * quantity + 1] = s[i];
                ++quantity;
            }
        }

        return quantity;
    }

    /**
     * Checks whether two quads (x1, y1) - (x2, y2) - (x3, y3) and (qx1, qy1) - (qx2, qy2) - (qx3,
     * qy3) intersect. The result is saved to {@code params}. Thus {@code params} must be of length
     * at least 4.
     *
     * @return the number of roots that lie in the interval.
     */
    public static int intersectQuads (float x1, float y1, float x2, float y2, float x3, float y3,
                                      float qx1, float qy1, float qx2, float qy2, float qx3,
                                      float qy3, float[] params) {
        float[] initParams = new float[2];
        float[] xCoefs1 = new float[3];
        float[] yCoefs1 = new float[3];
        float[] xCoefs2 = new float[3];
        float[] yCoefs2 = new float[3];
        int quantity = 0;

        xCoefs1[0] = x1 - 2 * x2 + x3;
        xCoefs1[1] = -2 * x1 + 2 * x2;
        xCoefs1[2] = x1;

        yCoefs1[0] = y1 - 2 * y2 + y3;
        yCoefs1[1] = -2 * y1 + 2 * y2;
        yCoefs1[2] = y1;

        xCoefs2[0] = qx1 - 2 * qx2 + qx3;
        xCoefs2[1] = -2 * qx1 + 2 * qx2;
        xCoefs2[2] = qx1;

        yCoefs2[0] = qy1 - 2 * qy2 + qy3;
        yCoefs2[1] = -2 * qy1 + 2 * qy2;
        yCoefs2[2] = qy1;

        // initialize params[0] and params[1]
        params[0] = params[1] = 0.25f;
        quadNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, initParams);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
        // initialize params
        params[0] = params[1] = 0.75f;
        quadNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }

        return quantity;
    }

    /**
     * Checks whether the quad (x1, y1) - (x2, y2) - (x3, y3) and the cubic (cx1, cy1) - (cx2, cy2)
     * - (cx3, cy3) - (cx4, cy4) curves intersect. The points of the intersection are saved to
     * {@code params}. Thus {@code params} must be of length at least 6.
     *
     * @return the number of intersection points that lie in the interval.
     */
    public static int intersectQuadAndCubic (float qx1, float qy1, float qx2, float qy2,
                                             float qx3, float qy3, float cx1, float cy1,
                                             float cx2, float cy2, float cx3, float cy3,
                                             float cx4, float cy4, float[] params) {
        int quantity = 0;
        float[] initParams = new float[3];
        float[] xCoefs1 = new float[3];
        float[] yCoefs1 = new float[3];
        float[] xCoefs2 = new float[4];
        float[] yCoefs2 = new float[4];
        xCoefs1[0] = qx1 - 2 * qx2 + qx3;
        xCoefs1[1] = 2 * qx2 - 2 * qx1;
        xCoefs1[2] = qx1;

        yCoefs1[0] = qy1 - 2 * qy2 + qy3;
        yCoefs1[1] = 2 * qy2 - 2 * qy1;
        yCoefs1[2] = qy1;

        xCoefs2[0] = -cx1 + 3 * cx2 - 3 * cx3 + cx4;
        xCoefs2[1] = 3 * cx1 - 6 * cx2 + 3 * cx3;
        xCoefs2[2] = -3 * cx1 + 3 * cx2;
        xCoefs2[3] = cx1;

        yCoefs2[0] = -cy1 + 3 * cy2 - 3 * cy3 + cy4;
        yCoefs2[1] = 3 * cy1 - 6 * cy2 + 3 * cy3;
        yCoefs2[2] = -3 * cy1 + 3 * cy2;
        yCoefs2[3] = cy1;

        // initialize params[0] and params[1]
        params[0] = params[1] = 0.25f;
        quadAndCubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, initParams);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
        // initialize params
        params[0] = params[1] = 0.5f;
        quadAndCubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }

        params[0] = params[1] = 0.75f;
        quadAndCubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
        return quantity;
    }

    /**
     * Checks whether two cubic curves (x1, y1) - (x2, y2) - (x3, y3) - (x4, y4) and (cx1, cy1) -
     * (cx2, cy2) - (cx3, cy3) - (cx4, cy4) intersect. The result is saved to {@code params}. Thus
     * {@code params} must be of length at least 6.
     *
     * @return the number of intersection points that lie in the interval.
     */
    public static int intersectCubics (float x1, float y1, float x2, float y2, float x3, float y3,
                                       float x4, float y4, float cx1, float cy1,
                                       float cx2, float cy2, float cx3, float cy3,
                                       float cx4, float cy4, float[] params) {
        int quantity = 0;
        float[] initParams = new float[3];
        float[] xCoefs1 = new float[4];
        float[] yCoefs1 = new float[4];
        float[] xCoefs2 = new float[4];
        float[] yCoefs2 = new float[4];
        xCoefs1[0] = -x1 + 3 * x2 - 3 * x3 + x4;
        xCoefs1[1] = 3 * x1 - 6 * x2 + 3 * x3;
        xCoefs1[2] = -3 * x1 + 3 * x2;
        xCoefs1[3] = x1;

        yCoefs1[0] = -y1 + 3 * y2 - 3 * y3 + y4;
        yCoefs1[1] = 3 * y1 - 6 * y2 + 3 * y3;
        yCoefs1[2] = -3 * y1 + 3 * y2;
        yCoefs1[3] = y1;

        xCoefs2[0] = -cx1 + 3 * cx2 - 3 * cx3 + cx4;
        xCoefs2[1] = 3 * cx1 - 6 * cx2 + 3 * cx3;
        xCoefs2[2] = -3 * cx1 + 3 * cx2;
        xCoefs2[3] = cx1;

        yCoefs2[0] = -cy1 + 3 * cy2 - 3 * cy3 + cy4;
        yCoefs2[1] = 3 * cy1 - 6 * cy2 + 3 * cy3;
        yCoefs2[2] = -3 * cy1 + 3 * cy2;
        yCoefs2[3] = cy1;

        // TODO
        params[0] = params[1] = 0.25f;
        cubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, initParams);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }

        params[0] = params[1] = 0.5f;
        cubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }

        params[0] = params[1] = 0.75f;
        cubicNewton(xCoefs1, yCoefs1, xCoefs2, yCoefs2, params);
        if (initParams[0] <= 1 && initParams[0] >= 0 && initParams[1] >= 0 && initParams[1] <= 1) {
            params[2 * quantity] = initParams[0];
            params[2 * quantity + 1] = initParams[1];
            ++quantity;
        }
        return quantity;
    }

    public static float line (float t, float x1, float x2) {
        return x1 * (1f - t) + x2 * t;
    }

    public static float quad (float t, float x1, float x2, float x3) {
        return x1 * (1f - t) * (1f - t) + 2f * x2 * t * (1f - t) + x3 * t * t;
    }

    public static float cubic (float t, float x1, float x2, float x3, float x4) {
        return x1 * (1f - t) * (1f - t) * (1f - t) + 3f * x2 * (1f - t) * (1f - t) * t + 3f * x3 *
            (1f - t) * t * t + x4 * t * t * t;
    }

    // x, y - the coordinates of new vertex
    // t0 - ?
    public static void subQuad (float[] coef, float t0, boolean left) {
        if (left) {
            coef[2] = (1 - t0) * coef[0] + t0 * coef[2];
            coef[3] = (1 - t0) * coef[1] + t0 * coef[3];
        } else {
            coef[2] = (1 - t0) * coef[2] + t0 * coef[4];
            coef[3] = (1 - t0) * coef[3] + t0 * coef[5];
        }
    }

    public static void subCubic (float[] coef, float t0, boolean left) {
        if (left) {
            coef[2] = (1 - t0) * coef[0] + t0 * coef[2];
            coef[3] = (1 - t0) * coef[1] + t0 * coef[3];
        } else {
            coef[4] = (1 - t0) * coef[4] + t0 * coef[6];
            coef[5] = (1 - t0) * coef[5] + t0 * coef[7];
        }
    }

    private static void cubicNewton (float[] xCoefs1, float[] yCoefs1,
                                     float[] xCoefs2, float[] yCoefs2, float[] params) {
        float t = 0f, s = 0f;
        float t1 = params[0];
        float s1 = params[1];
        float d, dt, ds;

        while (Math.sqrt((t - t1) * (t - t1) + (s - s1) * (s - s1)) > EPSILON) {
            d = -(3 * t * t * xCoefs1[0] + 2 * t * xCoefs1[1] + xCoefs1[2]) *
                (3 * s * s * yCoefs2[0] + 2 * s * yCoefs2[1] + yCoefs2[2]) +
                (3 * t * t * yCoefs1[0] + 2 * t * yCoefs1[1] + yCoefs1[2]) *
                (3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2]);

            dt = (t * t * t * xCoefs1[0] + t * t * xCoefs1[1] + t * xCoefs1[2] + xCoefs1[3] -
                  s * s * s * xCoefs2[0] - s * s * xCoefs2[1] - s * xCoefs2[2] - xCoefs2[3]) *
                (-3 * s * s * yCoefs2[0] - 2 * s * yCoefs2[1] - yCoefs2[2]) +
                (t * t * t * yCoefs1[0] + t * t * yCoefs1[1] + t * yCoefs1[2] + yCoefs1[3] -
                 s * s * s * yCoefs2[0] - s * s * yCoefs2[1] - s * yCoefs2[2] - yCoefs2[3]) *
                (3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2]);

            ds = (3 * t * t * xCoefs1[0] + 2 * t * xCoefs1[1] + xCoefs1[2]) *
                (t * t * t * yCoefs1[0] + t * t * yCoefs1[1] + t * yCoefs1[2] + yCoefs1[3] -
                 s * s * s * yCoefs2[0] - s * s * yCoefs2[1] - s * yCoefs2[2] - yCoefs2[3]) -
                (3 * t * t * yCoefs1[0] + 2 * t * yCoefs1[1] + yCoefs1[2]) *
                (t * t * t * xCoefs1[0] + t * t * xCoefs1[1] + t * xCoefs1[2] + xCoefs1[3] -
                 s * s * s * xCoefs2[0] - s * s * xCoefs2[1] - s * xCoefs2[2] - xCoefs2[3]);

            t1 = t - dt / d;
            s1 = s - ds / d;
        }
        params[0] = t1;
        params[1] = s1;
    }

    private static void quadAndCubicNewton (float xCoefs1[], float yCoefs1[],
                                            float xCoefs2[], float yCoefs2[], float[] params) {
        float t = 0f, s = 0f;
        float t1 = params[0];
        float s1 = params[1];
        float d, dt, ds;

        while (Math.sqrt((t - t1) * (t - t1) + (s - s1) * (s - s1)) > EPSILON) {
            d = -(2 * t * xCoefs1[0] + xCoefs1[1]) *
                (3 * s * s * yCoefs2[0] + 2 * s * yCoefs2[1] + yCoefs2[2]) +
                (2 * t * yCoefs1[0] + yCoefs1[1]) *
                (3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2]);

            dt = (t * t * xCoefs1[0] + t * xCoefs1[1] + xCoefs1[2] + -s * s * s * xCoefs2[0] -
                  s * s * xCoefs2[1] - s * xCoefs2[2] - xCoefs2[3]) *
                (-3 * s * s * yCoefs2[0] - 2 * s * yCoefs2[1] - yCoefs2[2]) +
                (t * t * yCoefs1[0] + t * yCoefs1[1] + yCoefs1[2] - s * s * s * yCoefs2[0] -
                 s * s * yCoefs2[1] - s * yCoefs2[2] - yCoefs2[3]) *
                (3 * s * s * xCoefs2[0] + 2 * s * xCoefs2[1] + xCoefs2[2]);

            ds = (2 * t * xCoefs1[0] + xCoefs1[1]) *
                (t * t * yCoefs1[0] + t * yCoefs1[1] + yCoefs1[2] - s * s * s * yCoefs2[0] -
                 s * s * yCoefs2[1] - s * yCoefs2[2] - yCoefs2[3]) -
                (2 * t * yCoefs1[0] + yCoefs1[1]) *
                (t * t * xCoefs1[0] + t * xCoefs1[1] + xCoefs1[2] - s * s * s * xCoefs2[0] -
                 s * s * xCoefs2[1] - s * xCoefs2[2] - xCoefs2[3]);

            t1 = t - dt / d;
            s1 = s - ds / d;
        }
        params[0] = t1;
        params[1] = s1;
    }

    private static void quadNewton (float xCoefs1[], float yCoefs1[],
                                    float xCoefs2[], float yCoefs2[], float params[]) {
        float t = 0f, s = 0f;
        float t1 = params[0];
        float s1 = params[1];
        float d, dt, ds;

        while (Math.sqrt((t - t1) * (t - t1) + (s - s1) * (s - s1)) > EPSILON) {
            t = t1;
            s = s1;
            d = -(2 * t * xCoefs1[0] + xCoefs1[1]) * (2 * s * yCoefs2[0] + yCoefs2[1]) +
                (2 * s * xCoefs2[0] + xCoefs2[1]) * (2 * t * yCoefs1[0] + yCoefs1[1]);

            dt = -(t * t * xCoefs1[0] + t * xCoefs1[1] + xCoefs1[1] - s * s * xCoefs2[0] -
                   s * xCoefs2[1] - xCoefs2[2]) * (2 * s * yCoefs2[0] + yCoefs2[1]) +
                (2 * s * xCoefs2[0] + xCoefs2[1]) *
                (t * t * yCoefs1[0] + t * yCoefs1[1] + yCoefs1[2] - s * s * yCoefs2[0] -
                 s * yCoefs2[1] - yCoefs2[2]);

            ds = (2 * t * xCoefs1[0] + xCoefs1[1]) *
                (t * t * yCoefs1[0] + t * yCoefs1[1] + yCoefs1[2] - s * s * yCoefs2[0] -
                 s * yCoefs2[1] - yCoefs2[2]) - (2 * t * yCoefs1[0] + yCoefs1[1]) *
                (t * t * xCoefs1[0] + t * xCoefs1[1] + xCoefs1[2] - s * s * xCoefs2[0] -
                 s * xCoefs2[1] - xCoefs2[2]);

            t1 = t - dt / d;
            s1 = s - ds / d;
        }
        params[0] = t1;
        params[1] = s1;
    }
}
