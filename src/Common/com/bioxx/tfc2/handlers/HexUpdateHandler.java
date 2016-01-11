package com.bioxx.tfc2.handlers;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.api.events.HexUpdateEvent;
import com.bioxx.tfc2.core.Timekeeper;
import com.bioxx.tfc2.tileentities.TileCrop;

public class HexUpdateHandler 
{
	@SubscribeEvent
	public void handle(HexUpdateEvent event)
	{
		NBTTagCompound nbt = event.centerToUpdate.getCustomNBT();
		if(nbt.hasKey("TFC2_Data"))
		{
			NBTTagCompound data = nbt.getCompoundTag("TFC2_Data");
			if(data.hasKey("CropData"))
			{
				NBTTagCompound cropData = data.getCompoundTag("CropData");
				long lastRegenTick = cropData.getLong("lastRegenTick");
				if(lastRegenTick + Timekeeper.ticksInPeriod < Timekeeper.getInstance().getTotalTicks())
				{
					cropData.setLong("lastRegenTick", lastRegenTick + Timekeeper.ticksInPeriod);
					float nutrients = cropData.getFloat("nutrients");
					float maxNutrients = TileCrop.GetMaxNutrients(event.map);
					cropData.setFloat("nutrients", Math.min(maxNutrients, nutrients + maxNutrients/4));
				}
			}
			if(data.hasKey("hydration"))
			{
				byte[] hydrationArray = data.getByteArray("hydration");
				int waterLevel = 0;
				for(int i = 0; i < 64; i++)
				{
					hydrationArray[i] = (byte)Math.max(0, hydrationArray[i]-5);
					waterLevel += hydrationArray[i];
				}
				if(waterLevel > 0)
					data.setByteArray("hydration", hydrationArray);
				else
					data.removeTag("hydration");
			}
		}
	}
}
