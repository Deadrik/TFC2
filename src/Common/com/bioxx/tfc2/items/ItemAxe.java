package com.bioxx.tfc2.items;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	public int maxTreeSize = 0;

	public ItemAxe(ToolMaterial mat, int maxCutSize)
	{
		super(mat, EFFECTIVE_ON);
		this.damageVsEntity = 1;
		this.attackSpeed = -3.2f;
		this.setCreativeTab(TFCTabs.TFCTools);
		this.maxTreeSize = maxCutSize;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		arraylist.add(Core.translate("gui.axe.maxcutsize") + ": " + maxTreeSize);
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
	public int getMaxDamage(ItemStack stack)
	{
		return this.getMaxDamage() - (stack.hasTagCompound() ? stack.getTagCompound().getInteger("reducedDur") : 0);
	}

	@Override
	public ItemStack onRepair(ItemStack is)
	{
		if(!is.hasTagCompound())
		{
			is.setTagCompound(new NBTTagCompound());
		}
		is.getTagCompound().setInteger("reducedDur", Math.max((is.hasTagCompound() ? is.getTagCompound().getInteger("reducedDur") : 0)*2, 1));
		return is;
	}
}
