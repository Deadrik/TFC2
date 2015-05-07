package com.bioxx.tfc2.Blocks.Terrain;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import com.bioxx.tfc2.Blocks.BlockTerra;

public class BlockDirt extends BlockTerra
{
	static PropertyInteger META_PROPERTY = PropertyInteger.create("meta", 0, 15);

	public BlockDirt()
	{
		super(Material.ground, META_PROPERTY);

		this.setCreativeTab(CreativeTabs.tabBlock);
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
	public int tickRate(World worldIn)
	{
		return 3;
	}


}
