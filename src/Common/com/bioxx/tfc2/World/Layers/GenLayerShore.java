package com.bioxx.tfc2.World.Layers;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import com.bioxx.tfc2.World.TerrainTypes.TerrainType;

public class GenLayerShore extends GenLayerTFC
{
	public GenLayerShore(long par1, GenLayer par3GenLayer)
	{
		super(par1);
		this.parent = (GenLayerTFC) par3GenLayer;
	}

	@Override
	public int[] getInts(int par1, int par2, int par3, int par4)
	{
		int[] var5 = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
		int[] outArray = IntCache.getIntCache(par3 * par4);

		for (int var7 = 0; var7 < par4; ++var7)
		{
			for (int var8 = 0; var8 < par3; ++var8)
			{
				this.initChunkSeed((long)(var7 + par1), (long)(var8 + par2));
				int id = var5[var8 + 1 + (var7 + 1) * (par3 + 2)];
				int var10;
				int var11;
				int var12;
				int var13;

				if (id != TerrainType.Ocean.getID())
				{
					var10 = var5[var8 + 1 + (var7 + 1 - 1) * (par3 + 2)];
					var11 = var5[var8 + 1 + 1 + (var7 + 1) * (par3 + 2)];
					var12 = var5[var8 + 1 - 1 + (var7 + 1) * (par3 + 2)];
					var13 = var5[var8 + 1 + (var7 + 1 + 1) * (par3 + 2)];

					if (var10 != TerrainType.Ocean.getID() && var11 != TerrainType.Ocean.getID() && var12 != TerrainType.Ocean.getID() && var13 != TerrainType.Ocean.getID())
						outArray[var8 + var7 * par3] = id;
					else
					{
						int beachid = TerrainType.Beach.getID();
						outArray[var8 + var7 * par3] = beachid;
					}
				}
				else
				{
					outArray[var8 + var7 * par3] = id;
				}
			}
		}
		return outArray;
	}
}
