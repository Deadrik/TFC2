package com.bioxx.tfc2.api.Trees;

import com.bioxx.tfc2.CoreStuff.Schematic;

public class TreeSchematic extends Schematic
{
	private int size;

	public TreeSchematic(String p)
	{
		super(p);
	}

	@Override
	public boolean Load()
	{
		super.Load();
		int num = path.indexOf('-');
		String s = path.substring(0, num);
		if(s.equals("Large"))
			size = 2;
		else if(s.equals("Normal"))
			size = 1;
		else
			size = 0;
		return true;
	}

	public int getGrowthStage()
	{
		return size;
	}
}
