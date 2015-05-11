package com.bioxx.tfc2.Blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class BlockTerra extends Block
{
	/**
	 * This is the internal Property for metadata
	 */
	private final PropertyHelper META_PROP;

	protected BlockTerra()
	{
		this(Material.rock, null);
	}

	protected BlockTerra(Material material, PropertyHelper meta)
	{
		super(material);
		this.META_PROP = meta;
		if (META_PROP != null)
			this.setDefaultState(this.getBlockState().getBaseState().withProperty(META_PROP, (Comparable)META_PROP.getAllowedValues().toArray()[0]));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		if(hasMeta())
		{
			for(int l = 0; l < META_PROP.getAllowedValues().size(); l++)
				list.add(new ItemStack(itemIn, 1, l));
		}
		else
			super.getSubBlocks(itemIn, tab, list);
	}



	@Override
	public int damageDropped(IBlockState state)
	{
		if (META_PROP != null)
			return getMetaFromState(state);
		return super.damageDropped(state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return hasMeta() ? this.getDefaultState().withProperty(META_PROP, (Comparable)META_PROP.getAllowedValues().toArray()[meta]) : super.getStateFromMeta(meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return hasMeta() ? ((Integer)state.getValue(META_PROP)).intValue() : super.getMetaFromState(state);
	}

	public final boolean hasMeta()
	{
		return META_PROP != null;
	}
}
