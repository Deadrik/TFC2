package com.bioxx.tfc2.api.animals;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

public interface IHerdBrain 
{
	public void process(World world, IslandMap map, long currentHour);

	public HerdActivityEnum getActivity();

	public void setLocation(Center center);

	public Center getLocation();

	public void setGoal(HerdGoalEnum goal, Center loc);

	public void readFromNBT(NBTTagCompound nbt, IslandMap map);

	public void writeToNBT(NBTTagCompound nbt);
}
