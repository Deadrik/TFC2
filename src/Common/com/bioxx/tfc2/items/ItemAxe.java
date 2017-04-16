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
import com.bioxx.tfc2.blocks.BlockSapling2;
import com.bioxx.tfc2.core.TFCTabs;
import com.google.common.collect.Sets;

public class ItemAxe extends ItemTerraTool 
{
	private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, TFCBlocks.LogNatural, TFCBlocks.LogNatural2, TFCBlocks.LogNaturalPalm});

	public ItemAxe(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
		this.damageVsEntity = 1;
		this.attackSpeed = -3.2f;
		this.setCreativeTab(TFCTabs.TFCTools);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		//ItemStack stack = playerIn.getHeldItem(hand);
		if(worldIn.isRemote || !playerIn.capabilities.isCreativeMode)
			return EnumActionResult.FAIL;
		IBlockState state = worldIn.getBlockState(pos);
		if(playerIn.isCreative() && state.getBlock() == TFCBlocks.Sapling)
			((BlockSapling)state.getBlock()).grow(worldIn, worldIn.rand, pos, state);
		if(playerIn.isCreative() && state.getBlock() == TFCBlocks.Sapling2)
			((BlockSapling2)state.getBlock()).grow(worldIn, worldIn.rand, pos, state);

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
		int count = 0;
		while(!queue.isEmpty())
		{
			scanPos = queue.pop();
			scanState = worldIn.getBlockState(scanPos);
			if(Core.isNaturalLog(scanState) || scanState.getBlock() == TFCBlocks.Leaves2) //Leaves2 is needed for the palm top (maybe there is a better way to do this?)
			{
				scanState.getBlock().dropBlockAsItem(worldIn, scanPos, scanState, 0);
				worldIn.setBlockToAir(scanPos);
				count++;
				Iterable<BlockPos> list = BlockPos.getAllInBox(scanPos.add(-1, 0, -1), scanPos.add(1, 1, 1));
				for(BlockPos p : list)
				{
					queue.add(p);
				}
			}
		}

		if(count > 0)
			stack.damageItem(count, entityLiving);

		return false;
	}

}
