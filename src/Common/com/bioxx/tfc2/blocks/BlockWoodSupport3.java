package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockWoodSupport3 extends BlockWoodSupport2
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));

	public BlockWoodSupport3() 
	{
		super(Material.WOOD, META_PROPERTY);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, WoodType.Blackwood).
				withProperty(SPAN, Boolean.valueOf(false)).
				withProperty(NORTH_CONNECTION, Boolean.valueOf(false)).
				withProperty(EAST_CONNECTION, Boolean.valueOf(false)).
				withProperty(SOUTH_CONNECTION, Boolean.valueOf(false)).
				withProperty(WEST_CONNECTION, Boolean.valueOf(false)));
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos, IBlockState myState)
	{
		return ((WoodType)myState.getValue(META_PROPERTY)).getSupportRange();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(TFCBlocks.SupportBeam3);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public int convertMetaToBlock(int meta) 
	{
		return meta & 7;
	}

	@Override
	public int convertMetaToItem(int meta) 
	{
		return (meta & 7) + 16;
	}

	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state.withProperty(NORTH_CONNECTION, world.getBlockState(pos.north()).getBlock() == this).
				withProperty(SOUTH_CONNECTION, world.getBlockState(pos.south()).getBlock() == this).
				withProperty(EAST_CONNECTION, world.getBlockState(pos.east()).getBlock() == this).
				withProperty(WEST_CONNECTION, world.getBlockState(pos.west()).getBlock() == this).
				withProperty(SPAN, !canBeSupportedBy(state, world.getBlockState(pos.down())));
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY, SPAN, NORTH_CONNECTION, SOUTH_CONNECTION, EAST_CONNECTION, WEST_CONNECTION});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta((meta & 7) + 16)).withProperty(SPAN, (meta & 8) == 0 ? false : true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (((WoodType)state.getValue(META_PROPERTY)).getMeta() & 7) + ((Boolean)state.getValue(SPAN) ? 8 : 0);
	}
}
