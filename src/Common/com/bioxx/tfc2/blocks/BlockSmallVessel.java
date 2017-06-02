package com.bioxx.tfc2.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.tileentities.TileSmallVessel;
import com.google.common.collect.Lists;

public class BlockSmallVessel extends BlockTerra implements ITileEntityProvider
{
	public static final PropertyItem INVENTORY = new PropertyItem();

	public static final AxisAlignedBB aabb0_z = new AxisAlignedBB(0.05,0,0.21875,0.45,0.5,0.28125);
	public static final AxisAlignedBB aabb1_z = new AxisAlignedBB(0.05,0,0.71875,0.45,0.5,0.78125);
	public static final AxisAlignedBB aabb2_z = new AxisAlignedBB(0.55,0,0.21875,0.95,0.5,0.28125);
	public static final AxisAlignedBB aabb3_z = new AxisAlignedBB(0.55,0,0.71875,0.95,0.5,0.78125);

	public static final AxisAlignedBB aabb0_x = new AxisAlignedBB(0.21875,0,0.05,0.28125,0.5,0.45);
	public static final AxisAlignedBB aabb1_x = new AxisAlignedBB(0.71875,0,0.05,0.78125,0.5,0.45);
	public static final AxisAlignedBB aabb2_x = new AxisAlignedBB(0.21875,0,0.55,0.28125,0.5,0.95);
	public static final AxisAlignedBB aabb3_x = new AxisAlignedBB(0.71875,0,0.55,0.78125,0.5,0.95);

	public BlockSmallVessel()
	{
		super(Material.GRASS, null);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.isBlockContainer = true;
		setSoundType(SoundType.GROUND);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return false;

		if(player.getHeldItemMainhand().isEmpty())
		{
			TileSmallVessel tile = (TileSmallVessel)world.getTileEntity(pos);
			int index = getIndex(hitX, hitZ); 
			if(!tile.getStackInSlot(index).isEmpty())
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, tile.removeStackFromSlot(index));
			}
		}


		return true;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		return worldIn.isSideSolid(pos.down(), EnumFacing.UP);
	}

	private int getIndex(float hitX, float hitZ)
	{
		int index = 0;
		if(hitX < 0.5 && hitZ < 0.5)
			index = 0;
		else if(hitX < 0.5 && hitZ >= 0.5)
			index = 1;
		else if(hitX >= 0.5 && hitZ < 0.5)
			index = 2;
		else if(hitX >= 0.5 && hitZ >= 0.5)
			index = 3;
		return index;
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
		return new AxisAlignedBB(0,0,0,1,0.02,1);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return new AxisAlignedBB(0.1,0,0.1,0.9,0.5,0.9);
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
	{
		TileSmallVessel tile = (TileSmallVessel) world.getTileEntity(pos);
		for(AxisAlignedBB aabb : getCollisionBoxList(tile))
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb);
		}
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
	{
		List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

		for (AxisAlignedBB axisalignedbb : getCollisionBoxList((TileSmallVessel)worldIn.getTileEntity(pos)))
		{
			list.add(this.rayTrace(pos, start, end, axisalignedbb));
		}

		RayTraceResult raytraceresult1 = null;
		double d1 = 0.0D;

		for (RayTraceResult raytraceresult : list)
		{
			if (raytraceresult != null)
			{
				double d0 = raytraceresult.hitVec.squareDistanceTo(end);

				if (d0 > d1)
				{
					raytraceresult1 = raytraceresult;
					d1 = d0;
				}
			}
		}

		return raytraceresult1;
	}

	private static List<AxisAlignedBB> getCollisionBoxList(TileSmallVessel tile)
	{
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();


		if(tile.getRotation() == EnumFacing.Axis.Z)
		{
			if(!tile.getStackInSlot(0).isEmpty())
				list.add(aabb0_z);
			if(!tile.getStackInSlot(1).isEmpty())
				list.add(aabb1_z);
			if(!tile.getStackInSlot(2).isEmpty())
				list.add(aabb2_z);
			if(!tile.getStackInSlot(3).isEmpty())
				list.add(aabb3_z);
		}
		else
		{
			if(!tile.getStackInSlot(0).isEmpty())
				list.add(aabb0_x);
			if(!tile.getStackInSlot(1).isEmpty())
				list.add(aabb1_x);
			if(!tile.getStackInSlot(2).isEmpty())
				list.add(aabb2_x);
			if(!tile.getStackInSlot(3).isEmpty())
				list.add(aabb3_x);
		}

		list.add(new AxisAlignedBB(0.02,0,0.02,0.98,0.001,0.98));

		return list;
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
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileSmallVessel te = (TileSmallVessel) world.getTileEntity(pos);
		if(te != null)
			return te.writeExtendedBlockState((IExtendedBlockState) state);
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[]{}, new IUnlistedProperty[]{INVENTORY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileSmallVessel();
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
