package com.bioxx.tfc2.api;

import java.util.*;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.common.registry.ForgeRegistries;

import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.core.util.FoodReader.FoodJSON;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class FoodRegistry 
{
	private static final FoodRegistry INSTANCE = new FoodRegistry();
	public static final FoodRegistry getInstance()
	{
		return INSTANCE;
	}

	private Multimap<String, ItemStack> cropMap;
	private Map<String, TFCFood> foodMap;

	private FoodRegistry()
	{
		cropMap = ArrayListMultimap.create();
		foodMap = new HashMap<String, TFCFood>();
	}

	public void registerCropProduce(String crop, ItemStack i)
	{
		cropMap.put(crop, i);
	}

	public void registerCropProduce(Crop crop, ItemStack i)
	{
		cropMap.put(crop.getName(), i);
	}

	public Collection<ItemStack> getProduceForCrop(String crop)
	{
		return cropMap.get(crop);
	}

	public void registerFood(FoodJSON json)
	{
		foodMap.put(json.itemName + " " + json.itemMeta, new TFCFood(json));
	}

	public boolean hasKey(Item i, int meta)
	{
		return foodMap.containsKey(ForgeRegistries.ITEMS.getKey(i) + " " + meta);
	}

	public TFCFood getFood(Item i, int meta)
	{
		return foodMap.get(ForgeRegistries.ITEMS.getKey(i) + " " + meta);
	}

	public static class TFCFood
	{
		public long expiration = 72000L;
		public boolean isEdible = true;
		public List<FoodGroupPair> foodGroup = new ArrayList<FoodGroupPair>();

		public TFCFood(FoodJSON f)
		{
			expiration = f.decayTime;
			isEdible = f.isEdible;
			foodGroup = f.foodGroup;
		}

		public String getDisplayString()
		{
			String s = null;
			for(FoodGroupPair fg : foodGroup)
			{
				if(s == null)
					s = "";
				else
					s += TextFormatting.BLACK+"  ";
				s += fg.foodGroup.getColoredAbbrv()+ " " + fg.amount+"%";
			}
			return s;
		}
	}

	public static class FoodGroupPair
	{
		public final EnumFoodGroup foodGroup;
		public final float amount;
		public FoodGroupPair(EnumFoodGroup fg, float a)
		{
			foodGroup = fg;
			amount = a;
		}
	}
}
