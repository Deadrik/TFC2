package com.bioxx.tfc2.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.containers.ContainerPlayerTFC;
import com.bioxx.tfc2.networking.client.CFoodPacket;

public class JoinWorldHandler 
{
	@SubscribeEvent
	public void onJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityData().hasKey("hasSpawned"))
		{
			/*if(!(((EntityPlayer)event.getEntity()).inventory instanceof InventoryPlayerTFC))
				((EntityPlayer)event.getEntity()).inventory = Core.getNewInventory((EntityPlayer)event.getEntity());*/

			//((EntityPlayer)event.getEntity()).getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000);
			//((EntityPlayer)event.getEntity()).setHealth(1000);
			event.getEntity().getEntityData().setBoolean("hasSpawned", true);
		}

		if (event.getEntity() instanceof EntityPlayer)
		{
			/*if(!(((EntityPlayer)event.getEntity()).inventory instanceof InventoryPlayerTFC))
				((EntityPlayer)event.getEntity()).inventory = Core.getNewInventory((EntityPlayer)event.getEntity());*/

			((EntityPlayer)event.getEntity()).inventoryContainer = new ContainerPlayerTFC(((EntityPlayer)event.getEntity()).inventory, !event.getWorld().isRemote, (EntityPlayer)event.getEntity());
			((EntityPlayer)event.getEntity()).openContainer = ((EntityPlayer)event.getEntity()).inventoryContainer;
			if(!event.getWorld().isRemote)
			{
				TFC.network.sendTo(new CFoodPacket((IFoodStatsTFC)((EntityPlayer)event.getEntity()).getFoodStats()), (EntityPlayerMP)event.getEntity());
			}
		}
	}
}
