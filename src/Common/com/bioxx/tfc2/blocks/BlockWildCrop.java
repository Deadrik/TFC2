package com.bioxx.tfc2.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.tileentities.TileCrop;

public class BlockWildCrop extends BlockTerra implements ITileEntityProvider
{
	public static PropertyEnum CROPTYPE = PropertyEnum.create("crop", Crop.class, Crop.cropList);
	public static PropertyInteger GROWTH = PropertyInteger.create("stage", 0, 15);

	public BlockWildCrop()
	{
		super(Material.ground, GROWTH);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.isBlockContainer = true;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{GROWTH, CROPTYPE});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(GROWTH, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return (Integer)state.getValue(GROWTH);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileCrop();
	}

}
