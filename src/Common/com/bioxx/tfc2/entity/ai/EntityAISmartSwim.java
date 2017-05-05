package com.bioxx.tfc2.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAISmartSwim extends EntityAIBase {

	private final EntityLiving theEntity;

	public EntityAISmartSwim(EntityLiving entitylivingIn)
	{
		this.theEntity = entitylivingIn;
		this.setMutexBits(4);
		((PathNavigateGround)entitylivingIn.getNavigator()).setCanSwim(true);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute()
	{
		return this.theEntity.isInWater() || this.theEntity.isInLava();
	}

	/**
	 * Updates the task
	 */
	@Override
	public void updateTask()
	{
		if (this.theEntity.getRNG().nextFloat() < 0.8F)
		{
			theEntity.getMoveHelper().strafe(1.0f, 0f);
			this.theEntity.getJumpHelper().setJumping();
		}
	}

}
