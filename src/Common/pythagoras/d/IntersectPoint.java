//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * An internal helper class that represents the intersection point of two edges.
 */
class IntersectPoint
{
    public IntersectPoint (int begIndex1, int endIndex1, int begIndex2, int endIndex2,
                           double x, double y) {
        this.begIndex1 = begIndex1;
        this.endIndex1 = endIndex1;
        this.begIndex2 = begIndex2;
        this.endIndex2 = endIndex2;
        this.x = x;
        this.y = y;
    }

    public IntersectPoint (int begIndex1, int endIndex1, int rule1, int ruleIndex1,
                           int begIndex2, int endIndex2, int rule2, int ruleIndex2,
                           double x, double y, double param1, double param2) {
        this.begIndex1 = begIndex1;
        this.endIndex1 = endIndex1;
        this.rule1 = rule1;
        this.ruleIndex1 = ruleIndex1;
        this.param1 = param1;
        this.begIndex2 = begIndex2;
        this.endIndex2 = endIndex2;
        this.rule2 = rule2;
        this.ruleIndex2 = ruleIndex2;
        this.param2 = param2;
        this.x = x;
        this.y = y;
    }

    public int begIndex (boolean isCurrentArea) {
        return isCurrentArea ? begIndex1 : begIndex2;
    }

    public int endIndex (boolean isCurrentArea) {
        return isCurrentArea ? endIndex1 : endIndex2;
    }

    public int ruleIndex (boolean isCurrentArea) {
        return isCurrentArea ? ruleIndex1 : ruleIndex2;
    }

    public double param (boolean isCurrentArea) {
        return isCurrentArea ? param1 : param2;
    }

    public int rule (boolean isCurrentArea) {
        return isCurrentArea ? rule1 : rule2;
    }

    public double x () {
        return x;
    }

    public double y () {
        return y;
    }

    public void setBegIndex1 (int begIndex) {
        this.begIndex1 = begIndex;
    }

    public void setEndIndex1 (int endIndex) {
        this.endIndex1 = endIndex;
    }

    public void setBegIndex2 (int begIndex) {
        this.begIndex2 = begIndex;
    }

    public void setEndIndex2 (int endIndex) {
        this.endIndex2 = endIndex;
    }

    // the edge begin number of first line
    private int begIndex1;
    // the edge end number of first line
    private int endIndex1;
    // the edge rule of first figure
    private int rule1;
    // the index of the first figure rules array
    private int ruleIndex1;
    // the parameter value of edge1
    private double param1;
    // the edge begin number of second line
    private int begIndex2;
    // the edge end number of second line
    private int endIndex2;
    // the edge rule of second figure
    private int rule2;
    // the index of the second figure rules array
    private int ruleIndex2;
    // the absciss coordinate of the point
    private final double x;
    // the ordinate coordinate of the point
    private final double y;
    // the parameter value of edge2
    private double param2;
}
