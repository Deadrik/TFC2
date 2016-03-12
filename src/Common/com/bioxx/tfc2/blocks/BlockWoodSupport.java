package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;

public class BlockWoodSupport extends BlockCollapsible implements ISupportBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 8));
	public static PropertyBool SPAN = PropertyBool.create("isSpan");
	public static PropertyBool NORTH_CONNECTION = PropertyBool.create("north");
	public static PropertyBool SOUTH_CONNECTION = PropertyBool.create("south");
	public static PropertyBool EAST_CONNECTION = PropertyBool.create("east");
	public static PropertyBool WEST_CONNECTION = PropertyBool.create("west");

	public BlockWoodSupport() 
	{
		super(Material.wood, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, WoodType.Oak).
				withProperty(SPAN, Boolean.valueOf(false)).
				withProperty(NORTH_CONNECTION, Boolean.valueOf(false)).
				withProperty(EAST_CONNECTION, Boolean.valueOf(false)).
				withProperty(SOUTH_CONNECTION, Boolean.valueOf(false)).
				withProperty(WEST_CONNECTION, Boolean.valueOf(false)));
	}

	protected BlockWoodSupport(Material material, PropertyHelper meta)
	{
		super(material, meta);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/

	@Override
	public int getNaturalSupportRange(IBlockState myState)
	{
		return 5;
	}

	@Override
	public boolean canSupport(IBlockState myState, IBlockState otherState)
	{
		if(otherState.getBlock() == this || Core.isSoil(otherState) || Core.isStone(otherState) || otherState.getBlock() instanceof ISupportBlock)
			return true;
		return false;
	}

	@Override
	public int getMaxSupportWeight(IBlockAccess world, BlockPos pos,
			IBlockState myState) {
		int maxWeight = 500;
		Block b = world.getBlockState(pos.east()).getBlock();
		if(b instanceof ISupportBlock && ((ISupportBlock)b).isSpan(world, pos.east()))
			return maxWeight*2;
		b = world.getBlockState(pos.west()).getBlock();
		if(b instanceof ISupportBlock && ((ISupportBlock)b).isSpan(world, pos.west()))
			return maxWeight*2;
		b = world.getBlockState(pos.north()).getBlock();
		if(b instanceof ISupportBlock && ((ISupportBlock)b).isSpan(world, pos.north()))
			return maxWeight*2;
		b = world.getBlockState(pos.south()).getBlock();
		if(b instanceof ISupportBlock && ((ISupportBlock)b).isSpan(world, pos.south()))
			return maxWeight*2;
		return maxWeight;
	}

	@Override
	public boolean isSpan(IBlockAccess world, BlockPos pos) {
		//If this block has an air block or partial block beneath it should be considered to be holding all of the weight above it.
		return world.getBlockState(pos).getValue(SPAN);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(TFCBlocks.LogVertical);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}
	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/
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
				withProperty(SPAN, !canSupport(state, world.getBlockState(pos.down())));
	}
	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY, SPAN, NORTH_CONNECTION, SOUTH_CONNECTION, EAST_CONNECTION, WEST_CONNECTION});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta((meta & 7))).withProperty(SPAN, (meta & 8) == 0 ? false : true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta() + ((Boolean)state.getValue(SPAN) ? 8 : 0);
	}
}
