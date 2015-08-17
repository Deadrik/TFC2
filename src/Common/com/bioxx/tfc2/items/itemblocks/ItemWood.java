package com.bioxx.tfc2.items.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.interfaces.INeedOffset;

public class ItemWood extends ItemTerraBlock
{
	public ItemWood(Block b)
	{
		super(b);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);

		if (is.getItemDamage() < Global.WOOD_STANDARD.length)
		{
			Block b = ((ItemBlock)is.getItem()).getBlock();
			int meta = is.getItemDamage();
			if(b instanceof INeedOffset)
				meta = ((INeedOffset)b).convertMeta(meta);
			arraylist.add(EnumChatFormatting.DARK_GRAY + Core.translate("global." + Global.WOOD_STANDARD[meta]));
		}
		else
			arraylist.add(EnumChatFormatting.DARK_RED + Core.translate("global.unknown"));
	}
}
