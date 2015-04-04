package za.co.luma.math.sampling;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import za.co.iocom.math.MathUtil;
import za.co.luma.geom.Vector2DDouble;
import za.co.luma.geom.Vector2DInt;
import za.co.luma.math.function.RealFunction2DDouble;

public class PoissonDiskMultiSampler
{
	private final static int DEFAULT_POINTS_TO_GENERATE = 30;
	private final int pointsToGenerate; // k in literature
	private final Vector2DDouble p0, p1;
	private final Vector2DDouble dimensions;
	private final double[] cellSize; // r / sqrt(n), for 2D: r / sqrt(2)
	private final double[] minDist; // r
	private final double[] radii; // r
	private final double[] minRadii; // r
	private final int gridWidth[], gridHeight[];
	private int layerCount;
	private boolean multiLayer;

	/**
	 * A safety measure - no more than this number of points are produced by
	 * ther algorithm.
	 */
	public final static int MAX_POINTS = 10000;

	@SuppressWarnings("unused") //left here for later...
	private RealFunction2DDouble distribution;

	public List<List<Circle>[][]> grids;

	/**
	 * Construct a new PoissonDisk object, with a given domain and minimum
	 * distance between points.
	 * 
	 * @param x0
	 *            x-coordinate of bottom left corner of domain.
	 * @param y0
	 *            x-coordinate of bottom left corner of domain.
	 * @param x1
	 *            x-coordinate of bottom left corner of domain.
	 * @param y1
	 *            x-coordinate of bottom left corner of domain.
	 * @param distribution
	 *            A function that gives the minimum radius between points in the
	 *            vicinity of a point.
	 */
	public PoissonDiskMultiSampler(double x0, double y0, double x1, double y1, double[] minDist, double[] minRadii,
			double radii[], RealFunction2DDouble distribution, boolean multiLayer, int pointsToGenerate)
	{
		layerCount = minDist.length;

		if (minRadii.length != layerCount)
			throw new RuntimeException("minRadii[] must have the same length as minDist[]");

		if (radii.length != layerCount)
			throw new RuntimeException("radii[] must have the same length as minDist[]");

		p0 = new Vector2DDouble(x0, y0);
		p1 = new Vector2DDouble(x1, y1);
		dimensions = new Vector2DDouble(x1 - x0, y1 - y0);

		this.minDist = minDist;
		this.radii = radii;
		this.minRadii = minRadii;
		this.distribution = distribution;
		this.pointsToGenerate = pointsToGenerate;
		this.multiLayer = multiLayer;

		cellSize = new double[layerCount];
		gridWidth = new int[layerCount];
		gridHeight = new int[layerCount];

		for (int k = 0; k < layerCount; k++)
		{
			cellSize[k] = minDist[k] / Math.sqrt(2);
			gridWidth[k] = (int) (dimensions.x / cellSize[k]) + 1;
			gridHeight[k] = (int) (dimensions.y / cellSize[k]) + 1;
		}
	}

	/**
	 * Construct a new PoissonDisk object, with a given domain and minimum
	 * distance between points.
	 * 
	 * @param x0
	 *            x-coordinate of bottom left corner of domain.
	 * @param y0
	 *            x-coordinate of bottom left corner of domain.
	 * @param x1
	 *            x-coordinate of bottom left corner of domain.
	 * @param y1
	 *            x-coordinate of bottom left corner of domain.
	 * @param distribution
	 *            A function that gives the minimum radius between points in the
	 *            vicinity of a point.
	 */
	public PoissonDiskMultiSampler(double x0, double y0, double x1, double y1, double[] minDist, double[] minRadii,
			double[] radii, RealFunction2DDouble distribution, boolean multiLayer)
	{
		this(x0, y0, x1, y1, minDist, radii, minRadii, distribution, multiLayer, DEFAULT_POINTS_TO_GENERATE);
	}

	/**
	 * Generates an array of lists of points following the Poisson distribution.
	 * No more than MAX_POINTS are produced. If multilayer is true, then a check
	 * is performed removing any points that collide with points in lower
	 * layers.
	 * 
	 * @return The sample set.
	 */
	@SuppressWarnings("unchecked")
	public List<Circle>[] sample()
	{
		List<Circle>[] pointList = new List[layerCount];
		grids = new LinkedList();

		for (int k = 0; k < layerCount; k++)
		{
			List<Circle> activeList = new LinkedList<Circle>();
			List<Circle> grid[][] = new List[gridWidth[k]][gridHeight[k]];

			grids.add(grid);

			pointList[k] = new LinkedList<Circle>();

			for (int i = 0; i < gridWidth[k]; i++)
			{
				for (int j = 0; j < gridHeight[k]; j++)
				{
					grid[i][j] = new LinkedList<Circle>();
				}
			}

			addFirstPoint(grid, activeList, pointList[k], k);

			while (!activeList.isEmpty() && (pointList[k].size() < MAX_POINTS))
			{
				int listIndex = MathUtil.random.nextInt(activeList.size());

				Circle point = activeList.get(listIndex);
				boolean found = false;

				for (int m = 0; m < pointsToGenerate; m++)
				{
					found |= addNextPoint(grid, activeList, pointList[k], point, k);
				}

				if (!found)
				{
					activeList.remove(listIndex);
				}
			}
		}

		if (multiLayer)
		{
			for (int k = 1; k < layerCount; k++)
			{
				for (ListIterator<Circle> pointItr = pointList[k].listIterator(); pointItr.hasNext();)
				{
					Circle point = pointItr.next();

					if (checkPoint(point, k, grids))
					{
						point.x *= -1;
						// point.y *= -1;
						// pointItr.remove();
					}
				}
			}
		}

		return pointList;
	}

	private boolean checkPoint(Circle q, int layerIndex, List<List<Circle>[][]> grids)
	{
		int k = 0;
		double fraction = 1;
		boolean tooClose = false;

		for (k = layerIndex - 1; (k >= 0) && !tooClose; k--)
		{
			List<Circle> grid[][] = grids.get(k);
			Vector2DInt qIndex = pointDoubleToInt(q, p0, cellSize[k]);

			for (int i = Math.max(0, qIndex.x - 2); (i < Math.min(gridWidth[k], qIndex.x + 3)) && !tooClose; i++)
			{
				for (int j = Math.max(0, qIndex.y - 2); (j < Math.min(gridHeight[k], qIndex.y + 3)) && !tooClose; j++)
				{
					for (Circle gridPoint : grid[i][j])
					{
						if (Vector2DDouble.distance(gridPoint, q) < (q.getRadius() + gridPoint.getRadius()) * fraction)
						{
							tooClose = true;
						}
					}
				}
			}
		}

		/*
		 * if (!tooClose) { found = true; activeList.add(q); pointList.add(q);
		 * grid[qIndex.x][qIndex.y].add(q); }
		 */

		return tooClose;
	}

	private boolean addNextPoint(List<Circle>[][] grid, List<Circle> activeList, List<Circle> pointList, Circle point,
			int layerIndex)
	{
		boolean found = false;
		// double fraction = distribution.getDouble((int) point.x, (int)
		// point.y);
		double fraction = 1;
		Circle q = generateAround(point, fraction * minDist[layerIndex], minRadii[layerIndex], radii[layerIndex],
				MathUtil.random.nextDouble(), MathUtil.random.nextDouble(), MathUtil.random.nextDouble());

		if ((q.x >= p0.x) && (q.x < p1.x) && (q.y > p0.y) && (q.y < p1.y))
		{
			Vector2DInt qIndex = pointDoubleToInt(q, p0, cellSize[layerIndex]);

			boolean tooClose = false;

			for (int i = Math.max(0, qIndex.x - 2); (i < Math.min(gridWidth[layerIndex], qIndex.x + 3)) && !tooClose; i++)
			{
				for (int j = Math.max(0, qIndex.y - 2); (j < Math.min(gridHeight[layerIndex], qIndex.y + 3))
						&& !tooClose; j++)
				{
					for (Circle gridPoint : grid[i][j])
					{
						double distance = Circle.distance(gridPoint, q);

						if (distance < minDist[layerIndex] * fraction)
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
				grid[qIndex.x][qIndex.y].add(q);
			}
		}

		return found;
	}

	private void addFirstPoint(List<Circle>[][] grid, List<Circle> activeList, List<Circle> pointList, int layerIndex)
	{
		double d = MathUtil.random.nextDouble();
		double xr = p0.x + dimensions.x * (d);

		d = MathUtil.random.nextDouble();
		double yr = p0.y + dimensions.y * (d);

		d = MathUtil.random.nextDouble();
		double rr = minRadii[layerIndex] + d * (radii[layerIndex] - minRadii[layerIndex]);// radii[layerIndex]
		// *
		// (0.5
		// + d
		// *
		// 0.5);

		Circle p = new Circle(xr, yr, rr);
		Vector2DInt index = pointDoubleToInt(p, p0, cellSize[layerIndex]);

		grid[index.x][index.y].add(p);
		activeList.add(p);
		pointList.add(p);
	}

	/**
	 * Converts a PointDouble to a PointInt that represents the index
	 * coordinates of the point in the background grid.
	 */
	public static Vector2DInt pointDoubleToInt(Vector2DDouble pointDouble, Vector2DDouble origin, double cellSize)
	{
		return new Vector2DInt((int) ((pointDouble.x - origin.x) / cellSize),
				(int) ((pointDouble.y - origin.y) / cellSize));
	}

	/**
	 * Generates a random point in the analus around the given point. The analus
	 * has inner radius minimum distance and outer radius twice that.
	 * 
	 * @param centre
	 *            The point around which the random point should be.
	 * @param distanceScale
	 *            TODO
	 * @param angleScale
	 *            TODO
	 * @param radiusScale
	 *            TODO
	 * @return A new point, randomly selected.
	 */
	public static Circle generateAround(Vector2DDouble centre, double minDist, double minRadius, double radius,
			double distanceScale, double angleScale, double radiusScale)
	{
		double r = (minDist + minDist * (distanceScale));
		double angle = 2 * Math.PI * (angleScale);

		double newX = r * Math.cos(angle);
		double newY = r * Math.sin(angle);

		double newRadius = minRadius + radiusScale * (radius - minRadius);
		Circle randomPoint = new Circle(centre.x + newX, centre.y + newY, newRadius);

		return randomPoint;
	}

	public static class Circle extends Vector2DDouble
	{
		public Circle(double x, double y, double radius)
		{
			super(x, y);
			this.radius = radius;
		}

		public double getRadius()
		{
			return radius;
		}

		double radius;
	}
}
