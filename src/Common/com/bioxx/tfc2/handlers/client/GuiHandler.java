package com.bioxx.tfc2.handlers.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.gui.GuiKnapping;

public class GuiHandler extends com.bioxx.tfc2.handlers.GuiHandler
{
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) 
	{
		BlockPos pos = new BlockPos(x, y, z);

		TileEntity te;
		try
		{
			te = world.getTileEntity(pos);
		}
		catch(Exception e)
		{
			te = null;
		}

		switch(id)
		{
		case 0:
			PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player);
			return new GuiKnapping(player.inventory, pi.specialCraftingTypeAlternate == null ? pi.specialCraftingType : null, world, x, y, z);

		default:
			return null;
		}
	}

	@SubscribeEvent
	public void openGuiHandler(GuiOpenEvent event)
	{
		/*if(event.gui instanceof GuiInventory && !(event.gui instanceof GuiInventoryTFC))
			event.gui = new GuiInventoryTFC(Minecraft.getMinecraft().thePlayer);*/
	}
}
