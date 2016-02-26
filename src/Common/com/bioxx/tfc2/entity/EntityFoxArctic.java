package com.bioxx.tfc2.entity;

import net.minecraft.world.World;

import com.bioxx.tfc2.api.types.Gender;

public class EntityFoxArctic extends EntityFoxRed
{
	Gender gender;

	public EntityFoxArctic(World worldIn) 
	{
		super(worldIn);
		this.setSize(0.4F, 0.5F);
	}
}
