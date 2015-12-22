package com.bioxx.tfc2.core;

import java.util.Calendar;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Food 
{
	public static final int DRYHOURS = 4;
	public static final int SMOKEHOURS = 12;

	private static NBTTagCompound getProcTag(ItemStack is)
	{
		if(is.hasTagCompound() && is.getTagCompound().hasKey("Processing Tag"))
		{
			return (NBTTagCompound) is.getTagCompound().getTag("Processing Tag");
		}
		else
			return new NBTTagCompound();
	}

	private static void setProcTag(ItemStack is, NBTTagCompound nbt)
	{
		if(!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setTag("Processing Tag", nbt);
	}

	private static NBTTagCompound getNBT(ItemStack is)
	{
		if (is != null && is.hasTagCompound())
		{
			return is.getTagCompound();
		}
		else
		{
			//TerraFirmaCraft.LOG.error(TFC_Core.translate("error.error") + " " + is.getUnlocalizedName() + " " +
			//		TFC_Core.translate("error.NBT") + " " + TFC_Core.translate("error.Contact"));
			return new NBTTagCompound();
		}
	}

	public static boolean areEqual(ItemStack is1, ItemStack is2)
	{
		if(isBrined(is1) != isBrined(is2))
			return false;
		if(isPickled(is1) != isPickled(is2))
			return false;
		if(isCooked(is1) != isCooked(is2))
			return false;
		if(isDried(is1) != isDried(is2))
			return false;
		if(isSalted(is1) != isSalted(is2))
			return false;
		return true;
	}

	public static boolean isBrined(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getBoolean("Brined");
	}

	public static void setBrined(ItemStack is, boolean value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setBoolean("Brined", value);
		setProcTag(is, nbt);
	}

	public static boolean isPickled(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getBoolean("Pickled");
	}

	public static void setPickled(ItemStack is, boolean value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setBoolean("Pickled", value);
		setProcTag(is, nbt);
	}

	public static boolean isSalted(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getBoolean("Salted");
	}

	public static void setSalted(ItemStack is, boolean value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setBoolean("Salted", value);
		setProcTag(is, nbt);
	}

	public static boolean isCooked(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getFloat("Cooked") > 600;
	}

	public static float getCooked(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getFloat("Cooked");
	}

	public static void setCooked(ItemStack is, float value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setFloat("Cooked", value);
		setProcTag(is, nbt);
	}

	public static void setDecayTimer(ItemStack is, long value)
	{
		NBTTagCompound nbt = getNBT(is);
		nbt.setLong("Expiration", value);
	}

	public static long getDecayTimer(ItemStack is)
	{
		NBTTagCompound nbt = getNBT(is);
		if (nbt.hasKey("Expiration"))
			return nbt.getLong("Expiration");
		else
			//return (int) TFC_Time.getTotalHours(); //Removed when porting
			return Calendar.getInstance().getTimeInMillis();
	}

	public static boolean isDried(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getShort("Dried") >= DRYHOURS;
	}

	public static short getDried(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getShort("Dried");
	}

	public static void setDried(ItemStack is, int value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setShort("Dried", (short)value);
		setProcTag(is, nbt);
	}

	public static short getSmokeCounter(ItemStack is)
	{
		NBTTagCompound nbt = getProcTag(is);
		return nbt.getShort("SmokeCounter");
	}

	public static void setSmokeCounter(ItemStack is, int value)
	{
		NBTTagCompound nbt = getProcTag(is);
		nbt.setShort("SmokeCounter", (short)value);
		setProcTag(is, nbt);
	}

	public static int getCookedColorMultiplier(ItemStack is)
	{
		float cookedLevel = Food.getCooked(is);
		int r = 255 - (int)(160 * (Math.max(cookedLevel-600, 0) / 600f)); 
		int b = 255 - (int)(160 * (Math.max(cookedLevel-600, 0) / 600f));
		int g = 255 - (int)(160 * (Math.max(cookedLevel-600, 0) / 600f));
		return (r << 16) + (b << 8) + g;
	}

	public static void setMealSkill(ItemStack is, int val)
	{
		if(!is.hasTagCompound())
			is.setTagCompound(new NBTTagCompound());
		is.getTagCompound().setInteger("mealSkill", val);
	}

	public static int getMealSkill(ItemStack is)
	{
		return is.getTagCompound().getInteger("mealSkill");
	}

	public static boolean hasMealSkill(ItemStack is)
	{
		return is.getTagCompound().hasKey("mealSkill");
	}

}
