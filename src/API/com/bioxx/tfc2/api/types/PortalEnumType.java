package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum PortalEnumType implements IStringSerializable
{
	None("none"), Disabled("disabled"), Enabled("enabled"), Locked("locked"), Gate("gate");

	String name;

	PortalEnumType(String s)
	{
		name = s;
	}

	@Override
	public String getName() {
		return name;
	}
}