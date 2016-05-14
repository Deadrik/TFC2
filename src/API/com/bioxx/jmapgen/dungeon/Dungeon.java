package com.bioxx.jmapgen.dungeon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.api.util.Helper;

public class Dungeon 
{
	String theme = "generic";
	public Map<String, IBlockState> blockMap = new HashMap<String, IBlockState>();
	private Map <Integer, DungeonChunk> dungeonMap;
	public RoomPos dungeonStart;

	public Dungeon(String theme, int x, int y, int z)
	{
		this.theme = theme;
		dungeonMap = new HashMap<Integer, DungeonChunk>();
		dungeonStart = new RoomPos(x, y, z);
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("theme", theme);
		nbt.setInteger("xPos", dungeonStart.getX());
		nbt.setInteger("yPos", dungeonStart.getY());
		nbt.setInteger("zPos", dungeonStart.getZ());

		NBTTagList blockMapTag = new NBTTagList();
		Iterator iter = blockMap.keySet().iterator();
		while(iter.hasNext())
		{
			String key = (String)iter.next();
			IBlockState state = blockMap.get(key);
			NBTTagCompound blockTag = new NBTTagCompound();
			blockTag.setString("Key", key);
			blockTag.setString("Block", state.getBlock().getRegistryName().toString());
			blockTag.setInteger("Meta", state.getBlock().getMetaFromState(state));
			blockMapTag.appendTag(blockTag);
		}
		nbt.setTag("BlockMap", blockMapTag);

		NBTTagList chunkMapTag = new NBTTagList();
		iter = dungeonMap.keySet().iterator();
		while(iter.hasNext())
		{
			int id = (Integer)iter.next();
			NBTTagCompound chunknbt = new NBTTagCompound();
			chunknbt.setInteger("id", id);
			dungeonMap.get(id).writeToNBT(chunknbt);
			chunkMapTag.appendTag(chunknbt);
		}
		nbt.setTag("ChunkMap", chunkMapTag);

	}

	public void readFromNBT(IslandMap map, NBTTagCompound nbt)
	{
		theme = nbt.getString("theme");
		this.dungeonStart = new RoomPos(nbt.getInteger("xPos"), nbt.getInteger("yPos"), nbt.getInteger("zPos"));
		NBTTagList tagList = nbt.getTagList("BlockMap", 10);
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound blockTag = tagList.getCompoundTagAt(i);
			String key = blockTag.getString("Key");
			Block b = Block.getBlockFromName(blockTag.getString("Block"));
			IBlockState state = b.getStateFromMeta(blockTag.getInteger("Meta"));
			blockMap.put(key, state);
		}

		tagList = nbt.getTagList("ChunkMap", 10);
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound blockTag = tagList.getCompoundTagAt(i);
			int id = blockTag.getInteger("id");
			DungeonChunk dc = new DungeonChunk(Helper.getXCoord(id), Helper.getYCoord(id));
			dc.readFromNBT(blockTag);
			this.setChunk(dc, dc.chunkX, dc.chunkZ);
		}

	}

	public String getTheme()
	{
		return theme;
	}

	public void setRoom(DungeonRoom room)
	{
		getChunk(room.getPosition().getX(), room.getPosition().getZ()).set(room.getPosition().getY(), room);
	}

	public void setRoom(RoomPos pos, DungeonRoom room)
	{
		getChunk(pos.getX(), pos.getZ()).set(pos.getY(), room);
	}

	public void setRoom(int x, int y, int z, DungeonRoom room)
	{
		getChunk(x, z).set(y, room);
	}

	public DungeonRoom getRoom(int x, int y, int z)
	{
		return getChunk(x, z).get(y);
	}

	public DungeonRoom getRoom(RoomPos pos)
	{
		return getRoom(pos.getX(), pos.getY(), pos.getZ());
	}

	public DungeonChunk getChunk(int x, int z)
	{
		DungeonChunk dc = dungeonMap.get(Helper.combineCoords(x, z));
		if(dc == null)
		{
			dc = new DungeonChunk(x, z);
			dungeonMap.put(Helper.combineCoords(x, z), dc);
		}
		return dc;
	}

	public void setChunk(DungeonChunk dc, int x, int z)
	{
		dungeonMap.put(Helper.combineCoords(x, z), dc);
	}

	public void resetDungeonMap()
	{
		dungeonMap.clear();
	}

	public int getRoomCount()
	{
		int count = 0;

		for(DungeonChunk c : dungeonMap.values())
		{
			Iterator iter = c.getRoomMap().values().iterator();
			while(iter.hasNext())
			{
				iter.next();
				count++;
			}
		}

		return count;
	}
}
