package com.bioxx.tfc2.potion;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;

public class PotionTFC extends Potion 
{
	public static Potion THIRST_POTION = new PotionTFC(true, 0xffffff, "Thirsty").setRegistryName(new ResourceLocation(Reference.getResID()+"thirst"));

	public PotionTFC(boolean isBadEffectIn, int liquidColorIn, String name) 
	{
		super(isBadEffectIn, liquidColorIn);
		this.setPotionName(name);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "333d113b-0cac-492d-ba64-8c55865cab68", -0.3, 2);
	}

}
