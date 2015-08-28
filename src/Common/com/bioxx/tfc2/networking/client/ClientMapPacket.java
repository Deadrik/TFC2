package com.bioxx.tfc2.networking.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.world.WorldGen;

public class ClientMapPacket implements IMessage
{
	public int islandX;
	public int islandZ;
	public long seed;

	public ClientMapPacket()
	{

	}

	public ClientMapPacket(int iX, int iZ, long seed)
	{
		islandX = iX;
		islandZ = iZ; 
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(islandX);
		buffer.writeInt(islandZ);
		buffer.writeLong(seed);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.islandX = buffer.readInt();
		this.islandZ = buffer.readInt();
		this.seed = buffer.readLong();
	}

	public static class Handler implements IMessageHandler<ClientMapPacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final ClientMapPacket message, MessageContext ctx) {
			IThreadListener mainThread = net.minecraft.client.Minecraft.getMinecraft(); //(WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					int j;
					IslandMap map = WorldGen.instance.createFakeMap(message.islandX, message.islandZ, message.seed);
				}
			});
			return null; // no response in this case
		}
	}

}
