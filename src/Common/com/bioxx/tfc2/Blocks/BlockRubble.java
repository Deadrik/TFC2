package com.bioxx.tfc2.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;

import com.bioxx.tfc2.api.Types.WoodType;

public class BlockRubble extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", WoodType.class);

	public BlockRubble()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getStoneTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}
}
