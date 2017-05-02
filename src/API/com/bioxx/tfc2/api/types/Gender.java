package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum Gender implements IStringSerializable
{
	Male, Female;

	@Override
	public String getName() {
		if(this == Male)
			return "Male";
		else
			return "Female";
	}
}
