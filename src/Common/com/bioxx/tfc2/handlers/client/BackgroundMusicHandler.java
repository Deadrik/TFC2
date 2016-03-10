package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.core.TFC_Sounds;

import com.bioxx.tfc2.Reference;

public class BackgroundMusicHandler
{
	private ISound iSound;
	
	@SubscribeEvent
	public void onBGMusic(PlaySoundEvent event)
	{
		if(event.sound != null && event.category != null && event.category.getCategoryName().equalsIgnoreCase("music"))
		{
			if(event.manager.isSoundPlaying(iSound))
			{
				event.result = null;
			}
			else
			{
				iSound = PositionedSoundRecord.create(new ResourceLocation(TFC_Sounds.TFCMUSIC));
				event.result = iSound;
			}
		}
	}
}
