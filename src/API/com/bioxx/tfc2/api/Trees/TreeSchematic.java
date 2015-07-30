package com.bioxx.tfc2.api.Trees;

import com.bioxx.tfc2.api.Schematic;

public class TreeSchematic extends Schematic
{
	private int size;

	public TreeSchematic(String p, String f)
	{
		super(p, f);
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
}
