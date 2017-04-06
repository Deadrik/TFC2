package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

import com.bioxx.tfc2.TFC;

public enum WoodType implements IStringSerializable
{
	Ash("ash", 0, 12600, 5970),//Black Ash
	Aspen("aspen", 1, 9230, 5220),//Black Poplar
	Birch("birch", 2, 12300, 5690),//Paper Birch
	Chestnut("chestnut", 3, 8600, 5320),//Sweet Chestnut
	DouglasFir("douglas_fir", 4, 12500, 6950),//Douglas Fir
	Hickory("hickory", 5, 17100, 9040),//Bitternut Hickory
	Maple("maple", 6, 13400, 6540),//Red Maple
	Oak("oak", 7, 14830, 7370),//White Oak
	Pine("pine", 8, 14500, 8470),//Longleaf Pine
	Sequoia("sequoia", 9, 8950, 5690),//Redwood
	Spruce("spruce", 10, 8640, 4730),//White Spruce
	Sycamore("sycamore", 11, 10000, 5380),//American Sycamore
	WhiteCedar("white_cedar", 12, 6500, 3960),//Northern White Cedar
	Willow("willow", 13, 8150, 3900),//White Willow
	Kapok("kapok",14, 14320, 6690),//Chakte Kok - No data on kapok so went with the brazilian Chakte Kok(Redheart Tree)
	Acacia("acacia",15, 12620, 7060),//Acacia Koa
	Rosewood("rosewood", 16, 19570, 9740),//Brazilian Rosewood
	Blackwood("blackwood", 17, 15020, 7770),//Australian Blackwood
	Palm("palm", 18, 12970, 9590);//Red Palm

	private String name;
	private int meta;

	/*
	 * These values are roughly based on the Modulus of Rupture values found 
	 * here: http://www.wood-database.com/
	 * 
	 * Some values may be altered for gameplay purposes or a ballpark average psi may be 
	 * used if a number of psi values are available for a tree type.
	 */
	private float bendStrength;

	/*
	 * These values are roughly based on the Crushing Strength values found 
	 * here: http://www.wood-database.com/
	 * 
	 * Some values may be altered for gameplay purposes or a ballpark average psi may be 
	 * used if a number of psi values are available for a tree type.
	 */
	private float compressionWeight;

	WoodType(String s, int id, float bend, float compression)
	{
		name = s;
		meta = id;
		bendStrength = (bend-5000) / 10;
		compressionWeight = compression / 10;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getRupture()
	{
		return (int)Math.floor(bendStrength);
	}

	public int getSupportRange()
	{
		return getRupture() / 100;
	}

	public int getCompression()
	{
		return (int)Math.floor(compressionWeight);
	}

	public int getMeta()
	{
		return meta;
	}

	public static String[] getNamesArray()
	{
		String[] s = new String[values().length];
		for(int i = 0; i < WoodType.values().length; i++)
		{
			s[i] = WoodType.values()[i].getName();
		}
		return s;
	}

	public static WoodType getTypeFromMeta(int meta)
	{
		for(int i = 0; i < WoodType.values().length; i++)
		{
			if(WoodType.values()[i].meta == meta)
				return WoodType.values()[i];
		}
		return null;
	}

	public static WoodType getTypeFromString(String s)
	{
		for(int i = 0; i < WoodType.values().length; i++)
		{
			if(WoodType.values()[i].getName().equals(s))
				return WoodType.values()[i];
		}
		TFC.log.info("Can't find tree: " + s);
		return null;
	}
}