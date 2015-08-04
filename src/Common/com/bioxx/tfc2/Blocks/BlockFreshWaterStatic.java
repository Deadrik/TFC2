package com.bioxx.tfc2.Blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import com.bioxx.tfc2.TFCBlocks;

public class BlockFreshWaterStatic extends BlockFreshWater 
{

	public BlockFreshWaterStatic(Fluid fluid, Material material) 
	{
		super(fluid, material);
		this.setTickRandomly(false);
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

}
