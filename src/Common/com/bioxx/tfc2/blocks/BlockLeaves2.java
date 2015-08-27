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
	public static PropertyBool IS_OUTER = PropertyBool.create("is_outer");
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
		return new BlockState(this, new IProperty[]{META_PROPERTY, IS_OUTER});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean outer = true;

		if(!isTransparent)
			outer = false;

		//North
		if(world.getBlockState(pos.north()).getBlock() != state.getBlock())
			outer = true;

		//South
		if(world.getBlockState(pos.south()).getBlock() != state.getBlock())
			outer = true;

		//East
		if(world.getBlockState(pos.east()).getBlock() != state.getBlock())
			outer = true;

		//West
		if(world.getBlockState(pos.west()).getBlock() != state.getBlock())
			outer = true;

		if(world.getBlockState(pos.up()).getBlock() != this)
			outer = true;
		if(world.getBlockState(pos.down()).getBlock() != this)
			outer = true;

		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			outer = false;

		return state.withProperty(IS_OUTER, outer);

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
