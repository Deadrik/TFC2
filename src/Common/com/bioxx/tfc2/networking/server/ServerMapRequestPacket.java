package com.bioxx.tfc2.networking.server;

import io.netty.buffer.ByteBuf;

import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.networking.client.ClientMapPacket;
import com.bioxx.tfc2.world.WorldGen;

public class ServerMapRequestPacket implements IMessage
{
	public int islandX;
	public int islandZ;

	public ServerMapRequestPacket()
	{

	}

	public ServerMapRequestPacket(int iX, int iZ)
	{
		islandX = iX;
		islandZ = iZ; 
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(islandX);
		buffer.writeInt(islandZ);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.islandX = buffer.readInt();
		this.islandZ = buffer.readInt();
	}

	public static class Handler implements IMessageHandler<ServerMapRequestPacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final ServerMapRequestPacket message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					int j;
					IslandMap map = WorldGen.getInstance().getIslandMap(message.islandX, message.islandZ);
					TFC.network.sendTo(new ClientMapPacket(message.islandX, message.islandZ, map.seed), ctx.getServerHandler().playerEntity);
				}
			});
			return null; // no response in this case
		}
	}

}
