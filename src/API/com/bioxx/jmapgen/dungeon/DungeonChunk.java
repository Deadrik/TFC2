package com.bioxx.jmapgen.dungeon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class DungeonChunk 
{
	public final int chunkX;
	public final int chunkZ;

	private Map<Integer, DungeonRoom> roomMap = new HashMap<Integer, DungeonRoom>();

	public DungeonChunk(int x, int z)
	{
		chunkX = x;
		chunkZ = z;
	}

	public DungeonRoom get(int y)
	{
		return roomMap.get(y);
	}

	public void set(int y, DungeonRoom room)
	{
		roomMap.put(y, room);
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList mapTag = new NBTTagList();
		Iterator iter = roomMap.keySet().iterator();
		while(iter.hasNext())
		{
			int id = (Integer)iter.next();
			NBTTagCompound roomnbt = new NBTTagCompound();
			roomMap.get(id).writeToNBT(roomnbt);
			mapTag.appendTag(roomnbt);
		}
		nbt.setTag("RoomMap", mapTag);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList tagList = nbt.getTagList("RoomMap", 10);
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound roomTag = tagList.getCompoundTagAt(i);
			DungeonRoom dr = new DungeonRoom(null, null);
			dr.readFromNBT(roomTag);
			roomMap.put(dr.position.getY(), dr);
		}
	}



	public Map<Integer, DungeonRoom> getRoomMap()
	{
		return roomMap;
	}

}
