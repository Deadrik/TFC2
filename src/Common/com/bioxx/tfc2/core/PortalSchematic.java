package com.bioxx.tfc2.core;

import java.util.ArrayList;

import net.minecraft.init.Blocks;

import com.bioxx.tfc2.api.Schematic;

public class PortalSchematic extends Schematic
{

	public PortalSchematic(String p, String f)
	{
		super(p, f);
	}

	@Override
	public boolean Load()
	{
		if(super.Load())
		{
			ArrayList<SchemBlock> map = new ArrayList<SchemBlock>();
			for(SchemBlock b : blockMap)
			{
				if(b.state.getBlock() != Blocks.AIR)
					map.add(b);
			}
			blockMap = map;
			return true;
		}
		return false;
	}
}
