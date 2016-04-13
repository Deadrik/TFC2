package com.bioxx.tfc2.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.FoodRegistry;
import com.bioxx.tfc2.api.interfaces.ICookableFood;
import com.bioxx.tfc2.api.interfaces.IUpdateInInventory;
import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.FoodStatsTFC;

public class ItemFoodTFC extends ItemTerra implements ICookableFood, IUpdateInInventory
{
	private int foodID = 0;
	private long expiration = 300000L;//time expressed in milliseconds
	private float nourishment = 1f;
	private int filling = 1;
	public boolean edible = true;
	public boolean canBeUsedRaw = true;
	protected boolean canBeSmoked;
	private EnumFoodGroup foodGroup = EnumFoodGroup.None;

	public ItemFoodTFC(EnumFoodGroup fg, float n, int f)
	{
		foodGroup = fg;
		nourishment = n;
		filling = f;
		foodID = FoodRegistry.getInstance().registerFood(fg, this);
		this.setCreativeTab(CreativeTabs.FOOD);
	}

	/**
	 * @param seconds How long should each item last in seconds
	 * @return this food item to allow chaining
	 */
	public ItemFoodTFC setExpiration(int seconds)
	{
		expiration = seconds*20;
		return this;
	}

	public ItemFoodTFC setIsEdible(boolean isEdible)
	{
		edible = isEdible;
		return this;
	}

	public ItemFoodTFC setCanUseRaw(boolean canUseRaw)
	{
		canBeUsedRaw = canUseRaw;
		return this;
	}

	@Override
	public long getExpirationTimer(ItemStack is)
	{
		return expiration;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		long time = Food.getDecayTimer(is)-net.minecraft.client.Minecraft.getMinecraft().theWorld.getWorldTime();

		if(time <= 0)
		{
			arraylist.add(TextFormatting.RED+"Expired x"+Math.min(1+(time / expiration)* (-1), is.stackSize));
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
		Food.setDecayTimer(is, net.minecraft.client.Minecraft.getMinecraft().theWorld.getWorldTime()+getExpirationTimer(is));
		subItems.add(is);
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase player)
	{
		FoodStatsTFC fs = Core.getPlayerFoodStats(player);
		fs.addNutrition(foodGroup, nourishment);
		Core.setPlayerFoodStats((EntityPlayer)player, fs);
		worldIn.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
		this.onFoodEaten(stack, worldIn, (EntityPlayer)player);
		((EntityPlayer) player).addStat(StatList.getObjectUseStats(this));
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
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		if (playerIn.canEat(false))
		{
			playerIn.setActiveHand(hand);
			return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
		}

		return new ActionResult(EnumActionResult.FAIL, itemStackIn);
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
	public boolean canSmoke() {
		return canBeSmoked;
	}

	@Override
	public void inventoryUpdate(EntityPlayer player, ItemStack is) 
	{
		long time = Food.getDecayTimer(is)-net.minecraft.client.Minecraft.getMinecraft().theWorld.getWorldTime();
		if(time < 0)
		{
			int expiredAmt = (int)Math.min(1+(time / expiration)* (-1), is.stackSize);
			is.stackSize-=expiredAmt;
			Food.setDecayTimer(is, Food.getDecayTimer(is)+expiration*expiredAmt);
		}
	}
}
