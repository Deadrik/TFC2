package com.bioxx.tfc2.Blocks.Terrain;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import com.bioxx.tfc2.Blocks.BlockTerra;

public class BlockStone extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockStone()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		for(int l = 0; l < META_PROPERTY.getAllowedValues().size(); l++)
			list.add(new ItemStack(itemIn, 1, l));
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { META_PROPERTY });
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		if (META_PROPERTY != null)
			return getMetaFromState(state);
		return super.damageDropped(state);
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

	public enum StoneType implements IStringSerializable
	{
		Andesite("Andesite", 10), Basalt("Basalt", 9), Blueschist("Blueschist", 12), Chert("Chert", 7), Claystone("Claystone", 4), Dacite("Dacite", 11), 
		Diorite("Diorite", 1), Dolomite("Dolomite", 6), Gabbro("Gabbro", 2), Gneiss("Gneiss", 14), Granite("Granite", 0), Limestone("Limestone", 5), 
		Marble("Marble", 15), Rhyolite("Rhyolite", 8), Schist("Schist", 13), Shale("Shale", 3);

		private String name;
		private int meta;

		StoneType(String s, int id)
		{
			name = s;
			meta = id;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMeta()
		{
			return meta;
		}

		public static StoneType getStoneTypeFromMeta(int meta)
		{
			for(int i = 0; i < StoneType.values().length; i++)
			{
				if(StoneType.values()[i].meta == meta)
					return StoneType.values()[i];
			}
			return null;
		}
	}

}
