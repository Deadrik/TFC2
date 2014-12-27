package com.bioxx.tfc2;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderInformation()
	{
		MinecraftForge.EVENT_BUS.register(new RenderOverlayHandler());
		for(int l = 0; l < Global.STONE_ALL.length; l++)
		{
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(TFCBlocks.Dirt), l, new ModelResourceLocation(Reference.ModID + ":" + Global.STONE_ALL[l], "inventory"));
		}
	}

}
