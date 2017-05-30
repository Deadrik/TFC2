package com.bioxx.tfc2.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.bioxx.tfc2.api.types.EnumSize;
import com.bioxx.tfc2.api.types.EnumWeight;
import com.bioxx.tfc2.api.util.SizeWeightReader.SizeWeightJSON;

public class SizeWeightRegistry 
{
	private static SizeWeightRegistry instance = new SizeWeightRegistry();
	public static SizeWeightRegistry GetInstance(){return instance;};

	private Map<String, SizeWeightProp> propertyMap = new HashMap<String, SizeWeightProp>();

	public void addProperty(SizeWeightJSON json)
	{
		String key = json.itemName + " " + json.itemMeta;
		if(!propertyMap.containsKey(key))
		{
			propertyMap.put(key, new SizeWeightProp(json.size, json.weight));
		}
	}

	public SizeWeightProp getProperty(ItemStack is)
	{
		int meta = is.getItem().getHasSubtypes() ? is.getItemDamage() : -1;
		String key = ForgeRegistries.ITEMS.getKey(is.getItem()).toString();
		SizeWeightProp prop = propertyMap.get(key + " " + meta);
		if(prop == null && meta >= 0 && is.getItem().getHasSubtypes())
		{
			prop = propertyMap.get(key + " -1");
		}

		if(prop != null)
			return prop;

		return new SizeWeightProp(EnumSize.SMALL, EnumWeight.VERYLIGHT);
	}

	public static class SizeWeightProp
	{
		public final EnumSize size;
		public final EnumWeight weight;

		public SizeWeightProp(EnumSize s, EnumWeight w)
		{
			size = s;
			weight = w;
		}
	}
}
