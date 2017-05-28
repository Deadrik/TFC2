package com.bioxx.tfc2.blocks.terrain;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.interfaces.IGravityBlock;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.util.BlockPosList;
import com.bioxx.tfc2.blocks.BlockTerra;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockCollapsible extends BlockTerra
{
	protected int scanDepth = 20;
	protected boolean compressionBreak = false;

	protected CollapsibleType collapseType = CollapsibleType.Structure;

	public BlockCollapsible(Material m, PropertyHelper p)
	{
		super(m, p);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		world.scheduleUpdate(pos, this, tickRate(world));
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		((World)worldIn).scheduleUpdate(pos, this, tickRate((World)worldIn));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		doCollapse(world, pos, state);
	}

	protected void doCollapse(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote)
		{
			state = state.getBlock().getActualState(state, world, pos);
			//If this block is considered natural then it uses the support scan for natural blocks
			if(collapseType == CollapsibleType.Nature && !hasSupport(world, pos, state))
			{
				createFallingEntity(world, pos, state);
				scheduleNeighbors(world, pos);
				return;
			}
			if(collapseType == CollapsibleType.Structure && !hasSupport(world, pos, state))
			{
				createFallingEntity(world, pos, state);
				scheduleNeighbors(world, pos);
				return;
			}
		}
	}

	public void createFallingEntity(World world, BlockPos pos, IBlockState state)
	{
		IBlockState stateNew = getFallBlockType(state);

		if(stateNew.getBlock() instanceof IGravityBlock)
		{
			world.setBlockToAir(pos);
			EntityFallingBlockTFC entityfallingblock = new EntityFallingBlockTFC(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, stateNew);
			entityfallingblock.shouldDropItem = false;
			((IGravityBlock)getFallBlockType(state).getBlock()).onStartFalling(entityfallingblock);
			world.spawnEntity(entityfallingblock);
			onCreateFallingEntity(entityfallingblock, state, world, pos);

		}
		else
		{
			world.setBlockState(pos, getFallBlockType(state));
		}
	}

	protected void onCreateFallingEntity(EntityFallingBlockTFC entity, IBlockState state, World world, BlockPos pos)
	{

	}

	protected void scheduleNeighbors(World world, BlockPos pos)
	{
		for(int x = -1; x <= 1; x++)
		{
			for(int z = -1; z <= 1; z++)
			{
				for(int y = 0; y <= 2; y++)
				{
					world.scheduleUpdate(pos.add(x, y, z), world.getBlockState(pos.add(x, y, z)).getBlock(), tickRate(world));
				}
			}
		}
	}

	protected boolean hasSupport(World world, BlockPos pos, IBlockState state)
	{
		IBlockState scanState, scanState2;
		Block scanBlock;
		BlockPos scanPos;
		BlockPos pos1 = new BlockPos(pos.getX()+0.5, 0, pos.getZ()+0.5), pos2;

		boolean isSupported = false;
		float supportRangeDefault = getNaturalSupportRange(world, pos, state);
		float supportRangeSqDefault = supportRangeDefault*supportRangeDefault;

		//If the block beneath this one is solid then we will assume for now that this block is naturally supported
		IBlockState stateDown = world.getBlockState(pos.down());
		if(this.canSupportFacing(state, world, pos, EnumFacing.DOWN) && 
				((stateDown.getBlock() instanceof BlockCollapsible && ((BlockCollapsible)stateDown.getBlock()).canSupportFacing(stateDown, world, pos.down(), EnumFacing.UP)) || 
						stateDown.getBlock().isBlockSolid(world, pos.down(), EnumFacing.UP)))
			return true;
		//If we fail the first case scenario then we will begin a recursive scan downward for each of the blocks surrounding this block

		Queue<BlockPos> scanQueue = new LinkedList<BlockPos>();
		BlockPosList scannedQueue = new BlockPosList();
		scannedQueue.add(pos);
		if(recievesHorizontalSupport(state, world, pos, EnumFacing.NORTH))
			scanQueue.add(pos.north());
		if(recievesHorizontalSupport(state, world, pos, EnumFacing.SOUTH))
			scanQueue.add(pos.south());
		if(recievesHorizontalSupport(state, world, pos, EnumFacing.EAST))
			scanQueue.add(pos.east());
		if(recievesHorizontalSupport(state, world, pos, EnumFacing.WEST))
			scanQueue.add(pos.west());


		while(scanQueue.peek() != null)
		{
			scanPos = scanQueue.poll();
			scanState = world.getBlockState(scanPos);
			scanBlock = scanState.getBlock();
			scanState = scanBlock.getActualState(scanState, world, scanPos);
			scannedQueue.add(scanPos);
			//If we've scanned down X blocks and we're still scanning then just assume that we're supported so we do not waste time
			if(pos.subtract(scanPos).getY() > scanDepth)
			{
				isSupported = true; 
				break;
			}


			if(scanState.getBlock() instanceof BlockCollapsible/* && canBeSupportedBy(state, scanState)*/)
			{
				float supportRange = supportRangeDefault;
				float supportRangeSq = supportRangeSqDefault;
				if((!Core.isTerrain(state) && !Core.isTerrain(scanState)) || (Core.isTerrain(state) && Core.isTerrain(scanState)))
				{
					supportRange = Math.min(supportRange, ((BlockCollapsible)scanState.getBlock()).getNaturalSupportRange(world, pos, scanState));
					supportRangeSq = supportRange*supportRange;
				}

				//If we move horizontally more than X blocks then stop scanning any further away
				pos2 = new BlockPos(scanPos.getX()+0.5, 0, scanPos.getZ()+0.5);
				double dist = pos1.distanceSq(pos2);
				if(dist > supportRangeSq)
				{
					continue;
				}

				scanState2 = world.getBlockState(scanPos.down());
				if(scanState.getBlock().isSideSolid(scanState, world, scanPos, EnumFacing.DOWN) && 
						canBeSupportedBy(scanState, scanState2) && scanState2.getBlock().isSideSolid(scanState2, world, scanPos.down(), EnumFacing.UP))
				{
					if(!scannedQueue.contains(scanPos.down()))
						scanQueue.add(scanPos.down());
				}
				if(scanState.getBlock() instanceof BlockCollapsible)
					if(((BlockCollapsible)scanState.getBlock()).recievesHorizontalSupport(scanState, world, scanPos, EnumFacing.NORTH) && notCurrentlyScanning((List<BlockPos>) scanQueue, scannedQueue, scanPos.north()))
						scanQueue.add(scanPos.north());
				if(scanState.getBlock() instanceof BlockCollapsible)
					if(((BlockCollapsible)scanState.getBlock()).recievesHorizontalSupport(scanState, world, scanPos, EnumFacing.SOUTH) && notCurrentlyScanning((List<BlockPos>) scanQueue, scannedQueue, scanPos.south()))
						scanQueue.add(scanPos.south());
				if(scanState.getBlock() instanceof BlockCollapsible)
					if(((BlockCollapsible)scanState.getBlock()).recievesHorizontalSupport(scanState, world, scanPos, EnumFacing.EAST) && notCurrentlyScanning((List<BlockPos>) scanQueue, scannedQueue, scanPos.east()))
						scanQueue.add(scanPos.east());
				if(scanState.getBlock() instanceof BlockCollapsible)
					if(((BlockCollapsible)scanState.getBlock()).recievesHorizontalSupport(scanState, world, scanPos,EnumFacing.WEST) && notCurrentlyScanning((List<BlockPos>) scanQueue, scannedQueue, scanPos.west()))
						scanQueue.add(scanPos.west());
			}
		}

		return isSupported;
	}

	private boolean notCurrentlyScanning(List<BlockPos> scanQueue, List<BlockPos> scannedQueue, BlockPos pos)
	{
		if(scanQueue.contains(pos) || scannedQueue.contains(pos))
			return false;
		return true;
	}

	/**
	 * @return Can this block recieve support from the block in this direction. Usually false 
	 * if the neighboring block is not a full block or not a support block.
	 */
	public boolean recievesHorizontalSupport(IBlockState myState, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		BlockPos otherPos = pos.offset(facing);
		IBlockState otherState = world.getBlockState(otherPos);

		if(otherState.getBlock().isAir(otherState, world, otherPos))
			return false;

		//Both this block and the one in the facing direction need to be able to support each other
		if(!((BlockCollapsible) myState.getBlock()).canSupportFacing(myState, world, pos, facing))
			return false;

		if(otherState.getBlock() instanceof BlockCollapsible) 
		{
			return ((BlockCollapsible) otherState.getBlock()).canSupportFacing(otherState, world, otherPos, facing.getOpposite());
		}
		else return otherState.getBlock().isSideSolid(otherState, world, otherPos, facing.getOpposite());
	}

	/**
	 * @return Can this block attach to other blocks for support checks in this direction
	 */
	public boolean canSupportFacing(IBlockState myState, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		return myState.getBlock().isSideSolid(myState, world, pos, facing);
	}

	public boolean canBeSupportedBy(IBlockState myState, IBlockState otherState)
	{
		if(otherState.getBlock() == this || Core.isTerrain(otherState) || otherState.getBlock() instanceof ISupportBlock || otherState.getBlock() == Blocks.BEDROCK)
			return true;
		return false;
	}

	/**
	 * @return What blockstate should this block turn into if it collapses
	 */
	public IBlockState getFallBlockType(IBlockState myState)
	{
		return myState;
	}

	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos, IBlockState myState)
	{
		return 5;
	}

	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/


	/**
	 * 
	 * @author Bioxx
	 *
	 */
	public static enum CollapsibleType
	{
		Nature,
		Structure;
	}


}
