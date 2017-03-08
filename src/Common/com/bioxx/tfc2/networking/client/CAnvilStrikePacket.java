package com.bioxx.tfc2.networking.client;

import io.netty.buffer.ByteBuf;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.tfc2.tileentities.TileAnvil;
import com.bioxx.tfc2.tileentities.TileAnvil.AnvilStrikePoint;
import com.bioxx.tfc2.tileentities.TileAnvil.AnvilStrikeType;

public class CAnvilStrikePacket implements IMessage
{
	public BlockPos pos;//Position of the anvil
	public int strikeIndex;//Index of the strike point in our array
	public AnvilStrikePoint strikePoint;

	public CAnvilStrikePacket()
	{

	}

	public CAnvilStrikePacket(BlockPos p, int index, AnvilStrikePoint strike)
	{
		pos = p;
		strikeIndex = index;
		strikePoint = strike;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());
		buffer.writeInt(strikeIndex);
		buffer.writeLong(strikePoint.getBirthTime());
		buffer.writeInt(strikePoint.getType().ordinal());
		buffer.writeInt(strikePoint.getLifeTime());
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		pos = BlockPos.fromLong(buffer.readLong());
		strikeIndex = buffer.readInt();
		strikePoint = new AnvilStrikePoint();
		strikePoint.setBirthTime(buffer.readLong());
		strikePoint.setType(AnvilStrikeType.values()[buffer.readInt()]);
		strikePoint.setLifeTime(buffer.readInt());
	}

	public static class Handler implements IMessageHandler<CAnvilStrikePacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final CAnvilStrikePacket message, MessageContext ctx) {
			IThreadListener mainThread = net.minecraft.client.Minecraft.getMinecraft(); //(WorldServer) ctx.getServerHandler().playerentityIn.world; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					TileEntity te = net.minecraft.client.Minecraft.getMinecraft().world.getTileEntity(message.pos);
					if(te != null)
					{
						TileAnvil anvil = (TileAnvil)te;
						anvil.setStrikePoint(message.strikeIndex, message.strikePoint);
					}
				}
			});
			return null; // no response in this case
		}
	}

}
