package com.bioxx.tfc2.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.HexGenRegistry;

public class RegenChunkCommand extends CommandBase
{
	@Override
	public String getName()
	{
		return "regen";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params)
	{
		EntityPlayerMP player;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			return;
		}

		if(!player.isCreative())
			return;

		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimension());

		if(player.getEntityWorld().provider.getDimension() == 0 && params.length == 1)
		{
			int radius = Integer.parseInt(params[0]);

			for(int i = -radius; i <= radius; i++)
			{
				for(int k = -radius; k <= radius; k++)
				{
					int x = (((int)player.posX+i*16) >> 4);
					int z = (((int)player.posZ+k*16) >> 4);

					ChunkProviderServer cps = world.getChunkProvider();

					Chunk c = cps.chunkGenerator.provideChunk(x, z);
					Chunk old = world.getChunkProvider().provideChunk(x, z);
					old.setStorageArrays(c.getBlockStorageArray());
					c.setTerrainPopulated(false);
					c.onChunkLoad();
					c.setChunkModified();
					c.checkLight();
					cps.chunkGenerator.populate(c.xPosition, c.zPosition);
					net.minecraftforge.fml.common.registry.GameRegistry.generateWorld(c.xPosition, c.zPosition, world, cps.chunkGenerator, world.getChunkProvider());
					c.setChunkModified();
					player.connection.sendPacket(new SPacketChunkData(cps.provideChunk(x, z), 0xffffffff));
				}
			}

			/*for(int i = -radius; i <= radius; i++)
			{
				for(int k = -radius; k <= radius; k++)
				{
					int x = (((int)player.posX+i*16) >> 4);
					int z = (((int)player.posZ+k*16) >> 4);

					ChunkProviderServer cps = world.getChunkProvider();

					Chunk c = cps.chunkGenerator.provideChunk(x, z);
					Chunk old = world.getChunkProvider().provideChunk(x, z);
					old.populateChunk(cps, cps.chunkGenerator);

					if (c.isTerrainPopulated())
					{
						if (cps.chunkGenerator.generateStructures(c, c.xPosition, c.zPosition))
						{
							c.setChunkModified();
						}
					}
					else
					{
						c.checkLight();
						cps.chunkGenerator.populate(c.xPosition, c.zPosition);
						net.minecraftforge.fml.common.registry.GameRegistry.generateWorld(c.xPosition, c.zPosition, world, cps.chunkGenerator, world.getChunkProvider());
						c.setChunkModified();
					}

					player.connection.sendPacket(new SPacketChunkData(cps.provideChunk(x, z), 0xffffffff));
				}
			}*/
		}
		else if(player.getEntityWorld().provider.getDimension() == 0 && params.length == 2 && params[1].equalsIgnoreCase("hex"))
		{
			execute(server, sender, new String[] {params[0]});
			IslandMap map = Core.getMapForWorld(world, player.getPosition());
			HexGenRegistry.generate(Core.getMapForWorld(world, player.getPosition()), map.getClosestCenter(player.getPosition()), world);
		}
	}

	@Override
	public String getUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
