package za.co.luma.math.sampling;

import jMapGen.Point;

import java.util.LinkedList;
import java.util.List;

import za.co.iocom.math.MathUtil;
import za.co.luma.geom.Vector2DInt;

/**
 * Algorithm based on <emph>Fast Poisson Disk Sampling in Arbitrary Dimensions</emph> by Robert Bridson. To use,
 * construct a new PoissonDisk with suitable parameters, and call generate to get a list of points.
 * 
 * @author Herman Tulleken
 * 
 */
public class UniformPoissonDiskSampler implements Sampler<Point>
{
	private final static int DEFAULT_POINTS_TO_GENERATE = 10;
	private final int pointsToGenerate; // k in literature
	private final Point p0, p1;
	private final Point dimensions;
	private final double cellSize; // r / sqrt(n), for 2D: r / sqrt(2)
	private final double minDist; // r
	private final int gridWidth, gridHeight;

	/**
	 * Construct a new PoissonDisk object, with a given domain and minimum distance between points.
	 * 
	 * @param x0
	 *            x-coordinate of bottom left corner of domain.
	 * @param y0
	 *            x-coordinate of bottom left corner of domain.
	 * @param x1
	 *            x-coordinate of bottom left corner of domain.
	 * @param y1
	 *            x-coordinate of bottom left corner of domain.
	 * 
	 * @param minDist
	 *            The minimum distance between two points.
	 */
	public UniformPoissonDiskSampler(double x0, double y0, double x1, double y1, double minDist)
	{
		this(x0, y0, x1, y1, minDist, DEFAULT_POINTS_TO_GENERATE);
	}

	public UniformPoissonDiskSampler(double x0, double y0, double x1, double y1, double minDist, int pointsToGenerate)
	{
		p0 = new Point(x0, y0);
		p1 = new Point(x1, y1);
		dimensions = new Point(x1 - x0, y1 - y0);

		this.minDist = minDist;
		this.pointsToGenerate = pointsToGenerate;
		cellSize = minDist / Math.sqrt(2);
		gridWidth = (int) (dimensions.x / cellSize) + 1;
		gridHeight = (int) (dimensions.y / cellSize) + 1;
	}

	/**
	 * Generates a list of points following the Poisson distribution. No more than MAX_POINTS are produced.
	 * 
	 * @return
	 */
	public List<Point> sample()
	{
		Point grid[][] = new Point[gridWidth][gridHeight]; // background grid

		List<Point> activeList = new LinkedList<Point>();
		List<Point> pointList = new LinkedList<Point>();

		for (int i = 0; i < gridWidth; i++)
		{
			for (int j = 0; j < gridHeight; j++)
			{
				grid[i][j] = null;
			}
		}

		addFirstPoint(grid, activeList, pointList);

		while (!activeList.isEmpty() && (pointList.size() < PoissonDiskSampler.MAX_POINTS))
		{
			int listIndex = MathUtil.random.nextInt(activeList.size());

			Point point = activeList.get(listIndex);
			boolean found = false;

			for (int k = 0; k < pointsToGenerate; k++)
			{
				found |= addNextPoint(grid, activeList, pointList, point);
			}

			if (!found)
			{
				activeList.remove(listIndex);
			}
		}

		return pointList;
	}

	/**
	 * Adds a given point in the sampling collection, provided it is not tooo close to an exisiting sampling point.
	 * 
	 * @param grid
	 *            The background grid, used to obtain points near a point quickly.
	 * @param activeList
	 *            Points not yet processed.
	 * @param pointList
	 *            Points in the sampling collection.
	 * @param point
	 *            The new point to add.
	 * @return
	 */
	private boolean addNextPoint(Point[][] grid, List<Point> activeList,
			List<Point> pointList, Point point)
	{
		boolean found = false;
		Point q = PoissonDiskSampler.generateRandomAround(point, minDist);

		if ((q.x >= p0.x) && (q.x < p1.x) && (q.y > p0.y) && (q.y < p1.y))
		{
			Vector2DInt qIndex = PoissonDiskSampler.pointDoubleToInt(q, p0, cellSize);
			boolean tooClose = false;

			for (int i = Math.max(0, qIndex.x - 2); (i < Math.min(gridWidth, qIndex.x + 3)) && !tooClose; i++)
			{
				for (int j = Math.max(0, qIndex.y - 2); (j < Math.min(gridHeight, qIndex.y + 3)) && !tooClose; j++)
				{
					if (grid[i][j] != null)
					{
						if (Point.distance(grid[i][j], q) < minDist)
						{
							tooClose = true;
						}
					}
				}
			}

			if (!tooClose)
			{
				found = true;
				activeList.add(q);
				pointList.add(q);
				grid[qIndex.x][qIndex.y] = q;
			}
		}
		return found;
	}

	/**
	 * Randomly selects the first sampling point.
	 * 
	 * @param grid
	 *            The background grid, used to obtain points near a point quickly.
	 * @param activeList
	 *            Points not yet processed.
	 * @param pointList
	 *            Points in the sampling collection.
	 */
	private void addFirstPoint(Point[][] grid, List<Point> activeList, List<Point> pointList)
	{
		double d = MathUtil.random.nextDouble();
		double xr = p0.x + dimensions.x * (d);

		d = MathUtil.random.nextDouble();
		double yr = p0.y + dimensions.y * (d);

		Point p = new Point(xr, yr);
		Vector2DInt index = PoissonDiskSampler.pointDoubleToInt(p, p0, cellSize);

		grid[index.x][index.y] = p;

		activeList.add(p);
		pointList.add(p);
	}
}
