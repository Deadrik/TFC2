package com.bioxx.tfc2;

import jMapGen.Map;
import jMapGen.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.World.WorldGen;

public class RenderOverlayHandler
{
	private FontRenderer fontrenderer = null;

	@SubscribeEvent
	public void renderText(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		int xM = ((int)(mc.thePlayer.posX) >> 12);
		int zM = ((int)(mc.thePlayer.posZ) >> 12);
		Map map = WorldGen.instance.getIslandMap(xM, zM);
		//Biome biome = ((ChunkManager)mc.theWorld.getWorldChunkManager()).getBiomeAt((int)Math.floor(mc.thePlayer.posX), (int)Math.floor(mc.thePlayer.posZ));
		event.left.add("biome: "+map.getSelectedHexagon(new Point(mc.thePlayer.posX, mc.thePlayer.posZ)).biome.name());
	}
}
