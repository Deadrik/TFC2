package com.bioxx.tfc2.blocks;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.api.FoodRegistry;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.events.CropEvent;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.api.properties.PropertyClass;
import com.bioxx.tfc2.core.Food;
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

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return false;

		IslandMap map = Core.getMapForWorld(worldIn, pos);
		TileCrop tile = (TileCrop) worldIn.getTileEntity(pos);
		CropEvent.Harvest event = new CropEvent.Harvest(worldIn, map, pos);
		Global.EVENT_BUS.post(event);
		//If the event is canceled then skip all of our code.
		if(!event.isCanceled())
		{
			worldIn.setBlockToAir(pos);

			if(tile.getGrowthStage() >= tile.getCropType().getGrowthStages() - 1)
			{
				Collection produce = FoodRegistry.getInstance().getProduceForCrop(tile.getCropType().getName());
				Iterator iter = produce.iterator();
				while(iter.hasNext())
				{
					ItemStack is = ((ItemStack) iter.next()).copy();
					is.stackSize = 1;
					if(is.getItem() instanceof IFood)
						Food.setDecayTimer(is, worldIn.getWorldTime()+((IFood)is.getItem()).getExpirationTimer(is));
					Core.dropItem(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, is);
				}
			}
		}
		return true;
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

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

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		TileCrop tc = (TileCrop) worldIn.getTileEntity(pos);
		return state.withProperty(CROPTYPE, tc.getCropType()).withProperty(GROWTH, tc.getGrowthStage());
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
	public Item getItemDropped(IBlockState paramIBlockState, Random paramRandom, int paramInt)
	{
		return null;
	}
}
