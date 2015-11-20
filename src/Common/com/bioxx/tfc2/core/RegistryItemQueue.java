package com.bioxx.tfc2.core;

import java.util.LinkedList;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.rendering.MeshDef;

/**
 * This class is meant to facilitate loading simple objects without the need to always write a new line to 
 * register the object or load the items mesh in the proxy. Simply add your item to the registry 
 * and call registerItems() where you would normally make your GameRegistry registrations and call 
 * registerMeshes() once in the client proxy and it will register meshes for all of the items in the 
 * queue at that point.
 * @author Bioxx
 */
public class RegistryItemQueue 
{
	LinkedList<Entry> listMesh = new LinkedList<Entry>();
	LinkedList<Entry> listItem = new LinkedList<Entry>();
	static RegistryItemQueue instance = new RegistryItemQueue();
	public static RegistryItemQueue getInstance()
	{
		return instance;
	}

	/**
	 * Adds this item to both queues for registration. Use this method for registration of simple items with no subtypes.
	 */
	public void addFull(Item i)
	{
		listMesh.add(new Entry(i, i.getUnlocalizedName().replace("item.", "")));
		listItem.add(new Entry(i, i.getUnlocalizedName().replace("item.", "")));
	}

	/**
	 * Adds this item to the item registry queue only. For items with multiple subtypes that 
	 * need to have their meshes registered manually.
	 */
	public void addItemOnly(Item i)
	{
		listItem.add(new Entry(i, i.getUnlocalizedName().replace("item.", "")));
	}

	public void registerItems()
	{
		Entry e; 
		while (!listItem.isEmpty())
		{
			e = listItem.pop();
			GameRegistry.registerItem(e.item, e.name);
		}

	}

	public void registerMeshes()
	{
		Entry e; 
		while (!listMesh.isEmpty())
		{
			e = listMesh.pop();
			ModelLoader.setCustomMeshDefinition(e.item, new MeshDef(new ModelResourceLocation(Reference.ModID + ":"+e.name, "inventory")));
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
