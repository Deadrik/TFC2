package com.bioxx.tfc2.Blocks.Terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
