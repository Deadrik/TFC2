package com.bioxx.tfc2.networking.client;

import io.netty.buffer.ByteBuf;

import net.minecraft.util.IThreadListener;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.handlers.client.ClientRenderHandler;
import com.bioxx.tfc2.world.WorldGen;

public class CMapPacket implements IMessage
{
	public int islandX;
	public int islandZ;
	public long seed;

	public CMapPacket()
	{

	}

	public CMapPacket(int iX, int iZ, long seed)
	{
		islandX = iX;
		islandZ = iZ; 
		this.seed = seed;
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

	public static class Handler implements IMessageHandler<CMapPacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final CMapPacket message, MessageContext ctx) {
			IThreadListener mainThread = net.minecraft.client.Minecraft.getMinecraft(); //(WorldServer) ctx.getServerHandler().playerentityIn.world; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					if(WorldGen.getInstance().worldSeed == Long.MIN_VALUE)
					{
						WorldGen.getInstance().worldSeed = message.seed - Helper.combineCoords(message.islandX, message.islandZ);
						ClientRenderHandler.IsGeneratingFirstIsland = true;

					}
					//WorldGen.getInstance().createIsland(message.islandX, message.islandZ, message.seed, true);
					WorldGen.getInstance().forceBuildIsland(message.islandX, message.islandZ, message.seed);

				}
			});
			return null; // no response in this case
		}
	}

}
