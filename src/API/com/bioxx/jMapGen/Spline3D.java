package com.bioxx.jmapgen;

import java.util.ArrayList;

import net.minecraft.util.BlockPos;

public class Spline3D {

	/** 
	 *  Array representing the relative proportion of the total distance
	 *  of each point in the line ( i.e. first point is 0.0, end point is
	 *  1.0, a point halfway on line is 0.5 ).
	 */
	private double[] t;
	private Spline splineX;
	private Spline splineY;
	private Spline splineZ;
	/**
	 * Total length tracing the points on the spline
	 */
	private double length;

	/**
	 * Creates a new Spline2D.
	 * @param points
	 */
	public Spline3D(ArrayList<BlockPos> points) {
		double[] x = new double[points.size()];
		double[] y = new double[points.size()];
		double[] z = new double[points.size()];

		for(int i = 0; i< points.size(); i++) {
			x[i] = points.get(i).getX();
			y[i] = points.get(i).getY();
			z[i] = points.get(i).getZ(); 
		}

		init(x, y, z);
	}

	/**
	 * Creates a new Spline2D.
	 * @param x
	 * @param y
	 */
	public Spline3D(double[] x, double[] y, double[] z) {
		init(x, y, z);
	}

	private void init(double[] x, double[] y, double[] z) 
	{
		if (x.length != y.length || x.length != z.length || y.length != z.length) {
			throw new IllegalArgumentException("Arrays must have the same length.");
		}

		if (x.length < 2) {
			throw new IllegalArgumentException("Spline edges must have at least two points.");
		}

		t = new double[x.length];
		t[0] = 0.0; // start point is always 0.0

		// Calculate the partial proportions of each section between each set
		// of points and the total length of sum of all sections
		for (int i = 1; i < t.length; i++) {
			double lx = x[i] - x[i-1];
			double ly = y[i] - y[i-1];
			double lz = z[i] - z[i-1];
			// If either diff is zero there is no point performing the square root
			if ( 0.0 == lx ) {
				t[i] = Math.abs(lz);
			} else if ( 0.0 == lz ) {
				t[i] = Math.abs(lx);
			} else {
				t[i] = Math.sqrt(lx*lx+ly*ly+lz*lz);
			}

			length += t[i];
			t[i] += t[i-1];
		}

		for(int i = 1; i< (t.length)-1; i++) {
			t[i] = t[i] / length;
		}

		t[(t.length)-1] = 1.0; // end point is always 1.0

		splineX = new Spline(t, x);
		splineY = new Spline(t, y);
		splineZ = new Spline(t, z);
	}

	/**
	 * @param t 0 <= t <= 1
	 */
	public BlockPos getPoint(double t) {
		return new BlockPos(splineX.getValue(t), splineY.getValue(t), splineZ.getValue(t));
	}

	/**
	 * Used to check the correctness of this spline
	 */
	public boolean checkValues() {
		return (splineX.checkValues() && splineY.checkValues() && splineZ.checkValues());
	}

	public double getDx(double t) {
		return splineX.getDx(t);
	}

	public double getDy(double t) {
		return splineY.getDx(t);
	}

	public double getDz(double t) {
		return splineZ.getDx(t);
	}

	public Spline getSplineX() {
		return splineX;
	}

	public Spline getSplineY() {
		return splineY;
	}

	public Spline getSplineZ() {
		return splineZ;
	}

	public double getLength() {
		return length;
	}

}
