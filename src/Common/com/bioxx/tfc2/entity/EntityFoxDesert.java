package com.bioxx.tfc2.entity;

import net.minecraft.world.World;

import com.bioxx.tfc2.api.types.Gender;

public class EntityFoxDesert extends EntityFoxRed
{
	Gender gender;

	public EntityFoxDesert(World worldIn) 
	{
		super(worldIn);
		this.setSize(0.4F, 0.5F);
	}
}
