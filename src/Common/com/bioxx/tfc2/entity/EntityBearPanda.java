package com.bioxx.tfc2.entity;

import net.minecraft.world.World;

public class EntityBearPanda extends EntityBear
{

	public EntityBearPanda(World worldIn) 
	{
		super(worldIn);
	}


	@Override
	protected void entityInit ()
	{
		super.entityInit ();
		dataWatcher.updateObject (14, BearType.Panda.ordinal());
	}
}
