package com.bioxx.tfc2.blocks;

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

import com.bioxx.tfc2.api.types.WoodType;

public class BlockLeaves2 extends BlockLeaves
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));
	public static PropertyBool EAST_DOWN = PropertyBool.create("downeast");
	public static PropertyBool WEST_DOWN = PropertyBool.create("downwest");
	public static PropertyBool NORTH_DOWN = PropertyBool.create("downnorth");
	public static PropertyBool SOUTH_DOWN = PropertyBool.create("downsouth");
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
		return new BlockState(this, new IProperty[]{META_PROPERTY, BlockLeaves.FANCY, EAST_DOWN, NORTH_DOWN, SOUTH_DOWN, WEST_DOWN});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean east_down = false;
		boolean west_down = false;
		boolean north_down = false;
		boolean south_down = false;
		boolean fancy = true;
		if(!isTransparent)
			fancy = false;
		//North
		if((world.getBlockState(pos.north().down()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.north().down()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			north_down = true;
		}

		//South
		if((world.getBlockState(pos.south().down()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.south().down()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			south_down = true;
		}


		if((world.getBlockState(pos.east().down()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.east().down()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			east_down = true;
		}

		//West
		if((world.getBlockState(pos.west().down()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.west().down()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			west_down = true;
		}
		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			fancy = false;


		return state.withProperty(BlockLeaves.FANCY, fancy).withProperty(EAST_DOWN, east_down).withProperty(WEST_DOWN, west_down).
				withProperty(NORTH_DOWN, north_down).withProperty(SOUTH_DOWN, south_down);

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
