package jMapGen.pathfinding;

import jMapGen.Map;
import jMapGen.Point;
import jMapGen.graph.Center;
import jMapGen.graph.Center.Marker;

import java.util.Vector;

public class PathFinder 
{
	Map map;

	public PathFinder(Map m)
	{
		map = m;
	}

	public Path findPath(Center start, Center end)
	{
		Vector<PathNode> reachable = new Vector<PathNode>();
		Vector<Center> explored = new Vector<Center>();
		reachable.add(new PathNode(start, 0));

		while(!reachable.isEmpty())
		{
			PathNode node = choose_node(reachable, end);
			Center c = node.center;

			if(c == end)
			{
				return buildPath(node);
			}

			explored.add(c);
			reachable.remove(node);

			Vector<PathNode> newReachable = new Vector<PathNode>();

			for(Center n : c.neighbors)
			{
				if(!explored.contains(n) && !n.hasAnyMarkersOf(Marker.Water, Marker.Border, Marker.Ocean, Marker.Coast, Marker.Lava))
					newReachable.add(new PathNode(n, Integer.MAX_VALUE));
			}

			for(PathNode n : newReachable)
			{
				if(!reachable.contains(n))
					reachable.add(n);
				if(node.nodeCost + n.transitCost < n.nodeCost)
				{
					n.prev = node;
					n.nodeCost = node.nodeCost + n.transitCost;
				}
			}

		}

		return null;
	}

	private Path buildPath(PathNode endNode)
	{
		Path p = new Path();
		p.addNode(endNode);
		PathNode pn = endNode.prev;
		while(pn != null)
		{
			p.addNode(pn);
			pn = pn.prev;
		}

		return p;
	}

	private PathNode choose_node(Vector<PathNode>reachable, Center end)
	{
		int min_cost = Integer.MAX_VALUE;
		PathNode best_node = null;

		for(PathNode node : reachable)
		{
			int cost_start_to_node = node.nodeCost;
			int cost_node_to_goal = estimateDistance(node.center.point, end.point);
			int total_cost = cost_start_to_node + cost_node_to_goal;

			if( min_cost > total_cost)
			{
				min_cost = total_cost;
				best_node = node;
			}
		}
		return best_node;
	}

	private int estimateDistance(Point p0, Point p1)
	{
		return (int)Math.floor(Math.sqrt( Math.pow((p0.x - p1.x),2) + Math.pow((p0.y - p1.y),2)));
	}
}
