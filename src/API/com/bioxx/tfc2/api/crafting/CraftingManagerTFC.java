package com.bioxx.tfc2.api.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.IRecipeTFC;

public class CraftingManagerTFC
{
	private static final CraftingManagerTFC INSTANCE = new CraftingManagerTFC();
	public static final CraftingManagerTFC getInstance()
	{
		return INSTANCE;
	}

	private List<IRecipeTFC> recipes;
	private List<IRecipeTFC> recipes_knapping;
	private List<IRecipeTFC> recipes_anvil;

	private CraftingManagerTFC()
	{
		recipes = new ArrayList<IRecipeTFC>();
		recipes_knapping = new ArrayList<IRecipeTFC>();
		recipes_anvil = new ArrayList<IRecipeTFC>();

		Collections.sort(recipes, new RecipeSorterTFC(this));
		Collections.sort(recipes_knapping, new RecipeSorterTFC(this));
		Collections.sort(recipes_anvil, new RecipeSorterTFC(this));
	}

	public ShapedOreRecipeTFC addRecipe(ItemStack itemstack, Object... aobj)
	{
		return addRecipe(RecipeType.NORMAL, itemstack, aobj);
	}

	public ShapedOreRecipeTFC addRecipe(RecipeType rt, ItemStack itemstack, Object... aobj)
	{
		/*String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		if (aobj[i] instanceof String[])
		{
			String as[] = (String[])aobj[i++];
			for (int l = 0; l < as.length; l++)
			{
				String s2 = as[l];
				k++;
				j = s2.length();
				s = s + s2;
			}
		}
		else
		{
			while (aobj[i] instanceof String)
			{
				String s1 = (String)aobj[i++];
				k++;
				j = s1.length();
				s = s + s1;
			}
		}
		HashMap<Character, ItemStack> hashmap = new HashMap<Character, ItemStack>();
		for (; i < aobj.length; i += 2)
		{
			Character character = (Character)aobj[i];
			ItemStack itemstack1 = null;
			if (aobj[i + 1] instanceof Item)
			{
				itemstack1 = new ItemStack((Item)aobj[i + 1]);
			}
			else if (aobj[i + 1] instanceof Block)
			{
				itemstack1 = new ItemStack((Block)aobj[i + 1], 1, -1);
			}
			else if (aobj[i + 1] instanceof ItemStack)
			{
				itemstack1 = (ItemStack)aobj[i + 1];
			}
			hashmap.put(character, itemstack1);
		}

		List<ItemStack> aitemstack = new ArrayList<ItemStack>(j * k);
		for (int i1 = 0; i1 < j * k; i1++)
		{
			char c = s.charAt(i1);
			if (hashmap.containsKey(Character.valueOf(c)))
			{
				aitemstack.add(i1, hashmap.get(Character.valueOf(c)).copy());
			}
			else
			{
				aitemstack.add(i1, null);
			}
		}*/

		ShapedOreRecipeTFC shapedRecipesTFC = new ShapedOreRecipeTFC(itemstack, aobj);
		if(rt == RecipeType.NORMAL)
			recipes.add(shapedRecipesTFC);
		else if(rt == RecipeType.KNAPPING)
			recipes_knapping.add(shapedRecipesTFC);
		else if(rt == RecipeType.ANVIL)
			recipes_anvil.add(shapedRecipesTFC);
		return shapedRecipesTFC;
	}

	public ShapelessOreRecipeTFC addShapelessRecipe(ItemStack itemstack, Object... aobj)
	{
		return addShapelessRecipe(RecipeType.NORMAL, itemstack, aobj);
	}

	public ShapelessOreRecipeTFC addShapelessRecipe(RecipeType rt, ItemStack itemstack, Object... aobj)
	{
		/*ArrayList<ItemStack> arraylist = new ArrayList<ItemStack>();
		Object aobj1[] = aobj;
		int i = aobj1.length;
		for (int j = 0; j < i; j++)
		{
			Object obj = aobj1[j];
			if (obj instanceof ItemStack)
			{
				arraylist.add(((ItemStack)obj).copy());
				continue;
			}
			if (obj instanceof Item)
			{
				arraylist.add(new ItemStack((Item)obj));
				continue;
			}
			if (obj instanceof Block)
			{
				arraylist.add(new ItemStack((Block)obj));
			}
			else
			{
				throw new RuntimeException("Invalid shapeless recipe!");
			}
		}*/
		ShapelessOreRecipeTFC recipesTFC = new ShapelessOreRecipeTFC(itemstack, aobj);
		if(rt == RecipeType.NORMAL)
			recipes.add(recipesTFC);
		else if(rt == RecipeType.KNAPPING)
			recipes_knapping.add(recipesTFC);
		else if(rt == RecipeType.ANVIL)
			recipes_anvil.add(recipesTFC);
		return recipesTFC;
	}

	public ItemStack findMatchingRecipe(InventoryCrafting inventorycrafting, World world)
	{
		return findMatchingRecipe(RecipeType.NORMAL, inventorycrafting, world);
	}

	public ItemStack findMatchingRecipe(RecipeType rt, InventoryCrafting inventorycrafting, World world)
	{
		List<IRecipeTFC> rec = Collections.emptyList();
		if(rt == RecipeType.NORMAL)
			rec = recipes;
		else if(rt == RecipeType.KNAPPING)
			rec = recipes_knapping;
		else if(rt == RecipeType.ANVIL)
			rec = recipes_anvil;

		for (int k = 0; k < rec.size(); k++)
		{
			IRecipeTFC irecipe = rec.get(k);
			if (irecipe.matches(inventorycrafting, world))
			{
				return irecipe.getCraftingResult(inventorycrafting);
			}
		}

		return null;
	}

	public List<IRecipeTFC> getRecipeList(RecipeType rt)
	{
		if(rt == RecipeType.KNAPPING)
			return recipes_knapping;
		else if(rt == RecipeType.ANVIL)
			return recipes_anvil;

		return recipes;
	}

	public enum RecipeType
	{
		NORMAL, KNAPPING, ANVIL;
	}

}
