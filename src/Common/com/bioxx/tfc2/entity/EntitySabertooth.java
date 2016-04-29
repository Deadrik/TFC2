package com.bioxx.tfc2.entity;

import net.minecraft.world.World;

public class EntitySabertooth extends EntityBigCat
{
	public EntitySabertooth(World worldIn) 
	{
		super(worldIn);
		this.setCatType(BigCatType.Sabertooth);
	}
}
