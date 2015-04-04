//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.util.NoSuchElementException;

import pythagoras.util.Platform;

/**
 * Stores and manipulates an enclosed area of 2D space.
 * See http://download.oracle.com/javase/6/docs/api/java/awt/geom/Area.html
 */
public class Area implements IShape, Cloneable
{
    /**
     * Creates an empty area.
     */
    public Area () {
    }

    /**
     * Creates an area from the supplied shape.
     */
    public Area (IShape s) {
        float[] segmentCoords = new float[6];
        float lastMoveX = 0f;
        float lastMoveY = 0f;
        int rulesIndex = 0;
        int coordsIndex = 0;

        for (PathIterator pi = s.pathIterator(null); !pi.isDone(); pi.next()) {
            coords = adjustSize(coords, coordsIndex + 6);
            rules = adjustSize(rules, rulesIndex + 1);
            offsets = adjustSize(offsets, rulesIndex + 1);
            rules[rulesIndex] = pi.currentSegment(segmentCoords);
            offsets[rulesIndex] = coordsIndex;

            switch (rules[rulesIndex]) {
            case PathIterator.SEG_MOVETO:
                coords[coordsIndex++] = segmentCoords[0];
                coords[coordsIndex++] = segmentCoords[1];
                lastMoveX = segmentCoords[0];
                lastMoveY = segmentCoords[1];
                ++moveToCount;
                break;
            case PathIterator.SEG_LINETO:
                if ((segmentCoords[0] != lastMoveX) || (segmentCoords[1] != lastMoveY)) {
                    coords[coordsIndex++] = segmentCoords[0];
                    coords[coordsIndex++] = segmentCoords[1];
                } else {
                    --rulesIndex;
                }
                break;
            case PathIterator.SEG_QUADTO:
                System.arraycopy(segmentCoords, 0, coords, coordsIndex, 4);
                coordsIndex += 4;
                isPolygonal = false;
                break;
            case PathIterator.SEG_CUBICTO:
                System.arraycopy(segmentCoords, 0, coords, coordsIndex, 6);
                coordsIndex += 6;
                isPolygonal = false;
                break;
            case PathIterator.SEG_CLOSE:
                break;
            }
            ++rulesIndex;
        }

        if ((rulesIndex != 0) && (rules[rulesIndex - 1] != PathIterator.SEG_CLOSE)) {
            rules[rulesIndex] = PathIterator.SEG_CLOSE;
            offsets[rulesIndex] = coordsSize;
        }

        rulesSize = rulesIndex;
        coordsSize = coordsIndex;
    }

    /**
     * Returns true if this area is polygonal.
     */
    public boolean isPolygonal () {
        return isPolygonal;
    }

    /**
     * Returns true if this area is rectangular.
     */
    public boolean isRectangular () {
        return (isPolygonal) && (rulesSize <= 5) && (coordsSize <= 8) &&
            (coords[1] == coords[3]) && (coords[7] == coords[5]) &&
            (coords[0] == coords[6]) && (coords[2] == coords[4]);
    }

    /**
     * Returns true if this area encloses only a single contiguous space.
     */
    public boolean isSingular () {
        return (moveToCount <= 1);
    }

    /**
     * Resets this area to empty.
     */
    public void reset () {
        coordsSize = 0;
        rulesSize = 0;
    }

    /**
     * Transforms this area with the supplied transform.
     */
    public void transform (Transform t) {
        copy(new Area(Transforms.createTransformedShape(t, this)), this);
    }

    /**
     * Creates a new area equal to this area transformed by the supplied transform.
     */
    public Area createTransformedArea (Transform t) {
        return new Area(Transforms.createTransformedShape(t, this));
    }

    /**
     * Adds the supplied area to this area.
     */
    public void add (Area area) {
        if (area == null || area.isEmpty()) {
            return;
        } else if (isEmpty()) {
            copy(area, this);
            return;
        }

        if (isPolygonal() && area.isPolygonal()) {
            addPolygon(area);
        } else {
            addCurvePolygon(area);
        }

        if (areaBoundsSquare() < GeometryUtil.EPSILON) {
            reset();
        }
    }

    /**
     * Intersects the supplied area with this area.
     */
    public void intersect (Area area) {
        if (area == null) {
            return;
        } else if (isEmpty() || area.isEmpty()) {
            reset();
            return;
        }

        if (isPolygonal() && area.isPolygonal()) {
            intersectPolygon(area);
        } else {
            intersectCurvePolygon(area);
        }

        if (areaBoundsSquare() < GeometryUtil.EPSILON) {
            reset();
        }
    }

    /**
     * Subtracts the supplied area from this area.
     */
    public void subtract (Area area) {
        if (area == null || isEmpty() || area.isEmpty()) {
            return;
        }

        if (isPolygonal() && area.isPolygonal()) {
            subtractPolygon(area);
        } else {
            subtractCurvePolygon(area);
        }

        if (areaBoundsSquare() < GeometryUtil.EPSILON) {
            reset();
        }
    }

    /**
     * Computes the exclusive or of this area and the supplied area and sets this area to the
     * result.
     */
    public void exclusiveOr (Area area) {
        Area a = clone();
        a.intersect(area);
        add(area);
        subtract(a);
    }

    @Override // from interface IShape
    public boolean isEmpty () {
        return (rulesSize == 0) && (coordsSize == 0);
    }

    @Override // from interface IShape
    public boolean contains (float x, float y) {
        return !isEmpty() && containsExact(x, y) > 0;
    }

    @Override // from interface IShape
    public boolean contains (float x, float y, float width, float height) {
        int crossCount = Crossing.intersectPath(pathIterator(null), x, y, width, height);
        return crossCount != Crossing.CROSSING && Crossing.isInsideEvenOdd(crossCount);
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
    public boolean intersects (float x, float y, float width, float height) {
        if ((width <= 0f) || (height <= 0f)) {
            return false;
        } else if (!bounds().intersects(x, y, width, height)) {
            return false;
        }
        int crossCount = Crossing.intersectShape(this, x, y, width, height);
        return Crossing.isInsideEvenOdd(crossCount);
    }

    @Override // from interface IShape
    public boolean intersects (IRectangle r) {
        return intersects(r.x(), r.y(), r.width(), r.height());
    }

    @Override // from interface IShape
    public Rectangle bounds () {
        return bounds(new Rectangle());
    }

    @Override // from interface IShape
    public Rectangle bounds (Rectangle target) {
        float maxX = coords[0], maxY = coords[1];
        float minX = coords[0], minY = coords[1];
        for (int i = 0; i < coordsSize;) {
            minX = Math.min(minX, coords[i]);
            maxX = Math.max(maxX, coords[i++]);
            minY = Math.min(minY, coords[i]);
            maxY = Math.max(maxY, coords[i++]);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t) {
        return new AreaPathIterator(t);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t, float flatness) {
        return new FlatteningPathIterator(pathIterator(t), flatness);
    }

    @Override // from Object
    public boolean equals (Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof Area)) {
            return false;
        }
        Area area = clone();
        area.subtract((Area)obj);
        return area.isEmpty();
    }

    // @Override // can't declare @Override due to GWT
    public Area clone () {
        Area area = new Area();
        copy(this, area);
        return area;
    }

    private void addCurvePolygon (Area area) {
        CurveCrossingHelper crossHelper = new CurveCrossingHelper(
            new float[][] { coords, area.coords },
            new int[] { coordsSize, area.coordsSize },
            new int[][] { rules, area.rules },
            new int[] { rulesSize, area.rulesSize },
            new int[][] { offsets, area.offsets });
        IntersectPoint[] intersectPoints = crossHelper.findCrossing();

        if (intersectPoints.length == 0) {
            if (area.contains(bounds())) {
                copy(area, this);
            } else if (!contains(area.bounds())) {
                coords = adjustSize(coords, coordsSize + area.coordsSize);
                System.arraycopy(area.coords, 0, coords, coordsSize, area.coordsSize);
                coordsSize += area.coordsSize;
                rules = adjustSize(rules, rulesSize + area.rulesSize);
                System.arraycopy(area.rules, 0, rules, rulesSize, area.rulesSize);
                rulesSize += area.rulesSize;
                offsets = adjustSize(offsets, rulesSize + area.rulesSize);
                System.arraycopy(area.offsets, 0, offsets, rulesSize, area.rulesSize);
            }

            return;
        }

        float[] resultCoords = new float[coordsSize + area.coordsSize + intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;

        do {
            resultCoords[resultCoordPos++] = point.x();
            resultCoords[resultCoordPos++] = point.y();
            int curIndex = point.endIndex(true);
            if (curIndex < 0) {
                isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], coords[2 * curIndex + 1]) > 0) {
                isCurrentArea = false;
            } else {
                isCurrentArea = true;
            }

            IntersectPoint nextPoint = nextIntersectPoint(intersectPoints, point, isCurrentArea);
            float[] coords = (isCurrentArea) ? this.coords : area.coords;
            int[] offsets = (isCurrentArea) ? this.offsets : area.offsets;
            int[] rules = (isCurrentArea) ? this.rules : area.rules;
            int offset = point.ruleIndex(isCurrentArea);
            boolean isCopyUntilZero = false;
            if ((point.ruleIndex(isCurrentArea) > nextPoint.ruleIndex(isCurrentArea))) {
                int rulesSize = (isCurrentArea) ? this.rulesSize : area.rulesSize;
                resultCoordPos = includeCoordsAndRules(offset + 1, rulesSize, rules, offsets,
                        resultRules, resultOffsets, resultCoords, coords, resultRulesPos,
                        resultCoordPos, point, isCurrentArea, false, 0);
                resultRulesPos += rulesSize - offset - 1;
                offset = 1;
                isCopyUntilZero = true;
            }

            int length = nextPoint.ruleIndex(isCurrentArea) - offset + 1;
            if (isCopyUntilZero) {
                offset = 0;
            }

            resultCoordPos = includeCoordsAndRules(offset, length, rules, offsets, resultRules,
                    resultOffsets, resultCoords, coords, resultRulesPos, resultCoordPos, point,
                    isCurrentArea, true, 0);
            resultRulesPos += length - offset;
            point = nextPoint;
        } while (point != intersectPoints[0]);

        resultRules[resultRulesPos++] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
        this.coords = resultCoords;
        this.rules = resultRules;
        this.offsets = resultOffsets;
        this.coordsSize = resultCoordPos;
        this.rulesSize = resultRulesPos;
    }

    private void addPolygon (Area area) {
        CrossingHelper crossHelper = new CrossingHelper(
            new float[][] { coords, area.coords },
            new int[] { coordsSize, area.coordsSize });
        IntersectPoint[] intersectPoints = crossHelper.findCrossing();

        if (intersectPoints.length == 0) {
            if (area.contains(bounds())) {
                copy(area, this);
            } else if (!contains(area.bounds())) {
                coords = adjustSize(coords, coordsSize + area.coordsSize);
                System.arraycopy(area.coords, 0, coords, coordsSize, area.coordsSize);
                coordsSize += area.coordsSize;
                rules = adjustSize(rules, rulesSize + area.rulesSize);
                System.arraycopy(area.rules, 0, rules, rulesSize, area.rulesSize);
                rulesSize += area.rulesSize;
                offsets = adjustSize(offsets, rulesSize + area.rulesSize);
                System.arraycopy(area.offsets, 0, offsets, rulesSize, area.rulesSize);
            }
            return;
        }

        float[] resultCoords = new float[coordsSize + area.coordsSize + intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;

        do {
            resultCoords[resultCoordPos++] = point.x();
            resultCoords[resultCoordPos++] = point.y();
            resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            resultOffsets[resultRulesPos++] = resultCoordPos - 2;
            int curIndex = point.endIndex(true);
            if (curIndex < 0) {
                isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], coords[2 * curIndex + 1]) > 0) {
                isCurrentArea = false;
            } else {
                isCurrentArea = true;
            }

            IntersectPoint nextPoint = nextIntersectPoint(intersectPoints, point, isCurrentArea);
            float[] coords = (isCurrentArea) ? this.coords : area.coords;
            int offset = 2 * point.endIndex(isCurrentArea);
            if ((offset >= 0) &&
                (nextPoint.begIndex(isCurrentArea) < point.endIndex(isCurrentArea))) {
                int coordSize = (isCurrentArea) ? this.coordsSize : area.coordsSize;
                int length = coordSize - offset;
                System.arraycopy(coords, offset, resultCoords, resultCoordPos, length);

                for (int i = 0; i < length / 2; i++) {
                    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                    resultOffsets[resultRulesPos++] = resultCoordPos;
                    resultCoordPos += 2;
                }

                offset = 0;
            }

            if (offset >= 0) {
                int length = 2 * nextPoint.begIndex(isCurrentArea) - offset + 2;
                System.arraycopy(coords, offset, resultCoords, resultCoordPos, length);

                for (int i = 0; i < length / 2; i++) {
                    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                    resultOffsets[resultRulesPos++] = resultCoordPos;
                    resultCoordPos += 2;
                }
            }

            point = nextPoint;
        } while (point != intersectPoints[0]);

        resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
        coords = resultCoords;
        rules = resultRules;
        offsets = resultOffsets;
        coordsSize = resultCoordPos;
        rulesSize = resultRulesPos;
    }

    private void intersectCurvePolygon (Area area) {
        CurveCrossingHelper crossHelper = new CurveCrossingHelper(
            new float[][] { coords, area.coords },
            new int[] { coordsSize, area.coordsSize },
            new int[][] { rules, area.rules },
            new int[] { rulesSize, area.rulesSize },
            new int[][] { offsets, area.offsets });
        IntersectPoint[] intersectPoints = crossHelper.findCrossing();
        if (intersectPoints.length == 0) {
            if (contains(area.bounds())) {
                copy(area, this);
            } else if (!area.contains(bounds())) {
                reset();
            }
            return;
        }

        float[] resultCoords = new float[coordsSize + area.coordsSize + intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        IntersectPoint nextPoint = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;

        do {
            resultCoords[resultCoordPos++] = point.x();
            resultCoords[resultCoordPos++] = point.y();

            int curIndex = point.endIndex(true);
            if ((curIndex < 0) ||
                (area.containsExact(coords[2 * curIndex], coords[2 * curIndex + 1]) == 0)) {
                isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], coords[2 * curIndex + 1]) > 0) {
                isCurrentArea = true;
            } else {
                isCurrentArea = false;
            }

            nextPoint = nextIntersectPoint(intersectPoints, point, isCurrentArea);
            float[] coords = (isCurrentArea) ? this.coords : area.coords;
            int[] offsets = (isCurrentArea) ? this.offsets : area.offsets;
            int[] rules = (isCurrentArea) ? this.rules : area.rules;
            int offset = point.ruleIndex(isCurrentArea);
            boolean isCopyUntilZero = false;

            if (point.ruleIndex(isCurrentArea) > nextPoint.ruleIndex(isCurrentArea)) {
                int rulesSize = (isCurrentArea) ? this.rulesSize : area.rulesSize;
                resultCoordPos = includeCoordsAndRules(
                    offset + 1, rulesSize, rules, offsets, resultRules, resultOffsets,
                    resultCoords, coords, resultRulesPos, resultCoordPos, point, isCurrentArea,
                    false, 1);
                resultRulesPos += rulesSize - offset - 1;
                offset = 1;
                isCopyUntilZero = true;
            }

            int length = nextPoint.ruleIndex(isCurrentArea) - offset + 1;

            if (isCopyUntilZero) {
                offset = 0;
                isCopyUntilZero = false;
            }
            if ((length == offset) &&
                (nextPoint.rule(isCurrentArea) != PathIterator.SEG_LINETO) &&
                (nextPoint.rule(isCurrentArea) != PathIterator.SEG_CLOSE) &&
                (point.rule(isCurrentArea) != PathIterator.SEG_LINETO) &&
                (point.rule(isCurrentArea) != PathIterator.SEG_CLOSE)) {
                isCopyUntilZero = true;
                length++;
            }

            resultCoordPos = includeCoordsAndRules(
                offset, length, rules, offsets, resultRules, resultOffsets, resultCoords, coords,
                resultRulesPos, resultCoordPos, nextPoint, isCurrentArea, true, 1);
            resultRulesPos = ((length <= offset) || (isCopyUntilZero)) ?
                resultRulesPos + 1 : resultRulesPos + length;

            point = nextPoint;
        } while (point != intersectPoints[0]);

        if (resultRules[resultRulesPos - 1] == PathIterator.SEG_LINETO) {
            resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        } else {
            resultCoords[resultCoordPos++] = nextPoint.x();
            resultCoords[resultCoordPos++] = nextPoint.y();
            resultRules[resultRulesPos++] = PathIterator.SEG_CLOSE;
        }

        resultOffsets[resultRulesPos - 1] = resultCoordPos;
        coords = resultCoords;
        rules = resultRules;
        offsets = resultOffsets;
        coordsSize = resultCoordPos;
        rulesSize = resultRulesPos;
    }

    private void intersectPolygon (Area area) {
        CrossingHelper crossHelper = new CrossingHelper(
            new float[][] { coords, area.coords },
            new int[] { coordsSize, area.coordsSize });
        IntersectPoint[] intersectPoints = crossHelper.findCrossing();
        if (intersectPoints.length == 0) {
            if (contains(area.bounds())) {
                copy(area, this);
            } else if (!area.contains(bounds())) {
                reset();
            }
            return;
        }

        float[] resultCoords = new float[coordsSize + area.coordsSize + intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;

        do {
            resultCoords[resultCoordPos++] = point.x();
            resultCoords[resultCoordPos++] = point.y();
            resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            resultOffsets[resultRulesPos++] = resultCoordPos - 2;
            int curIndex = point.endIndex(true);

            if ((curIndex < 0) ||
                (area.containsExact(coords[2 * curIndex], coords[2 * curIndex + 1]) == 0)) {
                isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], coords[2 * curIndex + 1]) > 0) {
                isCurrentArea = true;
            } else {
                isCurrentArea = false;
            }

            IntersectPoint nextPoint = nextIntersectPoint(intersectPoints, point, isCurrentArea);
            float[] coords = (isCurrentArea) ? this.coords : area.coords;
            int offset = 2 * point.endIndex(isCurrentArea);
            if ((offset >= 0) &&
                (nextPoint.begIndex(isCurrentArea) < point.endIndex(isCurrentArea))) {
                int coordSize = (isCurrentArea) ? this.coordsSize : area.coordsSize;
                int length = coordSize - offset;
                System.arraycopy(coords, offset, resultCoords, resultCoordPos, length);

                for (int i = 0; i < length / 2; i++) {
                    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                    resultOffsets[resultRulesPos++] = resultCoordPos;
                    resultCoordPos += 2;
                }

                offset = 0;
            }

            if (offset >= 0) {
                int length = 2 * nextPoint.begIndex(isCurrentArea) - offset + 2;
                System.arraycopy(coords, offset, resultCoords, resultCoordPos, length);

                for (int i = 0; i < length / 2; i++) {
                    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                    resultOffsets[resultRulesPos++] = resultCoordPos;
                    resultCoordPos += 2;
                }
            }

            point = nextPoint;
        } while (point != intersectPoints[0]);

        resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
        coords = resultCoords;
        rules = resultRules;
        offsets = resultOffsets;
        coordsSize = resultCoordPos;
        rulesSize = resultRulesPos;
    }

    private void subtractCurvePolygon (Area area) {
        CurveCrossingHelper crossHelper = new CurveCrossingHelper(
            new float[][] { coords, area.coords },
            new int[] { coordsSize, area.coordsSize },
            new int[][] { rules, area.rules },
            new int[] { rulesSize, area.rulesSize },
            new int[][] { offsets, area.offsets });
        IntersectPoint[] intersectPoints = crossHelper.findCrossing();
        if (intersectPoints.length == 0 && contains(area.bounds())) {
            copy(area, this);
            return;
        }

        float[] resultCoords = new float[coordsSize + area.coordsSize + intersectPoints.length];
        int[] resultRules = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int[] resultOffsets = new int[rulesSize + area.rulesSize + intersectPoints.length];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;

        do {
            resultCoords[resultCoordPos++] = point.x();
            resultCoords[resultCoordPos++] = point.y();
            int curIndex = offsets[point.ruleIndex(true)] % coordsSize;
            if (area.containsExact(coords[curIndex], coords[curIndex + 1]) == 0) {
                isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[curIndex], coords[curIndex + 1]) > 0) {
                isCurrentArea = false;
            } else {
                isCurrentArea = true;
            }

            IntersectPoint nextPoint = (isCurrentArea) ?
                nextIntersectPoint(intersectPoints, point, isCurrentArea) :
                prevIntersectPoint(intersectPoints, point, isCurrentArea);
            float[] coords = (isCurrentArea) ? this.coords : area.coords;
            int[] offsets = (isCurrentArea) ? this.offsets : area.offsets;
            int[] rules = (isCurrentArea) ? this.rules : area.rules;
            int offset = (isCurrentArea) ? point.ruleIndex(isCurrentArea) :
                nextPoint.ruleIndex(isCurrentArea);
            boolean isCopyUntilZero = false;

            if (((isCurrentArea) &&
                 (point.ruleIndex(isCurrentArea) > nextPoint.ruleIndex(isCurrentArea))) ||
                ((!isCurrentArea) &&
                 (nextPoint.ruleIndex(isCurrentArea) > nextPoint.ruleIndex(isCurrentArea)))) {
                int rulesSize = (isCurrentArea) ? this.rulesSize : area.rulesSize;
                resultCoordPos = includeCoordsAndRules(
                    offset + 1, rulesSize, rules, offsets, resultRules, resultOffsets, resultCoords,
                    coords, resultRulesPos, resultCoordPos, point, isCurrentArea, false, 2);
                resultRulesPos += rulesSize - offset - 1;
                offset = 1;
                isCopyUntilZero = true;
            }

            int length = nextPoint.ruleIndex(isCurrentArea) - offset + 1;

            if (isCopyUntilZero) {
                offset = 0;
                isCopyUntilZero = false;
            }

            resultCoordPos = includeCoordsAndRules(
                offset, length, rules, offsets, resultRules, resultOffsets, resultCoords, coords,
                resultRulesPos, resultCoordPos, point, isCurrentArea, true, 2);

            if ((length == offset) &&
                ((rules[offset] == PathIterator.SEG_QUADTO) ||
                 (rules[offset] == PathIterator.SEG_CUBICTO))) {
                resultRulesPos++;
            } else {
                resultRulesPos = (length < offset || isCopyUntilZero) ?
                    resultRulesPos + 1 : resultRulesPos + length - offset;
            }

            point = nextPoint;
        } while (point != intersectPoints[0]);

        resultRules[resultRulesPos++] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
        coords = resultCoords;
        rules = resultRules;
        offsets = resultOffsets;
        coordsSize = resultCoordPos;
        rulesSize = resultRulesPos;
    }

    private void subtractPolygon (Area area) {
        CrossingHelper crossHelper = new CrossingHelper(
            new float[][] { coords, area.coords },
            new int[] { coordsSize, area.coordsSize });
        IntersectPoint[] intersectPoints = crossHelper.findCrossing();
        if (intersectPoints.length == 0) {
            if (contains(area.bounds())) {
                copy(area, this);
                return;
            }
            return;
        }

        float[] resultCoords = new float[
            2 * (coordsSize + area.coordsSize + intersectPoints.length)];
        int[] resultRules = new int[2 * (rulesSize + area.rulesSize + intersectPoints.length)];
        int[] resultOffsets = new int[2 * (rulesSize + area.rulesSize + intersectPoints.length)];
        int resultCoordPos = 0;
        int resultRulesPos = 0;
        boolean isCurrentArea = true;
        int countPoints = 0;
        boolean curArea = false;
        boolean addArea = false;

        IntersectPoint point = intersectPoints[0];
        resultRules[resultRulesPos] = PathIterator.SEG_MOVETO;
        resultOffsets[resultRulesPos++] = resultCoordPos;

        do {
            resultCoords[resultCoordPos++] = point.x();
            resultCoords[resultCoordPos++] = point.y();
            resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
            resultOffsets[resultRulesPos++] = resultCoordPos - 2;
            int curIndex = point.endIndex(true);

            if ((curIndex < 0) ||
                (area.isVertex(coords[2 * curIndex], coords[2 * curIndex + 1]) &&
                 crossHelper.containsPoint(new float[] { coords[2 * curIndex],
                                                         coords[2 * curIndex + 1] }) &&
                 (coords[2 * curIndex] != point.x() ||
                  coords[2 * curIndex + 1] != point.y()))) {
                isCurrentArea = !isCurrentArea;
            } else if (area.containsExact(coords[2 * curIndex], coords[2 * curIndex + 1]) > 0) {
                isCurrentArea = false;
            } else {
                isCurrentArea = true;
            }

            if (countPoints >= intersectPoints.length) {
                isCurrentArea = !isCurrentArea;
            }

            if (isCurrentArea) {
                curArea = true;
            } else {
                addArea = true;
            }

            IntersectPoint nextPoint = (isCurrentArea) ?
                nextIntersectPoint(intersectPoints, point, isCurrentArea) :
                prevIntersectPoint(intersectPoints, point, isCurrentArea);
            float[] coords = (isCurrentArea) ? this.coords : area.coords;

            int offset = (isCurrentArea) ? 2 * point.endIndex(isCurrentArea) :
                2 * nextPoint.endIndex(isCurrentArea);

            if ((offset > 0) &&
                (((isCurrentArea) &&
                  (nextPoint.begIndex(isCurrentArea) < point.endIndex(isCurrentArea))) ||
                 ((!isCurrentArea) &&
                  (nextPoint.endIndex(isCurrentArea) < nextPoint.begIndex(isCurrentArea))))) {

                int coordSize = (isCurrentArea) ? this.coordsSize : area.coordsSize;
                int length = coordSize - offset;

                if (isCurrentArea) {
                    System.arraycopy(coords, offset, resultCoords, resultCoordPos, length);
                } else {
                    float[] temp = new float[length];
                    System.arraycopy(coords, offset, temp, 0, length);
                    reverseCopy(temp);
                    System.arraycopy(temp, 0, resultCoords, resultCoordPos, length);
                }

                for (int i = 0; i < length / 2; i++) {
                    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                    resultOffsets[resultRulesPos++] = resultCoordPos;
                    resultCoordPos += 2;
                }

                offset = 0;
            }

            if (offset >= 0) {
                int length = (isCurrentArea) ?
                    2 * nextPoint.begIndex(isCurrentArea) - offset + 2 :
                    2 * point.begIndex(isCurrentArea) - offset + 2;

                if (isCurrentArea) {
                    System.arraycopy(coords, offset, resultCoords, resultCoordPos, length);
                } else {
                    float[] temp = new float[length];
                    System.arraycopy(coords, offset, temp, 0, length);
                    reverseCopy(temp);
                    System.arraycopy(temp, 0, resultCoords, resultCoordPos, length);
                }

                for (int i = 0; i < length / 2; i++) {
                    resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                    resultOffsets[resultRulesPos++] = resultCoordPos;
                    resultCoordPos += 2;
                }
            }

            point = nextPoint;
            countPoints++;
        } while (point != intersectPoints[0] || !(curArea && addArea));

        resultRules[resultRulesPos - 1] = PathIterator.SEG_CLOSE;
        resultOffsets[resultRulesPos - 1] = resultCoordPos;
        coords = resultCoords;
        rules = resultRules;
        offsets = resultOffsets;
        coordsSize = resultCoordPos;
        rulesSize = resultRulesPos;
    }

    private IntersectPoint nextIntersectPoint (IntersectPoint[] iPoints,
                                                  IntersectPoint isectPoint,
                                                  boolean isCurrentArea) {
        int endIndex = isectPoint.endIndex(isCurrentArea);
        if (endIndex < 0) {
            return iPoints[Math.abs(endIndex) - 1];
        }

        IntersectPoint firstIsectPoint = null;
        IntersectPoint nextIsectPoint = null;
        for (IntersectPoint point : iPoints) {
            int begIndex = point.begIndex(isCurrentArea);
            if (begIndex >= 0) {
                if (firstIsectPoint == null) {
                    firstIsectPoint = point;
                } else if (begIndex < firstIsectPoint.begIndex(isCurrentArea)) {
                    firstIsectPoint = point;
                }
            }

            if (endIndex <= begIndex) {
                if (nextIsectPoint == null) {
                    nextIsectPoint = point;
                } else if (begIndex < nextIsectPoint.begIndex(isCurrentArea)) {
                    nextIsectPoint = point;
                }
            }
        }

        return (nextIsectPoint != null) ? nextIsectPoint : firstIsectPoint;
    }

    private IntersectPoint prevIntersectPoint (IntersectPoint[] iPoints,
                                                  IntersectPoint isectPoint,
                                                  boolean isCurrentArea) {
        int begIndex = isectPoint.begIndex(isCurrentArea);
        if (begIndex < 0) {
            return iPoints[Math.abs(begIndex) - 1];
        }

        IntersectPoint firstIsectPoint = null;
        IntersectPoint predIsectPoint = null;
        for (IntersectPoint point : iPoints) {
            int endIndex = point.endIndex(isCurrentArea);
            if (endIndex >= 0) {
                if (firstIsectPoint == null) {
                    firstIsectPoint = point;
                } else if (endIndex < firstIsectPoint.endIndex(isCurrentArea)) {
                    firstIsectPoint = point;
                }
            }

            if (endIndex <= begIndex) {
                if (predIsectPoint == null) {
                    predIsectPoint = point;
                } else if (endIndex > predIsectPoint.endIndex(isCurrentArea)) {
                    predIsectPoint = point;
                }
            }
        }

        return (predIsectPoint != null) ? predIsectPoint : firstIsectPoint;
    }

    private int includeCoordsAndRules (
        int offset, int length, int[] rules, int[] offsets, int[] resultRules, int[] resultOffsets,
        float[] resultCoords, float[] coords, int resultRulesPos, int resultCoordPos,
        IntersectPoint point, boolean isCurrentArea, boolean way, int operation) {

        float[] temp = new float[8 * length];
        int coordsCount = 0;
        boolean isMoveIndex = true;
        boolean isMoveLength = true;
        boolean additional = false;

        if (length <= offset) {
            for (int i = resultRulesPos; i < resultRulesPos + 1; i++) {
                resultRules[i] = PathIterator.SEG_LINETO;
            }
        } else {
            int j = resultRulesPos;
            for (int i = offset; i < length; i++) {
                resultRules[j++] = PathIterator.SEG_LINETO;
            }
        }

        if ((length == offset) &&
            ((rules[offset] == PathIterator.SEG_QUADTO) ||
             (rules[offset] == PathIterator.SEG_CUBICTO))) {
            length++;
            additional = true;
        }

        for (int i = offset; i < length; i++) {
            int index = offsets[i];
            if (!isMoveIndex) {
                index -= 2;
            }

            if (!isMoveLength) {
                length++;
                isMoveLength = true;
            }

            switch (rules[i]) {
            case PathIterator.SEG_MOVETO:
                isMoveIndex = false;
                isMoveLength = false;
                break;

            case PathIterator.SEG_LINETO:
            case PathIterator.SEG_CLOSE:
                resultRules[resultRulesPos] = PathIterator.SEG_LINETO;
                resultOffsets[resultRulesPos++] = resultCoordPos + 2;
                boolean isLeft = CrossingHelper.compare(
                    coords[index], coords[index + 1], point.x(), point.y()) > 0;
                if (way || !isLeft) {
                    temp[coordsCount++] = coords[index];
                    temp[coordsCount++] = coords[index + 1];
                }
                break;

            case PathIterator.SEG_QUADTO:
                resultRules[resultRulesPos] = PathIterator.SEG_QUADTO;
                resultOffsets[resultRulesPos++] = resultCoordPos + 4;
                float[] coefs = new float[] {
                    coords[index - 2], coords[index - 1],
                    coords[index], coords[index + 1], coords[index + 2], coords[index + 3] };
                isLeft = CrossingHelper.compare(
                    coords[index - 2], coords[index - 1], point.x(), point.y()) > 0;

                if ((!additional) && (operation == 0 || operation == 2)) {
                    isLeft = !isLeft;
                    way = false;
                }
                GeometryUtil.subQuad(coefs, point.param(isCurrentArea), isLeft);

                if (way || isLeft) {
                    temp[coordsCount++] = coefs[2];
                    temp[coordsCount++] = coefs[3];
                } else {
                    System.arraycopy(coefs, 2, temp, coordsCount, 4);
                    coordsCount += 4;
                }
                break;

            case PathIterator.SEG_CUBICTO:
                resultRules[resultRulesPos] = PathIterator.SEG_CUBICTO;
                resultOffsets[resultRulesPos++] = resultCoordPos + 6;
                coefs = new float[] { coords[index - 2], coords[index - 1], coords[index],
                                      coords[index + 1], coords[index + 2], coords[index + 3],
                                      coords[index + 4], coords[index + 5] };
                isLeft = CrossingHelper.compare(
                    coords[index - 2], coords[index - 1], point.x(), point.y()) > 0;
                GeometryUtil.subCubic(coefs, point.param(isCurrentArea), !isLeft);

                if (isLeft) {
                    System.arraycopy(coefs, 2, temp, coordsCount, 6);
                    coordsCount += 6;
                } else {
                    System.arraycopy(coefs, 2, temp, coordsCount, 4);
                    coordsCount += 4;
                }
                break;
            }
        }

        if (operation == 2 && !isCurrentArea && coordsCount > 2) {
            reverseCopy(temp);
            System.arraycopy(temp, 0, resultCoords, resultCoordPos, coordsCount);
        } else {
            System.arraycopy(temp, 0, resultCoords, resultCoordPos, coordsCount);
        }

        return (resultCoordPos + coordsCount);
    }

    private void copy (Area src, Area dst) {
        dst.coordsSize = src.coordsSize;
        dst.coords = Platform.clone(src.coords);
        dst.rulesSize = src.rulesSize;
        dst.rules = Platform.clone(src.rules);
        dst.moveToCount = src.moveToCount;
        dst.offsets = Platform.clone(src.offsets);
    }

    private int containsExact (float x, float y) {
        PathIterator pi = pathIterator(null);
        int crossCount = Crossing.crossPath(pi, x, y);
        if (Crossing.isInsideEvenOdd(crossCount)) {
            return 1;
        }

        float[] segmentCoords = new float[6];
        float[] resultPoints = new float[6];
        int rule;
        float curX = -1;
        float curY = -1;
        float moveX = -1;
        float moveY = -1;

        for (pi = pathIterator(null); !pi.isDone(); pi.next()) {
            rule = pi.currentSegment(segmentCoords);
            switch (rule) {
            case PathIterator.SEG_MOVETO:
                moveX = curX = segmentCoords[0];
                moveY = curY = segmentCoords[1];
                break;

            case PathIterator.SEG_LINETO:
                if (GeometryUtil.intersectLines(curX, curY, segmentCoords[0], segmentCoords[1], x,
                                                y, x, y, resultPoints) != 0) {
                    return 0;
                }
                curX = segmentCoords[0];
                curY = segmentCoords[1];
                break;

            case PathIterator.SEG_QUADTO:
                if (GeometryUtil.intersectLineAndQuad(
                        x, y, x, y, curX, curY, segmentCoords[0], segmentCoords[1],
                        segmentCoords[2], segmentCoords[3], resultPoints) > 0) {
                    return 0;
                }
                curX = segmentCoords[2];
                curY = segmentCoords[3];
                break;

            case PathIterator.SEG_CUBICTO:
                if (GeometryUtil.intersectLineAndCubic(
                        x, y, x, y, curX, curY, segmentCoords[0], segmentCoords[1],
                        segmentCoords[2], segmentCoords[3], segmentCoords[4], segmentCoords[5],
                        resultPoints) > 0) {
                    return 0;
                }
                curX = segmentCoords[4];
                curY = segmentCoords[5];
                break;

            case PathIterator.SEG_CLOSE:
                if (GeometryUtil.intersectLines(
                        curX, curY, moveX, moveY, x, y, x, y, resultPoints) != 0) {
                    return 0;
                }
                curX = moveX;
                curY = moveY;
                break;
            }
        }
        return -1;
    }

    private void reverseCopy (float[] coords) {
        float[] temp = new float[coords.length];
        System.arraycopy(coords, 0, temp, 0, coords.length);
        for (int i = 0; i < coords.length;) {
            coords[i] = temp[coords.length - i - 2];
            coords[i + 1] = temp[coords.length - i - 1];
            i = i + 2;
        }
    }

    private float areaBoundsSquare () {
        Rectangle bounds = bounds();
        return bounds.height() * bounds.width();
    }

    private boolean isVertex (float x, float y) {
        for (int i = 0; i < coordsSize;) {
            if (x == coords[i++] && y == coords[i++]) {
                return true;
            }
        }
        return false;
    }

    // the method check up the array size and necessarily increases it.
    private static float[] adjustSize (float[] array, int newSize) {
        if (newSize <= array.length) {
            return array;
        }
        float[] newArray = new float[2 * newSize];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    private static int[] adjustSize (int[] array, int newSize) {
        if (newSize <= array.length) {
            return array;
        }
        int[] newArray = new int[2 * newSize];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    // the internal class implements PathIterator
    private class AreaPathIterator implements PathIterator
    {
        private final Transform transform;
        private int curRuleIndex = 0;
        private int curCoordIndex = 0;

        AreaPathIterator (Transform t) {
            this.transform = t;
        }

        @Override public int windingRule () {
            return WIND_EVEN_ODD;
        }

        @Override public boolean isDone () {
            return curRuleIndex >= rulesSize;
        }

        @Override public void next () {
            switch (rules[curRuleIndex]) {
            case PathIterator.SEG_MOVETO:
            case PathIterator.SEG_LINETO:
                curCoordIndex += 2;
                break;
            case PathIterator.SEG_QUADTO:
                curCoordIndex += 4;
                break;
            case PathIterator.SEG_CUBICTO:
                curCoordIndex += 6;
                break;
            }
            curRuleIndex++;
        }

        @Override @SuppressWarnings("fallthrough")
        public int currentSegment (float[] c) {
            if (isDone()) {
                throw new NoSuchElementException("Iterator out of bounds");
            }

            int count = 0;
            // the fallthrough below is on purpose
            switch (rules[curRuleIndex]) {
            case PathIterator.SEG_CUBICTO:
                c[4] = coords[curCoordIndex + 4];
                c[5] = coords[curCoordIndex + 5];
                count = 1;
            case PathIterator.SEG_QUADTO:
                c[2] = coords[curCoordIndex + 2];
                c[3] = coords[curCoordIndex + 3];
                count += 1;
            case PathIterator.SEG_MOVETO:
            case PathIterator.SEG_LINETO:
                c[0] = coords[curCoordIndex];
                c[1] = coords[curCoordIndex + 1];
                count += 1;
            }

            if (transform != null) {
                transform.transform(c, 0, c, 0, count);
            }

            return rules[curRuleIndex];
        }
    }

    /** The coordinates array of the shape vertices. */
    private float[] coords = new float[20];

    /** The coordinates quantity. */
    private int coordsSize = 0;

    /** The rules array for the drawing of the shape edges. */
    private int[] rules = new int[10];

    /** The rules quantity. */
    private int rulesSize = 0;

    /** offsets[i] - index in array of coords and i - index in array of rules. */
    private int[] offsets = new int[10];

    /** The quantity of MOVETO rule occurrences. */
    private int moveToCount = 0;

    /** True if the shape is polygonal. */
    private boolean isPolygonal = true;
}
