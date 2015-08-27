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
	public byte[] output;
	public int offset;

	public ClientMapPacket()
	{

	}

	public ClientMapPacket(int iX, int iZ, byte[] data, int off)
	{
		islandX = iX;
		islandZ = iZ; 
		output = data;
		offset = off;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeInt(islandX);
		buffer.writeInt(islandZ);
		buffer.writeInt(offset);
		buffer.writeBytes(output);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.islandX = buffer.readInt();
		this.islandZ = buffer.readInt();
		this.offset = buffer.readInt();
		output = new byte[buffer.readableBytes()];
		buffer.readBytes(output);
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
					IslandMap map = WorldGen.instance.getIslandMap(message.islandX, message.islandZ);
					System.out.println("Packet Island: " + message.islandX + "," + message.islandZ + " | Offset: " + message.offset + " | Length: " + message.output.length);
					for(int i = 0; i < message.output.length; i++)
					{
						j = 0xff & message.output[i];
						map.centers.get(message.offset+i).setMoistureRaw((float)j/255f);
					}
				}
			});
			return null; // no response in this case
		}
	}

}
