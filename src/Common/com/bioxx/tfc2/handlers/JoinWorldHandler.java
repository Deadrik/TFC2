package com.bioxx.tfc2.handlers;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.containers.ContainerPlayerTFC;
import com.bioxx.tfc2.core.InventoryPlayerTFC;

public class JoinWorldHandler 
{
	@SubscribeEvent
	public void onJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.entity instanceof EntityPlayer && !event.entity.getEntityData().hasKey("hasSpawned"))
		{
			if(!(((EntityPlayer)event.entity).inventory instanceof InventoryPlayerTFC))
				((EntityPlayer)event.entity).inventory = Core.getNewInventory((EntityPlayer)event.entity);

			((EntityPlayer)event.entity).getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000);
			((EntityPlayer)event.entity).setHealth(1000);
			event.entity.getEntityData().setBoolean("hasSpawned", true);
		}

		if (event.entity instanceof EntityPlayer)
		{
			if(!(((EntityPlayer)event.entity).inventory instanceof InventoryPlayerTFC))
				((EntityPlayer)event.entity).inventory = Core.getNewInventory((EntityPlayer)event.entity);

			((EntityPlayer)event.entity).inventoryContainer = new ContainerPlayerTFC(((EntityPlayer)event.entity).inventory, !event.world.isRemote, (EntityPlayer)event.entity);
			((EntityPlayer)event.entity).openContainer = ((EntityPlayer)event.entity).inventoryContainer;
		}
	}
}
