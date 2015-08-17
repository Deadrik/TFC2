package com.bioxx.tfc2.api.trees;

import java.util.ArrayList;

import net.minecraft.init.Blocks;

import com.bioxx.tfc2.api.Schematic;
import com.bioxx.tfc2.api.types.WoodType;

public class TreeSchematic extends Schematic
{
	private int size;
	private WoodType type;

	public TreeSchematic(String p, String f, WoodType w)
	{
		super(p, f);
		type = w;
	}

	@Override
	public boolean Load()
	{
		if(super.Load())
		{
			ArrayList<SchemBlock> map = new ArrayList<SchemBlock>();
			for(SchemBlock b : blockMap)
			{
				if(b.state.getBlock() != Blocks.air)
					map.add(b);
			}
			blockMap = map;

			int num = filename.indexOf('_');
			String s = filename.substring(0, num);
			if(s.equals("Large"))
				size = 2;
			else if(s.equals("Normal"))
				size = 1;
			else
				size = 0;
			return true;
		}
		return false;
	}

	public int getGrowthStage()
	{
		return size;
	}

	public WoodType getWoodType()
	{
		return type;
	}
}
