package com.bioxx.tfc2.core;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public class PlayerInfo
{
	public String playerName;
	public UUID playerUUID;

	public ItemStack specialCraftingType;
	public ItemStack specialCraftingTypeAlternate;

	//If we are checking the inventory of an entity then we store it here when we right click so that the gui handler has something to work with
	public Entity entityForInventory;

	public boolean isInDebug = false;

	//Clientside only variables
	public boolean[] knappingInterface;
	public boolean shouldDrawKnappingHighlight;

	public PlayerInfo(String name, UUID uuid)
	{
		playerName = name;
		playerUUID = uuid;
		specialCraftingType = null;
		specialCraftingTypeAlternate = null;
		knappingInterface = new boolean[81];
	}
}
