package com.bioxx.tfc2.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;

public class PotionTFC extends Potion 
{
	public static Potion THIRST_POTION = new PotionTFC(true, 0xffffff, "potion.thirst").setRegistryName(new ResourceLocation(Reference.getResID()+"thirst"));
	public static Potion ENCUMB_MEDIUM_POTION = new PotionTFC(true, 0xffffff, "potion.encumb_med").setRegistryName(new ResourceLocation(Reference.getResID()+"encumb_med"));
	public static Potion ENCUMB_HEAVY_POTION = new PotionTFC(true, 0xffffff, "potion.encumb_hvy").setRegistryName(new ResourceLocation(Reference.getResID()+"encumb_hvy"));
	public static Potion ENCUMB_MAX_POTION = new PotionTFC(true, 0xffffff, "potion.encumb_max").setRegistryName(new ResourceLocation(Reference.getResID()+"encumb_max"));

	public PotionTFC(boolean isBadEffectIn, int liquidColorIn, String name) 
	{
		super(isBadEffectIn, liquidColorIn);
		this.setPotionName(name);
	}

}
