package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum EnumAnimalDiet implements IStringSerializable
{
	Herbivore, Carnivore, Omnivore;

	@Override
	public String getName() {
		if(this == Herbivore)
			return "Herbivore";
		else if(this == Omnivore)
			return "Omnivore";
		else
			return "Carnivore";
	}
}
