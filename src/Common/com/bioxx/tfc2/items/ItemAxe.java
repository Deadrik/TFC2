package com.bioxx.tfc2.items;

import java.util.LinkedList;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.BlockSapling;
import com.google.common.collect.Sets;

public class ItemAxe extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER});

	public ItemAxe(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return EnumActionResult.FAIL;
		IBlockState state = worldIn.getBlockState(pos);
		if(state.getBlock() == TFCBlocks.Sapling)
			((BlockSapling)state.getBlock()).grow(worldIn, worldIn.rand, pos, state);

		return EnumActionResult.SUCCESS;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving)
	{
		if(worldIn.isRemote || !Core.isNaturalLog(state))
			return super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);

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
				state.getBlock().dropBlockAsItem(worldIn, scanPos, scanState, 0);
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
