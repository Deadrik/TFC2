package com.bioxx.tfc2.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;

public class BlockSaltWater extends BlockFluidClassic {

	public BlockSaltWater(Fluid fluid, Material material) 
	{
		super(fluid, material);
		this.setTickRate(3);
	}

	@Override
	public float getFluidHeightForRender(IBlockAccess world, BlockPos pos)
	{
		IBlockState here = world.getBlockState(pos);
		IBlockState up = world.getBlockState(pos.offsetDown(densityDir));
		if (here.getBlock() instanceof BlockFluidBase)
		{
			if (up.getBlock().getMaterial().isLiquid() || up.getBlock() instanceof IFluidBlock)
			{
				return 1;
			}

			if (getMetaFromState(here) == getMaxRenderHeightMeta())
			{
				return 0.875F;
			}
		}
		return !here.getBlock().getMaterial().isSolid() && up.getBlock() == this ? 1 : this.getQuantaPercentage(world, pos) * 0.875F;
	}

}
