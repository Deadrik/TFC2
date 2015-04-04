package com.bioxx.tfc2.World;

import com.bioxx.libnoise.module.Module;

public class WorldGen extends Module {

	public WorldGen() 
	{
		super(0);

	}

	@Override
	public int GetSourceModuleCount() {
		return 0;
	}

	@Override
	public double GetValue(double x, double y, double z) {
		return 0;
	}

}
