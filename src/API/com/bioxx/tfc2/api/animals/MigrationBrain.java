package com.bioxx.tfc2.api.animals;

import java.util.ArrayList;
import java.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.NeedZoneAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.pathfinding.CenterPath;
import com.bioxx.jmapgen.pathfinding.CenterPathFinder;
import com.bioxx.tfc2.Core;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class MigrationBrain implements IHerdBrain
{
	public Herd herd;
	public HerdGoal currentGoal;
	public HerdActivityEnum currentActivity;
	public int activityTimer;
	public Center currentLocation;

	int hoursWaitingOnHerd = 0;

	public MigrationBrain(Herd h, Center curLoc)
	{
		herd = h;
		activityTimer = 1;
		currentActivity = HerdActivityEnum.WORKING;
		currentLocation = curLoc;
		currentGoal = new HerdGoal(HerdGoalEnum.REST, curLoc);
	}

	@Override
	public void process(World world, IslandMap map, long currentHour)
	{
		activityTimer--;
		if(activityTimer <= 0)
		{
			if(currentActivity == HerdActivityEnum.WORKING)//The herd is ready to leave the current need zone
			{
				activityTimer = 1;

				//========Create a new goal========
				//1. Decide what the herd needs
				HerdGoalEnum goal = HerdGoalEnum.REST;
				if(currentGoal.goalType == HerdGoalEnum.REST)
				{
					goal = world.rand.nextBoolean() ? HerdGoalEnum.FOOD : HerdGoalEnum.WATER;
				}
				else if(currentGoal.goalType == HerdGoalEnum.FOOD)
				{
					goal = world.rand.nextBoolean() ? HerdGoalEnum.REST : HerdGoalEnum.WATER;
				}
				else
				{
					goal = world.rand.nextBoolean() ? HerdGoalEnum.REST : HerdGoalEnum.FOOD;
				}
				//2. Find an appropriate location to fill this need
				Vector<Center> allZones = map.filterKeepAttributes(map.centers, Attribute.NeedZone);
				Vector<Center> goalZones = new Vector<Center>();
				for(Center z : allZones)
				{
					NeedZoneAttribute attrib = (NeedZoneAttribute) z.getAttribute(Attribute.NeedZone);
					if(attrib.goalType == goal)
						goalZones.add(z);
				}
				Center goalLoc = goalZones.get(world.rand.nextInt(goalZones.size()));
				//3. Figure out a path to reach the destination
				CenterPathFinder pathfinder = new CenterPathFinder(AnimalSpawnRegistry.getInstance().getDefFromName(herd.animalType).getPathProfile());
				CenterPath path = pathfinder.findPath(map, currentLocation, goalLoc);
				//4. Move the herd
				if(path == null)
					return;
				currentGoal = new HerdGoal(goal, goalLoc, new HerdPath(currentHour, path));
				currentActivity = HerdActivityEnum.TRAVELING;
			}
			else if(currentActivity == HerdActivityEnum.TRAVELING)//The herd is actively moving to a new needzone
			{
				if(herd.isLoaded())
				{
					ArrayList<VirtualAnimal> animalsToRemove = new ArrayList<VirtualAnimal>();
					for(VirtualAnimal animal : herd.animals)
					{
						if(animal.getEntity() == null)
						{
							animalsToRemove.add(animal);
						}
						else
						{
							Center c = map.getClosestCenter(animal.getEntity().getPosition());
							if(c != currentLocation)
							{
								if(hoursWaitingOnHerd < 3)
								{
									hoursWaitingOnHerd++;
									activityTimer = 1;
									return;
								}
								else
								{
									Predicate<Entity> predicate = Predicates.<Entity>and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(animal.getEntity()));
									Entity closestEntity = animal.getEntity().world.getClosestPlayer(animal.getEntity().posX, animal.getEntity().posY, animal.getEntity().posZ, 100D, predicate);
									if(closestEntity == null)
									{
										BlockPos pos = new BlockPos(currentLocation.point.getX()+map.getParams().getWorldX(), 0, currentLocation.point.getZ()+map.getParams().getWorldZ());
										BlockPos randPos = pos;

										for(int i = 0; i < 10; i++)
										{
											randPos = pos.add(world.rand.nextInt(21)-10, 0, world.rand.nextInt(21)-10);
											randPos = world.getTopSolidOrLiquidBlock(pos);
											if(Core.isTerrain(world.getBlockState(randPos.down())))
												break;
										}
										animal.getEntity().setPosition(randPos.getX(), randPos.getY(), randPos.getZ());
									}
									else
									{
										animalsToRemove.add(animal);
									}
								}
							}
						}
					}

					for(VirtualAnimal animal : animalsToRemove)
					{
						herd.animals.remove(animal);
					}
				}
				hoursWaitingOnHerd = 0;
				currentLocation = currentGoal.path.move();
				activityTimer = 1;
				if(currentGoal.goalLocation == currentLocation)
				{
					activityTimer = 24;
					currentActivity = HerdActivityEnum.WORKING;
				}
			}
			else if(currentActivity == HerdActivityEnum.WAITING)//Something interrupted this herd while it was traveling and it should recalculate its route
			{
				activityTimer = 2;
				currentLocation = currentGoal.path.move();
				currentGoal.path.recalculatePath(currentLocation);
			}
			else if(currentActivity == HerdActivityEnum.FLEEING)//If we have recently been fleeing from danger we should reset to waiting for now
			{
				activityTimer = 1;
				currentActivity = HerdActivityEnum.WAITING;
			}
		}
	}

	@Override
	public HerdActivityEnum getActivity()
	{
		return currentActivity;
	}

	@Override
	public void setLocation(Center center)
	{
		currentLocation = center;
	}

	@Override
	public Center getLocation()
	{
		return currentLocation;
	}

	@Override
	public void setGoal(HerdGoalEnum goal, Center loc)
	{
		currentGoal = new HerdGoal(goal, loc);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, IslandMap map)
	{
		currentActivity = HerdActivityEnum.getEnum(nbt.getString("currentActivity"));
		activityTimer = nbt.getInteger("activityTimer");
		currentLocation = map.centers.get(nbt.getInteger("currentLocation"));
		currentGoal = new HerdGoal(HerdGoalEnum.REST, currentLocation);
		currentGoal.readFromNBT(nbt.getCompoundTag("currentGoal"), map);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("activityTimer", activityTimer);

		NBTTagCompound goalNBT = new NBTTagCompound();
		currentGoal.writeToNBT(goalNBT);
		nbt.setTag("currentGoal", goalNBT);

		nbt.setInteger("currentLocation", currentLocation.index);
		nbt.setString("currentActivity", currentActivity.getName());
	}
}