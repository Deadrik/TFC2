package com.bioxx.tfc2.Blocks.Terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;

import com.bioxx.tfc2.Blocks.BlockTerra;
import com.bioxx.tfc2.api.Types.StoneType;

public class BlockSand extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockSand()
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
		return this.getDefaultState().withProperty(META_PROPERTY, StoneType.getStoneTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((StoneType)state.getValue(META_PROPERTY)).getMeta();
	}
}
