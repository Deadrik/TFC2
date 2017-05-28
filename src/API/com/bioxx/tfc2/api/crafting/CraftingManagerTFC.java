package com.bioxx.tfc2.api.crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.IRecipeTFC;
import com.bioxx.tfc2.items.ItemTerraTool;

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
	private List<IRecipeTFC> recipes_pottery;

	private CraftingManagerTFC()
	{
		recipes = new ArrayList<IRecipeTFC>();
		recipes_knapping = new ArrayList<IRecipeTFC>();
		recipes_anvil = new ArrayList<IRecipeTFC>();
		recipes_pottery = new ArrayList<IRecipeTFC>();

		Collections.sort(recipes, new RecipeSorterTFC(this));
		Collections.sort(recipes_knapping, new RecipeSorterTFC(this));
		Collections.sort(recipes_anvil, new RecipeSorterTFC(this));
		Collections.sort(recipes_pottery, new RecipeSorterTFC(this));
	}

	public ShapedOreRecipeTFC addRecipe(ItemStack itemstack, Object... aobj)
	{
		return addRecipe(RecipeType.NORMAL, itemstack, aobj);
	}

	public ShapedOreRecipeTFC addRecipe(RecipeType rt, ItemStack itemstack, Object... aobj)
	{
		ShapedOreRecipeTFC shapedRecipesTFC = new ShapedOreRecipeTFC(itemstack, aobj);
		if(rt == RecipeType.NORMAL)
			recipes.add(shapedRecipesTFC);
		else if(rt == RecipeType.KNAPPING)
			recipes_knapping.add(shapedRecipesTFC);
		else if(rt == RecipeType.ANVIL)
			recipes_anvil.add(shapedRecipesTFC);
		else if(rt == RecipeType.POTTERY)
			recipes_pottery.add(shapedRecipesTFC);
		return shapedRecipesTFC;
	}

	public ShapelessOreRecipeTFC addShapelessRecipe(ItemStack itemstack, Object... aobj)
	{
		return addShapelessRecipe(RecipeType.NORMAL, itemstack, aobj);
	}

	public ShapelessOreRecipeTFC addShapelessRecipe(RecipeType rt, ItemStack itemstack, Object... aobj)
	{
		ShapelessOreRecipeTFC recipesTFC = new ShapelessOreRecipeTFC(itemstack, aobj);
		if(rt == RecipeType.NORMAL_REPAIR)
		{
			recipesTFC.isRepairRecipe = true;
		}
		if(rt == RecipeType.NORMAL || rt == RecipeType.NORMAL_REPAIR)
			recipes.add(recipesTFC);
		else if(rt == RecipeType.KNAPPING)
			recipes_knapping.add(recipesTFC);
		else if(rt == RecipeType.ANVIL)
			recipes_anvil.add(recipesTFC);
		else if(rt == RecipeType.POTTERY)
			recipes_pottery.add(recipesTFC);
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
		else if(rt == RecipeType.POTTERY)
			rec = recipes_pottery;

		for (int k = 0; k < rec.size(); k++)
		{
			IRecipeTFC irecipe = rec.get(k);
			if (irecipe.matches(inventorycrafting, world))
			{
				ItemStack out = irecipe.getCraftingResult(inventorycrafting);
				if(irecipe.isRepairRecipe())
				{
					for(int i = 0; i < inventorycrafting.getSizeInventory(); i++)
					{
						ItemStack is = inventorycrafting.getStackInSlot(i);
						if(is.getItem() == out.getItem())
						{
							int dam = is.getItemDamage();
							out.setItemDamage(dam/2);
							if(is.hasTagCompound() && ! out.hasTagCompound())
								out.setTagCompound(is.getTagCompound());

							if(out.getItem() instanceof ItemTerraTool)
							{
								((ItemTerraTool)out.getItem()).onRepair(out);
							}

							break;
						}
					}
				}

				return out;
			}
		}

		return ItemStack.EMPTY;
	}

	public List<IRecipeTFC> getRecipeList(RecipeType rt)
	{
		if(rt == RecipeType.KNAPPING)
			return recipes_knapping;
		else if(rt == RecipeType.ANVIL)
			return recipes_anvil;
		else if(rt == RecipeType.POTTERY)
			return recipes_pottery;
		return recipes;
	}

	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World worldIn)
	{
		for (IRecipe irecipe : this.recipes)
		{
			if (irecipe.matches(craftMatrix, worldIn))
			{
				return irecipe.getRemainingItems(craftMatrix);
			}
		}

		NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(craftMatrix.getSizeInventory(), ItemStack.EMPTY);

		/*for (int i = 0; i < nonnulllist.size(); ++i)
		{
			nonnulllist.set(i, craftMatrix.getStackInSlot(i));
		}*/

		return nonnulllist;
	}

	public enum RecipeType
	{
		NORMAL, NORMAL_REPAIR, KNAPPING, ANVIL, POTTERY;
	}

}
