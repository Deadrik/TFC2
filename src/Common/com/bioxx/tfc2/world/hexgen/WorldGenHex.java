package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.api.interfaces.IHexGenerator;
import com.bioxx.tfc2.api.types.Moisture;

public class WorldGenHex implements IHexGenerator
{
	public IslandMap map;
	protected Moisture iMoisture;
	public int centerX;
	public int centerZ;
	public BlockPos centerPos;

	@Override
	public void generate(Random random, IslandMap map, Center c, World world) 
	{
		if(map == null || c == null)
			return;
		this.map = map;
		iMoisture = map.getParams().getIslandMoisture();
		centerX = (int)(map.getParams().getWorldX()+c.point.getX());
		centerZ = (int)(map.getParams().getWorldZ()+c.point.getZ());
		centerPos = new BlockPos(centerX, 0, centerZ);
	}

}
