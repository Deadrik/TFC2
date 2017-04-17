package com.bioxx.tfc2.items;

import java.util.List;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.interfaces.IRegisterSelf;
import com.bioxx.tfc2.core.TFCTabs;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemToolHead extends ItemTerra implements IRegisterSelf
{
	public ItemToolHead()
	{
		super();
		this.hasSubtypes = true;
		this.subTypeNames = ToolHeadType.getNamesArray();
		this.maxSubTypeMeta = this.subTypeNames.length - 1;
		this.setCreativeTab(TFCTabs.TFCMisc);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		arraylist.add(TextFormatting.DARK_GRAY + Core.translate(this.subTypeNames[is.getItemDamage()]));
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}

	@Override
	public String getPath()
	{
		return "toolheads/";
	}
}
