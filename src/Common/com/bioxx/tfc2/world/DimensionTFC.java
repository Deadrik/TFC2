package com.bioxx.tfc2.world;

import net.minecraft.world.DimensionType;

public class DimensionTFC 
{
	public static DimensionType SURFACE = DimensionType.register("Surface", "_surf", 0, WorldProviderSurface.class, true);
	public static DimensionType PATHS = DimensionType.register("Paths", "_paths", 2, WorldProviderPaths.class, true);
}
