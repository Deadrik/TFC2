package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.entity.EntityCart;
import com.bioxx.tfc2.gui.GuiCart;
import com.bioxx.tfc2.gui.GuiInventoryTFC;
import com.bioxx.tfc2.gui.GuiKnapping;

public class GuiHandler extends com.bioxx.tfc2.handlers.GuiHandler
{
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) 
	{
		BlockPos pos = new BlockPos(x, y, z);
		PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player);
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
			return new GuiKnapping(player.inventory, pi.specialCraftingTypeAlternate == null ? pi.specialCraftingType : null, world, x, y, z);
		case 1:
			return new GuiCart(player.inventory, ((EntityCart)pi.entityForInventory).cartInv, world, x, y, z);
		default:
			return null;
		}
	}

	@SubscribeEvent
	public void openGuiHandler(GuiOpenEvent event)
	{
		if(event.gui instanceof GuiInventory && !(event.gui instanceof GuiInventoryTFC))
			event.gui = new GuiInventoryTFC(Minecraft.getMinecraft().thePlayer);
	}
}
