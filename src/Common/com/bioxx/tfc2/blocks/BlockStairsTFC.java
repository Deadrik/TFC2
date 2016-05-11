package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;
import com.google.common.collect.Lists;

public class BlockStairsTFC extends BlockCollapsible
{
	protected static final AxisAlignedBB AABB_SLAB_TOP = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_QTR_TOP_WEST = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_QTR_TOP_EAST = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_QTR_TOP_NORTH = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
	protected static final AxisAlignedBB AABB_QTR_TOP_SOUTH = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_OCT_TOP_NW = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 0.5D, 1.0D, 0.5D);
	protected static final AxisAlignedBB AABB_OCT_TOP_NE = new AxisAlignedBB(0.5D, 0.5D, 0.0D, 1.0D, 1.0D, 0.5D);
	protected static final AxisAlignedBB AABB_OCT_TOP_SW = new AxisAlignedBB(0.0D, 0.5D, 0.5D, 0.5D, 1.0D, 1.0D); 
	protected static final AxisAlignedBB AABB_OCT_TOP_SE = new AxisAlignedBB(0.5D, 0.5D, 0.5D, 1.0D, 1.0D, 1.0D); 
	protected static final AxisAlignedBB AABB_SLAB_BOTTOM = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	protected static final AxisAlignedBB AABB_QTR_BOT_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 1.0D);
	protected static final AxisAlignedBB AABB_QTR_BOT_EAST = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	protected static final AxisAlignedBB AABB_QTR_BOT_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D);
	protected static final AxisAlignedBB AABB_QTR_BOT_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
	protected static final AxisAlignedBB AABB_OCT_BOT_NW = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 0.5D, 0.5D);
	protected static final AxisAlignedBB AABB_OCT_BOT_NE = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 0.5D, 0.5D);
	protected static final AxisAlignedBB AABB_OCT_BOT_SW = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 0.5D, 0.5D, 1.0D);
	protected static final AxisAlignedBB AABB_OCT_BOT_SE = new AxisAlignedBB(0.5D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
	private static final int[][] field_150150_a = { { 4, 5 }, { 5, 7 }, { 6, 7 }, { 4, 6 }, { 0, 1 }, { 1, 3 }, { 2, 3 }, { 0, 2 } };
	private final Block modelBlock;
	private final IBlockState modelState;
	private boolean hasRaytraced;
	private int rayTracePass;

	public BlockStairsTFC(IBlockState modelState)
	{
		super(modelState.getBlock().getMaterial(modelState), null);
		setDefaultState(this.blockState.getBaseState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT));
		this.modelBlock = modelState.getBlock();
		this.modelState = modelState;
		//setHardness(this.modelBlock.blockHardness);
		//setResistance(this.modelBlock.blockResistance / 3.0F);
		this.setSoundType(SoundType.WOOD);
		setLightOpacity(255);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
	}

	@Override
	public void createFallingEntity(World world, BlockPos pos, IBlockState state)
	{
		/*world.setBlockToAir(pos);
		EntityItem ei = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.STICK, 1+world.rand.nextInt(3)));
		world.spawnEntityInWorld(ei);*/
		if(modelBlock instanceof BlockCollapsible)
		{
			((BlockCollapsible)modelBlock).createFallingEntity(world, pos, modelState);
		}
		else super.createFallingEntity(world, pos, state);
	}

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos, IBlockState myState)
	{
		if(modelBlock instanceof BlockCollapsible)
		{
			((BlockCollapsible)modelBlock).getNaturalSupportRange(world, pos, modelState);
		}
		return 5;
	}

	@Override
	public boolean canBeSupportedBy(IBlockState myState, IBlockState otherState)
	{
		if(modelBlock instanceof BlockCollapsible)
		{
			((BlockCollapsible)modelBlock).canBeSupportedBy(modelState, otherState);
		}
		if(otherState.getBlock() == this || Core.isSoil(otherState) || Core.isStone(otherState) || otherState.getBlock() instanceof ISupportBlock)
			return true;
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockState state,IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		boolean flipped = state.getValue(BlockStairs.HALF) == EnumHalf.TOP;
		EnumShape shape = (EnumShape)state.getValue(BlockStairs.SHAPE);
		EnumFacing facing = (EnumFacing)state.getValue(BlockStairs.FACING);
		if (side == EnumFacing.UP) return flipped;
		if (side == EnumFacing.DOWN) return !flipped;
		if (facing == side) return true;
		if (flipped)
		{
			if (shape == EnumShape.INNER_LEFT) return side == facing.rotateYCCW();
			if (shape == EnumShape.INNER_RIGHT) return side == facing.rotateY();
		}
		else
		{
			if (shape == EnumShape.INNER_LEFT) return side == facing.rotateY();
			if (shape == EnumShape.INNER_RIGHT) return side == facing.rotateYCCW();
		}
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}


	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
	{
		if (isOpaqueCube(state)) {
			return true;
		}

		IBlockState iblockstate = world.getBlockState(pos);
		EnumHalf half = (EnumHalf)iblockstate.getValue(BlockStairs.HALF);
		EnumFacing side = (EnumFacing)iblockstate.getValue(BlockStairs.FACING);
		return (side == face.getOpposite()) || ((half == EnumHalf.TOP) && (face == EnumFacing.DOWN)) || ((half == EnumHalf.BOTTOM) && (face == EnumFacing.UP));
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	public void setBaseCollisionBounds(IBlockAccess worldIn, BlockPos pos)
	{
		if (worldIn.getBlockState(pos).getValue(BlockStairs.HALF) == EnumHalf.TOP)
		{
			setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
		}
		else
		{
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
		}
	}

	public static boolean isBlockStairs(Block blockIn)
	{
		return blockIn instanceof BlockStairsTFC;
	}

	public static boolean isSameStair(IBlockAccess worldIn, BlockPos pos, IBlockState state)
	{
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();



		return (isBlockStairs(block)) && (iblockstate.getValue(BlockStairs.HALF) == state.getValue(BlockStairs.HALF)) && (iblockstate.getValue(BlockStairs.FACING) == state.getValue(BlockStairs.FACING));
	}

	public int func_176307_f(IBlockAccess blockAccess, BlockPos pos)
	{
		IBlockState iblockstate = blockAccess.getBlockState(pos);
		EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockStairs.FACING);
		EnumHalf blockstairs$enumhalf = (EnumHalf)iblockstate.getValue(BlockStairs.HALF);
		boolean flag = blockstairs$enumhalf == EnumHalf.TOP;

		if (enumfacing == EnumFacing.EAST)
		{
			IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
			Block block = iblockstate1.getBlock();

			if ((isBlockStairs(block)) && (blockstairs$enumhalf == iblockstate1.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(BlockStairs.FACING);

				if ((enumfacing1 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					return flag ? 1 : 2;
				}

				if ((enumfacing1 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					return flag ? 2 : 1;
				}
			}
		}
		else if (enumfacing == EnumFacing.WEST)
		{
			IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
			Block block1 = iblockstate2.getBlock();

			if ((isBlockStairs(block1)) && (blockstairs$enumhalf == iblockstate2.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(BlockStairs.FACING);

				if ((enumfacing2 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					return flag ? 2 : 1;
				}

				if ((enumfacing2 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					return flag ? 1 : 2;
				}
			}
		}
		else if (enumfacing == EnumFacing.SOUTH)
		{
			IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
			Block block2 = iblockstate3.getBlock();

			if ((isBlockStairs(block2)) && (blockstairs$enumhalf == iblockstate3.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(BlockStairs.FACING);

				if ((enumfacing3 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					return flag ? 2 : 1;
				}

				if ((enumfacing3 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					return flag ? 1 : 2;
				}
			}
		}
		else if (enumfacing == EnumFacing.NORTH)
		{
			IBlockState iblockstate4 = blockAccess.getBlockState(pos.north());
			Block block3 = iblockstate4.getBlock();

			if ((isBlockStairs(block3)) && (blockstairs$enumhalf == iblockstate4.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(BlockStairs.FACING);

				if ((enumfacing4 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					return flag ? 1 : 2;
				}

				if ((enumfacing4 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					return flag ? 2 : 1;
				}
			}
		}

		return 0;
	}

	public int func_176305_g(IBlockAccess blockAccess, BlockPos pos)
	{
		IBlockState iblockstate = blockAccess.getBlockState(pos);
		EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockStairs.FACING);
		EnumHalf blockstairs$enumhalf = (EnumHalf)iblockstate.getValue(BlockStairs.HALF);
		boolean flag = blockstairs$enumhalf == EnumHalf.TOP;

		if (enumfacing == EnumFacing.EAST)
		{
			IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
			Block block = iblockstate1.getBlock();

			if ((isBlockStairs(block)) && (blockstairs$enumhalf == iblockstate1.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(BlockStairs.FACING);

				if ((enumfacing1 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					return flag ? 1 : 2;
				}

				if ((enumfacing1 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					return flag ? 2 : 1;
				}
			}
		}
		else if (enumfacing == EnumFacing.WEST)
		{
			IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
			Block block1 = iblockstate2.getBlock();

			if ((isBlockStairs(block1)) && (blockstairs$enumhalf == iblockstate2.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(BlockStairs.FACING);

				if ((enumfacing2 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					return flag ? 2 : 1;
				}

				if ((enumfacing2 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					return flag ? 1 : 2;
				}
			}
		}
		else if (enumfacing == EnumFacing.SOUTH)
		{
			IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
			Block block2 = iblockstate3.getBlock();

			if ((isBlockStairs(block2)) && (blockstairs$enumhalf == iblockstate3.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(BlockStairs.FACING);

				if ((enumfacing3 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					return flag ? 2 : 1;
				}

				if ((enumfacing3 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					return flag ? 1 : 2;
				}
			}
		}
		else if (enumfacing == EnumFacing.NORTH)
		{
			IBlockState iblockstate4 = blockAccess.getBlockState(pos.south());
			Block block3 = iblockstate4.getBlock();

			if ((isBlockStairs(block3)) && (blockstairs$enumhalf == iblockstate4.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(BlockStairs.FACING);

				if ((enumfacing4 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					return flag ? 1 : 2;
				}

				if ((enumfacing4 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					return flag ? 2 : 1;
				}
			}
		}

		return 0;
	}

	public boolean func_176306_h(IBlockAccess blockAccess, BlockPos pos)
	{
		IBlockState iblockstate = blockAccess.getBlockState(pos);
		EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockStairs.FACING);
		EnumHalf blockstairs$enumhalf = (EnumHalf)iblockstate.getValue(BlockStairs.HALF);
		boolean flag = blockstairs$enumhalf == EnumHalf.TOP;
		float f = 0.5F;
		float f1 = 1.0F;

		if (flag)
		{
			f = 0.0F;
			f1 = 0.5F;
		}

		float f2 = 0.0F;
		float f3 = 1.0F;
		float f4 = 0.0F;
		float f5 = 0.5F;
		boolean flag1 = true;

		if (enumfacing == EnumFacing.EAST)
		{
			f2 = 0.5F;
			f5 = 1.0F;
			IBlockState iblockstate1 = blockAccess.getBlockState(pos.east());
			Block block = iblockstate1.getBlock();

			if ((isBlockStairs(block)) && (blockstairs$enumhalf == iblockstate1.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(BlockStairs.FACING);

				if ((enumfacing1 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					f5 = 0.5F;
					flag1 = false;
				}
				else if ((enumfacing1 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					f4 = 0.5F;
					flag1 = false;
				}
			}
		}
		else if (enumfacing == EnumFacing.WEST)
		{
			f3 = 0.5F;
			f5 = 1.0F;
			IBlockState iblockstate2 = blockAccess.getBlockState(pos.west());
			Block block1 = iblockstate2.getBlock();

			if ((isBlockStairs(block1)) && (blockstairs$enumhalf == iblockstate2.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(BlockStairs.FACING);

				if ((enumfacing2 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					f5 = 0.5F;
					flag1 = false;
				}
				else if ((enumfacing2 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					f4 = 0.5F;
					flag1 = false;
				}
			}
		}
		else if (enumfacing == EnumFacing.SOUTH)
		{
			f4 = 0.5F;
			f5 = 1.0F;
			IBlockState iblockstate3 = blockAccess.getBlockState(pos.south());
			Block block2 = iblockstate3.getBlock();

			if ((isBlockStairs(block2)) && (blockstairs$enumhalf == iblockstate3.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(BlockStairs.FACING);

				if ((enumfacing3 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					f3 = 0.5F;
					flag1 = false;
				}
				else if ((enumfacing3 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					f2 = 0.5F;
					flag1 = false;
				}
			}
		}
		else if (enumfacing == EnumFacing.NORTH)
		{
			IBlockState iblockstate4 = blockAccess.getBlockState(pos.north());
			Block block3 = iblockstate4.getBlock();

			if ((isBlockStairs(block3)) && (blockstairs$enumhalf == iblockstate4.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(BlockStairs.FACING);

				if ((enumfacing4 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					f3 = 0.5F;
					flag1 = false;
				}
				else if ((enumfacing4 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					f2 = 0.5F;
					flag1 = false;
				}
			}
		}

		setBlockBounds(f2, f, f4, f3, f1, f5);
		return flag1;
	}

	public boolean func_176304_i(IBlockAccess blockAccess, BlockPos pos)
	{
		IBlockState iblockstate = blockAccess.getBlockState(pos);
		EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockStairs.FACING);
		EnumHalf blockstairs$enumhalf = (EnumHalf)iblockstate.getValue(BlockStairs.HALF);
		boolean flag = blockstairs$enumhalf == EnumHalf.TOP;
		float f = 0.5F;
		float f1 = 1.0F;

		if (flag)
		{
			f = 0.0F;
			f1 = 0.5F;
		}

		float f2 = 0.0F;
		float f3 = 0.5F;
		float f4 = 0.5F;
		float f5 = 1.0F;
		boolean flag1 = false;

		if (enumfacing == EnumFacing.EAST)
		{
			IBlockState iblockstate1 = blockAccess.getBlockState(pos.west());
			Block block = iblockstate1.getBlock();

			if ((isBlockStairs(block)) && (blockstairs$enumhalf == iblockstate1.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing1 = (EnumFacing)iblockstate1.getValue(BlockStairs.FACING);

				if ((enumfacing1 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					f4 = 0.0F;
					f5 = 0.5F;
					flag1 = true;
				}
				else if ((enumfacing1 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					f4 = 0.5F;
					f5 = 1.0F;
					flag1 = true;
				}
			}
		}
		else if (enumfacing == EnumFacing.WEST)
		{
			IBlockState iblockstate2 = blockAccess.getBlockState(pos.east());
			Block block1 = iblockstate2.getBlock();

			if ((isBlockStairs(block1)) && (blockstairs$enumhalf == iblockstate2.getValue(BlockStairs.HALF)))
			{
				f2 = 0.5F;
				f3 = 1.0F;
				EnumFacing enumfacing2 = (EnumFacing)iblockstate2.getValue(BlockStairs.FACING);

				if ((enumfacing2 == EnumFacing.NORTH) && (!isSameStair(blockAccess, pos.north(), iblockstate)))
				{
					f4 = 0.0F;
					f5 = 0.5F;
					flag1 = true;
				}
				else if ((enumfacing2 == EnumFacing.SOUTH) && (!isSameStair(blockAccess, pos.south(), iblockstate)))
				{
					f4 = 0.5F;
					f5 = 1.0F;
					flag1 = true;
				}
			}
		}
		else if (enumfacing == EnumFacing.SOUTH)
		{
			IBlockState iblockstate3 = blockAccess.getBlockState(pos.north());
			Block block2 = iblockstate3.getBlock();

			if ((isBlockStairs(block2)) && (blockstairs$enumhalf == iblockstate3.getValue(BlockStairs.HALF)))
			{
				f4 = 0.0F;
				f5 = 0.5F;
				EnumFacing enumfacing3 = (EnumFacing)iblockstate3.getValue(BlockStairs.FACING);

				if ((enumfacing3 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					flag1 = true;
				}
				else if ((enumfacing3 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					f2 = 0.5F;
					f3 = 1.0F;
					flag1 = true;
				}
			}
		}
		else if (enumfacing == EnumFacing.NORTH)
		{
			IBlockState iblockstate4 = blockAccess.getBlockState(pos.south());
			Block block3 = iblockstate4.getBlock();

			if ((isBlockStairs(block3)) && (blockstairs$enumhalf == iblockstate4.getValue(BlockStairs.HALF)))
			{
				EnumFacing enumfacing4 = (EnumFacing)iblockstate4.getValue(BlockStairs.FACING);

				if ((enumfacing4 == EnumFacing.WEST) && (!isSameStair(blockAccess, pos.west(), iblockstate)))
				{
					flag1 = true;
				}
				else if ((enumfacing4 == EnumFacing.EAST) && (!isSameStair(blockAccess, pos.east(), iblockstate)))
				{
					f2 = 0.5F;
					f3 = 1.0F;
					flag1 = true;
				}
			}
		}

		if (flag1)
		{
			setBlockBounds(f2, f, f4, f3, f1, f5);
		}

		return flag1;
	}




	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn)
	{
		state = getActualState(state, worldIn, pos);

		for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state))
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
		}
	}

	private static List<AxisAlignedBB> getCollisionBoxList(IBlockState bstate)
	{
		List<AxisAlignedBB> list = Lists.newArrayList();
		boolean flag = bstate.getValue(BlockStairs.HALF) == EnumHalf.TOP;
		list.add(flag ? AABB_SLAB_TOP : AABB_SLAB_BOTTOM);
		EnumShape blockstairs$enumshape = (EnumShape)bstate.getValue(BlockStairs.SHAPE);

		if ((blockstairs$enumshape == EnumShape.STRAIGHT) || (blockstairs$enumshape == EnumShape.INNER_LEFT) || (blockstairs$enumshape == EnumShape.INNER_RIGHT))
		{
			list.add(getCollQuarterBlock(bstate));
		}

		if (blockstairs$enumshape != EnumShape.STRAIGHT)
		{
			list.add(getCollEighthBlock(bstate));
		}

		return list;
	}





	private static AxisAlignedBB getCollQuarterBlock(IBlockState bstate)
	{
		boolean flag = bstate.getValue(BlockStairs.HALF) == EnumHalf.TOP;

		switch (((EnumFacing)bstate.getValue(BlockStairs.FACING)).ordinal())
		{
		case 1: 
		default: 
			return flag ? AABB_QTR_BOT_NORTH : AABB_QTR_TOP_NORTH;
		case 2: 
			return flag ? AABB_QTR_BOT_SOUTH : AABB_QTR_TOP_SOUTH;
		case 3: 
			return flag ? AABB_QTR_BOT_WEST : AABB_QTR_TOP_WEST;
		}
	}







	private static AxisAlignedBB getCollEighthBlock(IBlockState bstate)
	{
		EnumFacing enumfacing = (EnumFacing)bstate.getValue(BlockStairs.FACING);
		EnumFacing enumfacing1;
		switch (((EnumShape)bstate.getValue(BlockStairs.SHAPE)).ordinal())
		{
		case 1: 
		default: 
			enumfacing1 = enumfacing;
			break;
		case 2: 
			enumfacing1 = enumfacing.rotateY();
			break;
		case 3: 
			enumfacing1 = enumfacing.getOpposite();
			break;
		case 4: 
			enumfacing1 = enumfacing.rotateYCCW();
		}

		boolean flag = bstate.getValue(BlockStairs.HALF) == EnumHalf.TOP;

		switch (enumfacing1.ordinal())
		{
		case 1: 
		default: 
			return flag ? AABB_OCT_BOT_NW : AABB_OCT_TOP_NW;
		case 2: 
			return flag ? AABB_OCT_BOT_SE : AABB_OCT_TOP_SE;
		case 3: 
			return flag ? AABB_OCT_BOT_SW : AABB_OCT_TOP_SW;
		}
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
	{
		this.modelBlock.onBlockClicked(worldIn, pos, playerIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand)
	{
		this.modelBlock.randomDisplayTick(state, worldIn, pos, rand);
	}




	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state)
	{
		this.modelBlock.onBlockDestroyedByPlayer(worldIn, pos, state);
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public int getMixedBrightnessForBlock(IBlockAccess worldIn, BlockPos pos)
	{
		return this.modelBlock.getMixedBrightnessForBlock(worldIn, pos);
	}*/




	@Override
	public float getExplosionResistance(Entity exploder)
	{
		return this.modelBlock.getExplosionResistance(exploder);
	}




	@Override
	public int tickRate(World worldIn)
	{
		return this.modelBlock.tickRate(worldIn);
	}

	@Override
	public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion)
	{
		return this.modelBlock.modifyAcceleration(worldIn, pos, entityIn, motion);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return this.modelBlock.getBlockLayer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
	{
		return this.modelBlock.getSelectedBoundingBox(state, worldIn, pos);
	}




	@Override
	public boolean isCollidable()
	{
		return this.modelBlock.isCollidable();
	}

	@Override
	public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
	{
		return this.modelBlock.canCollideCheck(state, hitIfLiquid);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		return this.modelBlock.canPlaceBlockAt(worldIn, pos);
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		onNeighborBlockChange(worldIn, pos, this.modelState, net.minecraft.init.Blocks.AIR);
		this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		this.modelBlock.breakBlock(worldIn, pos, this.modelState);
	}




	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		this.modelBlock.onEntityCollidedWithBlock(worldIn, pos, state, entityIn);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		this.modelBlock.updateTick(worldIn, pos, state, rand);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, net.minecraft.util.EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, hand, heldItem, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
	}




	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
	{
		this.modelBlock.onBlockDestroyedByExplosion(worldIn, pos, explosionIn);
	}




	@Override
	public MapColor getMapColor(IBlockState state)
	{
		return this.modelBlock.getMapColor(this.modelState);
	}





	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		IBlockState iblockstate = super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
		iblockstate = iblockstate.withProperty(BlockStairs.FACING, placer.getHorizontalFacing()).withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		return (facing != EnumFacing.DOWN) && ((facing == EnumFacing.UP) || (hitY <= 0.5D)) ? iblockstate.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM) : iblockstate.withProperty(BlockStairs.HALF, EnumHalf.TOP);
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
	{
		RayTraceResult[] amovingobjectposition = new RayTraceResult[8];
		IBlockState iblockstate = worldIn.getBlockState(pos);
		int i = ((EnumFacing)iblockstate.getValue(BlockStairs.FACING)).getHorizontalIndex();
		boolean flag = iblockstate.getValue(BlockStairs.HALF) == EnumHalf.TOP;
		int[] aint = field_150150_a[(i + 0)];
		this.hasRaytraced = true;

		for (int j = 0; j < 8; j++)
		{
			this.rayTracePass = j;

			if (Arrays.binarySearch(aint, j) < 0)
			{
				amovingobjectposition[j] = super.collisionRayTrace(state, worldIn, pos, start, end);
			}
		}

		for (int k : aint)
		{
			amovingobjectposition[k] = null;
		}

		RayTraceResult movingobjectposition1 = null;
		double d1 = 0.0D;

		for (RayTraceResult movingobjectposition : amovingobjectposition)
		{
			if (movingobjectposition != null)
			{
				double d0 = movingobjectposition.hitVec.squareDistanceTo(end);

				if (d0 > d1)
				{
					movingobjectposition1 = movingobjectposition;
					d1 = d0;
				}
			}
		}

		return movingobjectposition1;
	}




	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		IBlockState iblockstate = getDefaultState().withProperty(BlockStairs.HALF, (meta & 0x4) > 0 ? EnumHalf.TOP : EnumHalf.BOTTOM);
		iblockstate = iblockstate.withProperty(BlockStairs.FACING, EnumFacing.getFront(5 - (meta & 0x3)));
		return iblockstate;
	}




	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;

		if (state.getValue(BlockStairs.HALF) == EnumHalf.TOP)
		{
			i |= 0x4;
		}

		i |= 5 - ((EnumFacing)state.getValue(BlockStairs.FACING)).getIndex();
		return i;
	}





	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if (func_176306_h(worldIn, pos))
		{
			switch (func_176305_g(worldIn, pos))
			{
			case 0: 
				state = state.withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
				break;
			case 1: 
				state = state.withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
				break;
			case 2: 
				state = state.withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);

			}

		} else {
			switch (func_176307_f(worldIn, pos))
			{
			case 0: 
				state = state.withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
				break;
			case 1: 
				state = state.withProperty(BlockStairs.SHAPE, EnumShape.OUTER_RIGHT);
				break;
			case 2: 
				state = state.withProperty(BlockStairs.SHAPE, EnumShape.OUTER_LEFT);
			}

		}
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { BlockStairs.FACING, BlockStairs.HALF, BlockStairs.SHAPE });
	}
}
