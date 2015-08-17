package com.bioxx.jmapgen.com.nodename.geom;

	public class Winding
	{
		public static final Winding CLOCKWISE = new Winding("clockwise");
		public static final Winding COUNTERCLOCKWISE = new Winding("counterclockwise");
		public static final Winding NONE = new Winding("none");
		
		private String _name;
		
		public Winding(String name)
		{
			super();
			_name = name;
		}
		
		public String toString()
		{
			return _name;
		}
	}