package com.bioxx.tfc2.api.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IFood
{

	ItemStack onDecayed(ItemStack is, World world, int i, int j, int k);

}
