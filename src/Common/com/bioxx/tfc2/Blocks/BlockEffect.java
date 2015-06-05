package com.bioxx.tfc2.Blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;

import com.bioxx.tfc2.api.Types.EffectType;

public class BlockEffect extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("effect", EffectType.class);

	public BlockEffect()
	{
		super(Material.fire, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setBlockBounds(0, 0, 0, 1, 0.001f, 1);
		this.fullBlock = false;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, EffectType.getTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EffectType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}
}
