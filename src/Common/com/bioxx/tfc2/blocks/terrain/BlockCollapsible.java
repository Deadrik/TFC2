package com.bioxx.tfc2.blocks.terrain;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.IGravityBlock;
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
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn)
	{
		world.scheduleUpdate(pos, this, tickRate(world));
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
			((IGravityBlock)getFallBlockType(state).getBlock()).onStartFalling(entityfallingblock);
			world.spawnEntityInWorld(entityfallingblock);
			onCreateFallingEntity(entityfallingblock, world, pos);
		}
		else
		{
			world.setBlockState(pos, getFallBlockType(state));
		}
	}

	protected void onCreateFallingEntity(EntityFallingBlockTFC entity, World world, BlockPos pos)
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
		float supportRange = getNaturalSupportRange(world, pos, state);
		float supportRangeSq = supportRange*supportRange;

		//If the block beneath this one is solid then we will assume for now that this block is naturally supported
		if(world.getBlockState(pos.down()).getBlock().isBlockSolid(world, pos.down(), EnumFacing.UP))
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

			//If we move horizontally more than X blocks then stop scanning any further away
			pos2 = new BlockPos(scanPos.getX()+0.5, 0, scanPos.getZ()+0.5);
			double dist = pos1.distanceSq(pos2);
			if(dist > supportRangeSq)
			{
				continue;
			}
			else if(canBeSupportedBy(state, scanState))
			{
				scanState2 = world.getBlockState(scanPos.down());
				if(scanState2.getBlock().isSideSolid(scanState2, world, scanPos.down(), EnumFacing.UP))
				{
					if(!scannedQueue.contains(scanPos.down()))
						scanQueue.add(scanPos.down());
				}
				else
				{
					if(recievesHorizontalSupport(scanState, world, scanPos, EnumFacing.NORTH) && !scannedQueue.contains(scanPos.north()))
						scanQueue.add(scanPos.north());
					if(recievesHorizontalSupport(scanState, world, scanPos, EnumFacing.SOUTH) && !scannedQueue.contains(scanPos.south()))
						scanQueue.add(scanPos.south());
					if(recievesHorizontalSupport(scanState, world, scanPos, EnumFacing.EAST) && !scannedQueue.contains(scanPos.east()))
						scanQueue.add(scanPos.east());
					if(recievesHorizontalSupport(scanState, world, scanPos,EnumFacing.WEST) && !scannedQueue.contains(scanPos.west()))
						scanQueue.add(scanPos.west());
				}
			}
		}

		return isSupported;
	}

	/**
	 * @return Can this block recieve support from the block in this direction. Usually false 
	 * if the neighboring block is not a full block or not a support block.
	 */
	public boolean recievesHorizontalSupport(IBlockState myState, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		BlockPos otherPos = pos.offset(facing);
		IBlockState otherState = world.getBlockState(otherPos);

		if(otherState.getBlock().isAir(otherState, world, otherPos) || !otherState.getBlock().isSideSolid(otherState, world, otherPos, facing.getOpposite()))
			return false;
		//Both this block and the one in the facing direction need to have solid sides touching
		if(!myState.getBlock().isSideSolid(myState, world, pos, facing))
			return false;
		return true;
	}



	public boolean canBeSupportedBy(IBlockState myState, IBlockState otherState)
	{
		if(otherState.getBlock() instanceof BlockCollapsible)
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

	/***
	 * Built this Custom List because BlockPos.equals as used in generic list types
	 * did not seem to be properly comparing BlockPos Objects.
	 * @author Bioxx
	 *
	 */
	private static class BlockPosList extends AbstractList<BlockPos> {

		private static final int INITIAL_CAPACITY = 16;
		private BlockPos[] a;
		private int size = 0;
		BlockPosList() {
			a = new BlockPos[INITIAL_CAPACITY];
		}

		@Override
		public BlockPos get(int index) {
			return a[index];
		}

		@Override
		public BlockPos set(int index, BlockPos element) {
			BlockPos oldValue = a[index];
			a[index] = element;
			return oldValue;
		}

		@Override
		public boolean add(BlockPos e) {
			if (size == a.length) {
				ensureCapacity(); //increase current capacity of list, make it double.
			} 
			a[size++] = e;
			return true;
		}

		private void ensureCapacity() 
		{
			int newIncreasedCapacity = a.length * 2;
			a = Arrays.copyOf(a, newIncreasedCapacity);
		}

		@Override
		public int size() {
			return a.length;
		}

		@Override
		public boolean contains(Object obj)
		{
			BlockPos compare = (BlockPos)obj;
			for(int i = 0; i < size; i++)
			{
				if(a[i] != null && a[i].getX() == compare.getX() && a[i].getY() == compare.getY() && a[i].getZ() == compare.getZ())
					return true;
			}
			return false;
		}
	}


}
