package com.bioxx.tfc2.blocks;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.api.FoodRegistry;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.events.CropEvent;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.api.properties.PropertyClass;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.tileentities.TileCrop;

public class BlockCrop extends BlockTerra implements ITileEntityProvider
{
	public static PropertyClass CROPTYPE = PropertyClass.create("crop", Crop.class, Crop.cropList);
	public static PropertyInteger GROWTH = PropertyInteger.create("stage", 0, 7);
	private static final AxisAlignedBB[] CROPS_AABB = { new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D) };

	public BlockCrop()
	{
		super(Material.GRASS, GROWTH);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.isBlockContainer = true;
		setSoundType(SoundType.GROUND);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
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
			((World)worldIn).setBlockToAir(pos);

			if(tile.getGrowthStage() >= tile.getCropType().getGrowthStages() - 1)
			{
				Collection produce = FoodRegistry.getInstance().getProduceForCrop(tile.getCropType().getName());
				Iterator iter = produce.iterator();
				while(iter.hasNext())
				{
					ItemStack is = ((ItemStack) iter.next()).copy();
					is.setCount(1);
					if(is.getItem() instanceof IFood)
						Food.setDecayTimer(is, worldIn.getWorldTime()+Food.getExpirationTimer(is));
					Core.dropItem(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, is);
				}
			}
		}
		return true;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		IBlockState soil = worldIn.getBlockState(pos.down());
		return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && (Core.isSoil(soil) || soil.getBlock() == TFCBlocks.Farmland);
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return CROPS_AABB[((Integer)state.getValue(GROWTH)).intValue()];
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
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
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{GROWTH, CROPTYPE});
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

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}
}
