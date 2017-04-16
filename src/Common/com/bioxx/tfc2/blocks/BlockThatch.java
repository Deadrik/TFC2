package com.bioxx.tfc2.blocks;

import com.bioxx.tfc2.core.TFCTabs;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockThatch extends BlockTerra
{
	public BlockThatch()
	{
		super(Material.GRASS, null);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setSoundType(SoundType.PLANT);
		this.lightOpacity = 255;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(!world.isRemote && pos.up().equals(fromPos) && world.isAirBlock(pos.down()))
		{
			if(world.getBlockState(fromPos).getBlock() instanceof BlockGravity)
				world.destroyBlock(pos, true);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		entityIn.motionX *= 0.1D;
		entityIn.motionZ *= 0.1D;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return null;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

}
