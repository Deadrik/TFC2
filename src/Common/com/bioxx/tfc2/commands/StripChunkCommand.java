package com.bioxx.tfc2.commands;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

import com.bioxx.tfc2.TFCBlocks;

public class StripChunkCommand extends CommandBase
{
	@Override
	public String getName()
	{
		return "stripchunk";
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
		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimension());

		/*if(!TFCOptions.enableDebugMode)
		{
			TFC_Core.sendInfoMessage(player, new ChatComponentText("Debug Mode Required"));
			return;
		}*/

		if(!player.isCreative())
			return;

		if(params.length == 0)
		{

			BlockPos pos = new BlockPos((int)player.posX, 0, (int)player.posZ);
			Chunk chunk = world.getChunkFromBlockCoords(pos);
			pos = new BlockPos(chunk.xPosition << 4, 0, chunk.zPosition << 4);
			for(int x = 0; x < 16; x++)
			{
				for(int z = 0; z < 16; z++)
				{
					for(int y = 0; y < 256; y++)
					{
						Block id = chunk.getBlockState(x, y, z).getBlock();
						if(id != TFCBlocks.Ore && id != Blocks.BEDROCK)
						{
							world.setBlockState(pos.add(x, y, z), Blocks.AIR.getDefaultState());
						}
					}
				}
			}
		}
		else if(params.length == 1)
		{
			int radius = Integer.parseInt(params[0]);
			for(int i = -radius; i <= radius; i++)
			{
				for(int k = -radius; k <= radius; k++)
				{
					BlockPos pos = new BlockPos((int)player.posX+i*16, 0, (int)player.posZ+k*16);
					Chunk chunk = world.getChunkFromBlockCoords(pos);
					pos = new BlockPos(chunk.xPosition << 4, 0, chunk.zPosition << 4);
					for(int x = 0; x < 16; x++)
					{
						for(int z = 0; z < 16; z++)
						{
							for(int y = 0; y < 256; y++)
							{
								Block id = chunk.getBlockState(x, y, z).getBlock();
								if(id != TFCBlocks.Ore && id != Blocks.BEDROCK)
									world.setBlockState(pos.add(x, y, z), Blocks.AIR.getDefaultState());
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String getUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
