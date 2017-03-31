package com.bioxx.tfc2.networking.server;

import io.netty.buffer.ByteBuf;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.tfc2.containers.ContainerSpecialCrafting;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class SKnappingPacket implements IMessage
{
	byte id = 0;

	public SKnappingPacket()
	{

	}

	public SKnappingPacket(int id)
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

	public static class Handler implements IMessageHandler<SKnappingPacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final SKnappingPacket message, final MessageContext ctx) {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromUUID(ctx.getServerHandler().player.getUniqueID());
					pi.knappingInterface[message.id] = true;
					if(ctx.getServerHandler().player.openContainer != null && ctx.getServerHandler().player.openContainer instanceof ContainerSpecialCrafting)
					{
						if(pi.specialCraftingTypeAlternate == null)
							((ContainerSpecialCrafting)ctx.getServerHandler().player.openContainer).craftMatrix.setInventorySlotContents(message.id, ItemStack.EMPTY);
						else
							((ContainerSpecialCrafting)ctx.getServerHandler().player.openContainer).craftMatrix.setInventorySlotContents(message.id, pi.specialCraftingTypeAlternate);
					}
				}
			});
			return null; // no response in this case
		}
	}

}
