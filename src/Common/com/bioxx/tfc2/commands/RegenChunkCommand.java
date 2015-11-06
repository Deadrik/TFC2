package com.bioxx.tfc2.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

public class RegenChunkCommand extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "regen";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		MinecraftServer server = MinecraftServer.getServer();
		EntityPlayerMP player;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			return;
		}
		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimensionId());

		if(params.length == 1)
		{
			int radius = Integer.parseInt(params[0]);
			for(int i = -radius; i <= radius; i++)
			{
				for(int k = -radius; k <= radius; k++)
				{
					int x = (((int)player.posX+i*16) >> 4);
					int z = (((int)player.posZ+k*16) >> 4);
					BlockPos pos = new BlockPos((int)player.posX+i*16, 0, (int)player.posZ+k*16);

					ChunkProviderServer cps = ((ChunkProviderServer)world.getChunkProvider());

					Chunk c = cps.serverChunkGenerator.provideChunk(x, z);
					c.onChunkLoad();
					c.populateChunk(cps.serverChunkGenerator, cps, x, z);
					Chunk old = world.getChunkProvider().provideChunk(pos);
					old.setStorageArrays(c.getBlockStorageArray());
					old.setChunkModified();
				}
			}

			for(int i = -radius; i <= radius; i++)
			{
				for(int k = -radius; k <= radius; k++)
				{
					int x = (((int)player.posX+i*16) >> 4);
					int z = (((int)player.posZ+k*16) >> 4);
					BlockPos pos = new BlockPos((int)player.posX+i*16, 0, (int)player.posZ+k*16);
					ChunkProviderServer cps = ((ChunkProviderServer)world.getChunkProvider());
					net.minecraftforge.fml.common.registry.GameRegistry.generateWorld(x, z, world, cps.serverChunkGenerator, cps);

					player.playerNetServerHandler.sendPacket(new S21PacketChunkData(cps.provideChunk(pos), true, 0xffffffff));
				}
			}
		}
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
