package com.bioxx.tfc2.blocks.liquids;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;

public class BlockFreshWaterStatic extends BlockFreshWater 
{

	public BlockFreshWaterStatic(Fluid fluid, Material material) 
	{
		super(fluid, material);
		this.setTickRandomly(true);
		this.setTickRate(10);
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		this.updateLiquid(worldIn, pos, state);
	}

	private void updateLiquid(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.setBlockState(pos, TFCBlocks.FreshWater.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		worldIn.scheduleUpdate(pos, TFCBlocks.FreshWater, this.tickRate(worldIn));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		//super.updateTick(world, pos, state, rand);
		if(isGroundWater(world, pos) && world.provider.getDimensionId() == 0)
		{
			IslandMap map = Core.getMapForWorld(world, pos);
			NBTTagCompound nbt = map.getClosestCenter(pos).getCustomNBT();
			NBTTagCompound data;
			if(!nbt.hasKey("TFC2_Data"))
				nbt.setTag("TFC2_Data", new NBTTagCompound());
			data = nbt.getCompoundTag("TFC2_Data");
			data.setInteger("hydration", (int)Math.min(data.getInteger("hydration")+1, 10000));
		}
	}

	public boolean isGroundWater(World w, BlockPos pos)
	{
		if(Core.isSoil(w.getBlockState(pos.down(1))))
		{
			return true;
		}
		return false;
	}

}
