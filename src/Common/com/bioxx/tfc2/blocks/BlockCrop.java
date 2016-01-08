package com.bioxx.tfc2.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.api.properties.PropertyClass;
import com.bioxx.tfc2.tileentities.TileCrop;

public class BlockCrop extends BlockTerra implements ITileEntityProvider
{
	public static PropertyClass CROPTYPE = PropertyClass.create("crop", Crop.class, Crop.cropList);
	public static PropertyInteger GROWTH = PropertyInteger.create("stage", 0, 7);

	public BlockCrop()
	{
		super(Material.grass, GROWTH);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.isBlockContainer = true;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileCrop tc = (TileCrop) worldIn.getTileEntity(pos);
		return state.withProperty(CROPTYPE, tc.getCropType());
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

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
	{
		return null;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

}
