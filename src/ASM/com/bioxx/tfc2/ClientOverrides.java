package com.bioxx.tfc2;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.FoodRegistry;
import com.bioxx.tfc2.api.FoodRegistry.TFCFood;
import com.bioxx.tfc2.api.heat.ItemHeat;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.Timekeeper;

@SideOnly(Side.CLIENT)
public class ClientOverrides 
{
	public static void addInformation(ItemStack is, EntityPlayer player, List arraylist, Item item)
	{
		//Do heat stuff
		if(ItemHeat.Get(is) > 0)
		{
			arraylist.add("Temp: " + ItemHeat.Get(is));
		}

		//Do decay stuff
		if(FoodRegistry.getInstance().hasKey(is.getItem(), is.getItemDamage()))
		{
			TFCFood food = FoodRegistry.getInstance().getFood(is.getItem(), is.getItemDamage());
			IFood ifood = (IFood)item;
			arraylist.add(food.getDisplayString());
			long time = Food.getDecayTimer(is)-Timekeeper.getInstance().getTotalTicks();
			if(!Food.hasDecayTimer(is))
			{
				arraylist.add(TextFormatting.GREEN+"NO DECAY");
			}
			else if(time <= 0)
			{
				arraylist.add(TextFormatting.RED+"Expired x"+Math.min(1+(time / Food.getExpirationTimer(food, is))* (-1), is.getMaxStackSize()));
			}
			else
			{
				String out = String.format("%d:%02d", time/60/20, (time/20) % 60);
				arraylist.add("Expires: " + out);
			}
		}
	}
}
