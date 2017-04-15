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

public class RemoveAreaCommand extends CommandBase
{
	@Override
	public String getName()
	{
		return "removearea";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params)
	{
		EntityPlayerMP player = null;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException ignored) {
		}
		if(player == null)
			return;
		if(!player.isCreative())
			return;

		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimension());

		/*if(!TFCOptions.enableDebugMode)
		{
			TFC_Core.sendInfoMessage(player, new ChatComponentText("Debug Mode Required"));
			return;
		}
		 */
		BlockPos pos = new BlockPos((int)player.posX, (int)player.posY, (int)player.posZ);
		BlockPos pos2;
		if(params.length == 0)
		{
			for(int x = -15; x < 16; x++)
			{
				for(int z = -15; z < 16; z++)
				{
					for(int y = 0; y < 16; y++)
					{
						pos2 = pos.add(x, y, z);
						Block id = world.getBlockState(pos2).getBlock();
						if(id != Blocks.BEDROCK)
							world.setBlockState(pos2, Blocks.AIR.getDefaultState(), 2);
					}
				}
			}
		}
		else if(params.length == 3)
		{
			int radius = Integer.parseInt(params[0]);
			for (int x = -radius; x <= Integer.parseInt(params[0]); x++)
			{
				for(int z = -Integer.parseInt(params[2]); z <= Integer.parseInt(params[2]); z++)
				{
					for(int y = 0; y <= Integer.parseInt(params[1]); y++)
					{
						pos2 = pos.add(x, y, z);
						Block id = world.getBlockState(pos2).getBlock();
						if(id != Blocks.BEDROCK)
							world.setBlockState(pos2, Blocks.AIR.getDefaultState(), 2);
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
