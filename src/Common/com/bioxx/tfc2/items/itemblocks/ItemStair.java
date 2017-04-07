package com.bioxx.tfc2.items.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.INeedOffset;

public class ItemStair extends ItemTerraBlock
{
	public ItemStair(Block b)
	{
		super(b);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);

		if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsAcacia))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.acacia"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsAsh))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.ash"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsAspen))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.aspen"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsBirch))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.birch"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsChestnut))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.chestnut"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsDouglasFir))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.douglas_fir"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsHickory))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.hickory"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsMaple))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.maple"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsOak))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.oak"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsPine))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.pine"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsSequoia))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.sequoia"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsSpruce))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.spruce"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsSycamore))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.sycamore"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsWhiteCedar))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.white_cedar"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsWillow))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.willow"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsKapok))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.kapok"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsRosewood))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.rosewood"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsBlackwood))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.blackwood"));
		else if (is.getItem() == Item.getItemFromBlock(TFCBlocks.StairsPalm))
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.palm"));
		else
			arraylist.add(TextFormatting.DARK_RED + Core.translate("global.unknown"));
	}

	@Override
	public int getMetadata(int i)
	{
		if(block instanceof INeedOffset)
			return((INeedOffset)block).convertMetaToBlock(i);
		return i;
	}
}
