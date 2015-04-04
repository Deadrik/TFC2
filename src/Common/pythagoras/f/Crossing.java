//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Internal utility methods for computing intersections and containment.
 */
class Crossing
{
    /** Return value indicating that a crossing was found. */
    public static final int CROSSING = 255;

    /** Return value indicating the crossing result is unknown. */
    public static final int UNKNOWN = 254;

    /**
     * Solves quadratic equation
     *
     * @param eqn the coefficients of the equation
     * @param res the roots of the equation
     * @return a number of roots
     */
    public static int solveQuad (float[] eqn, float[] res) {
        float a = eqn[2];
        float b = eqn[1];
        float c = eqn[0];
        int rc = 0;
        if (a == 0f) {
            if (b == 0f) {
                return -1;
            }
            res[rc++] = -c / b;
        } else {
            float d = b * b - 4f * a * c;
            // d < 0f
            if (d < 0f) {
                return 0;
            }
            d = FloatMath.sqrt(d);
            res[rc++] = (-b + d) / (a * 2f);
            // d != 0f
            if (d != 0f) {
                res[rc++] = (-b - d) / (a * 2f);
            }
        }
        return fixRoots(res, rc);
    }

    /**
     * Solves cubic equation
     *
     * @param eqn the coefficients of the equation
     * @param res the roots of the equation
     * @return a number of roots
     */
    public static int solveCubic (float[] eqn, float[] res) {
        float d = eqn[3];
        if (d == 0) {
            return solveQuad(eqn, res);
        }
        float a = eqn[2] / d;
        float b = eqn[1] / d;
        float c = eqn[0] / d;
        int rc = 0;

        float Q = (a * a - 3f * b) / 9f;
        float R = (2f * a * a * a - 9f * a * b + 27f * c) / 54f;
        float Q3 = Q * Q * Q;
        float R2 = R * R;
        float n = -a / 3f;

        if (R2 < Q3) {
            float t = FloatMath.acos(R / FloatMath.sqrt(Q3)) / 3f;
            float p = 2f * FloatMath.PI / 3f;
            float m = -2f * FloatMath.sqrt(Q);
            res[rc++] = m * FloatMath.cos(t) + n;
            res[rc++] = m * FloatMath.cos(t + p) + n;
            res[rc++] = m * FloatMath.cos(t - p) + n;
        } else {
            // Debug.println("R2 >= Q3 (" + R2 + "/" + Q3 + ")");
            float A = FloatMath.pow(Math.abs(R) + FloatMath.sqrt(R2 - Q3), 1f / 3f);
            if (R > 0f) {
                A = -A;
            }
            // if (A == 0f) {
            if (-ROOT_DELTA < A && A < ROOT_DELTA) {
                res[rc++] = n;
            } else {
                float B = Q / A;
                res[rc++] = A + B + n;
                // if (R2 == Q3) {
                float delta = R2 - Q3;
                if (-ROOT_DELTA < delta && delta < ROOT_DELTA) {
                    res[rc++] = -(A + B) / 2f + n;
                }
            }

        }
        return fixRoots(res, rc);
    }

    /**
     * Excludes double roots. Roots are double if they lies enough close with each other.
     *
     * @param res the roots
     * @param rc the roots count
     * @return new roots count
     */
    protected static int fixRoots (float[] res, int rc) {
        int tc = 0;
        for (int i = 0; i < rc; i++) {
            out: {
                for (int j = i + 1; j < rc; j++) {
                    if (isZero(res[i] - res[j])) {
                        break out;
                    }
                }
                res[tc++] = res[i];
            }
        }
        return tc;
    }

    /**
     * QuadCurve class provides basic functionality to find curve crossing and calculating bounds
     */
    public static class QuadCurve
    {
        float ax, ay, bx, by;
        float Ax, Ay, Bx, By;

        public QuadCurve (float x1, float y1, float cx, float cy, float x2, float y2) {
            ax = x2 - x1;
            ay = y2 - y1;
            bx = cx - x1;
            by = cy - y1;

            Bx = bx + bx; // Bx = 2f * bx
            Ax = ax - Bx; // Ax = ax - 2f * bx

            By = by + by; // By = 2f * by
            Ay = ay - By; // Ay = ay - 2f * by
        }

        public int cross (float[] res, int rc, float py1, float py2) {
            int cross = 0;

            for (int i = 0; i < rc; i++) {
                float t = res[i];

                // CURVE-OUTSIDE
                if (t < -DELTA || t > 1 + DELTA) {
                    continue;
                }
                // CURVE-START
                if (t < DELTA) {
                    if (py1 < 0f && (bx != 0f ? bx : ax - bx) < 0f) {
                        cross--;
                    }
                    continue;
                }
                // CURVE-END
                if (t > 1 - DELTA) {
                    if (py1 < ay && (ax != bx ? ax - bx : bx) > 0f) {
                        cross++;
                    }
                    continue;
                }
                // CURVE-INSIDE
                float ry = t * (t * Ay + By);
                // ry = t * t * Ay + t * By
                if (ry > py2) {
                    float rxt = t * Ax + bx;
                    // rxt = 2f * t * Ax + Bx = 2f * t * Ax + 2f * bx
                    if (rxt > -DELTA && rxt < DELTA) {
                        continue;
                    }
                    cross += rxt > 0f ? 1 : -1;
                }
            } // for

            return cross;
        }

        public int solvePoint (float[] res, float px) {
            float[] eqn = { -px, Bx, Ax };
            return solveQuad(eqn, res);
        }

        public int solveExtreme (float[] res) {
            int rc = 0;
            if (Ax != 0f) {
                res[rc++] = -Bx / (Ax + Ax);
            }
            if (Ay != 0f) {
                res[rc++] = -By / (Ay + Ay);
            }
            return rc;
        }

        public int addBound (float[] bound, int bc, float[] res, int rc, float minX, float maxX,
                             boolean changeId, int id) {
            for (int i = 0; i < rc; i++) {
                float t = res[i];
                if (t > -DELTA && t < 1 + DELTA) {
                    float rx = t * (t * Ax + Bx);
                    if (minX <= rx && rx <= maxX) {
                        bound[bc++] = t;
                        bound[bc++] = rx;
                        bound[bc++] = t * (t * Ay + By);
                        bound[bc++] = id;
                        if (changeId) {
                            id++;
                        }
                    }
                }
            }
            return bc;
        }
    }

    /** CubicCurve helper for finding curve crossing and calculating bounds. */
    public static class CubicCurveH
    {
        float ax, ay, bx, by, cx, cy;
        float Ax, Ay, Bx, By, Cx, Cy;
        float Ax3, Bx2;

        public CubicCurveH (float x1, float y1, float cx1, float cy1, float cx2, float cy2,
                            float x2, float y2) {
            ax = x2 - x1;
            ay = y2 - y1;
            bx = cx1 - x1;
            by = cy1 - y1;
            cx = cx2 - x1;
            cy = cy2 - y1;

            Cx = bx + bx + bx; // Cx = 3f * bx
            Bx = cx + cx + cx - Cx - Cx; // Bx = 3f * cx - 6f * bx
            Ax = ax - Bx - Cx; // Ax = ax - 3f * cx + 3f * bx

            Cy = by + by + by; // Cy = 3f * by
            By = cy + cy + cy - Cy - Cy; // By = 3f * cy - 6f * by
            Ay = ay - By - Cy; // Ay = ay - 3f * cy + 3f * by

            Ax3 = Ax + Ax + Ax;
            Bx2 = Bx + Bx;
        }

        public int cross (float[] res, int rc, float py1, float py2) {
            int cross = 0;
            for (int i = 0; i < rc; i++) {
                float t = res[i];

                // CURVE-OUTSIDE
                if (t < -DELTA || t > 1 + DELTA) {
                    continue;
                }
                // CURVE-START
                if (t < DELTA) {
                    if (py1 < 0f && (bx != 0f ? bx : (cx != bx ? cx - bx : ax - cx)) < 0f) {
                        cross--;
                    }
                    continue;
                }
                // CURVE-END
                if (t > 1 - DELTA) {
                    if (py1 < ay && (ax != cx ? ax - cx : (cx != bx ? cx - bx : bx)) > 0f) {
                        cross++;
                    }
                    continue;
                }
                // CURVE-INSIDE
                float ry = t * (t * (t * Ay + By) + Cy);
                // ry = t * t * t * Ay + t * t * By + t * Cy
                if (ry > py2) {
                    float rxt = t * (t * Ax3 + Bx2) + Cx;
                    // rxt = 3f * t * t * Ax + 2f * t * Bx + Cx
                    if (rxt > -DELTA && rxt < DELTA) {
                        rxt = t * (Ax3 + Ax3) + Bx2;
                        // rxt = 6f * t * Ax + 2f * Bx
                        if (rxt < -DELTA || rxt > DELTA) {
                            // Inflection point
                            continue;
                        }
                        rxt = ax;
                    }
                    cross += rxt > 0f ? 1 : -1;
                }
            } // for

            return cross;
        }

        public int solvePoint (float[] res, float px) {
            float[] eqn = { -px, Cx, Bx, Ax };
            return solveCubic(eqn, res);
        }

        public int solveExtremeX (float[] res) {
            float[] eqn = { Cx, Bx2, Ax3 };
            return solveQuad(eqn, res);
        }

        public int solveExtremeY (float[] res) {
            float[] eqn = { Cy, By + By, Ay + Ay + Ay };
            return solveQuad(eqn, res);
        }

        public int addBound (float[] bound, int bc, float[] res, int rc, float minX, float maxX,
                             boolean changeId, int id) {
            for (int i = 0; i < rc; i++) {
                float t = res[i];
                if (t > -DELTA && t < 1 + DELTA) {
                    float rx = t * (t * (t * Ax + Bx) + Cx);
                    if (minX <= rx && rx <= maxX) {
                        bound[bc++] = t;
                        bound[bc++] = rx;
                        bound[bc++] = t * (t * (t * Ay + By) + Cy);
                        bound[bc++] = id;
                        if (changeId) {
                            id++;
                        }
                    }
                }
            }
            return bc;
        }
    }

    /**
     * Returns how many times ray from point (x,y) cross line.
     */
    public static int crossLine (float x1, float y1, float x2, float y2, float x, float y) {
        // LEFT/RIGHT/UP/EMPTY
        if ((x < x1 && x < x2) || (x > x1 && x > x2) || (y > y1 && y > y2) || (x1 == x2)) {
            return 0;
        }

        // DOWN
        if (y < y1 && y < y2) {
        } else {
            // INSIDE
            if ((y2 - y1) * (x - x1) / (x2 - x1) <= y - y1) {
                // INSIDE-UP
                return 0;
            }
        }

        // START
        if (x == x1) {
            return x1 < x2 ? 0 : -1;
        }

        // END
        if (x == x2) {
            return x1 < x2 ? 1 : 0;
        }

        // INSIDE-DOWN
        return x1 < x2 ? 1 : -1;
    }

    /**
     * Returns how many times ray from point (x,y) cross quard curve
     */
    public static int crossQuad (float x1, float y1, float cx, float cy, float x2, float y2,
                                 float x, float y) {
        // LEFT/RIGHT/UP/EMPTY
        if ((x < x1 && x < cx && x < x2) || (x > x1 && x > cx && x > x2)
                || (y > y1 && y > cy && y > y2) || (x1 == cx && cx == x2)) {
            return 0;
        }

        // DOWN
        if (y < y1 && y < cy && y < y2 && x != x1 && x != x2) {
            if (x1 < x2) {
                return x1 < x && x < x2 ? 1 : 0;
            }
            return x2 < x && x < x1 ? -1 : 0;
        }

        // INSIDE
        QuadCurve c = new QuadCurve(x1, y1, cx, cy, x2, y2);
        float px = x - x1, py = y - y1;
        float[] res = new float[3];
        int rc = c.solvePoint(res, px);
        return c.cross(res, rc, py, py);
    }

    /**
     * Returns how many times ray from point (x,y) cross cubic curve
     */
    public static int crossCubic (float x1, float y1, float cx1, float cy1, float cx2,
                                  float cy2, float x2, float y2, float x, float y) {
        // LEFT/RIGHT/UP/EMPTY
        if ((x < x1 && x < cx1 && x < cx2 && x < x2) || (x > x1 && x > cx1 && x > cx2 && x > x2)
                || (y > y1 && y > cy1 && y > cy2 && y > y2)
                || (x1 == cx1 && cx1 == cx2 && cx2 == x2)) {
            return 0;
        }

        // DOWN
        if (y < y1 && y < cy1 && y < cy2 && y < y2 && x != x1 && x != x2) {
            if (x1 < x2) {
                return x1 < x && x < x2 ? 1 : 0;
            }
            return x2 < x && x < x1 ? -1 : 0;
        }

        // INSIDE
        CubicCurveH c = new CubicCurveH(x1, y1, cx1, cy1, cx2, cy2, x2, y2);
        float px = x - x1, py = y - y1;
        float[] res = new float[3];
        int rc = c.solvePoint(res, px);
        return c.cross(res, rc, py, py);
    }

    /**
     * Returns how many times ray from point (x,y) cross path
     */
    public static int crossPath (PathIterator p, float x, float y) {
        int cross = 0;
        float mx, my, cx, cy;
        mx = my = cx = cy = 0f;
        float[] coords = new float[6];

        while (!p.isDone()) {
            switch (p.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO:
                if (cx != mx || cy != my) {
                    cross += crossLine(cx, cy, mx, my, x, y);
                }
                mx = cx = coords[0];
                my = cy = coords[1];
                break;
            case PathIterator.SEG_LINETO:
                cross += crossLine(cx, cy, cx = coords[0], cy = coords[1], x, y);
                break;
            case PathIterator.SEG_QUADTO:
                cross += crossQuad(cx, cy, coords[0], coords[1], cx = coords[2], cy = coords[3], x,
                        y);
                break;
            case PathIterator.SEG_CUBICTO:
                cross += crossCubic(cx, cy, coords[0], coords[1], coords[2], coords[3],
                        cx = coords[4], cy = coords[5], x, y);
                break;
            case PathIterator.SEG_CLOSE:
                if (cy != my || cx != mx) {
                    cross += crossLine(cx, cy, cx = mx, cy = my, x, y);
                }
                break;
            }

            // checks if the point (x,y) is the vertex of shape with PathIterator p
            if (x == cx && y == cy) {
                cross = 0;
                cy = my;
                break;
            }
            p.next();
        }
        if (cy != my) {
            cross += crossLine(cx, cy, mx, my, x, y);
        }
        return cross;
    }

    /**
     * Returns how many times a ray from point (x,y) crosses a shape.
     */
    public static int crossShape (IShape s, float x, float y) {
        if (!s.bounds().contains(x, y)) {
            return 0;
        }
        return crossPath(s.pathIterator(null), x, y);
    }

    /**
     * Returns true if value is close enough to zero.
     */
    public static boolean isZero (float val) {
        return -DELTA < val && val < DELTA;
    }

    /**
     * Returns how many times rectangle stripe cross line or the are intersect
     */
    public static int intersectLine (float x1, float y1, float x2, float y2, float rx1,
                                     float ry1, float rx2, float ry2) {
        // LEFT/RIGHT/UP
        if ((rx2 < x1 && rx2 < x2) || (rx1 > x1 && rx1 > x2) || (ry1 > y1 && ry1 > y2)) {
            return 0;
        }

        // DOWN
        if (ry2 < y1 && ry2 < y2) {

        } else {
            // INSIDE
            if (x1 == x2) {
                return CROSSING;
            }

            // Build bound
            float bx1, bx2;
            if (x1 < x2) {
                bx1 = x1 < rx1 ? rx1 : x1;
                bx2 = x2 < rx2 ? x2 : rx2;
            } else {
                bx1 = x2 < rx1 ? rx1 : x2;
                bx2 = x1 < rx2 ? x1 : rx2;
            }
            float k = (y2 - y1) / (x2 - x1);
            float by1 = k * (bx1 - x1) + y1;
            float by2 = k * (bx2 - x1) + y1;

            // BOUND-UP
            if (by1 < ry1 && by2 < ry1) {
                return 0;
            }

            // BOUND-DOWN
            if (by1 > ry2 && by2 > ry2) {
            } else {
                return CROSSING;
            }
        }

        // EMPTY
        if (x1 == x2) {
            return 0;
        }

        // CURVE-START
        if (rx1 == x1) {
            return x1 < x2 ? 0 : -1;
        }

        // CURVE-END
        if (rx1 == x2) {
            return x1 < x2 ? 1 : 0;
        }

        if (x1 < x2) {
            return x1 < rx1 && rx1 < x2 ? 1 : 0;
        }
        return x2 < rx1 && rx1 < x1 ? -1 : 0;
    }

    /**
     * Returns how many times rectangle stripe cross quad curve or the are
     * intersect
     */
    public static int intersectQuad (float x1, float y1, float cx, float cy, float x2,
                                     float y2, float rx1, float ry1, float rx2, float ry2) {
        // LEFT/RIGHT/UP ------------------------------------------------------
        if ((rx2 < x1 && rx2 < cx && rx2 < x2) || (rx1 > x1 && rx1 > cx && rx1 > x2) ||
            (ry1 > y1 && ry1 > cy && ry1 > y2)) {
            return 0;
        }

        // DOWN ---------------------------------------------------------------
        if (ry2 < y1 && ry2 < cy && ry2 < y2 && rx1 != x1 && rx1 != x2) {
            if (x1 < x2) {
                return x1 < rx1 && rx1 < x2 ? 1 : 0;
            }
            return x2 < rx1 && rx1 < x1 ? -1 : 0;
        }

        // INSIDE -------------------------------------------------------------
        QuadCurve c = new QuadCurve(x1, y1, cx, cy, x2, y2);
        float px1 = rx1 - x1;
        float py1 = ry1 - y1;
        float px2 = rx2 - x1;
        float py2 = ry2 - y1;

        float[] res1 = new float[3];
        float[] res2 = new float[3];
        int rc1 = c.solvePoint(res1, px1);
        int rc2 = c.solvePoint(res2, px2);

        // INSIDE-LEFT/RIGHT
        if (rc1 == 0 && rc2 == 0) {
            return 0;
        }

        // Build bound --------------------------------------------------------
        float minX = px1 - DELTA;
        float maxX = px2 + DELTA;
        float[] bound = new float[28];
        int bc = 0;
        // Add roots
        bc = c.addBound(bound, bc, res1, rc1, minX, maxX, false, 0);
        bc = c.addBound(bound, bc, res2, rc2, minX, maxX, false, 1);
        // Add extremal points
        rc2 = c.solveExtreme(res2);
        bc = c.addBound(bound, bc, res2, rc2, minX, maxX, true, 2);
        // Add start and end
        if (rx1 < x1 && x1 < rx2) {
            bound[bc++] = 0f;
            bound[bc++] = 0f;
            bound[bc++] = 0f;
            bound[bc++] = 4;
        }
        if (rx1 < x2 && x2 < rx2) {
            bound[bc++] = 1f;
            bound[bc++] = c.ax;
            bound[bc++] = c.ay;
            bound[bc++] = 5;
        }
        // End build bound ----------------------------------------------------

        int cross = crossBound(bound, bc, py1, py2);
        if (cross != UNKNOWN) {
            return cross;
        }
        return c.cross(res1, rc1, py1, py2);
    }

    /**
     * Returns how many times rectangle stripe cross cubic curve or the are
     * intersect
     */
    public static int intersectCubic (float x1, float y1, float cx1, float cy1,
                                      float cx2, float cy2, float x2, float y2,
                                      float rx1, float ry1, float rx2, float ry2) {
        // LEFT/RIGHT/UP
        if ((rx2 < x1 && rx2 < cx1 && rx2 < cx2 && rx2 < x2)
                || (rx1 > x1 && rx1 > cx1 && rx1 > cx2 && rx1 > x2)
                || (ry1 > y1 && ry1 > cy1 && ry1 > cy2 && ry1 > y2)) {
            return 0;
        }

        // DOWN
        if (ry2 < y1 && ry2 < cy1 && ry2 < cy2 && ry2 < y2 && rx1 != x1 && rx1 != x2) {
            if (x1 < x2) {
                return x1 < rx1 && rx1 < x2 ? 1 : 0;
            }
            return x2 < rx1 && rx1 < x1 ? -1 : 0;
        }

        // INSIDE
        CubicCurveH c = new CubicCurveH(x1, y1, cx1, cy1, cx2, cy2, x2, y2);
        float px1 = rx1 - x1;
        float py1 = ry1 - y1;
        float px2 = rx2 - x1;
        float py2 = ry2 - y1;

        float[] res1 = new float[3];
        float[] res2 = new float[3];
        int rc1 = c.solvePoint(res1, px1);
        int rc2 = c.solvePoint(res2, px2);

        // LEFT/RIGHT
        if (rc1 == 0 && rc2 == 0) {
            return 0;
        }

        float minX = px1 - DELTA;
        float maxX = px2 + DELTA;

        // Build bound --------------------------------------------------------
        float[] bound = new float[40];
        int bc = 0;
        // Add roots
        bc = c.addBound(bound, bc, res1, rc1, minX, maxX, false, 0);
        bc = c.addBound(bound, bc, res2, rc2, minX, maxX, false, 1);
        // Add extremal points
        rc2 = c.solveExtremeX(res2);
        bc = c.addBound(bound, bc, res2, rc2, minX, maxX, true, 2);
        rc2 = c.solveExtremeY(res2);
        bc = c.addBound(bound, bc, res2, rc2, minX, maxX, true, 4);
        // Add start and end
        if (rx1 < x1 && x1 < rx2) {
            bound[bc++] = 0f;
            bound[bc++] = 0f;
            bound[bc++] = 0f;
            bound[bc++] = 6;
        }
        if (rx1 < x2 && x2 < rx2) {
            bound[bc++] = 1f;
            bound[bc++] = c.ax;
            bound[bc++] = c.ay;
            bound[bc++] = 7;
        }
        // End build bound ----------------------------------------------------

        int cross = crossBound(bound, bc, py1, py2);
        if (cross != UNKNOWN) {
            return cross;
        }
        return c.cross(res1, rc1, py1, py2);
    }

    /**
     * Returns how many times rectangle stripe cross path or the are intersect
     */
    public static int intersectPath (PathIterator p, float x, float y, float w, float h) {
        int cross = 0;
        int count;
        float mx, my, cx, cy;
        mx = my = cx = cy = 0f;
        float[] coords = new float[6];

        float rx1 = x;
        float ry1 = y;
        float rx2 = x + w;
        float ry2 = y + h;

        while (!p.isDone()) {
            count = 0;
            switch (p.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO:
                if (cx != mx || cy != my) {
                    count = intersectLine(cx, cy, mx, my, rx1, ry1, rx2, ry2);
                }
                mx = cx = coords[0];
                my = cy = coords[1];
                break;
            case PathIterator.SEG_LINETO:
                count = intersectLine(cx, cy, cx = coords[0], cy = coords[1], rx1, ry1, rx2, ry2);
                break;
            case PathIterator.SEG_QUADTO:
                count = intersectQuad(cx, cy, coords[0], coords[1], cx = coords[2], cy = coords[3],
                        rx1, ry1, rx2, ry2);
                break;
            case PathIterator.SEG_CUBICTO:
                count = intersectCubic(cx, cy, coords[0], coords[1], coords[2], coords[3],
                        cx = coords[4], cy = coords[5], rx1, ry1, rx2, ry2);
                break;
            case PathIterator.SEG_CLOSE:
                if (cy != my || cx != mx) {
                    count = intersectLine(cx, cy, mx, my, rx1, ry1, rx2, ry2);
                }
                cx = mx;
                cy = my;
                break;
            }
            if (count == CROSSING) {
                return CROSSING;
            }
            cross += count;
            p.next();
        }
        if (cy != my) {
            count = intersectLine(cx, cy, mx, my, rx1, ry1, rx2, ry2);
            if (count == CROSSING) {
                return CROSSING;
            }
            cross += count;
        }
        return cross;
    }

    /**
     * Returns how many times rectangle stripe cross shape or the are intersect
     */
    public static int intersectShape (IShape s, float x, float y, float w, float h) {
        if (!s.bounds().intersects(x, y, w, h)) {
            return 0;
        }
        return intersectPath(s.pathIterator(null), x, y, w, h);
    }

    /**
     * Returns true if cross count correspond inside location for non zero path
     * rule
     */
    public static boolean isInsideNonZero (int cross) {
        return cross != 0;
    }

    /**
     * Returns true if cross count correspond inside location for even-odd path
     * rule
     */
    public static boolean isInsideEvenOdd (int cross) {
        return (cross & 1) != 0;
    }

    /**
     * Sorts a bound array.
     */
    protected static void sortBound (float[] bound, int bc) {
        for (int i = 0; i < bc - 4; i += 4) {
            int k = i;
            for (int j = i + 4; j < bc; j += 4) {
                if (bound[k] > bound[j]) {
                    k = j;
                }
            }
            if (k != i) {
                float tmp = bound[i];
                bound[i] = bound[k];
                bound[k] = tmp;
                tmp = bound[i + 1];
                bound[i + 1] = bound[k + 1];
                bound[k + 1] = tmp;
                tmp = bound[i + 2];
                bound[i + 2] = bound[k + 2];
                bound[k + 2] = tmp;
                tmp = bound[i + 3];
                bound[i + 3] = bound[k + 3];
                bound[k + 3] = tmp;
            }
        }
    }

    /**
     * Returns whether bounds intersect a rectangle or not.
     */
    protected static int crossBound (float[] bound, int bc, float py1, float py2) {
        // LEFT/RIGHT
        if (bc == 0) {
            return 0;
        }

        // Check Y coordinate
        int up = 0;
        int down = 0;
        for (int i = 2; i < bc; i += 4) {
            if (bound[i] < py1) {
                up++;
                continue;
            }
            if (bound[i] > py2) {
                down++;
                continue;
            }
            return CROSSING;
        }

        // UP
        if (down == 0) {
            return 0;
        }

        if (up != 0) {
            // bc >= 2
            sortBound(bound, bc);
            boolean sign = bound[2] > py2;
            for (int i = 6; i < bc; i += 4) {
                boolean sign2 = bound[i] > py2;
                if (sign != sign2 && bound[i + 1] != bound[i - 3]) {
                    return CROSSING;
                }
                sign = sign2;
            }
        }
        return UNKNOWN;
    }

    /** Allowable tolerance for bounds comparison */
    protected static final float DELTA = 1E-5f;

    /** If roots have distance less then <code>ROOT_DELTA</code> they are double */
    protected static final float ROOT_DELTA = 1E-10f;
}
