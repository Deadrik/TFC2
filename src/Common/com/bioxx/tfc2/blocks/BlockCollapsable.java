package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockCollapsable extends BlockTerra 
{

	public BlockCollapsable(Material m, PropertyHelper p)
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
			checkFallable(worldIn, pos);
		}
	}

	private void checkFallable(World worldIn, BlockPos pos)
	{
		if ((canFallInto(worldIn, pos.down())) && (pos.getY() >= 0))
		{
			int i = 32;

			if ((!BlockFalling.fallInstantly) && (worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))))
			{
				if (!worldIn.isRemote)
				{
					EntityFallingBlockTFC entityfallingblock = new EntityFallingBlockTFC(worldIn, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, worldIn.getBlockState(pos));
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
	}

	protected void onStartFalling(EntityFallingBlockTFC fallingEntity) {}


	@Override
	public int tickRate(World worldIn)
	{
		return 2;
	}

	public static boolean canFallInto(World worldIn, BlockPos pos)
	{
		if (worldIn.isAirBlock(pos)) return true;
		Block block = worldIn.getBlockState(pos).getBlock();
		Material material = block.getMaterial();
		return (block == Blocks.fire) || (material == Material.air) || (material == Material.water) || (material == Material.lava) || block.isReplaceable(worldIn, pos);
	}

	public void onEndFalling(World worldIn, BlockPos pos) {}
}
