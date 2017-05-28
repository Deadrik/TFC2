/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.bioxx.tfc2.api.crafting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc2.api.interfaces.IRecipeTFC;

public class ShapelessOreRecipeTFC implements IRecipeTFC
{
	protected ItemStack output = ItemStack.EMPTY;
	protected List<Object> input = new ArrayList<Object>();
	public boolean isRepairRecipe = false;

	public ShapelessOreRecipeTFC(Block result, Object... recipe){ this(new ItemStack(result), recipe); }
	public ShapelessOreRecipeTFC(Item  result, Object... recipe){ this(new ItemStack(result), recipe); }

	public ShapelessOreRecipeTFC(ItemStack result, Object... recipe)
	{
		output = result.copy();
		for (Object in : recipe)
		{
			if (in instanceof ItemStack)
			{
				input.add(((ItemStack)in).copy());
			}
			else if (in instanceof Item)
			{
				input.add(new ItemStack((Item)in));
			}
			else if (in instanceof Block)
			{
				input.add(new ItemStack((Block)in));
			}
			else if (in instanceof String)
			{
				input.add(OreDictionary.getOres((String)in));
			}
			else
			{
				String ret = "Invalid shapeless ore recipe: ";
				for (Object tmp :  recipe)
				{
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}
	}

	ShapelessOreRecipeTFC(ShapelessRecipes recipe, Map<ItemStack, String> replacements)
	{
		output = recipe.getRecipeOutput();

		for(ItemStack ingredient : recipe.recipeItems)
		{
			Object finalObj = ingredient;
			for(Entry<ItemStack, String> replace : replacements.entrySet())
			{
				if(OreDictionary.itemMatches(replace.getKey(), ingredient, false))
				{
					finalObj = OreDictionary.getOres(replace.getValue());
					break;
				}
			}
			input.add(finalObj);
		}
	}

	/**
	 * Returns the size of the recipe area
	 */
	@Override
	public int getRecipeSize(){ return input.size(); }

	@Override
	public ItemStack getRecipeOutput(){ return output; }

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1){ return output.copy(); }

	@Override
	public boolean matches(InventoryCrafting var1, World world)
	{
		ArrayList<Object> required = new ArrayList<Object>(input);

		for (int x = 0; x < var1.getSizeInventory(); x++)
		{
			ItemStack slot = var1.getStackInSlot(x);

			if (slot != ItemStack.EMPTY)
			{
				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();

				while (req.hasNext())
				{
					boolean match = false;

					Object next = req.next();

					if (next instanceof ItemStack)
					{
						match = OreDictionary.itemMatches((ItemStack)next, slot, false);
					}
					else if (next instanceof List)
					{
						Iterator<ItemStack> itr = ((List<ItemStack>)next).iterator();
						while (itr.hasNext() && !match)
						{
							match = OreDictionary.itemMatches(itr.next(), slot, false);
						}
					}

					if (match)
					{
						if(!tempMatch(slot))
						{
							break;
						}
						inRecipe = true;
						required.remove(next);
						break;
					}


				}

				if (!inRecipe)
				{
					return false;
				}
			}
		}



		return required.isEmpty();
	}
	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(NonNullList<ItemStack> var1, World world)
	{
		ArrayList<Object> required = new ArrayList<Object>(input);

		for (int x = 0; x < var1.size(); x++)
		{
			ItemStack slot = var1.get(x);

			if (slot != ItemStack.EMPTY)
			{
				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();

				while (req.hasNext())
				{
					boolean match = false;

					Object next = req.next();

					if (next instanceof ItemStack)
					{
						match = OreDictionary.itemMatches((ItemStack)next, slot, false);
					}
					else if (next instanceof List)
					{
						Iterator<ItemStack> itr = ((List<ItemStack>)next).iterator();
						while (itr.hasNext() && !match)
						{
							match = OreDictionary.itemMatches(itr.next(), slot, false);
						}
					}

					if (match)
					{
						if(!tempMatch(slot))
						{
							break;
						}
						inRecipe = true;
						required.remove(next);
						break;
					}


				}

				if (!inRecipe)
				{
					return false;
				}
			}
		}



		return required.isEmpty();
	}

	/**
	 * Returns the input for this recipe, any mod accessing this value should never
	 * manipulate the values in this array as it will effect the recipe itself.
	 * @return The recipes input vales.
	 */
	public ArrayList<Object> getInput()
	{
		return (ArrayList<Object>) this.input;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) //getRecipeLeftovers
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	private boolean tempMatch(ItemStack inputIS)
	{
		/*NBTTagCompound rnbt = recipeIS.getTagCompound();
		NBTTagCompound inbt = inputIS.getTagCompound();

		if(rnbt != null && rnbt.hasKey("noTemp"))
		{
			//Recipe expects a cold item and either the input has not tag at all or at the least is missing a temperature tag
			return inbt == null || !TFC_ItemHeat.hasTemp(inputIS);
		}

		if(rnbt != null && TFC_ItemHeat.hasTemp(recipeIS))
		{
			if(inbt != null && TFC_ItemHeat.hasTemp(inputIS))
			{
				return HeatRegistry.getInstance().getIsLiquid(inputIS);//Recipe expects a hot item and the input is liquid
			}
			else
			{
				return false;//Recipe expects a cold item and the input is not cold
			}
		}
		 */
		return true;
	}
	@Override
	public List<Object> getRecipeItems() {
		return input;
	}
	@Override
	public int getRecipeWidth() {
		return this.input.size();
	}
	@Override
	public int getRecipeHeight() {
		return this.input.size();
	}
	@Override
	public boolean isRepairRecipe() {
		return isRepairRecipe;
	}
}