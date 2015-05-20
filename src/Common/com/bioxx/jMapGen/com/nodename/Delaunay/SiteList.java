package com.bioxx.jMapGen.com.nodename.Delaunay;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Vector;

import com.bioxx.jMapGen.Point;


	public class SiteList
	{
		private Vector<Site> _sites;
		private int _currentIndex;
		
		private boolean _sorted;
		
		public SiteList()
		{
			_sites = new Vector<Site>();
			_sorted = false;
		}
		
		public int push(Site site)
		{
			_sorted = false;
			int i = _sites.size();
			_sites.add(site);
			return i;
		}
		
		public int getLength()
		{
			return _sites.size();
		}
		
		public Site next()
		{
			if (_sorted == false)
			{
				throw new Error("SiteList::next():  sites have not been sorted");
			}
			if (_currentIndex < _sites.size())
			{
				return _sites.get(_currentIndex++);
			}
			else
			{
				return null;
			}
		}

		public Rectangle getSitesBounds()
		{
			if (_sorted == false && _sites != null)
			{
				Collections.sort(_sites, new Site());
				_currentIndex = 0;
				_sorted = true;
			}
			double xmin, xmax, ymin, ymax;
			if (_sites.size() == 0)
			{
				return new Rectangle(0, 0, 0, 0);
			}
			xmin = Integer.MAX_VALUE;
			xmax = Integer.MIN_VALUE;

				for (int i = 0; i < _sites.size(); ++i)
				{
					Site site = _sites.get(i);
				if (site.getX() < xmin)
				{
					xmin = site.getX();
				}
				if (site.getX() > xmax)
				{
					xmax = site.getX();
				}
			}
			// here's where we assume that the sites have been sorted on y:
			ymin = _sites.get(0).getY();
			ymax = _sites.get(_sites.size() - 1).getY();
			
			Rectangle out = new Rectangle();
			out.setFrame(xmin, ymin, xmax - xmin, ymax - ymin);
			
			return out;
		}

		public Vector<Point> siteCoords()
		{
			Vector<Point> coords = new Vector<Point>();
			for (int i = 0; i < _sites.size(); ++i)
			{
				Site site = _sites.get(i);
				coords.add(site.getCoord());
			}
			return coords;
		}
		
}