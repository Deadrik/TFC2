package com.bioxx.tfc2.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRiver;

import net.minecraftforge.fml.common.eventhandler.EventBus;

import com.google.common.collect.ObjectArrays;

public class Global
{
	public static final EventBus EVENT_BUS = new EventBus();

	/* FruitTree Meta Names, also used for fruit items and FloraManager */
	public static final String[] FRUIT_META_NAMES = new String[] {
			"red_apple", "banana", "orange", "green_apple", "lemon", "olive", "cherry", "peach", "plum"
	};

	/* Flower Meta Names
	 * The first 10 flowers are from vanilla */
	public static final String[] FLOWER_META_NAMES = new String[] {
			"flower_rose", "flower_blue_orchid", "flower_allium", "flower_houstonia",
			"flower_tulip_red", "flower_tulip_orange", "flower_tulip_white", "flower_tulip_pink", "flower_oxeye_daisy",
			"flower_dandelion","flower_nasturtium", "flower_meads_milkweed", "flower_tropical_milkweed", "flower_butterfly_milkweed", "flower_calendula"
	};

	/* Fungi Meta Names
	 * The first 2 are vanilla mushrooms */
	public static final String[] FUNGI_META_NAMES = new String[] {
			"mushroom_brown", "mushroom_red"
	};

	/* Powder */
	public static final String[] POWDER_META_NAMES = {
			"flux", "kaolinite", "graphite", "sulfur", "saltpeter",
			"hematite", "lapis_lazuli", "limonite", "malachite", "salt"
	};

	/* Stone Types */
	public static final String[] STONE_IGIN = {"granite", "diorite", "gabbro"};
	public static final String[] STONE_SED  = {"shale", "claystone", "limestone", "dolomite", "chert"};
	public static final String[] STONE_IGEX = {"rhyolite", "basalt", "andesite", "dacite"};
	public static final String[] STONE_MM   = {"blueschist", "schist", "gneiss", "marble"};

	// Used for loose rocks and other places where the stone list is combined
	public static final int STONE_IGIN_START = 0;
	public static final int STONE_SED_START = STONE_IGIN_START + STONE_IGIN.length;
	public static final int STONE_IGEX_START = STONE_SED_START + STONE_SED.length;
	public static final int STONE_MM_START = STONE_IGEX_START + STONE_IGEX.length;
	public static final String[] STONE_ALL  = ObjectArrays.concat(ObjectArrays.concat(STONE_IGIN, STONE_SED, String.class), ObjectArrays.concat(STONE_IGEX, STONE_MM, String.class), String.class);

	// Stones that can be turned into flux
	public static final int[] STONE_FLUXINDEX = {
			Arrays.asList(STONE_ALL).indexOf("limestone"),
			Arrays.asList(STONE_ALL).indexOf("dolomite"),
			Arrays.asList(STONE_ALL).indexOf("marble")
	};

	/* Ore Types */
	public static final String[] ORE_METAL = {
			"native_copper", "native_gold", "native_platinum", "hematite",
			"native_silver", "cassiterite", "galena", "bismuthinite",
			"garnierite", "malachite", "magnetite", "limonite",
			"sphalerite", "tetrahedrite", "bituminous_coal", "lignite"
	};
	public static final String[] ORE_MINERAL = {
			"kaolinite", "gypsum", "satinspar", "selenite",
			"graphite", "kimberlite", "petrified_wood", "sulfur",
			"jet", "microcline", "pitchblende", "cinnabar",
			"cryolite", "saltpeter", "serpentine", "sylvite"
	};
	public static final String[] ORE_MINERAL2 = {"borax", "olivine", "lapis_lazuli"};

	public static final String[] WOOD_STANDARD = {
			"ash","aspen","birch","chestnut",
			"douglas_fir","hickory","maple","oak",
			"pine","sequoia","spruce","sycamore",
			"white_cedar","willow","kapok","acacia",
			"rosewood","blackwood","palm"
	};

	public static final String SKILL_GENERAL_SMITHING = "skill.gensmith";
	public static final String SKILL_TOOLSMITH = "skill.toolsmith";
	public static final String SKILL_WEAPONSMITH = "skill.weaponsmith";
	public static final String SKILL_ARMORSMITH = "skill.armorsmith";
	public static final String SKILL_AGRICULTURE = "skill.agriculture";
	public static final String SKILL_COOKING = "skill.cooking";
	public static final String SKILL_PROSPECTING = "skill.prospecting";
	public static final String SKILL_BUTCHERING = "skill.butchering";

	public static final Biome BIOME_OCEAN = new BiomeRiver(new Biome.BiomeProperties("ocean_tfc2"));
	public static final Biome BIOME_DEEP_OCEAN = new BiomeRiver(new Biome.BiomeProperties("deepocean_tfc2"));
	public static final Biome BIOME_BEACH = new BiomeRiver(new Biome.BiomeProperties("beach_tfc2"));
	public static final Biome BIOME_RIVER = new BiomeRiver(new Biome.BiomeProperties("river_tfc2"));
	public static final Biome BIOME_SCORCHED = new BiomeRiver(new Biome.BiomeProperties("scorched_tfc2"));
	public static final Biome BIOME_POLAR_DESERT = new BiomeRiver(new Biome.BiomeProperties("polar_desert_tfc2"));
	public static final Biome BIOME_TEMPERATE_DESERT = new BiomeRiver(new Biome.BiomeProperties("temperate_desert_tfc2"));
	public static final Biome BIOME_TROPICAL_DESERT = new BiomeRiver(new Biome.BiomeProperties("tropical_desert_tfc2"));
	public static final Biome BIOME_SUBTROPICAL_DESERT = new BiomeRiver(new Biome.BiomeProperties("subtropical_desert_tfc2"));
	public static final Biome BIOME_MARSH = new BiomeRiver(new Biome.BiomeProperties("marsh_tfc2"));
	public static final Biome BIOME_TUNDRA = new BiomeRiver(new Biome.BiomeProperties("tundra_tfc2"));
	public static final Biome BIOME_BARE = new BiomeRiver(new Biome.BiomeProperties("bare_tfc2"));
	public static final Biome BIOME_TAIGA = new BiomeRiver(new Biome.BiomeProperties("taiga_tfc2"));
	public static final Biome BIOME_SHRUBLAND = new BiomeRiver(new Biome.BiomeProperties("shrubland_tfc2"));
	public static final Biome BIOME_RAIN_FOREST = new BiomeRiver(new Biome.BiomeProperties("rainforest_tfc2"));
	public static final Biome BIOME_DECIDUOUS_FOREST = new BiomeRiver(new Biome.BiomeProperties("deciduous_forest_tfc2"));
	public static final Biome BIOME_DRY_FOREST = new BiomeRiver(new Biome.BiomeProperties("dry_forest_tfc2"));
	public static final Biome BIOME_GRASSLAND = new BiomeRiver(new Biome.BiomeProperties("grassland_tfc2"));
	public static final Biome BIOME_POND = new BiomeRiver(new Biome.BiomeProperties("pond_tfc2"));
	public static final Biome BIOME_LAKE = new BiomeRiver(new Biome.BiomeProperties("lake_tfc2"));
	public static final Biome BIOME_SWAMP = new BiomeRiver(new Biome.BiomeProperties("swamp_tfc2"));

	/*public static Metal BISMUTH;
	public static Metal BISMUTHBRONZE;
	public static Metal BLACKBRONZE;
	public static Metal BLACKSTEEL;
	public static Metal BLUESTEEL;
	public static Metal BRASS;
	public static Metal BRONZE;
	public static Metal COPPER;
	public static Metal GOLD;
	public static Metal WROUGHTIRON;
	public static Metal LEAD;
	public static Metal NICKEL;
	public static Metal PIGIRON;
	public static Metal PLATINUM;
	public static Metal REDSTEEL;
	public static Metal ROSEGOLD;
	public static Metal SILVER;
	public static Metal STEEL;
	public static Metal STERLINGSILVER;
	public static Metal TIN;
	public static Metal ZINC;
	public static Metal WEAKSTEEL;
	public static Metal HCBLACKSTEEL;
	public static Metal WEAKREDSTEEL;
	public static Metal HCREDSTEEL;
	public static Metal WEAKBLUESTEEL;
	public static Metal HCBLUESTEEL;
	public static Metal UNKNOWN;*/

	/*
	 * This is the nth root of 1.5 where the root is 24. This means that, excluding 
	 * environmental factors, food will decay at 50% per 24 hours.
	 * Easy calculator here: http://www.basic-mathematics.com/nth-root-calculator.html
	 */
	public static double FOOD_DECAY_RATE = 1.0170378966055869517978300569768f;
	public static float FOOD_MAX_WEIGHT = 160;
	public static float FOOD_MIN_DROP_WEIGHT = 0.1f;

	public static int SEALEVEL = 64;

	private static HashMap<ItemStack, Integer> fuelMap = new HashMap<ItemStack, Integer>();

	public static void AddFirepitFuel(ItemStack i, int fuelDuration)
	{
		fuelMap.put(i, fuelDuration);
	}

	public static int GetFirepitFuel(ItemStack i)
	{
		ItemStack is = i.copy();
		is.setCount(1);
		Iterator iter = fuelMap.keySet().iterator();

		while(iter.hasNext())
		{
			ItemStack iterStack = (ItemStack) iter.next();
			if(iterStack.isItemEqual(is))
				return fuelMap.get(iterStack);
		}
		return -1;
	}
}
