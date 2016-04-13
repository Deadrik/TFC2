package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.RandomCollection;
import com.bioxx.tfc2.api.interfaces.IGravityBlock;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockGravity extends BlockTerra implements IGravityBlock
{

	public BlockGravity(Material m, PropertyHelper p)
	{
		super(m, p);	
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (!worldIn.isRemote)
		{
			checkFallable(worldIn, pos, state);
		}
	}

	private void checkFallable(World worldIn, BlockPos pos, IBlockState state)
	{
		BlockPos slidePos = slideScan(worldIn, pos);
		if ((canFallInto(worldIn, pos.down())) && (pos.getY() >= 0))
		{
			fall(worldIn, pos, state);
		}
		else if(slidePos != null && (slidePos.getY() >= 0))
		{
			worldIn.setBlockToAir(pos);
			fall(worldIn, slidePos, state);
		}
	}

	private void fall(World worldIn, BlockPos pos, IBlockState state)
	{
		int i = 32;

		if ((!BlockFalling.fallInstantly) && (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))))
		{
			if (!worldIn.isRemote)
			{
				EntityFallingBlockTFC entityfallingblock = new EntityFallingBlockTFC(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, state);
				onStartFalling(entityfallingblock);
				worldIn.spawnEntityInWorld(entityfallingblock);
			}
		}
		else
		{
			worldIn.setBlockToAir(pos);

			BlockPos blockpos;
			for (blockpos = pos.down(); (canFallInto(worldIn, blockpos)) && (blockpos.getY() > 0); blockpos = blockpos.down()) {}

			if (blockpos.getY() > 0)
			{
				worldIn.setBlockState(blockpos.up(), getDefaultState());
			}
		}
	}

	@Override
	public void onStartFalling(EntityFallingBlockTFC fallingEntity) {}


	@Override
	public int tickRate(World worldIn)
	{
		return 5;
	}

	@Override
	public boolean canFallInto(World worldIn, BlockPos pos)
	{
		if (worldIn.isAirBlock(pos)) return true;
		Block block = worldIn.getBlockState(pos).getBlock();
		Material material = block.getMaterial(worldIn.getBlockState(pos));
		return (block == Blocks.FIRE) || (material == Material.AIR) || (material == Material.WATER) || (material == Material.LAVA) || block.isReplaceable(worldIn, pos);
	}

	@Override
	public void onEndFalling(World worldIn, BlockPos pos) {}

	/**
	 * @return Minimum cliffheight required for this block to slide down to lower elevation. -1 disables sliding
	 */
	@Override
	public int getSlideHeight()
	{
		return -1;
	}

	/**
	 * @return Chance that a block will slide [0.0 - 1.0]
	 */
	@Override
	public float getSlideChance()
	{
		return 0f;
	}

	protected BlockPos slideScan(World world, BlockPos pos)
	{
		if(pos.getY() == 0)
			return null;
		if(world.rand.nextFloat() < 1 - getSlideChance())
			return null;

		if(getSlideHeight() == -1)
			return null;
		else
		{
			RandomCollection<BlockPos> pot = new RandomCollection<BlockPos>();
			if(!world.isSideSolid(pos.east(), EnumFacing.WEST) && depthScan(world, pos.east()) >= getSlideHeight())
				pot.add(1.0, pos.east());
			if(!world.isSideSolid(pos.west(), EnumFacing.EAST) && depthScan(world, pos.west()) >= getSlideHeight())
				pot.add(1.0, pos.west());
			if(!world.isSideSolid(pos.north(), EnumFacing.SOUTH) && depthScan(world, pos.north()) >= getSlideHeight())
				pot.add(1.0, pos.north());
			if(!world.isSideSolid(pos.south(), EnumFacing.NORTH) && depthScan(world, pos.south()) >= getSlideHeight())
				pot.add(1.0, pos.south());

			if(pot.size() > 0)
				return pot.next();
		}

		return null;
	}

	private int depthScan(World world, BlockPos pos)
	{
		BlockPos scanPos;
		for(int i = 1; i < 255;i++)
		{
			scanPos = pos.down(i);
			if(!canFallInto(world, scanPos))
				return i-1;
		}
		return 0;
	}
}
