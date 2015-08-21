package com.bioxx.jmapgen.attributes;

import java.util.UUID;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bioxx.jmapgen.processing.CaveAttrNode;

public class CaveAttribute extends Attribute 
{
	public Vector<CaveAttrNode> nodes = new Vector<CaveAttrNode>();

	public CaveAttribute() 
	{
		super(Attribute.Cave);
	}

	public CaveAttribute(UUID i) 
	{
		super(i);
	}

	public void addNode(CaveAttrNode n)
	{
		nodes.add(n);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("uuid", id.toString());
		NBTTagList nodeList = new NBTTagList();
		for(CaveAttrNode n : nodes)
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
				CaveAttrNode c = new CaveAttrNode(0);
				c.readFromNBT(aNBT, m);
				nodes.add(c);
			}
		}
	}

}
