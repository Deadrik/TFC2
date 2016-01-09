package com.bioxx.tfc2.handlers;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.api.events.HexUpdateEvent;

public class HexUpdateHandler 
{
	@SubscribeEvent
	public void handle(HexUpdateEvent event)
	{
		NBTTagCompound nbt = event.centerToUpdate.getCustomNBT();
		if(nbt.hasKey("nutrientData"))
		{
			//TODO Refill Nutrient Levels
		}
	}
}
