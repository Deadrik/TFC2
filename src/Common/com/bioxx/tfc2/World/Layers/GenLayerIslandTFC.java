package com.bioxx.tfc2.World.Layers;

import java.util.List;

import net.minecraft.world.gen.layer.IntCache;

import com.bioxx.tfc2.World.TerrainTypes.TerrainType;
import com.google.common.collect.Lists;

public class GenLayerIslandTFC extends GenLayerTFC
{
	public static List<TerrainType> viableTerrains = Lists.newArrayList(TerrainType.FlatlandsLow, TerrainType.MountainsLow);
	public GenLayerIslandTFC(long par1)
	{
		super(par1);
	}

	@Override
	public int[] getInts(int pX, int pZ, int pWidth, int pHeight)
	{
		int[] outArray = IntCache.getIntCache(pWidth * pHeight);

		for (int z = 0; z < pHeight; ++z)
		{
			for (int x = 0; x < pWidth; ++x)
			{
				this.initChunkSeed(pX + x, pZ + z);
				if(((pX+x>>1) % 2 != 0) && ((pZ+z>>1) % 2 != 0) /*&& (this.nextInt(6) != 0)*/)
				{
					outArray[x + z * pWidth] = viableTerrains.get(this.nextInt(viableTerrains.size())).getID();
				}
				else
				{
					outArray[x + z * pWidth] = 0;
				}
			}
		}

		if (pX > -pWidth && pX <= 0 && pZ > -pHeight && pZ <= 0)
			outArray[-pX + -pZ * pWidth] = 1;

		return outArray;
	}
}
