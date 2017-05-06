package com.bioxx.tfc2.core;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.Reference;

public class TFC_Sounds
{
	private static final String LOCATION = Reference.ModID + ":";

	public static SoundEvent FALLININGROCKSHORT;
	public static SoundEvent FALLININGROCKLONG;
	public static SoundEvent FALLININGDIRTSHORT;
	public static SoundEvent METALIMPACT;
	public static SoundEvent STONEDRAG;
	public static SoundEvent ROOSTERCROW;
	public static SoundEvent BELLOWS;
	public static SoundEvent CERAMICBREAK;
	public static SoundEvent FIRESTARTER;
	public static SoundEvent DEERSAY;
	public static SoundEvent DEERCRY;
	public static SoundEvent DEERHURT;
	public static SoundEvent DEERDEATH;
	public static SoundEvent BEARSAY;
	public static SoundEvent BEARCRY;
	public static SoundEvent BEARCUBCRY;
	public static SoundEvent BEARHURT;
	public static SoundEvent BEARDEATH;
	public static SoundEvent BISONSAY;
	public static SoundEvent BISONCRY;
	public static SoundEvent BISONHURT;
	public static SoundEvent BISONDEATH;
	public static SoundEvent PHAESANTSAY;
	public static SoundEvent PHAESANTCHICKSAY;
	public static SoundEvent PHAESANTHURT;
	public static SoundEvent PHAESANTDEATH;
	public static SoundEvent CRICKET;
	public static SoundEvent FROG;
	public static SoundEvent FOXSAY;
	public static SoundEvent FOXHURT;
	public static SoundEvent FOXDEATH;
	public static SoundEvent LIONSAY;
	public static SoundEvent LIONHURT;
	public static SoundEvent LIONDEATH;
	public static SoundEvent KNAPPING;
	public static SoundEvent ELKHORNS;
	public static SoundEvent ELKCRY;
	public static SoundEvent ELKHURT;
	public static SoundEvent ELKDEATH;
	public static SoundEvent ELKCUBCRY;
	public static SoundEvent ELKCUBHURT;
	public static SoundEvent ELKCUBDEATH;

	public static SoundEvent TFCMUSIC;

	public static void register()
	{
		FALLININGROCKSHORT = createSound("rock.slide.short");
		FALLININGROCKLONG = createSound("rock.slide.long");
		FALLININGDIRTSHORT = createSound("dirt.slide.short");
		METALIMPACT = createSound("anvil.metalimpact");
		STONEDRAG = createSound("quern.stonedrag");
		ROOSTERCROW = createSound("mob.rooster.cry");
		BELLOWS = createSound("bellows.blow.air");
		CERAMICBREAK = createSound("item.ceramicbreak");
		FIRESTARTER = createSound("item.firestarter");
		DEERSAY = createSound("mob.deer.say");
		DEERCRY = createSound("mob.deer.cry");
		DEERHURT = createSound("mob.deer.hurt");
		DEERDEATH = createSound("mob.deer.death");
		BEARSAY = createSound("mob.bear.say");
		BEARCRY = createSound("mob.bear.cry");
		BEARCUBCRY = createSound("mob.bear.cub.cry");
		BEARHURT = createSound("mob.bear.hurt");
		BEARDEATH = createSound("mob.bear.death");
		BISONSAY = createSound("mob.bison.say");
		BISONCRY = createSound("mob.bison.cry");
		BISONHURT = createSound("mob.bison.hurt");
		BISONDEATH = createSound("mob.bison.death");
		PHAESANTSAY = createSound("mob.pheasant.say");
		PHAESANTCHICKSAY = createSound("mob.pheasant.chick.say");
		PHAESANTHURT = createSound("mob.pheasant.hurt");
		PHAESANTDEATH = createSound("mob.pheasant.death");
		CRICKET = createSound("mob.cricket");
		FROG = createSound("mob.frog");
		FOXSAY = createSound("mob.fox.say");
		FOXHURT = createSound("mob.fox.hurt");
		FOXDEATH = createSound("mob.fox.death");
		LIONSAY = createSound("mob.lion.say");
		LIONHURT = createSound("mob.lion.hurt");
		LIONDEATH = createSound("mob.lion.death");
		KNAPPING = createSound("knapping");
		ELKHORNS = createSound("mob.elk.horns.say");
		ELKCRY = createSound("mob.elk.cry");
		ELKHURT = createSound("mob.elk.hurt");
		ELKDEATH = createSound("mob.elk.death");
		ELKCUBCRY = createSound("mob.elk.cub.cry");
		ELKCUBHURT = createSound("mob.elk.cub.hurt");
		ELKCUBDEATH = createSound("mob.elk.cub.death");
		
		TFCMUSIC = createSound("music");
	}

	private static SoundEvent createSound(String s)
	{
		ResourceLocation rl = new ResourceLocation(LOCATION + s);
		SoundEvent sound = new SoundEvent(rl);
		GameRegistry.register(sound, rl);
		return sound;
	}
}
