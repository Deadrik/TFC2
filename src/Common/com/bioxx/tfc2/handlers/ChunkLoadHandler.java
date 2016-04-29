package com.bioxx.tfc2.handlers;

import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;
import com.bioxx.tfc2.world.AnimalSpawner;

public class ChunkLoadHandler
{
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		if(!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			BlockPos chunkWorldPos = new BlockPos(event.getChunk().xPosition * 16, 0, event.getChunk().zPosition * 16);
			Point islandPos = new Point(chunkWorldPos.getX() % 4096, chunkWorldPos.getZ() % 4096);
			IslandMap map = Core.getMapForWorld(event.getWorld(), chunkWorldPos);
			Center centerInChunk = null;

			Center temp = map.getClosestCenter(islandPos);
			if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
				centerInChunk = temp;
			else 
			{
				temp = map.getClosestCenter(islandPos.plus(15, 0));
				if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
					centerInChunk = temp;
				else
				{
					temp = map.getClosestCenter(islandPos.plus(0, 15));
					if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
						centerInChunk = temp;
					else
					{
						temp = map.getClosestCenter(islandPos.plus(15, 15));
						if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
							centerInChunk = temp;
					}
				}
			}

			if(centerInChunk != null)
			{
				if(centerInChunk.getCustomNBT().hasKey("animalsToSpawn"))
				{
					NBTTagCompound tag = centerInChunk.getCustomNBT().getCompoundTag("animalsToSpawn");
					Iterator iter = tag.getKeySet().iterator();
					while(iter.hasNext())
					{
						String key = (String)iter.next();
						String groupName = tag.getString(key);
						SpawnGroup group = AnimalSpawnRegistry.getInstance().getGroupFromName(groupName);
						AnimalSpawner.SpawnAnimalGroup(event.getWorld(), group, event.getChunk());
						tag.removeTag(key);
					}
					if(tag.hasNoTags())
						centerInChunk.getCustomNBT().removeTag("animalsToSpawn");
					else
						centerInChunk.getCustomNBT().setTag("animalsToSpawn", tag);
				}
			}
		}
	}
}
