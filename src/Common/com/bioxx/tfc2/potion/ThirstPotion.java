package com.bioxx.tfc2.potion;

import net.minecraft.entity.SharedMonsterAttributes;

public class ThirstPotion extends PotionTFC
{

	public ThirstPotion(boolean isBadEffectIn, int liquidColorIn, String name) 
	{
		super(isBadEffectIn, liquidColorIn, name);
		this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "333d113b-0cac-492d-ba64-8c55865cab68", -0.3, 2);
	}

}
