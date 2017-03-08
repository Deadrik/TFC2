package com.bioxx.tfc2.networking.server;

import java.util.UUID;

import io.netty.buffer.ByteBuf;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.tfc2.tileentities.TileAnvil;

public class SAnvilCraftingPacket implements IMessage
{
	public BlockPos pos;//Position of the anvil
	public int recipe = 0;
	public boolean startedCrafting = false;
	public UUID smithID;

	public SAnvilCraftingPacket()
	{

	}

	public SAnvilCraftingPacket(BlockPos pos, int recipe, boolean start, UUID id)
	{
		this.pos = pos;
		this.recipe = recipe;
		startedCrafting = start;
		smithID = id;
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeLong(pos.toLong());
		buffer.writeInt(recipe);
		buffer.writeBoolean(startedCrafting);
		if(startedCrafting)
		{
			buffer.writeLong(smithID.getMostSignificantBits());
			buffer.writeLong(smithID.getLeastSignificantBits());
		}
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.pos = BlockPos.fromLong(buffer.readLong());
		this.recipe = buffer.readInt();
		startedCrafting = buffer.readBoolean();
		if(startedCrafting)
			smithID = new UUID(buffer.readLong(), buffer.readLong());
	}

	public static class Handler implements IMessageHandler<SAnvilCraftingPacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final SAnvilCraftingPacket message, final MessageContext ctx) {
			final IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					TileEntity te = ((WorldServer)mainThread).getTileEntity(message.pos);
					if(te != null && te instanceof TileAnvil)
					{
						((TileAnvil)te).setAnvilRecipeIndex(message.recipe);
						if(message.startedCrafting)
						{
							((TileAnvil)te).startCrafting(message.smithID);
						}
					}
				}
			});
			return null; // no response in this case
		}
	}

}
