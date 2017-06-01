package com.bioxx.tfc2.potion;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;

public class PotionTFC extends Potion 
{
	static ResourceLocation iconTex = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "inv_effects.png");
	public static Potion THIRST_POTION = new PotionTFC(true, 0xffffff, "potion.thirst").setRegistryName(new ResourceLocation(Reference.getResID()+"thirst")).setIconIndex(1, 2);
	public static Potion ENCUMB_MEDIUM_POTION = new PotionTFC(true, 0xffffff, "potion.encumb_med").setRegistryName(new ResourceLocation(Reference.getResID()+"encumb_med")).setIconIndex(1, 0);
	public static Potion ENCUMB_HEAVY_POTION = new PotionTFC(true, 0xffffff, "potion.encumb_hvy").setRegistryName(new ResourceLocation(Reference.getResID()+"encumb_hvy")).setIconIndex(1, 0);
	public static Potion ENCUMB_MAX_POTION = new PotionTFC(true, 0xffffff, "potion.encumb_max").setRegistryName(new ResourceLocation(Reference.getResID()+"encumb_max")).setIconIndex(1, 0);

	public PotionTFC(boolean isBadEffectIn, int liquidColorIn, String name) 
	{
		super(isBadEffectIn, liquidColorIn);
		this.setPotionName(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasStatusIcon()
	{
		Core.bindTexture(iconTex);
		return super.hasStatusIcon();
	}

}
