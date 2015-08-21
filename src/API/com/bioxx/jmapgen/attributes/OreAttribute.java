package com.bioxx.jmapgen.attributes;

import java.util.UUID;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bioxx.jmapgen.processing.OreAttrNode;

public class OreAttribute extends Attribute 
{
	public Vector<OreAttrNode> nodes = new Vector<OreAttrNode>();

	public OreAttribute() 
	{
		super(Attribute.Ore);
	}

	public OreAttribute(UUID i) 
	{
		super(i);
	}

	public void addNode(OreAttrNode n)
	{
		nodes.add(n);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("uuid", id.toString());
		NBTTagList nodeList = new NBTTagList();
		for(OreAttrNode n : nodes)
		{
			NBTTagCompound nn = new NBTTagCompound();
			n.writeToNBT(nn);
			nodeList.appendTag(nn);
		}
		nbt.setTag("nodes", nodeList);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jmapgen.IslandMap m) 
	{
		this.id = UUID.fromString(nbt.getString("uuid"));
		if(nbt.hasKey("nodes"))
		{
			NBTTagList list = nbt.getTagList("nodes", 10);
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound aNBT = list.getCompoundTagAt(i);
				OreAttrNode c = new OreAttrNode("");
				c.readFromNBT(aNBT, m);
				nodes.add(c);
			}
		}
	}

}
