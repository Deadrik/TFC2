package com.bioxx.jMapGen.com.nodename.Delaunay;

import com.bioxx.jMapGen.Point;

	
	public final class EdgeList
	{
		private double _deltax;
		private double _xmin;
		
		private int _hashsize;
		private Halfedge[] _hash;
		private Halfedge _leftEnd;
		public Halfedge getLeftEnd()
		{
			return _leftEnd;
		}
		private Halfedge _rightEnd;
		public Halfedge getRightEnd()
		{
			return _rightEnd;
		}
		
		public EdgeList(double xmin, double deltax, int sqrt_nsites)
		{
			_xmin = xmin;
			_deltax = deltax;
			_hashsize = 2 * sqrt_nsites;

			_hash = new Halfedge[_hashsize];
			
			// two dummy Halfedges:
			_leftEnd = Halfedge.createDummy();
			_rightEnd = Halfedge.createDummy();
			_leftEnd.edgeListLeftNeighbor = null;
			_leftEnd.edgeListRightNeighbor = _rightEnd;
			_rightEnd.edgeListLeftNeighbor = _leftEnd;
			_rightEnd.edgeListRightNeighbor = null;
			_hash[0] = _leftEnd;
			_hash[_hashsize - 1] = _rightEnd;
		}

		/**
		 * Insert newHalfedge to the right of lb 
		 * @param lb
		 * @param newHalfedge
		 * 
		 */
		public void insert(Halfedge lb, Halfedge newHalfedge)
		{
			newHalfedge.edgeListLeftNeighbor = lb;
			
			//if(lb.edgeListRightNeighbor != null)
				newHalfedge.edgeListRightNeighbor = lb.edgeListRightNeighbor;
			
			//if(lb.edgeListRightNeighbor != null)
				lb.edgeListRightNeighbor.edgeListLeftNeighbor = newHalfedge;
			
			lb.edgeListRightNeighbor = newHalfedge;
		}

		/**
		 * This function only removes the Halfedge from the left-right list.
		 * We cannot dispose it yet because we are still using it. 
		 * @param halfEdge
		 * 
		 */
		public void remove(Halfedge halfEdge)
		{
			halfEdge.edgeListLeftNeighbor.edgeListRightNeighbor = halfEdge.edgeListRightNeighbor;
			halfEdge.edgeListRightNeighbor.edgeListLeftNeighbor = halfEdge.edgeListLeftNeighbor;
			halfEdge.edge = Edge.DELETED;
			halfEdge.edgeListLeftNeighbor = halfEdge.edgeListRightNeighbor = null;
		}

		/**
		 * Find the rightmost Halfedge that is still left of p 
		 * @param p
		 * @return 
		 * 
		 */
		public Halfedge edgeListLeftNeighbor(Point p)
		{
			int i, bucket;
			Halfedge halfEdge;
		
			/* Use hash table to get close to desired halfedge */
			bucket = (int) ((p.x - _xmin)/_deltax * _hashsize);
			if (bucket < 0)
			{
				bucket = 0;
			}
			if (bucket >= _hashsize)
			{
				bucket = _hashsize - 1;
			}
			halfEdge = getHash(bucket);
			if (halfEdge == null)
			{
				for (i = 1; true; ++i)
			    {
					if ((halfEdge = getHash(bucket - i)) != null) break;
					if ((halfEdge = getHash(bucket + i)) != null) break;
			    }
			}
			/* Now search linear list of halfedges for the correct one */
			if (halfEdge != null && (halfEdge == _leftEnd  || (halfEdge != getRightEnd() && halfEdge.isLeftOf(p))))
			{
				do
				{
					halfEdge = halfEdge.edgeListRightNeighbor;
				}
				while (halfEdge != getRightEnd() && halfEdge.isLeftOf(p));
				halfEdge = halfEdge.edgeListLeftNeighbor;
			}
			else
			{
				if(halfEdge != null)
				{
					do
					{
						halfEdge = halfEdge.edgeListLeftNeighbor;
					}
					while(halfEdge != _leftEnd && halfEdge.edge != null && !halfEdge.isLeftOf(p));
				}
			}
		
			/* Update hash table and reference counts */
			if (bucket > 0 && bucket <_hashsize - 1)
			{

					_hash[bucket] = halfEdge;
				
			}
			return halfEdge;
		}

		/* Get entry from hash table, pruning any deleted nodes */
		private Halfedge getHash(int b)
		{
			Halfedge halfEdge = null;
		
			if (b < 0 || b >= _hashsize)
			{
				return null;
			}
			try
			{
				halfEdge = _hash[b]; 
			}
			catch(Exception e){}
			
			if (halfEdge != null && halfEdge.edge == Edge.DELETED)
			{
				/* Hash table points to deleted halfedge.  Patch as necessary. */
				_hash[b] = null;
				// still can't dispose halfEdge yet!
				return null;
			}
			else
			{
				return halfEdge;
			}
		}

	}