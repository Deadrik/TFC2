package com.bioxx.tfc2.handlers;

import net.minecraft.block.Block;

import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import com.bioxx.tfc2.core.Food;

public class BlockHarvestHandler 
{
	@ObjectHolder("harvestcraft:aridGarden")
	public static final Block aridGarden = null;
	@ObjectHolder("harvestcraft:frostGarden")
	public static final Block frostGarden = null;
	@ObjectHolder("harvestcraft:tropicalGarden")
	public static final Block tropicalGarden = null;
	@ObjectHolder("harvestcraft:windyGarden")
	public static final Block windyGarden = null;
	@ObjectHolder("harvestcraft:shadedGarden")
	public static final Block shadedGarden = null;
	@ObjectHolder("harvestcraft:soggyGarden")
	public static final Block soggyGarden = null;

	@SubscribeEvent
	public void onBlockHarvest(HarvestDropsEvent event)
	{
		Block block = event.getState().getBlock();
		if(block == aridGarden || block == frostGarden || block == tropicalGarden || block == windyGarden || block == shadedGarden || block == soggyGarden)
			Food.addDecayTimerForCreative(event.getWorld(), event.getDrops());
	}
}
