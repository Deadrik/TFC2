package com.bioxx.jMapGen.com.nodename.Delaunay;

import java.util.Vector;

	
	public class Triangle
	{
		private Vector<Site> _sites;
		public Vector<Site> getSites()
		{
			return _sites;
		}
		
		public Triangle(Site a, Site b, Site c)
		{
			_sites = new Vector<Site>();
			_sites.add(a); _sites.add(b); _sites.add(c);
		}
	}