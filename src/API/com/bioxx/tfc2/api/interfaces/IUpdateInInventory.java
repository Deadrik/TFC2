package com.bioxx.tfc2.api.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IUpdateInInventory 
{
	//This is called by both Server and Client
	public void inventoryUpdate(EntityPlayer player, ItemStack is);
}
