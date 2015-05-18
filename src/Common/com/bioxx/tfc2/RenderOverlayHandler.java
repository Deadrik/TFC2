package com.bioxx.tfc2;

import jMapGen.Map;
import jMapGen.Point;
import jMapGen.attributes.Attribute;
import jMapGen.attributes.RiverAttribute;
import jMapGen.graph.Center;
import jMapGen.graph.Center.Marker;
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
		Center hex = map.getSelectedHexagon(new Point(mc.thePlayer.posX, mc.thePlayer.posZ));
		event.left.add("Elevation: "+hex.elevation);
		event.left.add("Biome: "+hex.biome.name() + " | Gorge: " + hex.hasAttribute(Attribute.gorgeUUID) + " | Lava: " + hex.hasMarker(Marker.Lava) + " | Valley: " + hex.hasMarker(Marker.Valley)+ " | Canyon: " + hex.hasAttribute(Attribute.canyonUUID));

		RiverAttribute attrib = (RiverAttribute)hex.getAttribute(Attribute.riverUUID);
		if(attrib != null)
			event.left.add("River: " + attrib.getRiver() + " | " + (attrib.upriver != null ?  attrib.upriver.size() : 0));
	}
}
