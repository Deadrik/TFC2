package com.bioxx.tfc2.entity;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.animals.Herd;
import com.bioxx.tfc2.api.animals.IGenderedAnimal;
import com.bioxx.tfc2.api.animals.VirtualAnimal;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.interfaces.IHerdAnimal;
import com.bioxx.tfc2.api.types.Gender;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.entity.ai.EntityAIHerdMove;
import com.bioxx.tfc2.entity.ai.EntityAISmartSwim;
import com.bioxx.tfc2.entity.ai.EntityAIWanderHex;

public class EntityBear extends EntityAnimal implements IHerdAnimal, IGenderedAnimal
{
	UUID herdID;
	BearType bearType;
	Gender gender;
	IAnimalDef animalDef;
	protected static final DataParameter<Gender> GENDER = EntityDataManager.createKey(EntityBear.class, DataSerializersTFC.GENDER);
	protected static final DataParameter<BearType> BEARTYPE = EntityDataManager.createKey(EntityBear.class, DataSerializersTFC.BEARTYPE);

	public EntityBear(World worldIn) 
	{
		super(worldIn);
		this.setSize(1.5F, 1.7F);
		((PathNavigateGround)this.getNavigator()).setCanSwim(true);
		this.tasks.addTask(0, new EntityAISmartSwim(this));
		this.tasks.addTask(2, new EntityAIMate(this, 0.8D));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 0.8D, true));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 0.8D));		
		this.tasks.addTask(6, new EntityAIHerdMove(this, 0.5D));
		this.tasks.addTask(7, new EntityAIWanderHex(this, 0.5D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true, new Class[0]));//The array seems to be for class types that this task should ignore
		setGender(worldIn.rand.nextBoolean() ? Gender.Male : Gender.Female);
		setBearType(BearType.values()[worldIn.rand.nextInt(3)]);
	}

	@Override
	public EntityAgeable createChild(EntityAgeable ageable) 
	{
		return null;
	}

	@Override
	protected void updateAITasks()
	{
		//getDataManager().set (18, getHealth());
		this.motionY += 0.03999999910593033D;
	}

	@Override
	protected void entityInit ()
	{
		super.entityInit ();
		getDataManager().register(GENDER, gender);
		getDataManager().register(BEARTYPE, bearType);
	}


	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(1000);//MaxHealth
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
		nbt.setInteger("BearType", bearType.ordinal());
		nbt.setInteger("gender", gender.ordinal());
	}


	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.setBearType(BearType.values()[nbt.getInteger("BearType")]);
		this.setGender(Gender.values()[nbt.getInteger("gender")]);
	}

	@Override
	public boolean writeToNBTOptional(NBTTagCompound compound)
	{
		//we should return true here later if the entity needs to stay around

		//return false here to prevent the entity from saving to disk
		return false;
	}

	/**
	 * Determines if an entity can be despawned, used on idle far away entities
	 */
	@Override
	protected boolean canDespawn ()
	{
		if(getEntityWorld().provider.getDimension() == 0)
		{
			IslandMap map = Core.getMapForWorld(getEntityWorld(), getPosition());
			boolean inLoaded = Core.isHexFullyLoaded(getEntityWorld(), map, map.getClosestCenter(getPosition()));
			return !inLoaded;
		}
		return false;
	}

	@Override
	protected void despawnEntity()
	{
		super.despawnEntity();
		if(canDespawn())
		{
			IslandMap map = Core.getMapForWorld(getEntityWorld(), getPosition());
			Herd h = map.getIslandData().wildlifeManager.getHerd(getHerdUUID());
			if(h != null)
			{
				for(VirtualAnimal a : h.getVirtualAnimals())
				{
					if(a.isLoaded() && a.getEntity() == this)
					{
						a.setUnloaded();
					}
				}
			}
			this.setDead();
		}
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	@Override
	protected SoundEvent getAmbientSound ()
	{
		if(isChild() && world.rand.nextInt(100) < 5)
			return TFC_Sounds.BEARCUBCRY;
		else if(world.rand.nextInt(100) < 5)
			return TFC_Sounds.BEARCRY;

		return isChild() ? null : TFC_Sounds.BEARSAY;
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	@Override
	protected SoundEvent getHurtSound()
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
	protected SoundEvent getDeathSound()
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
	}


	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void onLivingUpdate()
	{
		super.onLivingUpdate();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		super.onUpdate();
	}

	@Override
	public float getEyeHeight ()
	{
		return height * 0.8F;
	}

	@Override
	public boolean attackEntityAsMob (Entity par1Entity)
	{
		int dam =  5;
		return par1Entity.attackEntityFrom (DamageSource.causeMobDamage (this), dam);
	}

	@Override
	public void setGender(Gender t)
	{
		this.gender = t;
		getDataManager().set(GENDER, t);	
	}

	public void setBearType(BearType t)
	{
		this.bearType = t;
		getDataManager().set(BEARTYPE, t);	
	}

	public BearType getBearType()
	{
		return getDataManager().get(BEARTYPE);	
	}

	public enum BearType
	{
		Brown, Polar, Black, Panda;
	}

	@Override
	public Gender getGender() 
	{
		return gender;
	}

	@Override
	public UUID getHerdUUID() {
		return herdID;
	}

	@Override
	public void setHerdUUID(UUID id) {
		herdID = id;

	}

	@Override
	public IAnimalDef getAnimalDef() {
		return animalDef;
	}

	@Override
	public void setAnimalDef(IAnimalDef def) {
		animalDef = def;
	}
}
