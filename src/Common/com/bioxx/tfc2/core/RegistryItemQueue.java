package com.bioxx.tfc2.core;

import java.util.LinkedList;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.interfaces.IRegisterSelf;
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
		listMesh.add(new Entry(i, i.getRegistryName().getResourcePath()));
		listItem.add(new Entry(i, i.getRegistryName().getResourcePath()));
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
			GameRegistry.register(e.item);

			if(TFC.proxy.isClientSide())
				registerEntry(e);
		}

	}

	private void registerEntry(Entry e)
	{
		if(e.item instanceof IRegisterSelf)
		{
			for(int c = 0; c < ((IRegisterSelf)e.item).getSubTypeNames().length; c++)
			{
				String path = ((IRegisterSelf)e.item).getPath();
				String subName = ((IRegisterSelf)e.item).getSubTypeNames()[c];
				ModelLoader.setCustomModelResourceLocation(e.item, c, new ModelResourceLocation(Reference.ModID + ":"+path+subName, "inventory"));
			}
		}
		else
		{
			ModelLoader.setCustomModelResourceLocation(e.item, 0, new ModelResourceLocation(Reference.ModID + ":"+e.item.getRegistryName().getResourcePath(), "inventory"));
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerMeshes()
	{
		Entry e; 
		while (!listMesh.isEmpty())
		{
			e = listMesh.pop();
			ModelLoader.setCustomMeshDefinition(e.item, new MeshDef(new ModelResourceLocation(Reference.ModID + ":"+e.item.getRegistryName(), "inventory")));
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
