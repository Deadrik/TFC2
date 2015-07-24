package com.bioxx.tfc2.World;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;

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
		return b == TFCBlocks.Sand;
	}

	@Override
	public boolean canBlockFreeze(BlockPos pos, boolean byWater)
	{
		int x = pos.getX() << 12;
		int z = pos.getZ() << 12;

		if(WorldGen.instance == null)
			return false;

		/*Map m = WorldGen.instance.getIslandMap(x, z);

		if(m.islandParams.getIslandTemp() == ClimateTemp.SUBTROPICAL || m.islandParams.getIslandTemp() == ClimateTemp.TROPICAL)
			return false;*/

		return false;
	}

	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight)
	{
		int x = pos.getX() << 12;
		int z = pos.getZ() << 12;

		if(WorldGen.instance == null)
			return false;

		/*Map m = WorldGen.instance.getIslandMap(x, z);

		if(m.islandParams.getIslandTemp() == ClimateTemp.SUBTROPICAL || m.islandParams.getIslandTemp() == ClimateTemp.TROPICAL)
			return false;*/

		return worldObj.canSnowAtBody(pos, checkLight);
	}

	@Override
	public double getHorizon()
	{
		return 64D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight()
	{
		return 258;
	}
}
