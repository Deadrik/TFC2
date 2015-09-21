package com.bioxx.tfc2.api.crafting;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.IRecipeTFC;

public class ShapedRecipesTFC implements IRecipeTFC
{
	private int recipeWidth;
	private int recipeHeight;
	private final List<ItemStack> recipeItems;
	private ItemStack recipeOutput;

	public ShapedRecipesTFC(int i, int j, List<ItemStack> aitemstack, ItemStack itemstack)
	{
		recipeWidth = i;
		recipeHeight = j;
		recipeItems = aitemstack;
		recipeOutput = itemstack;
	}

	private boolean compare(InventoryCrafting inventorycrafting, int i, int j, boolean flag)
	{
		for (int k = 0; k < 9; k++)
		{
			for (int l = 0; l < 9; l++)
			{
				int i1 = k - i;
				int j1 = l - j;
				ItemStack recipeIS = null;
				if (i1 >= 0 && j1 >= 0 && i1 < recipeWidth && j1 < recipeHeight)
				{
					if (flag)
					{
						recipeIS = recipeItems.get(recipeWidth - i1 - 1 + j1 * recipeWidth);
					}
					else
					{
						recipeIS = recipeItems.get(i1 + j1 * recipeWidth);
					}
				}
				ItemStack inputIS = inventorycrafting.getStackInRowAndColumn(k, l);
				if (inputIS == null && recipeIS == null)
				{
					continue;
				}
				else if (inputIS == null || recipeIS == null) // No need for XOR since the X is handled above
				{
					return false;
				}
				else
				{
					if (recipeIS.getItem() != inputIS.getItem())
					{
						return false;
					}
					if (recipeIS.getItemDamage() != 32767 && recipeIS.getItemDamage() != inputIS.getItemDamage())
					{
						return false;
					}
				}
				if(!tempMatch(recipeIS, inputIS))
				{
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventorycrafting)
	{
		return new ItemStack(recipeOutput.getItem(), recipeOutput.stackSize, recipeOutput.getItemDamage());
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return recipeOutput;
	}

	@Override
	public int getRecipeSize()
	{
		return recipeWidth * recipeHeight;
	}

	@Override
	public int getRecipeWidth()
	{
		return recipeWidth;
	}

	@Override
	public int getRecipeHeight()
	{
		return recipeHeight;
	}

	@Override
	public List getRecipeItems() {
		return recipeItems;
	}

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world)
	{
		for (int i = 0; i <= 9 - recipeWidth; i++)
		{
			for (int j = 0; j <= 9 - recipeHeight; j++)
			{
				if (compare(inventorycrafting, i, j, true))
				{
					return true;
				}
				if (compare(inventorycrafting, i, j, false))
				{
					return true;
				}
			}
		}

		return false;
	}

	private boolean tempMatch(ItemStack recipeIS, ItemStack inputIS)
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
	public ItemStack[] getRemainingItems(InventoryCrafting inv)
	{
		ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];

		for (int i = 0; i < aitemstack.length; ++i)
		{
			ItemStack itemstack = inv.getStackInSlot(i);
			aitemstack[i] = net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack);
		}

		return aitemstack;
	}
}
