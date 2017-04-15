package com.bioxx.tfc2.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

import com.bioxx.jmapgen.Point;

public class TeleportInIslandCommand extends CommandBase
{
	public TeleportInIslandCommand()
	{

	}
	@Override
	public String getName()
	{
		return "tpi";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params)
	{
		EntityPlayerMP player = null;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimension());

		if(!player.isCreative())
			return;

		if(params.length == 3)
		{
			int x = (int)Math.floor(player.posX);
			int y = (int)Math.floor(player.posY);
			int z = (int)Math.floor(player.posZ);
			int mX = x >> 12;
			int mZ = z >> 12;

			int inX = Integer.parseInt(params[0]);
			int inY = Integer.parseInt(params[1]);
			int inZ = Integer.parseInt(params[2]);

			Point p = new Point(x, z);

			if(params[0].contains("+") || params[0].contains("-"))
				x += inX;
			else
				x = mX*4096+inX;

			if(params[1].contains("+") || params[1].contains("-"))
				y += inY;
			else
				y = inY;

			if(params[2].contains("+") || params[2].contains("-"))
				z += inZ;
			else
				z = mZ*4096+inZ;

			player.setPositionAndUpdate(x, y, z);
		}
	}

	@Override
	public String getUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
