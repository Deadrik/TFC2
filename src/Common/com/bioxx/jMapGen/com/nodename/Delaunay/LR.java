package com.bioxx.jMapGen.com.nodename.Delaunay;

public final class LR
{
	public static final LR LEFT = new LR("left", 0);
	public static final LR RIGHT = new LR("right", 1);

	private String _name;
	public int value;

	public LR(String name, int val)
	{
		_name = name;
		value = val;
	}

	public static LR other(LR leftRight)
	{
		return leftRight == LEFT ? RIGHT : LEFT;
	}

	public String toString()
	{
		return _name;
	}

}
