package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.SoundCategory;

import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.core.TFC_Sounds;

public class BackgroundMusicHandler
{
	private ISound iSound;

	@SubscribeEvent
	public void onBGMusic(PlaySoundEvent event)
	{
		if(event.getSound() != null && event.getSound().getCategory() != null && event.getSound().getCategory() == SoundCategory.MUSIC)
		{
			if(event.getManager().isSoundPlaying(iSound))
			{
				event.setResultSound(null);
			}
			else
			{
				iSound = PositionedSoundRecord.getMusicRecord(TFC_Sounds.TFCMUSIC);
				event.setResultSound(iSound);
			}
		}
	}
}
