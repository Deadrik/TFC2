package com.bioxx.tfc2.api.animals;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

public class HerdGoal
{
	HerdGoalEnum goalType;
	Center goalLocation;
	HerdPath path;

	public HerdGoal(HerdGoalEnum goal, Center loc)
	{
		goalType = goal;
		goalLocation = loc;
		path = new HerdPath(0L);
	}

	public HerdGoal(HerdGoalEnum goal, Center loc, HerdPath p)
	{
		this(goal, loc);
		path = p;
	}

	public void updateHerdPath(HerdPath p)
	{
		path = p;
	}

	public void readFromNBT(NBTTagCompound nbt, IslandMap map)
	{
		goalType = HerdGoalEnum.getEnum(nbt.getString("goalType"));
		goalLocation = map.centers.get(nbt.getInteger("goalLocation"));
		HerdPath path = new HerdPath(0);
		path.readFromNBT(nbt.getCompoundTag("path"), map);
		this.path = path;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("goalLocation", goalLocation.index);
		nbt.setString("goalType", goalType.getName());
		if(path != null){
			NBTTagCompound pathNBT = new NBTTagCompound();
			path.writeToNBT(pathNBT);
			nbt.setTag("path", pathNBT);
		}
	}
}