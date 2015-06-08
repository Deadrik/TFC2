package com.bioxx.tfc2.Items.ItemBlocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Global;

public class ItemStone extends ItemTerraBlock
{
	public ItemStone(Block b)
	{
		super(b);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);

		if (is.getItemDamage() < Global.STONE_ALL.length)
			arraylist.add(EnumChatFormatting.DARK_GRAY + Core.translate("global." + Global.STONE_ALL[is.getItemDamage()]));
		else
			arraylist.add(EnumChatFormatting.DARK_RED + Core.translate("global.unknown"));
	}
}
