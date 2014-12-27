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
	public static PropertyInteger META_PROP = PropertyInteger.create("meta", 0, 20);

	public BlockDirt()
	{
		super(Material.ground, META_PROP);
		this.setCreativeTab(CreativeTabs.tabBlock);
		//this.setTickRandomly(true);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { META_PROP });
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
