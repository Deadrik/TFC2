package com.bioxx.tfc2.World;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;

import com.bioxx.tfc2.Core;

public class WorldProviderSurface extends WorldProvider 
{

	@Override
	public String getDimensionName() {
		return "Surface";
	}

	@Override
	public String getInternalNameSuffix() {
		return "Surface";
	}

	@Override
	protected void registerWorldChunkManager()
	{
		this.worldChunkMgr = new ChunkManager(worldObj);
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderSurface(worldObj, worldObj.getSeed(), false, "");
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		Block b = Core.getGroundAboveSeaLevel(this.worldObj, new BlockPos(x, 0, z));
		return b == Blocks.grass;
	}

	@Override
	public double getHorizon()
	{
		return 32D;
	}
}
