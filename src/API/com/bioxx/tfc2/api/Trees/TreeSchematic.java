package com.bioxx.tfc2.api.Trees;

import com.bioxx.tfc2.api.Schematic;
import com.bioxx.tfc2.api.Types.WoodType;

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
