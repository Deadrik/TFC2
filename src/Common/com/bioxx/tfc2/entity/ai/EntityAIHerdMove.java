package com.bioxx.tfc2.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.Vec3d;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandWildlifeManager;
import com.bioxx.jmapgen.IslandWildlifeManager.Herd;
import com.bioxx.jmapgen.IslandWildlifeManager.HerdActivityEnum;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.interfaces.IHerdAnimal;

public class EntityAIHerdMove extends EntityAIBase
{
	protected final EntityAnimal entity;
	protected double xPosition;
	protected double yPosition;
	protected double zPosition;
	protected final double speed;
	protected int executionChance;
	protected boolean mustUpdate;
	Center target;

	public EntityAIHerdMove(EntityAnimal creatureIn, double speedIn)
	{
		this(creatureIn, speedIn, 120);
	}

	public EntityAIHerdMove(EntityAnimal creatureIn, double speedIn, int chance)
	{
		this.entity = creatureIn;
		this.speed = speedIn;
		this.executionChance = chance;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	@Override
	public boolean shouldExecute()
	{
		if(!mustUpdate)
		{
			if (this.entity.getRNG().nextInt(2/*this.executionChance*/) != 0)
			{
				return false;
			}
		}

		if(entity instanceof IHerdAnimal)
		{
			IHerdAnimal animal = (IHerdAnimal)entity;

			IslandMap map = Core.getMapForWorld(entity.world, entity.getPosition());
			IslandWildlifeManager iwf = map.getIslandData().wildlifeManager;
			Herd herd = iwf.getHerd(animal.getHerdUUID());
			if(herd!= null && herd.getHerdBrain().getActivity() == HerdActivityEnum.TRAVELING)
			{
				Center loc = iwf.getHerd(animal.getHerdUUID()).getHerdBrain().getLocation();
				if(loc == target)
					return false;
				target = loc;
				Point p = loc.point.plus(map.getParams().getWorldX(), map.getParams().getWorldZ());
				Vec3d vec3d = new Vec3d(p.getX(), Global.SEALEVEL+map.convertHeightToMC(loc.getElevation()), p.getZ());
				net.minecraft.pathfinding.Path path = entity.getNavigator().getPathToXYZ(vec3d.xCoord, vec3d.yCoord, vec3d.zCoord);
				if(path == null)
				{
					vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(entity, 20, 5, vec3d);
				}
				if(vec3d != null)
				{
					this.xPosition = vec3d.xCoord;
					this.yPosition = vec3d.yCoord;
					this.zPosition = vec3d.zCoord;
					mustUpdate = false;
					return true;
				}

			}
		}
		return false;
	}

	@Nullable
	protected Vec3d getPosition(Vec3d pos)
	{
		return RandomPositionGenerator.generateRandomPos(this.entity, 5, 5, pos, true);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean continueExecuting()
	{
		return !this.entity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting()
	{
		this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
	}
}
