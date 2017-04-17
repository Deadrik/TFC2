package com.bioxx.tfc2.items.pottery;

import net.minecraft.util.IStringSerializable;

public enum ClayMoldType implements IStringSerializable
{
	MOLD_CLAY                  ("clay_mold"),
	MOLD                       ("ceramic_mold"),

	MOLD_AXE_CLAY              ("clay_mold_axe"),
	MOLD_AXE                   ("ceramic_mold_axe"),
	MOLD_AXE_COPPER            ("ceramic_mold_axe_copper"),
	MOLD_AXE_BRONZE            ("ceramic_mold_axe_bronze"),
	MOLD_AXE_BISMUTH_BRONZE    ("ceramic_mold_axe_bismuth_bronze"),
	MOLD_AXE_BLACK_BRONZE      ("ceramic_mold_axe_black_bronze"),

	MOLD_CHISEL_CLAY           ("clay_mold_chisel"),
	MOLD_CHISEL                ("ceramic_mold_chisel"),
	MOLD_CHISEL_COPPER         ("ceramic_mold_chisel_copper"),
	MOLD_CHISEL_BRONZE         ("ceramic_mold_chisel_bronze"),
	MOLD_CHISEL_BISMUTH_BRONZE ("ceramic_mold_chisel_bismuth_bronze"),
	MOLD_CHISEL_BLACK_BRONZE   ("ceramic_mold_chisel_black_bronze"),

	MOLD_HAMMER_CLAY           ("clay_mold_hammer"),
	MOLD_HAMMER                ("ceramic_mold_hammer"),
	MOLD_HAMMER_COPPER         ("ceramic_mold_hammer_copper"),
	MOLD_HAMMER_BRONZE         ("ceramic_mold_hammer_bronze"),
	MOLD_HAMMER_BISMUTH_BRONZE ("ceramic_mold_hammer_bismuth_bronze"),
	MOLD_HAMMER_BLACK_BRONZE   ("ceramic_mold_hammer_black_bronze"),

	MOLD_HOE_CLAY              ("clay_mold_hoe"),
	MOLD_HOE                   ("ceramic_mold_hoe"),
	MOLD_HOE_COPPER            ("ceramic_mold_hoe_copper"),
	MOLD_HOE_BRONZE            ("ceramic_mold_hoe_bronze"),
	MOLD_HOE_BISMUTH_BRONZE    ("ceramic_mold_hoe_bismuth_bronze"),
	MOLD_HOE_BLACK_BRONZE      ("ceramic_mold_hoe_black_bronze"),

	MOLD_KNIFE_CLAY            ("clay_mold_knife"),
	MOLD_KNIFE                 ("ceramic_mold_knife"),
	MOLD_KNIFE_COPPER          ("ceramic_mold_knife_copper"),
	MOLD_KNIFE_BRONZE          ("ceramic_mold_knife_bronze"),
	MOLD_KNIFE_BISMUTH_BRONZE  ("ceramic_mold_knife_bismuth_bronze"),
	MOLD_KNIFE_BLACK_BRONZE    ("ceramic_mold_knife_black_bronze"),

	MOLD_MACE_CLAY             ("clay_mold_mace"),
	MOLD_MACE                  ("ceramic_mold_mace"),
	MOLD_MACE_COPPER           ("ceramic_mold_mace_copper"),
	MOLD_MACE_BRONZE           ("ceramic_mold_mace_bronze"),
	MOLD_MACE_BISMUTH_BRONZE   ("ceramic_mold_mace_bismuth_bronze"),
	MOLD_MACE_BLACK_BRONZE     ("ceramic_mold_mace_black_bronze"),

	MOLD_PICK_CLAY             ("clay_mold_pick"),
	MOLD_PICK                  ("ceramic_mold_pick"),
	MOLD_PICK_COPPER           ("ceramic_mold_pick_copper"),
	MOLD_PICK_BRONZE           ("ceramic_mold_pick_bronze"),
	MOLD_PICK_BISMUTH_BRONZE   ("ceramic_mold_pick_bismuth_bronze"),
	MOLD_PICK_BLACK_BRONZE     ("ceramic_mold_pick_black_bronze"),

	MOLD_PROPICK_CLAY          ("clay_mold_propick"),
	MOLD_PROPICK               ("ceramic_mold_propick"),
	MOLD_PROPICK_COPPER        ("ceramic_mold_propick_copper"),
	MOLD_PROPICK_BRONZE        ("ceramic_mold_propick_bronze"),
	MOLD_PROPICK_BISMUTH_BRONZE("ceramic_mold_propick_bismuth_bronze"),
	MOLD_PROPICK_BLACK_BRONZE  ("ceramic_mold_propick_black_bronze"),

	MOLD_SAW_CLAY              ("clay_mold_saw"),
	MOLD_SAW                   ("ceramic_mold_saw"),
	MOLD_SAW_COPPER            ("ceramic_mold_saw_copper"),
	MOLD_SAW_BRONZE            ("ceramic_mold_saw_bronze"),
	MOLD_SAW_BISMUTH_BRONZE    ("ceramic_mold_saw_bismuth_bronze"),
	MOLD_SAW_BLACK_BRONZE      ("ceramic_mold_saw_black_bronze"),

	MOLD_SCYTHE_CLAY           ("clay_mold_scythe"),
	MOLD_SCYTHE                ("ceramic_mold_scythe"),
	MOLD_SCYTHE_COPPER         ("ceramic_mold_scythe_copper"),
	MOLD_SCYTHE_BRONZE         ("ceramic_mold_scythe_bronze"),
	MOLD_SCYTHE_BISMUTH_BRONZE ("ceramic_mold_scythe_bismuth_bronze"),
	MOLD_SCYTHE_BLACK_BRONZE   ("ceramic_mold_scythe_black_bronze"),

	MOLD_SHOVEL_CLAY           ("clay_mold_shovel"),
	MOLD_SHOVEL                ("ceramic_mold_shovel"),
	MOLD_SHOVEL_COPPER         ("ceramic_mold_shovel_copper"),
	MOLD_SHOVEL_BRONZE         ("ceramic_mold_shovel_bronze"),
	MOLD_SHOVEL_BISMUTH_BRONZE ("ceramic_mold_shovel_bismuth_bronze"),
	MOLD_SHOVEL_BLACK_BRONZE   ("ceramic_mold_shovel_black_bronze"),

	MOLD_SWORD_CLAY            ("clay_mold_sword"),
	MOLD_SWORD                 ("ceramic_mold_sword"),
	MOLD_SWORD_COPPER          ("ceramic_mold_sword_copper"),
	MOLD_SWORD_BRONZE          ("ceramic_mold_sword_bronze"),
	MOLD_SWORD_BISMUTH_BRONZE  ("ceramic_mold_sword_bismuth_bronze"),
	MOLD_SWORD_BLACK_BRONZE    ("ceramic_mold_sword_black_bronze"),

	MOLD_JAVELIN_CLAY          ("clay_mold_javelin"),
	MOLD_JAVELIN               ("ceramic_mold_javelin"),
	MOLD_JAVELIN_COPPER        ("ceramic_mold_javelin_copper"),
	MOLD_JAVELIN_BRONZE        ("ceramic_mold_javelin_bronze"),
	MOLD_JAVELIN_BISMUTH_BRONZE("ceramic_mold_javelin_bismuth_bronze"),
	MOLD_JAVELIN_BLACK_BRONZE  ("ceramic_mold_javelin_black_bronze");


	private String name;

	ClayMoldType(String s)
	{
		name = s;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public static String[] getNamesArray()
	{
		String[] s = new String[values().length];
		for(int i = 0; i < ClayMoldType.values().length; i++)
		{
			s[i] = ClayMoldType.values()[i].getName();
		}
		return s;
	}
}
