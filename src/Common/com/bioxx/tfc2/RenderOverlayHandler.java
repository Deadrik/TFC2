package com.bioxx.tfc2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderOverlayHandler
{
	private FontRenderer fontrenderer = null;

	@SubscribeEvent
	public void renderText(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = Minecraft.getMinecraft();

		//Biome biome = ((ChunkManager)mc.theWorld.getWorldChunkManager()).getBiomeAt((int)Math.floor(mc.thePlayer.posX), (int)Math.floor(mc.thePlayer.posZ));
		//event.left.add("biome: "+biome.getName());
	}
}
