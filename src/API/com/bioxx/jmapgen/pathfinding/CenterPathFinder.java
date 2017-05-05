package com.bioxx.jmapgen.pathfinding;

import java.util.Vector;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

public class CenterPathFinder 
{
	IPathProfile pathProfile;
	public CenterPathFinder(IPathProfile profile)
	{
		pathProfile = profile;
	}

	public CenterPath findPath(IslandMap map, Center start, Center end)
	{
		Vector<CenterPathNode> reachable = new Vector<CenterPathNode>();
		Vector<Center> explored = new Vector<Center>();
		//Begin with the start Center being added to the reachable list for iteration
		reachable.add(new CenterPathNode(start, 0).calculate(map, pathProfile));

		while(!reachable.isEmpty())
		{
			CenterPathNode node = choose_node(reachable, end);
			Center c = node.center;

			if(reachable.size() > 10000)
				return null;

			//if we find our goal then build a path and return it
			if(c == end)
			{
				return buildPath(node);
			}

			//make sure we dont scan this hex again by adding it to the explored map
			explored.add(c);
			reachable.remove(node);

			Vector<CenterPathNode> newReachable = new Vector<CenterPathNode>();

			/*
			 * For every center near this one, add it to the reachable list if we havent already explored it and 
			 * the profile doesnt say to ignore it
			 */
			for(Center n : c.neighbors)
			{
				if(!explored.contains(n) && !pathProfile.shouldIgnoreCenter(map, c, n))
					newReachable.add(new CenterPathNode(n, node, Integer.MAX_VALUE).calculate(map, pathProfile));
			}

			for(CenterPathNode newNode : newReachable)
			{
				if(!reachable.contains(newNode))
					reachable.add(newNode);
				if(node.nodeCost + newNode.transitCost < newNode.nodeCost)
				{
					newNode.prev = node;
					newNode.nodeCost = node.nodeCost + newNode.transitCost;
				}
			}

		}

		return null;
	}

	private CenterPath buildPath(CenterPathNode endNode)
	{
		CenterPath p = new CenterPath();
		p.addNode(endNode);
		CenterPathNode pn = endNode.prev;
		while(pn != null)
		{
			p.addNode(pn);
			pn = pn.prev;
		}

		return p;
	}

	private CenterPathNode choose_node(Vector<CenterPathNode>reachable, Center end)
	{
		int min_cost = Integer.MAX_VALUE;
		CenterPathNode best_node = null;

		for(CenterPathNode node : reachable)
		{
			int cost_start_to_node = node.nodeCost;
			int cost_node_to_goal = (int) Math.floor(node.center.point.distanceSq(end.point));
			int total_cost = cost_start_to_node + cost_node_to_goal;

			if( min_cost > total_cost)
			{
				min_cost = total_cost;
				best_node = node;
			}
		}
		return best_node;
	}
}
