package com.bioxx.tfc2.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;

public class PotionTFC extends Potion 
{
	public static Potion THIRST_POTION = new PotionTFC(true, 0xffffff).setPotionName("Thirsty").setRegistryName(new ResourceLocation(Reference.getResID()+"thirst"));
	public PotionTFC(boolean isBadEffectIn, int liquidColorIn) 
	{
		super(isBadEffectIn, liquidColorIn);
	}

}
