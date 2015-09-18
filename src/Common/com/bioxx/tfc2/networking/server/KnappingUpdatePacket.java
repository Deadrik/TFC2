package com.bioxx.tfc2.networking.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.tfc2.containers.ContainerSpecialCrafting;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class KnappingUpdatePacket implements IMessage
{
	byte id = 0;

	public KnappingUpdatePacket()
	{

	}

	public KnappingUpdatePacket(int id)
	{
		this.id = (byte)id;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeByte(id);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.id = buffer.readByte();
	}

	public static class Handler implements IMessageHandler<KnappingUpdatePacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final KnappingUpdatePacket message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromUUID(ctx.getServerHandler().playerEntity.getUniqueID());
					pi.knappingInterface[message.id] = false;
					if(ctx.getServerHandler().playerEntity.openContainer != null && ctx.getServerHandler().playerEntity.openContainer instanceof ContainerSpecialCrafting)
					{
						((ContainerSpecialCrafting)ctx.getServerHandler().playerEntity.openContainer).craftMatrix.setInventorySlotContents(message.id, pi.specialCraftingTypeAlternate);
					}
				}
			});
			return null; // no response in this case
		}
	}

}
