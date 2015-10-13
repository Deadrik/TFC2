package com.bioxx.tfc2.items;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.FoodStatsTFC;

public class ItemFoodTFC extends ItemTerra
{
	private long expiration = 300000L;//time expressed in milliseconds
	private float foodFilling = 1f;
	private EnumFoodGroup foodGroup = EnumFoodGroup.None;

	public ItemFoodTFC()
	{
		this.setCreativeTab(CreativeTabs.tabFood);
	}

	/**
	 * @param time How long should each item last in seconds
	 * @return
	 */
	public ItemTerra setExpiration(int seconds)
	{
		expiration = seconds*1000;
		return this;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		long time = Food.getDecayTimer(is)-System.currentTimeMillis();

		String out = String.format("%d:%02d", 
				TimeUnit.MILLISECONDS.toMinutes(time),
				TimeUnit.MILLISECONDS.toSeconds(time) - 
				TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
				);
		arraylist.add("Expires: " + out);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
	{
		ItemStack is = new ItemStack(itemIn, 1, 0);
		NBTTagCompound nbt = new NBTTagCompound();
		is.setTagCompound(nbt);
		Food.setDecayTimer(is, System.currentTimeMillis()+expiration);
		subItems.add(is);
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer player)
	{
		FoodStatsTFC fs = Core.getPlayerFoodStats(player);
		fs.addNutrition(foodGroup, foodFilling);
		worldIn.playSoundAtEntity(player, "random.burp", 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
		this.onFoodEaten(stack, worldIn, player);
		player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
		return stack;
	}

	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
	{
		/*if (!worldIn.isRemote && this.potionId > 0 && worldIn.rand.nextFloat() < this.potionEffectProbability)
		{
			player.addPotionEffect(new PotionEffect(this.potionId, this.potionDuration * 20, this.potionAmplifier));
		}*/
	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 100;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.EAT;
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
	{
		if (playerIn.canEat(false))
		{
			playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
		}

		return itemStackIn;
	}
}
