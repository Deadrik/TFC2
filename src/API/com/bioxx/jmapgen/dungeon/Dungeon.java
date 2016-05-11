package com.bioxx.jmapgen.dungeon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bioxx.jmapgen.IslandMap;

public class Dungeon 
{
	String theme = "generic";
	public Map<String, IBlockState> blockMap = new HashMap<String, IBlockState>();
	private DungeonChunk[] dungeonMap;
	private int size;
	private int dungeonX, dungeonY, dungeonZ;

	public Dungeon(String theme, int size, int x, int y, int z)
	{
		this.theme = theme;
		dungeonMap = new DungeonChunk[size*size];
		this.size = size;
		dungeonX = x;
		dungeonY = y;
		dungeonZ = z;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("theme", theme);

		NBTTagList blockMapTag = new NBTTagList();
		Iterator<String> iter = blockMap.keySet().iterator();
		while(iter.hasNext())
		{
			String key = iter.next();
			IBlockState state = blockMap.get(key);
			NBTTagCompound blockTag = new NBTTagCompound();
			blockTag.setString("Key", key);
			blockTag.setString("Block", state.getBlock().getRegistryName().toString());
			blockTag.setInteger("Meta", state.getBlock().getMetaFromState(state));
			blockMapTag.appendTag(blockTag);
		}
		nbt.setTag("BlockMap", blockMapTag);
		nbt.setInteger("xPos", dungeonX);
		nbt.setInteger("yPos", dungeonY);
		nbt.setInteger("zPos", dungeonZ);
	}

	public void readFromNBT(IslandMap map, NBTTagCompound nbt)
	{
		theme = nbt.getString("theme");
		NBTTagList blockMapTag = nbt.getTagList("BlockMap", 10);
		for(int i = 0; i < blockMapTag.tagCount(); i++)
		{
			NBTTagCompound blockTag = blockMapTag.getCompoundTagAt(i);
			String key = blockTag.getString("Key");
			Block b = Block.getBlockFromName(blockTag.getString("Block"));
			IBlockState state = b.getStateFromMeta(blockTag.getInteger("Meta"));
			blockMap.put(key, state);
		}
		dungeonX = nbt.getInteger("xPos");
		dungeonY = nbt.getInteger("yPos");
		dungeonZ = nbt.getInteger("zPos");
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
		DungeonChunk dc = dungeonMap[x * size + z];
		if(dc == null)
		{
			dc = dungeonMap[x * size + z] = new DungeonChunk(dungeonX+x, dungeonZ+z);
		}
		return dc;
	}

	/**
	 * This is the X Coordinate of the dungeon
	 */
	public int getDungeonX() {
		return dungeonX;
	}

	/**
	 * This is 0-255 and would be the y level of the bottom of the first floor schematic
	 */
	public int getDungeonY() {
		return dungeonY;
	}
	/**
	 * This is the Z Coordinate of the dungeon
	 */
	public int getDungeonZ() {
		return dungeonZ;
	}

	public int getSize() {
		return size;
	}
}
