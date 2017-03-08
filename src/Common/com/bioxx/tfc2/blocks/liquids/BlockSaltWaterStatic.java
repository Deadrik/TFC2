package com.bioxx.tfc2.blocks.liquids;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;

public class BlockSaltWaterStatic extends BlockSaltWater 
{

	public BlockSaltWaterStatic(Fluid fluid, Material material) 
	{
		super(fluid, material);
		this.setTickRandomly(false);
	}

	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos blockIn)
	{
		this.updateLiquid((World)worldIn, pos, worldIn.getBlockState(pos));
	}

	private void updateLiquid(World worldIn, BlockPos pos, IBlockState state)
	{
		/*worldIn.setBlockState(pos, TFCBlocks.SaltWater.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		worldIn.scheduleUpdate(pos, TFCBlocks.SaltWater, this.tickRate(worldIn));*/
	}

}
