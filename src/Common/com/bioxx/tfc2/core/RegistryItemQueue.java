package com.bioxx.tfc2.core;

import java.util.LinkedList;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

import com.bioxx.tfc2.Reference;

/**
 * This class is meant to facilitate loading simple objects without the need to always write a new line to load 
 * the items mesh in the proxy. Simply add your item and its name to the registry and call registerQueue() once
 * in the client proxy and it will register meshes for all of the items in the queue at that point.
 * @author Bioxx
 *
 */
public class RegistryItemQueue 
{
	LinkedList<Entry> list = new LinkedList<Entry>();
	static RegistryItemQueue instance = new RegistryItemQueue();
	public static RegistryItemQueue getInstance()
	{
		return instance;
	}

	public void addItemToQueue(Item i, String name)
	{
		list.add(new Entry(i, name));
	}

	public void registerQueue()
	{
		Entry e; 
		while (!list.isEmpty())
		{
			e = list.pop();
			net.minecraft.client.Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(e.item, 0, new ModelResourceLocation(Reference.ModID + ":"+e.name, "inventory"));
		}

	}

	public class Entry
	{
		public Item item;
		public String name;

		public Entry(Item i, String n)
		{
			item = i;
			name = n;
		}
	}
}
