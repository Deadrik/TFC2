package com.bioxx.tfc2.api.interfaces;

import java.util.Random;

import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

public interface IHexGenerator {

	public void generate(Random random, IslandMap map, Center c, World world);
}
