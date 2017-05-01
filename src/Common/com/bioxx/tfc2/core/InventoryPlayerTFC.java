package com.bioxx.tfc2.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.interfaces.IFood;

public class InventoryPlayerTFC extends InventoryPlayer {

	public NonNullList<ItemStack> extraEquipInventory = NonNullList.<ItemStack>withSize(Core.getExtraEquipInventorySize(), ItemStack.EMPTY);

	public InventoryPlayerTFC(EntityPlayer par1EntityPlayer) {
		super(par1EntityPlayer);
		this.player = par1EntityPlayer;
		if(Core.getExtraEquipInventorySize() > 0)
			this.allInventories.add(extraEquipInventory);
	}

	@Override
	public int getSizeInventory()
	{
		return super.getSizeInventory() + this.extraEquipInventory.size();
	}

	@Override
	public boolean isEmpty()
	{
		for (ItemStack itemstack : this.extraEquipInventory)
		{
			if (!itemstack.isEmpty())
			{
				return false;
			}
		}

		return super.isEmpty();
	}

	public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2)
	{
		return stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() || stack1.getMetadata() == stack2.getMetadata()) && 
				(ItemStack.areItemStackTagsEqual(stack1, stack2) || (stack1.getItem() instanceof IFood && stack2.getItem() instanceof IFood && Food.areEqual(stack1, stack2)));
	}

	/*@Override
	public ItemStack getStackInSlot(int par1)
	{
		NonNullList<ItemStack> aitemstack = this.mainInventory;
		if (par1 >= this.mainInventory.size() + this.extraEquipInventory.size())
		{
			par1 -= this.mainInventory.size() + this.extraEquipInventory.size();
			aitemstack = this.armorInventory;
		}
		else if(par1 >= this.mainInventory.size()){
			par1-= aitemstack.size();
			aitemstack = this.extraEquipInventory;
		}
		return aitemstack[par1];

	}*/

	/*@Override
	public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT)
	{
		int k = 0;
		int l;
		ItemStack itemstack;
		int i1;

		k = super.clearMatchingItems(itemIn, metadataIn, removeCount, itemNBT);

		for(l = 0; l < this.extraEquipInventory.size(); l++)
		{
			itemstack = this.extraEquipInventory[l];

			if (itemstack != null && (itemIn == null || itemstack.getItem() == itemIn) && (metadataIn <= -1 || itemstack.getMetadata() == metadataIn) && (itemNBT == null || (NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true))))
			{
				i1 = removeCount <= 0 ? itemstack.getMaxStackSize() : Math.min(removeCount - k, itemstack.getMaxStackSize());
				k += i1;

				if (removeCount != 0)
				{
					this.extraEquipInventory[l].getMaxStackSize() -= i1;

					if (this.extraEquipInventory[l].getMaxStackSize() == 0)
					{
						this.extraEquipInventory[l] = null;
					}

					if (removeCount > 0 && k >= removeCount)
					{
						return k;
					}
				}
			}
		}

		return k;
	}*/

	/*@Override
	public void decrementAnimations()
	{
		for (int i = 0; i < this.extraEquipInventory.size(); ++i)
		{
			if (this.extraEquipInventory[i] != null)
			{
				this.extraEquipInventory[i].updateAnimation(this.player.world, this.player, i, this.currentItem == i);
			}
		}
		super.decrementAnimations();
	}*/

	/*@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		ItemStack[] aitemstack = this.mainInventory;

		if (par1 >= this.mainInventory.size() + this.extraEquipInventory.size())
		{
			aitemstack = this.armorInventory;
			par1 -= this.mainInventory.size() + this.extraEquipInventory.size();
		}
		else if(par1 >= this.mainInventory.size()){
			par1-= aitemstack.length;
			aitemstack = this.extraEquipInventory;
		}


		if (aitemstack[par1] != null)
		{
			ItemStack itemstack;

			if (aitemstack[par1].getMaxStackSize() <= par2)
			{
				itemstack = aitemstack[par1];
				aitemstack[par1] = null;
				return itemstack;
			}
			else
			{
				itemstack = aitemstack[par1].splitStack(par2);

				if (aitemstack[par1].getMaxStackSize() == 0)
				{
					aitemstack[par1] = null;
				}

				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}*/

	/*@Override
	public void dropAllItems()
	{
		int i;

		for (i = 0; i < this.extraEquipInventory.size(); ++i)
		{
			if (this.extraEquipInventory[i] != null)
			{
				this.player.dropItem(this.extraEquipInventory[i], true, false);
				this.extraEquipInventory[i] = null;
			}
		}
		super.dropAllItems();
	}
	 */
	/*@Override
	public boolean hasItemStack(ItemStack par1ItemStack)
	{
		int i;

		for (i = 0; i < this.extraEquipInventory.size(); ++i)
		{
			if (this.extraEquipInventory[i] != null && this.extraEquipInventory[i].isItemEqual(par1ItemStack))
			{
				return true;
			}
		}
		return super.hasItemStack(par1ItemStack);
	}*/

	/*@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{

		ItemStack[] aitemstack = this.mainInventory;

		if (par1 >= this.mainInventory.size() + this.extraEquipInventory.size())
		{
			par1 -= this.mainInventory.size() + this.extraEquipInventory.size();
			aitemstack = this.armorInventory;
		}
		else if(par1 >= this.mainInventory.size()){
			par1-= aitemstack.length;
			aitemstack = this.extraEquipInventory;
		}

		aitemstack[par1] = par2ItemStack;
	}*/

	/*@Override
	public void copyInventory(InventoryPlayer par1InventoryPlayer)
	{
		if(par1InventoryPlayer instanceof InventoryPlayerTFC){
			this.copyInventoryTFC((InventoryPlayerTFC)par1InventoryPlayer);
		}
		else{
			super.copyInventory(par1InventoryPlayer);
		}
	}

	public void copyInventoryTFC(InventoryPlayerTFC par1InventoryPlayer)
	{
		int i;

		for (i = 0; i < this.extraEquipInventory.size(); ++i)
		{
			this.extraEquipInventory[i] = ItemStack.copyItemStack(par1InventoryPlayer.extraEquipInventory[i]);
		}
		super.copyInventory(par1InventoryPlayer);
	}*/

	@Override
	public void readFromNBT(NBTTagList par1NBTTagList)
	{
		super.readFromNBT(par1NBTTagList);

		NBTTagList extraList = player.getEntityData().getTagList("ExtraInventory", 10);

		for (int i = 0; i < extraList.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = extraList.getCompoundTagAt(i);
			ItemStack itemstack = new ItemStack(nbttagcompound);
			if (itemstack != null)
			{
				extraEquipInventory.add(itemstack);
			}
		}
	}

	@Override
	public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
	{
		super.writeToNBT(par1NBTTagList);

		int i;
		NBTTagCompound nbt;
		NBTTagList tagList = new NBTTagList();
		for (i = 0; i < extraEquipInventory.size(); i++)
		{
			ItemStack is = extraEquipInventory.get(i);
			if (is != null)
			{
				nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte) i);
				is.writeToNBT(nbt);
				tagList.appendTag(nbt);
			}
		}
		player.getEntityData().setTag("ExtraInventory", tagList);
		return par1NBTTagList;
	}
}
