package com.bioxx.jMapGen.graph;

import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.graph.Center.Marker;

import net.minecraft.nbt.NBTTagCompound;

public class Edge 
{
	public int index = 0;

	public Center dCenter0, dCenter1;  // Delaunay edge
	public Corner vCorner0, vCorner1;  // Voronoi edge
	public Point midpoint;  // halfway between v0,v1

	public Edge()
	{

	}

	public Edge(int i)
	{
		index = i;
	}

	public void setVoronoiEdge(Corner c0, Corner c1)
	{
		vCorner0 = c0;
		vCorner1 = c1;

		if(/*v0 != null && */ !vCorner0.adjacent.contains(vCorner1) && vCorner0.index != vCorner1.index)
		{
			vCorner0.adjacent.add(vCorner1);
		}

		if(/*v1 != null && */ !vCorner1.adjacent.contains(vCorner0) && vCorner1.index != vCorner0.index)
		{
			vCorner1.adjacent.add(vCorner0);
		}
	}

	/**
	 * 
	 * @param c The Corner that you already have
	 * @return The opposite corner of the edge. Returns null if you feed it a corner that is not a part of this edge.
	 */
	public Corner otherCorner(Corner c)
	{
		if(c == vCorner0)
			return vCorner1;
		else if(c == vCorner1)
			return vCorner0;
		else return null;
	}

	/**
	 * 
	 * @param c The Center that you already have
	 * @return The opposite center of the edge. Returns null if you feed it a center that is not a part of this edge.
	 */
	public Center otherCenter(Center c)
	{
		if(c == dCenter0)
			return dCenter1;
		else if(c == dCenter1)
			return dCenter0;
		else return null;
	}

	public boolean isShoreline()
	{
		if((dCenter0.hasMarker(Marker.Water) && !dCenter1.hasMarker(Marker.Water)) || (!dCenter0.hasMarker(Marker.Water) && dCenter1.hasMarker(Marker.Water)))
			return true;

		return false;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("index", index);
		nbt.setDouble("midX", midpoint.x);
		nbt.setDouble("midY", midpoint.y);

		nbt.setInteger("dcenter0", dCenter0.index);
		nbt.setInteger("dcenter1", dCenter1.index);
		nbt.setInteger("vCorner0", vCorner0.index);
		nbt.setInteger("vCorner1", vCorner1.index);
	}

	public void readFromNBT(NBTTagCompound nbt, IslandMap m)
	{
		this.midpoint = new Point(nbt.getDouble("midX"), nbt.getDouble("midY"));
		dCenter0 = m.centers.get(nbt.getInteger("dcenter0"));
		dCenter1 = m.centers.get(nbt.getInteger("dcenter1"));
		vCorner0 = m.corners.get(nbt.getInteger("vCorner0"));
		vCorner1 = m.corners.get(nbt.getInteger("vCorner1"));
	}
}

