package com.bioxx.tfc2.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.blocks.BlockTerra;

public class BlockStoneStalag extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);
	public static PropertyInteger SIZE_PROPERTY = PropertyInteger.create("size", 0, 2);

	private static AxisAlignedBB AABB_0 = new AxisAlignedBB(0.125,0,0.125,0.875,1,0.875);
	private static AxisAlignedBB AABB_1 = new AxisAlignedBB(0.1875,0,0.1875,0.8125,1,0.8125);
	private static AxisAlignedBB AABB_2 = new AxisAlignedBB(0.25,0.1875,0.25,0.75,1,0.75);

	public BlockStoneStalag()
	{
		super(Material.ROCK, META_PROPERTY);
		this.setCreativeTab(null);
		this.setSoundType(SoundType.STONE);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		IBlockState s = worldIn.getBlockState(pos.up());
		if(s.getBlock() != this && s.getBlock() != TFCBlocks.Stone)
		{
			((World)worldIn).setBlockToAir(pos);
		}
		else
		{
			super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		}
	}

	/*******************************************************************************
	 * 1. Rendering 
	 *******************************************************************************/
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;//!this.isTransparent;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	/*******************************************************************************
	 * 1. Blockstate 
	 *******************************************************************************/
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { META_PROPERTY, SIZE_PROPERTY});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		int size = getSize(state, world, pos);
		if(size < 3)
			return state.withProperty(SIZE_PROPERTY, size);
		return state.withProperty(SIZE_PROPERTY, 0);
	}

	public int getSize(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		int size = 0;
		for (int i = 1; i < 4; i++)
		{
			IBlockState otherState = world.getBlockState(pos.up(i));
			if(otherState.getBlock() == TFCBlocks.Stone)
			{
				size = i-1;
				break;
			}
		}

		return size;
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, StoneType.getStoneTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((StoneType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		state = this.getActualState(state, source, pos);
		int size = ((Integer)state.getValue(SIZE_PROPERTY)).intValue();
		if(size== 0)
			return AABB_0;
		else if(size == 1)
			return AABB_1;
		else if(size == 2)
			return AABB_2;
		return NULL_AABB;
	}
}
