package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.BlockLeaves;

public class ClientRenderHandler
{
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		((BlockLeaves)TFCBlocks.Leaves).setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
	}
}
