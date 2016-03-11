package com.bioxx.tfc2.blocks.terrain;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.bioxx.tfc2.blocks.BlockGravity;
import com.bioxx.tfc2.blocks.BlockTerra;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockCollapsable extends BlockTerra
{

	public BlockCollapsable(Material m, PropertyHelper p)
	{
		super(m, p);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		world.scheduleUpdate(pos, this, tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (!world.isRemote)
		{
			//TODO: Check if this block is supported. If its not then collpase this block.
			if(!hasNaturalSupport(world, pos, state))
			{
				IBlockState stateNew = getFallBlockType(state);
				if(stateNew.getBlock() instanceof BlockGravity)
				{
					world.setBlockToAir(pos);
					EntityFallingBlockTFC entityfallingblock = new EntityFallingBlockTFC(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, stateNew);
					((BlockGravity)getFallBlockType(state).getBlock()).onStartFalling(entityfallingblock);
					world.spawnEntityInWorld(entityfallingblock);
				}
				else
				{
					world.setBlockState(pos, getFallBlockType(state));
				}
			}
		}
	}

	private boolean hasNaturalSupport(World world, BlockPos pos, IBlockState state)
	{
		IBlockState scanState;
		Block scanBlock;
		BlockPos scanPos;
		boolean isSupported = false;
		int supportRange = getNaturalSupportRange(state);

		//If the block beneath this one is solid then we will assume for now that this block is naturally supported
		if(world.getBlockState(pos.down()).getBlock().isBlockSolid(world, pos.down(), EnumFacing.UP))
			return true;
		//If we fail the first case scenario then we will begin a recursive scan downward for each of the blocks surrounding this block

		Queue<BlockPos> scanQueue = new LinkedList<BlockPos>();
		BlockPosList scannedQueue = new BlockPosList();
		scannedQueue.add(pos);
		scanQueue.add(pos.north());
		scanQueue.add(pos.south());
		scanQueue.add(pos.east());
		scanQueue.add(pos.west());


		while(scanQueue.peek() != null)
		{
			scanPos = scanQueue.poll();
			scanState = world.getBlockState(scanPos);
			scanBlock = scanState.getBlock();
			scannedQueue.add(scanPos);
			//If we've scanned down 20 blocks and we're still scanning then just assume that we're supported so we do not waste time
			if(pos.subtract(scanPos).getY() > 20){isSupported = true; break;}

			//If we move horizontally more than X blocks then stop scanning any further away
			if(scanPos.subtract(pos).getX() > supportRange || scanPos.subtract(pos).getZ() > supportRange || 
					scanPos.subtract(pos).getX() < -supportRange || scanPos.subtract(pos).getZ() < -supportRange)
			{
				continue;
			}
			else if(canSupport(state, scanState))
			{
				if(canSupport(state,world.getBlockState(scanPos.down())))
				{
					if(!scannedQueue.contains(scanPos.down()))
						scanQueue.add(scanPos.down());
				}
				else
				{
					if(!scannedQueue.contains(scanPos.north()))
						scanQueue.add(scanPos.north());
					if(!scannedQueue.contains(scanPos.south()))
						scanQueue.add(scanPos.south());
					if(!scannedQueue.contains(scanPos.east()))
						scanQueue.add(scanPos.east());
					if(!scannedQueue.contains(scanPos.west()))
						scanQueue.add(scanPos.west());
				}
			}
		}

		return isSupported;
	}

	public boolean canSupport(IBlockState myState, IBlockState otherState)
	{
		if(otherState.getBlock() instanceof BlockCollapsable)
			return true;
		return false;
	}

	public IBlockState getFallBlockType(IBlockState myState)
	{
		return myState;
	}

	public int getNaturalSupportRange(IBlockState myState)
	{
		return 5;
	}

	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/


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
