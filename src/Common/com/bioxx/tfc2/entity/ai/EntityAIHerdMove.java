package com.bioxx.tfc2.entity.ai;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.WildlifeManager;
import com.bioxx.tfc2.api.animals.Herd;
import com.bioxx.tfc2.api.animals.HerdActivityEnum;
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
	Vec3d targetVec;

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
			if (this.entity.getRNG().nextInt(10) != 0)
			{
				return false;
			}
		}

		if(entity instanceof IHerdAnimal)
		{
			IHerdAnimal animal = (IHerdAnimal)entity;

			IslandMap map = Core.getMapForWorld(entity.world, entity.getPosition());
			WildlifeManager iwf = map.getIslandData().wildlifeManager;
			Herd herd = iwf.getHerd(animal.getHerdUUID());
			if(herd!= null && herd.getHerdBrain().getActivity() == HerdActivityEnum.TRAVELING)
			{
				Center loc = iwf.getHerd(animal.getHerdUUID()).getHerdBrain().getLocation();
				Center closest = map.getClosestCenter(entity.getPosition());
				if(loc == closest)
					return false;

				Point p = loc.point.plus(map.getParams().getWorldX(), map.getParams().getWorldZ());
				//Vec3d vec3d = new Vec3d(-5+entity.world.rand.nextInt(11)+p.getX(), Global.SEALEVEL+map.convertHeightToMC(loc.getElevation()), -5+entity.world.rand.nextInt(11)+p.getZ());
				Vec3d vec3d = new Vec3d(p.getX(), Global.SEALEVEL+map.convertHeightToMC(loc.getElevation()),p.getZ());
				vec3d = findRandomTargetBlockTowards(entity, 20, 8, vec3d);
				if(vec3d != null)
				{
					//TFC.log.info(vec3d.toString());
					target = loc;
					targetVec = vec3d;
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

	@Nullable
	public static Vec3d findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3d targetVec3)
	{
		return generateRandomPos(entitycreatureIn, xz, y, targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ), true);
	}

	@Nullable
	public static Vec3d generateRandomPos(EntityCreature entity, int xz, int y, @Nullable Vec3d target, boolean p_191379_4_)
	{
		PathNavigate pathnavigate = entity.getNavigator();
		Random random = entity.getRNG();
		boolean flag;

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

			if (target == null || (double)_x * target.xCoord + (double)_z * target.zCoord >= 0.0D)
			{
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

				BlockPos blockpos1 = new BlockPos((double)_x + entity.posX, (double)_y + entity.posY, (double)_z + entity.posZ);

				if ((!flag || entity.isWithinHomeDistanceFromPosition(blockpos1)) && pathnavigate.canEntityStandOnPos(blockpos1))
				{
					if (!p_191379_4_)
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
		}

		if (flag1)
		{
			return new Vec3d((double)outX + entity.posX, (double)outY + entity.posY, (double)outZ + entity.posZ);
		}
		else
		{
			return null;
		}
	}

	private static BlockPos moveAboveSolid(BlockPos p_191378_0_, EntityCreature p_191378_1_)
	{
		if (!p_191378_1_.world.getBlockState(p_191378_0_).getMaterial().isSolid())
		{
			return p_191378_0_;
		}
		else
		{
			BlockPos blockpos;

			for (blockpos = p_191378_0_.up(); blockpos.getY() < p_191378_1_.world.getHeight() && p_191378_1_.world.getBlockState(blockpos).getMaterial().isSolid(); blockpos = blockpos.up())
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
