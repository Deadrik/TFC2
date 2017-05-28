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

import java.util.*;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc2.api.interfaces.IRecipeTFC;

public class ShapedOreRecipeTFC implements IRecipeTFC
{
	//Added in for future ease of change, but hard coded for now.
	public static final int MAX_CRAFT_GRID_WIDTH = 9;
	public static final int MAX_CRAFT_GRID_HEIGHT = 9;

	protected ItemStack output = null;
	protected List<Object> input = null;
	protected int width = 0;
	protected int height = 0;
	protected boolean mirrored = true;
	protected boolean isRepair;

	public ShapedOreRecipeTFC(Block     result, Object... recipe){ this(new ItemStack(result), recipe); }
	public ShapedOreRecipeTFC(Item      result, Object... recipe){ this(new ItemStack(result), recipe); }
	public ShapedOreRecipeTFC(ItemStack result, Object... recipe)
	{
		output = result.copy();

		String shape = "";
		int idx = 0;

		if (recipe[idx] instanceof Boolean)
		{
			mirrored = (Boolean)recipe[idx];
			if (recipe[idx+1] instanceof Object[])
			{
				recipe = (Object[])recipe[idx+1];
			}
			else
			{
				idx = 1;
			}
		}

		if (recipe[idx] instanceof String[])
		{
			String[] parts = ((String[])recipe[idx++]);

			for (String s : parts)
			{
				width = s.length();
				shape += s;
			}

			height = parts.length;
		}
		else
		{
			while (recipe[idx] instanceof String)
			{
				String s = (String)recipe[idx++];
				shape += s;
				width = s.length();
				height++;
			}
		}

		if (width * height != shape.length())
		{
			String ret = "Invalid shaped ore recipe: ";
			for (Object tmp :  recipe)
			{
				ret += tmp + ", ";
			}
			ret += output;
			throw new RuntimeException(ret);
		}

		HashMap<Character, Object> itemMap = new HashMap<Character, Object>();

		for (; idx < recipe.length; idx += 2)
		{
			Character chr = (Character)recipe[idx];
			Object in = recipe[idx + 1];

			if (in instanceof ItemStack)
			{
				itemMap.put(chr, ((ItemStack)in).copy());
			}
			else if (in instanceof Item)
			{
				itemMap.put(chr, new ItemStack((Item)in));
			}
			else if (in instanceof Block)
			{
				itemMap.put(chr, new ItemStack((Block)in, 1, OreDictionary.WILDCARD_VALUE));
			}
			else if (in instanceof String)
			{
				itemMap.put(chr, OreDictionary.getOres((String)in));
			}
			else
			{
				String ret = "Invalid shaped ore recipe: ";
				for (Object tmp :  recipe)
				{
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}

		input = new ArrayList<Object>();
		int x = 0;
		for (char chr : shape.toCharArray())
		{
			Object o = itemMap.get(chr);
			if(o == null)
				o = ItemStack.EMPTY;
			input.add(o);
		}
	}

	ShapedOreRecipeTFC(ShapedRecipes recipe, Map<ItemStack, String> replacements)
	{
		output = recipe.getRecipeOutput();
		width = recipe.recipeWidth;
		height = recipe.recipeHeight;

		input = new ArrayList<Object>(recipe.recipeItems.length);

		for(int i = 0; i < recipe.recipeItems.length; i++)
		{
			ItemStack ingredient = recipe.recipeItems[i];

			if(ingredient == null) continue;

			input.add(recipe.recipeItems[i]);

			for(Entry<ItemStack, String> replace : replacements.entrySet())
			{
				if(OreDictionary.itemMatches(replace.getKey(), ingredient, true))
				{
					input.set(i, OreDictionary.getOres(replace.getValue()));
					break;
				}
			}
		}
	}

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1){ return output.copy(); }

	/**
	 * Returns the size of the recipe area
	 */
	@Override
	public int getRecipeSize(){ return input.size(); }

	@Override
	public ItemStack getRecipeOutput(){ return output; }

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++)
		{
			for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y)
			{
				if (checkMatch(inv, x, y, false))
				{
					return true;
				}

				if (mirrored && checkMatch(inv, x, y, true))
				{
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean matches(NonNullList<ItemStack> inv, World world)
	{
		//Not intending to use this with shaped recipes for now
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror)
	{
		for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++)
		{
			for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++)
			{
				int subX = x - startX;
				int subY = y - startY;
				Object target = null;

				if (subX >= 0 && subY >= 0 && subX < width && subY < height)
				{
					if (mirror)
					{
						target = input.get(width - subX - 1 + subY * width);
					}
					else
					{
						target = input.get(subX + subY * width);
					}
				}

				if(target == null)
					continue;

				ItemStack slot = inv.getStackInRowAndColumn(x, y);

				if (target instanceof ItemStack)
				{
					if (!OreDictionary.itemMatches((ItemStack)target, slot, false))
					{
						return false;
					}
				}
				else if (target instanceof List)
				{
					boolean matched = false;

					Iterator<ItemStack> itr = ((List<ItemStack>)target).iterator();
					while (itr.hasNext() && !matched)
					{
						matched = OreDictionary.itemMatches(itr.next(), slot, false);
					}

					if (!matched)
					{
						return false;
					}
				}
				else if (target == null && slot != null)
				{
					return false;
				}

				if(target != null && !tempMatch(slot))
				{
					return false;
				}

			}
		}

		return true;
	}

	public ShapedOreRecipeTFC setMirrored(boolean mirror)
	{
		mirrored = mirror;
		return this;
	}

	/**
	 * Returns the input for this recipe, any mod accessing this value should never
	 * manipulate the values in this array as it will effect the recipe itself.
	 * @return The recipes input vales.
	 */
	public Object[] getInput()
	{
		return this.input.toArray();
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
		return width;
	}
	@Override
	public int getRecipeHeight() {
		return height;
	}
	@Override
	public boolean isRepairRecipe() {
		// TODO Auto-generated method stub
		return isRepair;
	}
}