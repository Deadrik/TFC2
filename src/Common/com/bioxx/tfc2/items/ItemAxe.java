package com.bioxx.tfc2.items;

import java.util.LinkedList;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.BlockSapling;
import com.google.common.collect.Sets;

public class ItemAxe extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin, Blocks.melon_block, Blocks.ladder});

	public ItemAxe(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return false;
		IBlockState state = worldIn.getBlockState(pos);
		if(state.getBlock() == TFCBlocks.Sapling)
			((BlockSapling)state.getBlock()).grow(worldIn, worldIn.rand, pos, state);

		return true;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn)
	{
		if(worldIn.isRemote || !Core.isNaturalLog(blockIn.getDefaultState()))
			return super.onBlockDestroyed(stack, worldIn, blockIn, pos, playerIn);

		LinkedList<BlockPos> queue = new LinkedList<BlockPos>();
		queue.add(pos);

		BlockPos scanPos;
		IBlockState scanState;
		while(!queue.isEmpty())
		{
			scanPos = queue.pop();
			scanState = worldIn.getBlockState(scanPos);
			if(Core.isNaturalLog(scanState))
			{
				blockIn.dropBlockAsItem(worldIn, scanPos, scanState, 0);
				worldIn.setBlockToAir(scanPos);
				queue.add(scanPos.north());
				queue.add(scanPos.north().east());
				queue.add(scanPos.north().west());
				queue.add(scanPos.south());
				queue.add(scanPos.south().east());
				queue.add(scanPos.south().west());
				queue.add(scanPos.east());
				queue.add(scanPos.west());

				queue.add(scanPos.up());
				queue.add(scanPos.up().north());
				queue.add(scanPos.up().north().east());
				queue.add(scanPos.up().north().west());
				queue.add(scanPos.up().south());
				queue.add(scanPos.up().south().east());
				queue.add(scanPos.up().south().west());
				queue.add(scanPos.up().east());
				queue.add(scanPos.up().west());
			}
		}


		return false;
	}

}
