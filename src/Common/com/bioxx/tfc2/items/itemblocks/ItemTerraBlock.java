package com.bioxx.tfc2.items.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTerraBlock extends ItemBlock
{
	public String[] MetaNames;
	public String folder;

	public ItemTerraBlock(Block b)
	{
		super(b);
		this.setHasSubtypes(true);
		this.folder = "";
	}

	public ItemTerraBlock setFolder(String f)
	{
		folder = f;
		return this;
	}

	@Override
	public String getUnlocalizedName(ItemStack is)
	{
		//if(MetaNames != null && is.getItemDamage() < MetaNames.length)
		//	return super.getUnlocalizedName() + "." + MetaNames[is.getItemDamage()];
		return super.getUnlocalizedName();
	}

	/**
	 * This is called by inventories in the world to tick things such as temperature and food decay. Override this and 
	 * return true if you want the item to be handled differently than the standard code. True will stop he standard TFC code from running.
	 */
	public boolean onUpdate(ItemStack is, World world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public int getMetadata(int i)
	{
		return i;
	}
}