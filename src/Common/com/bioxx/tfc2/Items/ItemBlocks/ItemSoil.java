package com.bioxx.tfc2.Items.ItemBlocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.bioxx.tfc2.Global;

public class ItemSoil extends ItemTerraBlock
{
	public ItemSoil(Block b)
	{
		super(b);
		MetaNames = Global.STONE_ALL;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);

		if (is.getItemDamage() < Global.STONE_ALL.length)
			arraylist.add(EnumChatFormatting.DARK_GRAY + Global.STONE_ALL[is.getItemDamage()]);
		else
			arraylist.add(EnumChatFormatting.DARK_RED + "Unknown");
	}
}
