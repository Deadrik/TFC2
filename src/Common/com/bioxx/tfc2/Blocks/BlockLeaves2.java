package com.bioxx.tfc2.Blocks;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc2.api.Types.WoodType;

public class BlockLeaves2 extends BlockLeaves
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));
	public static PropertyBool IS_OUTER = PropertyBool.create("is_outer");
	public static PropertyBool EAST = PropertyBool.create("east");
	public static PropertyBool WEST = PropertyBool.create("west");
	public static PropertyBool NORTH = PropertyBool.create("north");
	public static PropertyBool SOUTH = PropertyBool.create("south");
	public static PropertyBool EAST_DOWN = PropertyBool.create("downeast");
	public static PropertyBool WEST_DOWN = PropertyBool.create("downwest");
	public static PropertyBool NORTH_DOWN = PropertyBool.create("downnorth");
	public static PropertyBool SOUTH_DOWN = PropertyBool.create("downsouth");
	public static PropertyBool CORNER_NE = PropertyBool.create("corner_ne");
	public static PropertyBool CORNER_SE = PropertyBool.create("corner_se");
	public static PropertyBool CORNER_NW = PropertyBool.create("corner_nw");
	public static PropertyBool CORNER_SW = PropertyBool.create("corner_sw");
	private boolean isTransparent = true;

	public BlockLeaves2()
	{
		super();
		this.META_PROP = META_PROPERTY;
		//this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, WoodType.Oak).withProperty(IS_OUTER, Boolean.valueOf(false)));
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY, IS_OUTER, 
				EAST, NORTH, SOUTH, WEST, 
				EAST_DOWN, NORTH_DOWN, SOUTH_DOWN, WEST_DOWN, 
				CORNER_NE, CORNER_SE, CORNER_NW, CORNER_SW});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean outer = true;
		boolean east = false;
		boolean west = false;
		boolean north = false;
		boolean south = false;
		boolean east_down = false;
		boolean west_down = false;
		boolean north_down = false;
		boolean south_down = false;
		boolean corner_ne = false;
		boolean corner_se = false;
		boolean corner_nw = false;
		boolean corner_sw = false;

		if(!isTransparent)
			outer = false;

		//North
		if(world.getBlockState(pos.offsetNorth()).getBlock() != state.getBlock())
		{
			north = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetNorth().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetNorth().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			north_down = true;
			north = true;
		}

		//South
		if(world.getBlockState(pos.offsetSouth()).getBlock() != state.getBlock())
		{
			south = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetSouth().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetSouth().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			south_down = true;
			south = true;
		}

		//East
		if(world.getBlockState(pos.offsetEast()).getBlock() != state.getBlock())
		{
			east = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetEast().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetEast().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			east = true;
			east_down = true;
		}

		//West
		if(world.getBlockState(pos.offsetWest()).getBlock() != state.getBlock())
		{
			west = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetWest().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetWest().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			west = true;
			west_down = true;
		}

		if((world.getBlockState(pos.offsetNorth().offsetEast().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetNorth().offsetEast().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			corner_ne = true;
		}

		if((world.getBlockState(pos.offsetSouth().offsetEast().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetSouth().offsetEast().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			corner_se = true;
		}

		if((world.getBlockState(pos.offsetNorth().offsetWest().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetNorth().offsetWest().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			corner_nw = true;
		}

		if((world.getBlockState(pos.offsetSouth().offsetWest().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetSouth().offsetWest().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			corner_sw = true;
		}

		if(world.getBlockState(pos.offsetUp()).getBlock() != this)
			outer = true;
		if(world.getBlockState(pos.offsetDown()).getBlock() != this)
			outer = true;

		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			outer = false;

		return state.withProperty(IS_OUTER, outer).withProperty(EAST, east).withProperty(WEST, west).withProperty(NORTH, north).withProperty(SOUTH, south).
				withProperty(EAST_DOWN, east_down).withProperty(WEST_DOWN, west_down).withProperty(NORTH_DOWN, north_down).withProperty(SOUTH_DOWN, south_down).
				withProperty(CORNER_NE, corner_ne).withProperty(CORNER_NW, corner_nw).withProperty(CORNER_SE, corner_se).withProperty(CORNER_SW, corner_sw);

	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		for(int l = 16; l < 19; l++)
			list.add(new ItemStack(itemIn, 1, l));
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta((meta & 15) + 16));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta() & 15;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}
}
