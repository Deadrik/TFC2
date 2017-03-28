package com.bioxx.tfc2.entity;

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

import com.bioxx.tfc2.api.types.Gender;
import com.bioxx.tfc2.core.TFC_Sounds;

public class EntityTiger extends EntityAnimal
{
	private Gender gender = Gender.Male;
	private TigerType tigerType = TigerType.Normal;
	protected static final DataParameter<Gender> GENDER = EntityDataManager.createKey(EntityLion.class, DataSerializersTFC.GENDER);
	protected static final DataParameter<TigerType> TIGERTYPE = EntityDataManager.createKey(EntityTiger.class, DataSerializersTFC.TIGERTYPE);

	public EntityTiger(World worldIn) 
	{
		super(worldIn);
		this.setSize(1.5F, 1.7F);
		((PathNavigateGround)this.getNavigator()).setCanSwim(true);
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(2, new EntityAIMate(this, 0.8D));
		this.tasks.addTask(4, new EntityAIAttackMelee(this, 0.8D, true));
		this.tasks.addTask(5, new EntityAIFollowParent(this, 0.8D));		
		this.tasks.addTask(6, new EntityAIWander(this, 0.5D));
		this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(0, new EntityAIHurtByTarget(this, true, new Class[0]));//The array seems to be for class types that this task should ignore
		setGender(worldIn.rand.nextBoolean() ? Gender.Male : Gender.Female);
		setTigerType(worldIn.rand.nextBoolean() ? TigerType.Normal : TigerType.Snow);
	}

	public EntityTiger(World worldIn, Gender gender) 
	{
		this(worldIn);
		this.gender = gender;
	}

	public Gender getGender()
	{
		return gender;
	}


	@Override
	public EntityAgeable createChild(EntityAgeable ageable) 
	{
		return null;
	}

	@Override
	protected void updateAITasks()
	{
		this.motionY += 0.03999999910593033D;
	}

	@Override
	protected void entityInit ()
	{
		super.entityInit ();
		getDataManager().register(GENDER, gender);
		getDataManager().register(TIGERTYPE, tigerType);
	}


	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);//MaxHealth
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
		nbt.setInteger("gender", gender.ordinal());
		nbt.setInteger("tigerType", tigerType.ordinal());
	}


	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.gender = Gender.values()[nbt.getInteger("gender")];
		setTigerType(TigerType.values()[nbt.getInteger("tigerType")]);
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
		return height * 0.85F;
	}

	@Override
	public boolean attackEntityAsMob (Entity par1Entity)
	{
		int dam =  100;
		return par1Entity.attackEntityFrom (DamageSource.causeMobDamage (this), dam);
	}

	@Override
	public void handleStatusUpdate (byte par1)
	{
		if (par1 == 8)
		{
			//isWet = true;
		}
		else
		{
			super.handleStatusUpdate (par1);
		}
	}

	protected void setGender(Gender t)
	{
		this.gender = t;
		getDataManager().set(GENDER, t);	
	}

	public void setTigerType(TigerType t)
	{
		this.tigerType = t;
		getDataManager().set(TIGERTYPE, t);
	}

	public TigerType getTigerType()
	{
		return getDataManager().get(TIGERTYPE);
	}

	public static enum TigerType
	{
		Normal, Snow;
	}
}
