package com.bioxx.tfc2.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.FoodRegistry;
import com.bioxx.tfc2.api.interfaces.ICookableFood;
import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.FoodStatsTFC;

public class ItemFoodTFC extends ItemTerra implements ICookableFood
{
	private int foodID = 0;
	private long expiration = 300000L;//time expressed in milliseconds
	private float foodFilling = 1f;
	public boolean edible = true;
	public boolean canBeUsedRaw = true;
	protected int tasteSweet;
	protected int tasteSour;
	protected int tasteSalty;
	protected int tasteBitter;
	protected int tasteUmami;
	protected boolean canBeSmoked;
	protected float smokeAbsorb;
	private EnumFoodGroup foodGroup = EnumFoodGroup.None;

	public ItemFoodTFC(EnumFoodGroup fg)
	{
		foodGroup = fg;
		foodID = FoodRegistry.getInstance().registerFood(fg, this);
		this.setCreativeTab(CreativeTabs.tabFood);
	}

	/**
	 * @param time How long should each item last in seconds
	 * @return
	 */
	public ItemTerra setExpiration(int seconds)
	{
		expiration = seconds*20;
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		long time = Food.getDecayTimer(is)-net.minecraft.client.Minecraft.getMinecraft().theWorld.getWorldTime();

		if(time < 0)
		{
			arraylist.add(EnumChatFormatting.RED+"Expired x"+Math.min(1+(time / expiration)* (-1), is.stackSize));
		}
		else
		{
			String out = String.format("%d:%02d", time/60/20, (time/20) % 60);
			arraylist.add("Expires: " + out);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
	{
		ItemStack is = new ItemStack(itemIn, 1, 0);
		NBTTagCompound nbt = new NBTTagCompound();
		is.setTagCompound(nbt);
		Food.setDecayTimer(is, net.minecraft.client.Minecraft.getMinecraft().theWorld.getWorldTime()+expiration);
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
		Core.setPlayerFoodStats(player, fs);
		worldIn.playSoundAtEntity(player, "random.burp", 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
		this.onFoodEaten(stack, worldIn, player);
		player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
		return stack;
	}

	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
	{

	}

	@Override
	public int getItemStackLimit(ItemStack stack)
	{
		return 64;
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

	@Override
	public EnumFoodGroup getFoodGroup() {
		return this.foodGroup;
	}

	@Override
	public int getFoodID() 
	{
		return foodID;
	}

	@Override
	public ItemStack onDecayed(ItemStack is, World world, int i, int j, int k) {

		return is;
	}

	@Override
	public boolean isEdible(ItemStack is) {
		return edible;
	}

	@Override
	public boolean isUsable(ItemStack is) {
		return canBeUsedRaw;
	}

	@Override
	public int getTasteSweet(ItemStack is) {
		int base = tasteSweet;
		if(is != null)
		{
			if(is.getTagCompound().hasKey("tasteSweet"))
				base = is.getTagCompound().getInteger("tasteSweet");
			base += Food.getCookedProfile(is)[0];
			base += Food.getFuelProfile(is)[0]*getSmokeAbsorbMultiplier();
		}
		return Math.max(base + Food.getSweetMod(is), 0);
	}

	@Override
	public int getTasteSour(ItemStack is) {
		int base = tasteSour;
		if(is != null)
		{
			if(is.getTagCompound().hasKey("tasteSour"))
				base = is.getTagCompound().getInteger("tasteSour");
			base += Food.getCookedProfile(is)[1];
			base += Food.getFuelProfile(is)[1]*getSmokeAbsorbMultiplier();
		}
		if (Food.isBrined(is))
			base += 5;
		if(Food.isPickled(is))
			base += 30;
		return Math.max(base + Food.getSourMod(is), 0);
	}

	@Override
	public int getTasteSalty(ItemStack is) {
		int base = tasteSalty;
		if(is != null)
		{
			if(is.getTagCompound().hasKey("tasteSalty"))
				base = is.getTagCompound().getInteger("tasteSalty");
			base += Food.getCookedProfile(is)[2];
			base += Food.getFuelProfile(is)[2]*getSmokeAbsorbMultiplier();
		}
		if(Food.isSalted(is))
			base += 40;
		if(Food.isBrined(is))
			base += 10;

		return Math.max(base + Food.getSaltyMod(is), 0);
	}

	@Override
	public int getTasteBitter(ItemStack is) {
		int base = tasteBitter;
		if(is != null)
		{
			if(is.getTagCompound().hasKey("tasteBitter"))
				base = is.getTagCompound().getInteger("tasteBitter");
			base += Food.getCookedProfile(is)[3];
			base += Food.getFuelProfile(is)[3]*getSmokeAbsorbMultiplier();
		}
		return Math.max(base + Food.getBitterMod(is), 0);
	}

	@Override
	public int getTasteSavory(ItemStack is) {
		int base = tasteUmami;
		if(is != null)
		{
			if(is.getTagCompound().hasKey("tasteUmami"))
				base = is.getTagCompound().getInteger("tasteUmami");
			base += Food.getCookedProfile(is)[4];
			base += Food.getFuelProfile(is)[4]*getSmokeAbsorbMultiplier();
		}
		return Math.max(base + Food.getSavoryMod(is), 0);
	}

	@Override
	public boolean canSmoke() {
		return canBeSmoked;
	}

	@Override
	public float getSmokeAbsorbMultiplier() {
		return smokeAbsorb;
	}
}
