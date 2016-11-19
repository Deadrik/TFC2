package com.bioxx.tfc2.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class PotionTFC extends Potion 
{
	public static Potion THIRST_POTION = new PotionTFC(true, 0xffffff).setPotionName("Thirsty").setRegistryName(new ResourceLocation("thirst"));
	public PotionTFC(boolean isBadEffectIn, int liquidColorIn) 
	{
		super(isBadEffectIn, liquidColorIn);
	}

}
