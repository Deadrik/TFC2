package com.bioxx.tfc2.core;

import java.util.UUID;

import net.minecraft.item.ItemStack;

public class PlayerInfo
{
	public String playerName;
	public UUID playerUUID;

	public ItemStack specialCraftingType;
	public ItemStack specialCraftingTypeAlternate;

	//Clientside only variables
	public boolean[] knappingInterface;

	public PlayerInfo(String name, UUID uuid)
	{
		playerName = name;
		playerUUID = uuid;
		specialCraftingType = null;
		specialCraftingTypeAlternate = null;
		knappingInterface = new boolean[81];
	}
}
