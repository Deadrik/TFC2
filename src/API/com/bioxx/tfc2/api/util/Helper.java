package com.bioxx.tfc2.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

public class Helper 
{
	public static int combineCoords(int x, int y)
	{
		short xs = (short)x;
		short ys = (short)y;
		return (xs << 16) | (ys & 0xFFFF);
	}

	public static int getXCoord(int c)
	{
		return (short)(c >> 16);
	}
	public static int getYCoord(int c)
	{
		return (short)(c & 0xffff);
	}

	/**
	 * @param angle Any angle in degrees
	 * @return Returns the original angle after making sure that it is bounded between 0 and 360 degrees
	 */
	public static double normalizeAngle(double angle)
	{
		angle = angle % 360;
		if(angle < 0)
			angle += 360;

		return angle;
	}

	public static double dist2dSq(BlockPos a, BlockPos b)
	{
		double d0 = a.getX() - b.getX();
		double d2 = a.getZ() - b.getZ();

		return d0 * d0 + d2 * d2;
	}

	public static NBTTagList writeStackArrayToNBTList(ItemStack[] list)
	{
		NBTTagList invList = new NBTTagList();
		for(int i = 0; i < list.length; i++)
		{
			if(list[i] != null)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)i);
				list[i].writeToNBT(tag);
				invList.appendTag(tag);
			}
		}
		return invList;
	}

	public static ItemStack[] readStackArrayFromNBTList(NBTTagList list, int size)
	{
		ItemStack[] out = new ItemStack[size];
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte byte0 = tag.getByte("Slot");
			if(byte0 >= 0 && byte0 < size)
				out[byte0] = ItemStack.loadItemStackFromNBT(tag);
		}
		return out;
	}
}
