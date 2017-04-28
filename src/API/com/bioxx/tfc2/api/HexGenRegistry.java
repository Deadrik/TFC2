package com.bioxx.tfc2.api;

import java.util.*;

import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.api.interfaces.IHexGenerator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

/**
 * This class was mostly borrowed from Forge GameRegistry
 */

public class HexGenRegistry 
{
	private static Set<IHexGenerator> worldGenerators = Sets.newHashSet();
	private static Map<IHexGenerator, Integer> worldGeneratorIndex = Maps.newHashMap();
	private static List<IHexGenerator> sortedGeneratorList;

	public static void registerWorldGenerator(IHexGenerator generator, int modGenerationWeight)
	{
		worldGenerators.add(generator);
		worldGeneratorIndex.put(generator, modGenerationWeight);
		if (sortedGeneratorList != null)
		{
			sortedGeneratorList = null;
		}
	}

	public static void generate(IslandMap map, Center c, World world)
	{
		if (sortedGeneratorList == null)
		{
			computeSortedGeneratorList();
		}
		long mapSeed = map.seed;
		Random fmlRandom = new Random(mapSeed);
		long xSeed = fmlRandom.nextLong() >> 2 + 1L;
		long zSeed = fmlRandom.nextLong() >> 2 + 1L;
		long hexSeed = (xSeed * (int)c.point.x + zSeed * (int)c.point.y) ^ mapSeed;

		for (IHexGenerator generator : sortedGeneratorList)
		{
			fmlRandom.setSeed(hexSeed);
			generator.generate(fmlRandom, map, c, world);
		}
	}

	private static void computeSortedGeneratorList()
	{
		ArrayList<IHexGenerator> list = Lists.newArrayList(worldGenerators);
		Collections.sort(list, new Comparator<IHexGenerator>()
		{
			@Override
			public int compare(IHexGenerator o1, IHexGenerator o2)
			{
				return Ints.compare(worldGeneratorIndex.get(o1), worldGeneratorIndex.get(o2));
			}
		});
		sortedGeneratorList = ImmutableList.copyOf(list);
	}
}
