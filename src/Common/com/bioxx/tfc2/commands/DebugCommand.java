package com.bioxx.tfc2.commands;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;

public class DebugCommand extends CommandBase
{
	@Override
	public String getName()
	{
		return "dbg";
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

		if(params.length == 1 && params[0].equalsIgnoreCase("report"))
		{
			int xM =player.getPosition().getX() >> 12;
			int zM =player.getPosition().getZ() >> 12;
			String out = "World Seed: [" + world.getSeed() + "] | IslandMap: [" +xM + "," + zM + "] | PlayerPos: [" + player.getPosition().toString() + "]";
			StringSelection selection = new StringSelection(out);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if(clipboard != null)
				clipboard.setContents(selection, selection);

		}
	}

	@Override
	public String getUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
