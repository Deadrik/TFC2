package com.bioxx.tfc2.entity.ai;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.animals.Herd;
import com.bioxx.tfc2.api.animals.HerdActivityEnum;
import com.bioxx.tfc2.api.interfaces.IHerdAnimal;

public class EntityAIWanderHex extends EntityAIBase
{
	protected final EntityCreature entity;
	protected double xPosition;
	protected double yPosition;
	protected double zPosition;
	protected final double speed;
	protected int executionChance;
	protected boolean mustUpdate;

	public EntityAIWanderHex(EntityCreature creatureIn, double speedIn)
	{
		this(creatureIn, speedIn, 120);
	}

	public EntityAIWanderHex(EntityCreature creatureIn, double speedIn, int chance)
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
		if (!this.mustUpdate)
		{
			if (this.entity.getAge() >= 100)
			{
				return false;
			}

			if (this.entity.getRNG().nextInt(this.executionChance) != 0)
			{
				return false;
			}
		}
		IslandMap map = Core.getMapForWorld(entity.world, entity.getPosition());
		Vec3d vec3d = generateRandomPosInCenter(entity, 18, 5, true, map, map.getClosestCenter(entity.getPosition()));

		if(entity instanceof IHerdAnimal)
		{
			Herd herd = map.getIslandData().wildlifeManager.getHerd(((IHerdAnimal)entity).getHerdUUID());
			if(herd != null)
			{
				HerdActivityEnum activity = herd.getHerdBrain().getActivity();
				if(activity != HerdActivityEnum.WAITING && activity != HerdActivityEnum.WORKING)
				{
					return false;
				}
			}
		}

		if (vec3d == null)
		{
			return false;
		}
		else
		{
			this.xPosition = vec3d.xCoord;
			this.yPosition = vec3d.yCoord;
			this.zPosition = vec3d.zCoord;
			this.mustUpdate = false;
			return true;
		}
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

	/**
	 * Makes task to bypass chance
	 */
	public void makeUpdate()
	{
		this.mustUpdate = true;
	}

	/**
	 * Changes task random possibility for execution
	 */
	public void setExecutionChance(int newchance)
	{
		this.executionChance = newchance;
	}

	@Nullable
	public static Vec3d generateRandomPosInCenter(EntityCreature entity, int xz, int y,boolean pathThroughWater, IslandMap map, Center c)
	{
		PathNavigate pathnavigate = entity.getNavigator();
		Random random = entity.getRNG();
		boolean flag;
		BlockPos centerPos = c.point.toBlockPos().add(map.getParams().getWorldX(), 0, map.getParams().getWorldZ());

		if (entity.hasHome())
		{
			double d0 = entity.getHomePosition().distanceSq((double)MathHelper.floor(entity.posX), (double)MathHelper.floor(entity.posY), (double)MathHelper.floor(entity.posZ)) + 4.0D;
			double d1 = (double)(entity.getMaximumHomeDistance() + (float)xz);
			flag = d0 < d1 * d1;
		}
		else
		{
			flag = false;
		}

		boolean flag1 = false;
		float weight = -99999.0F;
		int outX = 0;
		int outY = 0;
		int outZ = 0;

		for (int k = 0; k < 10; ++k)
		{
			int _x = random.nextInt(2 * xz + 1) - xz;
			int _y = random.nextInt(2 * y + 1) - y;
			int _z = random.nextInt(2 * xz + 1) - xz;


			if (entity.hasHome() && xz > 1)
			{
				BlockPos blockpos = entity.getHomePosition();

				if (entity.posX > (double)blockpos.getX())
				{
					_x -= random.nextInt(xz / 2);
				}
				else
				{
					_x += random.nextInt(xz / 2);
				}

				if (entity.posZ > (double)blockpos.getZ())
				{
					_z -= random.nextInt(xz / 2);
				}
				else
				{
					_z += random.nextInt(xz / 2);
				}
			}

			BlockPos blockpos1 = new BlockPos((double)_x + centerPos.getX(), (double)_y + centerPos.getY(), (double)_z + centerPos.getZ());

			if ((!flag || entity.isWithinHomeDistanceFromPosition(blockpos1)) && pathnavigate.canEntityStandOnPos(blockpos1))
			{
				if (!pathThroughWater)
				{
					blockpos1 = moveAboveSolid(blockpos1, entity);

					if (isWaterDestination(blockpos1, entity))
					{
						continue;
					}
				}

				float _weight = entity.getBlockPathWeight(blockpos1);

				if (_weight > weight)
				{
					weight = _weight;
					outX = _x;
					outY = _y;
					outZ = _z;
					flag1 = true;
				}
			}
		}

		if (flag1)
		{
			return new Vec3d((double)outX + centerPos.getX(), (double)outY + centerPos.getY(), (double)outZ + centerPos.getZ());
		}
		else
		{
			return null;
		}
	}

	private static BlockPos moveAboveSolid(BlockPos pos, EntityCreature entity)
	{
		if (!entity.world.getBlockState(pos).getMaterial().isSolid())
		{
			return pos;
		}
		else
		{
			BlockPos blockpos;

			for (blockpos = pos.up(); blockpos.getY() < entity.world.getHeight() && entity.world.getBlockState(blockpos).getMaterial().isSolid(); blockpos = blockpos.up())
			{
				;
			}

			return blockpos;
		}
	}

	private static boolean isWaterDestination(BlockPos p_191380_0_, EntityCreature p_191380_1_)
	{
		return p_191380_1_.world.getBlockState(p_191380_0_).getMaterial() == Material.WATER;
	}
}
