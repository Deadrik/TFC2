package com.bioxx.tfc2.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.StoneType;

public class BlockClimbingRocks extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);
	public static PropertyEnum FACING = PropertyEnum.create("facing", EnumFacing.class);

	public BlockClimbingRocks()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setBlockBounds(0f, 0, 0f, 1f, 0.01f, 1f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, StoneType.Granite).withProperty(FACING, EnumFacing.DOWN));
		this.setShowInCreative(false);
	}

	@Override
	public boolean isLadder(IBlockAccess access, BlockPos blockPos, EntityLivingBase entity)
	{
		if(access.getBlockState(blockPos.down()).getBlock().isBlockNormalCube())
			return false;
		return true;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) 
	{
		IBlockState state = world.getBlockState(pos);
		EnumFacing facing = (EnumFacing) state.getValue(FACING);

		if(Core.isStone(world.getBlockState(pos.north())))
		{
			this.setBlockBounds(0.0f, 0.0f, 0f, 1f, 1f, 0.01f);
		}
		else if(Core.isStone(world.getBlockState(pos.south())))
		{
			this.setBlockBounds(0.0f, 0.0f, 0.99f, 1f, 1f, 1f);
		}
		else if(Core.isStone(world.getBlockState(pos.east())))
		{
			this.setBlockBounds(0.99f, 0.0f, 0.0f, 1f, 1f, 1f);
		}
		else if(Core.isStone(world.getBlockState(pos.west())))
		{
			this.setBlockBounds(0f, 0f, 0f, 0.01f, 1f, 1f);
		}
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY, FACING});
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
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if(Core.isStone(world.getBlockState(pos.north())))
			return state.withProperty(FACING, EnumFacing.NORTH);
		else if(Core.isStone(world.getBlockState(pos.south())))
			return state.withProperty(FACING, EnumFacing.SOUTH);
		else if(Core.isStone(world.getBlockState(pos.east())))
			return state.withProperty(FACING, EnumFacing.EAST);
		else if(Core.isStone(world.getBlockState(pos.west())))
			return state.withProperty(FACING, EnumFacing.WEST);

		return state.withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
	{

	}
}
