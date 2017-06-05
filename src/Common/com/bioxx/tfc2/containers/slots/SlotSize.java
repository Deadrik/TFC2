package com.bioxx.tfc2.containers.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc2.api.SizeWeightRegistry;
import com.bioxx.tfc2.api.SizeWeightRegistry.SizeWeightProp;
import com.bioxx.tfc2.api.types.EnumSize;

public class SlotSize extends Slot
{
	EnumSize minSize;
	EnumSize maxSize;

	public SlotSize(IInventory iinventory, int index, int x, int y, EnumSize min, EnumSize max)
	{
		super(iinventory, index, x, y);
		minSize = min;
		maxSize = max;
	}

	@Override
	public boolean isItemValid(ItemStack is)
	{
		SizeWeightProp prop = SizeWeightRegistry.GetInstance().getProperty(is);
		if(prop.size.ordinal() <= maxSize.ordinal() && prop.size.ordinal() >= minSize.ordinal())
			return true;
		return false;
	}
}
