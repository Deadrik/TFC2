package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import org.lwjgl.input.Keyboard;

import com.bioxx.tfc2.Reference;

public class KeyBindingHandler
{
	public static KeyBinding Key_CombatMode = new KeyBinding("key.combatmode", Keyboard.KEY_GRAVE, Reference.ModName);

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		EntityPlayerSP player = FMLClientHandler.instance().getClient().player;

		if(FMLClientHandler.instance().getClient().inGameHasFocus &&
				FMLClientHandler.instance().getClient().currentScreen == null)
		{
			if(Key_CombatMode.isPressed())
			{

			}
		}
	}
}
