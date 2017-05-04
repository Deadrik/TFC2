package com.bioxx.tfc2.handlers;

import net.minecraft.entity.player.EntityPlayerMP;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerDisconnectionFromClientEvent;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.networking.client.CMapPacket;
import com.bioxx.tfc2.world.WorldGen;

public class PlayerTracker
{
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		PlayerManagerTFC.getInstance().players.add(new PlayerInfo(
				event.player.getName(),
				event.player.getUniqueID()));

		if(event.player.world.isRemote || event.player.dimension != 0)
			return;
		int islandX = (int)(event.player.posX) >> 12;
		int islandZ = (int)(event.player.posZ) >> 12;

		IslandMap map = WorldGen.getInstance().getIslandMap(islandX, islandZ);
		TFC.network.sendTo(new CMapPacket(islandX, islandZ, event.player.world.getSeed()+Helper.combineCoords(islandX, islandZ)), (EntityPlayerMP)event.player);
	}

	@SubscribeEvent
	public void onClientConnect(ClientConnectedToServerEvent event)
	{
		TFC.proxy.onClientLogin();
	}

	@SubscribeEvent
	/**
	 * Runs on the client
	 */
	public void onClientDisconnectServer(ClientDisconnectionFromServerEvent event)
	{
		if(WorldGen.getInstance() != null)
		{
			WorldGen.getInstance().resetCache();
			WorldGen.ClearInstances();
			ChunkLoadHandler.loadedCentersMap.clear();
		}
	}

	@SubscribeEvent
	/**
	 * Runs on the server
	 */
	public void onServerDisconnectClient(ServerDisconnectionFromClientEvent event)
	{
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{

	}
}

