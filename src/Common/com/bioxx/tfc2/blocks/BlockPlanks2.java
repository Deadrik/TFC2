package com.bioxx.tfc2.blocks;

import java.util.Arrays;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;

import com.bioxx.tfc2.api.interfaces.INeedOffset;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockPlanks2 extends BlockPlanks implements INeedOffset
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 18));

	public BlockPlanks2()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, WoodType.Rosewood));
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		meta %= 16;
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta(meta + 16));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta() & 15;
	}

	@Override
	public int convertMetaToBlock(int meta) 
	{
		return meta & 15;
	}

	@Override
	public int convertMetaToItem(int meta) 
	{
		return meta + 16;
	}
}
