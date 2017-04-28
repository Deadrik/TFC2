package com.bioxx.tfc2.handlers;

import java.util.ArrayList;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.HexGenRegistry;

public class ChunkLoadHandler
{
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		if(!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			BlockPos chunkWorldPos = new BlockPos(event.getChunk().xPosition * 16, 0, event.getChunk().zPosition * 16);
			IslandMap map = Core.getMapForWorld(event.getWorld(), chunkWorldPos);

			Point islandPos = new Point(chunkWorldPos.getX(), chunkWorldPos.getZ()).toIslandCoord();
			Center temp = map.getClosestCenter(islandPos);

			ArrayList<Center> genList = new ArrayList<Center>();
			genList.add(temp);
			genList.addAll(temp.neighbors);

			for(Center c : genList)
			{
				AxisAlignedBB aabb = c.getAABB();
				if(!c.hasGenerated)
				{					
					if(Core.areChunksLoadedInArea(event.getWorld().getChunkProvider(), 
							new ChunkPos((int)(map.getParams().getWorldX()+aabb.minX) >> 4, (int)(map.getParams().getWorldZ() + aabb.minZ) >> 4), 
							new ChunkPos((int)(map.getParams().getWorldX()+aabb.maxX) >> 4, (int)(map.getParams().getWorldZ() + aabb.maxZ) >> 4)))
					{
						HexGenRegistry.generate(map, c, event.getWorld());
						c.hasGenerated = true;
					}
				}	
			}

		}

		//animal stuff
		/*if(!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			BlockPos chunkWorldPos = new BlockPos(event.getChunk().xPosition * 16, 0, event.getChunk().zPosition * 16);
			Point islandPos = new Point(chunkWorldPos.getX(), chunkWorldPos.getZ()).toIslandCoord();
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
		}*/
	}
}
