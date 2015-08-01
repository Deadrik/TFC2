package com.bioxx.tfc2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.IslandParameters.Feature;
import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.attributes.Attribute;
import com.bioxx.jMapGen.attributes.RiverAttribute;
import com.bioxx.jMapGen.graph.Center;
import com.bioxx.tfc2.World.WorldGen;
import com.bioxx.tfc2.api.Types.Moisture;

public class RenderOverlayHandler
{
	private FontRenderer fontrenderer = null;

	@SubscribeEvent
	public void renderText(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld.provider.getDimensionId() == 0 && WorldGen.instance != null)
		{
			int xM = ((int)(mc.thePlayer.posX) >> 12);
			int zM = ((int)(mc.thePlayer.posZ) >> 12);
			IslandMap map = WorldGen.instance.getIslandMap(xM, zM);
			Center hex = map.getSelectedHexagon(new Point(mc.thePlayer.posX, mc.thePlayer.posZ).toIslandCoord());
			event.right.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"--------Hex--------");
			event.right.add("Elevation: "+hex.elevation);
			//event.right.add("Biome:"+hex.biome.name());
			event.right.add("Moisture:"+Moisture.fromVal(hex.moisture));

			//event.right.add("Gorge:"+hex.hasAttribute(Attribute.gorgeUUID));
			//event.right.add("Lava:"+hex.hasMarker(Marker.Lava));
			//event.right.add("Valley:"+hex.hasMarker(Marker.Valley));
			//event.right.add("Canyon:" + hex.hasAttribute(Attribute.canyonUUID));

			RiverAttribute attrib = (RiverAttribute)hex.getAttribute(Attribute.riverUUID);
			if(attrib != null)
			{
				event.left.add("River: " + attrib.getRiver() + " | " + (attrib.upriver != null ?  attrib.upriver.size() : 0));	
				if(attrib.upriver != null && attrib.getDownRiver() != null)
					event.left.add("Up :" + hex.getDirection(attrib.upriver.get(0)).toString() + " | Dn :" + hex.getDirection(attrib.getDownRiver()).toString());
			}
			event.right.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"--Island Parmaters--");
			event.right.add("*Moisture: "+map.getParams().getIslandMoisture());
			event.right.add("*Temperature: "+map.getParams().getIslandTemp());

			event.right.add(EnumChatFormatting.BOLD+""+EnumChatFormatting.YELLOW+"---Island Features--");
			for(Feature f : Feature.values())
			{
				if(map.getParams().hasFeature(f))
				{
					event.right.add("*"+f.toString());
				}
			}
		}
	}
}
