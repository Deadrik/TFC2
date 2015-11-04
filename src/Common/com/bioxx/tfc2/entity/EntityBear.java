package com.bioxx.tfc2.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.bioxx.tfc2.core.TFC_Sounds;

public class EntityBear extends EntityTameable 
{

	public EntityBear(World worldIn) 
	{
		super(worldIn);
		this.setSize(1.3F, 1.3F);
		((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMate(this, 0.8D));
		this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 0.8D, true));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 0.8D));		
		this.tasks.addTask(6, new EntityAIWander(this, 0.5D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true, new Class[0]));//The array seems to be for class types that this task should ignore
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) 
	{
		return null;
	}

	@Override
	protected void updateAITick ()
	{
		//dataWatcher.updateObject (18, getHealth());
		this.motionY += 0.03999999910593033D;
	}

	@Override
	protected void entityInit ()
	{
		super.entityInit ();
		//dataWatcher.addObject (18, getHealth());
		/*this.dataWatcher.addObject(13, Integer.valueOf(0)); //sex (1 or 0)
		this.dataWatcher.addObject(15, Integer.valueOf(0));		//age
		this.dataWatcher.addObject(22, Integer.valueOf(0)); //Size, strength, aggression, obedience
		this.dataWatcher.addObject(23, Integer.valueOf(0)); //familiarity, familiarizedToday, pregnant, empty slot
		this.dataWatcher.addObject(24, String.valueOf("0")); // Time of conception, stored as a string since we can't do long*/
	}


	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1000);//MaxHealth
	}

	/**
	 * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
	 * prevent them from trampling crops
	 */
	@Override
	protected boolean canTriggerWalking ()
	{
		return true;
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	@Override
	public void writeEntityToNBT (NBTTagCompound nbt)
	{
		super.writeEntityToNBT (nbt);
	}


	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
	}


	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	@Override
	protected boolean canDespawn ()
	{
		return this.ticksExisted > 30000;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	@Override
	protected String getLivingSound ()
	{
		if(isChild() && worldObj.rand.nextInt(100) < 5)
			return TFC_Sounds.BEARCUBCRY;
		else if(worldObj.rand.nextInt(100) < 5)
			return TFC_Sounds.BEARCRY;

		return isChild() ? null : TFC_Sounds.BEARSAY;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected String getHurtSound ()
	{
		if(!isChild())
			return TFC_Sounds.BEARHURT;
		else
			return TFC_Sounds.BEARCUBCRY;
	}

	/**
	 * Returns the sound this mob makes on death.
	 */
	@Override
	protected String getDeathSound ()
	{
		if(!isChild())
			return TFC_Sounds.BEARDEATH;
		else
			return TFC_Sounds.BEARCUBCRY;
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	@Override
	protected float getSoundVolume ()
	{
		return 0.4F;
	}

	/**
	 * Returns the item ID for the item the mob drops on death.
	 */
	@Override
	protected Item getDropItem()
	{
		return Item.getItemById(0);
	}

	@Override
	protected void dropFewItems(boolean par1, int par2)
	{
		/*float ageMod = TFC_Core.getPercentGrown(this);

		this.entityDropItem(new ItemStack(TFCItems.hide, 1, Math.max(0, Math.min(2, (int)(ageMod * 3 - 1)))), 0);
		this.dropItem(Items.bone, (int) ((rand.nextInt(6) + 2) * ageMod));*/
	}


	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();

		/*if (!worldObj.isRemote)
		{
			if (!isWet &&!hasPath() && onGround)
			{
				isWet = true;
				worldObj.setEntityState(this, (byte) 8);
			}

			if (isPregnant())
			{
				if (TFC_Time.getTotalTicks() >= timeOfConception + pregnancyRequiredTime)
				{
					int i = rand.nextInt(3) + 1;
					for (int x = 0; x < i; x++)
					{
						EntityBear baby = (EntityBear) createChildTFC(this);//new EntityBear(worldObj, this,data);
						worldObj.spawnEntityInWorld(baby);
					}
					pregnant = false;
				}
			}
		}

		if (this.getLeashed() && !wasRoped)
			wasRoped = true;

		this.handleFamiliarityUpdate();
		this.syncData();*/
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		/*if (!this.worldObj.isRemote)
		{
			if (!isPeacefulAI && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL)
			{
				isPeacefulAI = true;
				tasks.removeTask(attackAI);
				tasks.removeTask(leapAI);
				targetTasks.removeTask(targetSheep);
				targetTasks.removeTask(targetDeer);
				targetTasks.removeTask(targetPig);
				targetTasks.removeTask(targetHorse);
				targetTasks.removeTask(targetPlayer);
				targetTasks.removeTask(hurtAI);
			}
			else if (isPeacefulAI && this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL)
			{
				isPeacefulAI = false;
				tasks.addTask(4, attackAI);
				tasks.addTask(3, leapAI);
				targetTasks.addTask(4, targetSheep);
				targetTasks.addTask(4, targetDeer);
				targetTasks.addTask(4, targetPig);
				targetTasks.addTask(4, targetHorse);
				targetTasks.addTask(4, targetPlayer);
				targetTasks.addTask(3, hurtAI);
			}
		}*/
	}

	@Override
	public float getEyeHeight ()
	{
		return height * 0.8F;
	}

	@Override
	public boolean attackEntityAsMob (Entity par1Entity)
	{
		int dam =  100;
		return par1Entity.attackEntityFrom (DamageSource.causeMobDamage (this), dam);
	}

	@Override
	public void handleHealthUpdate (byte par1)
	{
		if (par1 == 8)
		{
			//isWet = true;
		}
		else
		{
			super.handleHealthUpdate (par1);
		}
	}
}
