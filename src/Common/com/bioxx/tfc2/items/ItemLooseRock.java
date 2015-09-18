package com.bioxx.tfc2.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Global;

public class ItemLooseRock extends ItemTerra
{
	public ItemLooseRock()
	{
		this.setShowInCreative(true);
		this.setHasSubtypes(true);
		this.maxSubTypeMeta = 15;
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.subTypeNames = Global.STONE_ALL;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName();
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
