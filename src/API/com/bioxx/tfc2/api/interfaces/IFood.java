package com.bioxx.tfc2.api.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.types.EnumFoodGroup;

public interface IFood
{
	EnumFoodGroup getFoodGroup();

	int getFoodID();

	/**
	 * @return Returns an ItemStack that will replace the current ItemStack when the food has reached maximum decay.
	 * Normally returns null.
	 */
	ItemStack onDecayed(ItemStack is, World world, int i, int j, int k);
	/**
	 * @return Is this food edible as is.
	 */
	boolean isEdible(ItemStack is);
	/**
	 * @return Is this item usable in meals
	 */
	boolean isUsable(ItemStack is);

	long getExpirationTimer(ItemStack is);
}
