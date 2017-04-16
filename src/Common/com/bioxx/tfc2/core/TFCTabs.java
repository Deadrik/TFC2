package com.bioxx.tfc2.core;

import com.bioxx.tfc2.Core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TFCTabs extends CreativeTabs
{
	public static CreativeTabs TFCBuilding = new TFCTabs("TFCBuilding");
	public static CreativeTabs TFCDecoration = new TFCTabs("TFCDecoration");
	public static CreativeTabs TFCDevices = new TFCTabs("TFCDevices");
	public static CreativeTabs TFCPottery = new TFCTabs("TFCPottery");
	public static CreativeTabs TFCMisc = new TFCTabs("TFCMisc");
	public static CreativeTabs TFCFoods = new TFCTabs("TFCFoods");
	public static CreativeTabs TFCTools = new TFCTabs("TFCTools");
	public static CreativeTabs TFCWeapons = new TFCTabs("TFCWeapons");
	public static CreativeTabs TFCArmor = new TFCTabs("TFCArmor");
	public static CreativeTabs TFCMaterials = new TFCTabs("TFCMaterials");

	private ItemStack isIcon;

	public TFCTabs(String label)
	{
		super(label);
	}

	public void setTabIconItemStack(ItemStack is)
	{
		isIcon = is;
	}

	@Override
	public ItemStack getIconItemStack()
	{
		return isIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getTabIconItem()
	{
		return isIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getTranslatedTabLabel()
	{
		return Core.translate("itemGroup." + this.getTabLabel());
	}

	
}
