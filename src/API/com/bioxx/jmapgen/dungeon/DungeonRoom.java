package com.bioxx.jmapgen.dungeon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class DungeonRoom 
{
	RoomSchematic schematic;
	Map<DungeonDirection, RoomLink> linkMap = new HashMap<DungeonDirection, RoomLink>();
	RoomPos position;

	public DungeonRoom(RoomSchematic rs, RoomPos pos)
	{
		schematic = rs;
		position = pos;
	}

	public void addConnection(DungeonDirection c, RoomLink r)
	{
		linkMap.put(c, r);
	}

	public RoomLink getConnection(DungeonDirection c)
	{
		if(linkMap.containsKey(c))
			return linkMap.get(c);
		else return null;
	}

	public boolean hasConnection(DungeonDirection c)
	{
		return linkMap.containsKey(c);
	}

	public RoomPos getPosition() {
		return position;
	}

	public void removeConnection(DungeonDirection dir)
	{
		linkMap.remove(dir);
	}

	public void clearConnections(Dungeon d)
	{
		for(DungeonDirection dir : DungeonDirection.values())
		{
			RoomLink rl = getConnection(dir);
			if(rl != null)
			{
				DungeonRoom dr = d.getRoom(this.getPosition().offset(dir));
				if(dr != null)
					dr.removeConnection(dir.getOpposite());
				removeConnection(dir);
			}
		}
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("SchemTheme", schematic.getTheme());
		nbt.setString("SchemName", schematic.getFileName());
		nbt.setInteger("xPos", position.getX());
		nbt.setInteger("yPos", position.getY());
		nbt.setInteger("zPos", position.getZ());
		NBTTagList linkTag = new NBTTagList();
		Iterator iter = linkMap.keySet().iterator();
		while(iter.hasNext())
		{
			DungeonDirection dir = (DungeonDirection)iter.next();
			NBTTagCompound linkNBT = new NBTTagCompound();
			linkNBT.setString("dir", dir.name);
			linkMap.get(dir).writeToNBT(linkNBT);
			linkTag.appendTag(linkNBT);
		}
		nbt.setTag("LinkMap", linkTag);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		DungeonSchemManager dsm = DungeonSchemManager.getInstance();
		schematic = dsm.getSchematic(nbt.getString("SchemTheme"), nbt.getString("SchemName"));
		this.position = new RoomPos(nbt.getInteger("xPos"), nbt.getInteger("yPos"), nbt.getInteger("zPos"));

		NBTTagList tagList = nbt.getTagList("LinkMap", 10);
		for(int i = 0; i < tagList.tagCount(); i++)
		{
			NBTTagCompound roomTag = tagList.getCompoundTagAt(i);
			RoomLink link = new RoomLink(false);
			link.readFromNBT(roomTag);
			linkMap.put(DungeonDirection.fromString(roomTag.getString("dir")), link);
		}
	}

	public RoomSchematic getSchematic() {
		return schematic;
	}
}
