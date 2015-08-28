package com.bioxx.tfc2.handlers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.networking.client.ClientMapPacket;
import com.bioxx.tfc2.world.WorldGen;

public class PlayerTracker
{
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if(event.player.worldObj.isRemote)
			return;
		int islandX = (int)(event.player.posX) >> 12;
		int islandZ = (int)(event.player.posZ) >> 12;

		IslandMap map = WorldGen.instance.getIslandMap(islandX, islandZ);
		TFC.network.sendTo(new ClientMapPacket(islandX, islandZ, map.seed), (EntityPlayerMP)event.player);

	}

	@SubscribeEvent
	public void onClientConnect(ClientConnectedToServerEvent event)
	{
		TFC.proxy.onClientLogin();
	}

	@SubscribeEvent
	public void onClientDisconnect(ServerDisconnectionFromClientEvent event)
	{
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{

	}
}

