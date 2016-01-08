package com.bioxx.tfc2.api.properties;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.util.IStringSerializable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class PropertyClass extends PropertyHelper
{
	private final ImmutableSet allowedValues;
	/** Map of names to Enum values */
	private final Map nameToValue = Maps.newHashMap();

	protected PropertyClass(String name, Class valueClass, Collection allowedValues) 
	{
		super(name, valueClass);
		this.allowedValues = ImmutableSet.copyOf(allowedValues);
		Iterator iterator = allowedValues.iterator();

		while (iterator.hasNext())
		{
			IStringSerializable oenum = (IStringSerializable)iterator.next();
			String s1 = oenum.getName();

			if (this.nameToValue.containsKey(s1))
			{
				throw new IllegalArgumentException("Multiple values have the same name \'" + s1 + "\'");
			}

			this.nameToValue.put(s1, oenum);
		}
	}

	public static PropertyClass create(String name, Class clazz, Collection values)
	{
		return new PropertyClass(name, clazz, values);
	}

	@Override
	public Collection getAllowedValues() {
		return allowedValues;
	}

	@Override
	public String getName(Comparable value) {
		return ((IStringSerializable)value).getName();
	}
}
