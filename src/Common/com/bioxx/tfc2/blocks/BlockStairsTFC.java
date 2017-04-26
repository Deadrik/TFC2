package com.bioxx.tfc2.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;
import com.bioxx.tfc2.core.TFCTabs;
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
		this.setCreativeTab(TFCTabs.TFCBuilding);
	}

	@Override
	public void createFallingEntity(World world, BlockPos pos, IBlockState state)
	{
		/*world.setBlockToAir(pos);
		EntityItem ei = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.STICK, 1+world.rand.nextInt(3)));
		world.spawnEntity(ei);*/
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

	/**
	 * @return Can this block attach to other blocks for support checks in this direction
	 */
	@Override
	public boolean canSupportFacing(IBlockState myState, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		EnumFacing f = (EnumFacing)myState.getValue(BlockStairs.FACING);
		if(facing == f.rotateY() || facing == f.rotateYCCW())
			return true;

		return myState.getBlock().isSideSolid(myState, world, pos, facing);
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
		if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling)
			return super.doesSideBlockRendering(state, world, pos, face);

		if ( state.isOpaqueCube() )
			return true;

		state = this.getActualState(state, world, pos);

		EnumHalf half = state.getValue(BlockStairs.HALF);
		EnumFacing side = state.getValue(BlockStairs.FACING);
		EnumShape shape = state.getValue(BlockStairs.SHAPE);
		if (face == EnumFacing.UP) return half == EnumHalf.TOP;
		if (face == EnumFacing.DOWN) return half == EnumHalf.BOTTOM;
		if (shape == EnumShape.OUTER_LEFT || shape == EnumShape.OUTER_RIGHT) return false;
		if (face == side) return true;
		if (shape == EnumShape.INNER_LEFT && face.rotateY() == side) return true;
		if (shape == EnumShape.INNER_RIGHT && face.rotateYCCW() == side) return true;
		return false;
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

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_)
	{
		state = this.getActualState(state, worldIn, pos);

		for (AxisAlignedBB axisalignedbb : getCollisionBoxList(state))
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, axisalignedbb);
		}
	}

	private static List<AxisAlignedBB> getCollisionBoxList(IBlockState bstate)
	{
		List<AxisAlignedBB> list = Lists.<AxisAlignedBB>newArrayList();
		boolean flag = bstate.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;
		list.add(flag ? AABB_SLAB_TOP : AABB_SLAB_BOTTOM);
		BlockStairs.EnumShape blockstairs$enumshape = (BlockStairs.EnumShape)bstate.getValue(BlockStairs.SHAPE);

		if (blockstairs$enumshape == BlockStairs.EnumShape.STRAIGHT || blockstairs$enumshape == BlockStairs.EnumShape.INNER_LEFT || blockstairs$enumshape == BlockStairs.EnumShape.INNER_RIGHT)
		{
			list.add(getCollQuarterBlock(bstate));
		}

		if (blockstairs$enumshape != BlockStairs.EnumShape.STRAIGHT)
		{
			list.add(getCollEighthBlock(bstate));
		}

		return list;
	}

	private static AxisAlignedBB getCollQuarterBlock(IBlockState bstate)
	{
		boolean flag = bstate.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;

		switch ((EnumFacing)bstate.getValue(BlockStairs.FACING))
		{
		case NORTH:
		default:
			return flag ? AABB_QTR_BOT_NORTH : AABB_QTR_TOP_NORTH;
		case SOUTH:
			return flag ? AABB_QTR_BOT_SOUTH : AABB_QTR_TOP_SOUTH;
		case WEST:
			return flag ? AABB_QTR_BOT_WEST : AABB_QTR_TOP_WEST;
		case EAST:
			return flag ? AABB_QTR_BOT_EAST : AABB_QTR_TOP_EAST;
		}
	}


	/**
	 * Returns a bounding box representing an eighth of a block (a block whose three dimensions are halved).
	 * Used in all stair shapes except STRAIGHT (gets added alone in the case of OUTER; alone with a quarter block in
	 * case of INSIDE).
	 */
	private static AxisAlignedBB getCollEighthBlock(IBlockState bstate)
	{
		EnumFacing enumfacing = (EnumFacing)bstate.getValue(BlockStairs.FACING);
		EnumFacing enumfacing1;

		switch ((BlockStairs.EnumShape)bstate.getValue(BlockStairs.SHAPE))
		{
		case OUTER_LEFT:
		default:
			enumfacing1 = enumfacing;
			break;
		case OUTER_RIGHT:
			enumfacing1 = enumfacing.rotateY();
			break;
		case INNER_RIGHT:
			enumfacing1 = enumfacing.getOpposite();
			break;
		case INNER_LEFT:
			enumfacing1 = enumfacing.rotateYCCW();
		}

		boolean flag = bstate.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP;

		switch (enumfacing1)
		{
		case NORTH:
		default:
			return flag ? AABB_OCT_BOT_NW : AABB_OCT_TOP_NW;
		case SOUTH:
			return flag ? AABB_OCT_BOT_SE : AABB_OCT_TOP_SE;
		case WEST:
			return flag ? AABB_OCT_BOT_SW : AABB_OCT_TOP_SW;
		case EAST:
			return flag ? AABB_OCT_BOT_NE : AABB_OCT_TOP_NE;
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
		//this.modelState.neighborChanged(worldIn, pos, Blocks.AIR, pos);
		//this.modelBlock.onBlockAdded(worldIn, pos, this.modelState);
		worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
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
		super.updateTick(worldIn, pos, state, rand);
		//this.modelBlock.updateTick(worldIn, pos, state, rand);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return this.modelBlock.onBlockActivated(worldIn, pos, this.modelState, playerIn, hand, EnumFacing.DOWN, 0.0F, 0.0F, 0.0F);
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
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		IBlockState iblockstate = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
		iblockstate = iblockstate.withProperty(BlockStairs.FACING, placer.getHorizontalFacing()).withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		return (facing != EnumFacing.DOWN) && ((facing == EnumFacing.UP) || (hitY <= 0.5D)) ? iblockstate.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM) : iblockstate.withProperty(BlockStairs.HALF, EnumHalf.TOP);
	}

	@Override
	public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
	{
		List<RayTraceResult> list = Lists.<RayTraceResult>newArrayList();

		for (AxisAlignedBB axisalignedbb : getCollisionBoxList(this.getActualState(blockState, worldIn, pos)))
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

	private static BlockStairs.EnumShape func_185706_d(IBlockState p_185706_0_, IBlockAccess p_185706_1_, BlockPos p_185706_2_)
	{
		EnumFacing enumfacing = (EnumFacing)p_185706_0_.getValue(BlockStairs.FACING);
		IBlockState iblockstate = p_185706_1_.getBlockState(p_185706_2_.offset(enumfacing));

		if (isBlockStairs(iblockstate) && p_185706_0_.getValue(BlockStairs.HALF) == iblockstate.getValue(BlockStairs.HALF))
		{
			EnumFacing enumfacing1 = (EnumFacing)iblockstate.getValue(BlockStairs.FACING);

			if (enumfacing1.getAxis() != ((EnumFacing)p_185706_0_.getValue(BlockStairs.FACING)).getAxis() && isDifferentStairs(p_185706_0_, p_185706_1_, p_185706_2_, enumfacing1.getOpposite()))
			{
				if (enumfacing1 == enumfacing.rotateYCCW())
				{
					return BlockStairs.EnumShape.OUTER_LEFT;
				}

				return BlockStairs.EnumShape.OUTER_RIGHT;
			}
		}

		IBlockState iblockstate1 = p_185706_1_.getBlockState(p_185706_2_.offset(enumfacing.getOpposite()));

		if (isBlockStairs(iblockstate1) && p_185706_0_.getValue(BlockStairs.HALF) == iblockstate1.getValue(BlockStairs.HALF))
		{
			EnumFacing enumfacing2 = (EnumFacing)iblockstate1.getValue(BlockStairs.FACING);

			if (enumfacing2.getAxis() != ((EnumFacing)p_185706_0_.getValue(BlockStairs.FACING)).getAxis() && isDifferentStairs(p_185706_0_, p_185706_1_, p_185706_2_, enumfacing2))
			{
				if (enumfacing2 == enumfacing.rotateYCCW())
				{
					return BlockStairs.EnumShape.INNER_LEFT;
				}

				return BlockStairs.EnumShape.INNER_RIGHT;
			}
		}

		return BlockStairs.EnumShape.STRAIGHT;
	}

	private static boolean isDifferentStairs(IBlockState p_185704_0_, IBlockAccess p_185704_1_, BlockPos p_185704_2_, EnumFacing p_185704_3_)
	{
		IBlockState iblockstate = p_185704_1_.getBlockState(p_185704_2_.offset(p_185704_3_));
		return !isBlockStairs(iblockstate) || iblockstate.getValue(BlockStairs.FACING) != p_185704_0_.getValue(BlockStairs.FACING) || iblockstate.getValue(BlockStairs.HALF) != p_185704_0_.getValue(BlockStairs.HALF);
	}

	public static boolean isBlockStairs(IBlockState p_185709_0_)
	{
		return p_185709_0_.getBlock() instanceof BlockStairs || p_185709_0_.getBlock() instanceof BlockStairsTFC;
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
		return state.withProperty(BlockStairs.SHAPE, func_185706_d(state, worldIn, pos));
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { BlockStairs.FACING, BlockStairs.HALF, BlockStairs.SHAPE });
	}
}
