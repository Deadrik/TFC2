package com.bioxx.tfc2;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.util.Helper;

public class EntityCart extends Entity 
{
	EntityLivingBase pullEntity;
	double pullPrevPosX, pullPrevPosY, pullPrevPosZ;
	public boolean isMoving = false;

	public EntityCart(World worldIn) 
	{
		super(worldIn);
		this.setSize(1.0F, 1.0F);
	}
	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(20, new Integer(0));
		this.dataWatcher.addObject(21, new Float(this.rotationYaw));
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		this.setFacingAngle(this.rotationYaw);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound) {


	}

	/**
	 * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
	 * pushable on contact, like boats or minecarts.
	 */
	@Override
	public AxisAlignedBB getCollisionBox(Entity entityIn)
	{
		return entityIn.getEntityBoundingBox();
	}

	/**
	 * returns the bounding box for this entity
	 */
	@Override
	public AxisAlignedBB getBoundingBox()
	{
		AxisAlignedBB aabb = super.getEntityBoundingBox();
		if(isBeingPulled())
			aabb = aabb.contract(0.15, 0.55, 0.15);
		if(riddenByEntity != null)
			aabb = aabb.contract(0.15, 0.55, 0.15);

		return aabb;
	}

	@Override
	public AxisAlignedBB getEntityBoundingBox()
	{
		return super.getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed()
	{
		return true;
	}

	//Controls rightclick behavior, not physical collisions
	@Override	
	public boolean canBeCollidedWith()
	{
		return true;
	}

	@Override
	public boolean interactFirst(EntityPlayer player)
	{
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
		{
			return true;
		}
		else
		{
			double angle = Helper.normalizeAngle(getAngleToEntity(player));
			double fAngle = Helper.normalizeAngle(getFacingAngle());
			double revAngle =Helper.normalizeAngle(fAngle + 180);
			System.out.println(angle+"/"+fAngle);
			if(pullEntity == null && (angle > (fAngle - 25) && angle < (fAngle + 25)))
			{
				setPullEntity(player);
			}
			else if(this.riddenByEntity == null && (angle > revAngle - 25 && angle < revAngle + 25))
			{
				player.mountEntity(this);
				this.noClip = true;
			}

			return true;
		}
	}

	@Override
	public double getMountedYOffset()
	{
		return (double)this.height * 0.5D - 0.1D;
	}

	@Override
	public void updateRiderPosition()
	{
		if (this.riddenByEntity != null)
		{
			double posX = this.posX+Math.cos(Math.toRadians(getFacingAngle()))*0;
			double posZ = this.posZ+Math.sin(Math.toRadians(getFacingAngle()))*0;

			this.riddenByEntity.setPosition(posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(),  posZ);
		}
	}

	private double getAngleToEntity(Entity e)
	{
		double pX = this.posX - e.posX;
		double pZ = this.posZ - e.posZ;
		return Math.atan2(pZ, pX) * 180 / Math.PI;
	}

	private void setPullEntity(EntityLivingBase entity)
	{
		if(entity != null)
		{
			pullPrevPosX = entity.posX;
			pullPrevPosY = entity.posY;
			pullPrevPosZ = entity.posZ;
			dataWatcher.updateObject(20, 1);
		}
		else
		{
			this.rotationPitch = 0;
			dataWatcher.updateObject(20, 0);
		}
		pullEntity = entity;
	}

	public boolean isBeingPulled()
	{
		if((dataWatcher.getWatchableObjectInt(20) & 1) == 1)
			return true;
		return false;
	}

	public float getFacingAngle()
	{
		return dataWatcher.getWatchableObjectFloat(21);
	}

	public float setFacingAngle(float f)
	{
		dataWatcher.updateObject(21, f);
		return f;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.riddenByEntity == null)
		{
			noClip = false;
		}
		if(pullEntity == null)
		{
			if (this.onGround)
			{
				this.motionX *= 0.3D;
				this.motionY *= 0.3D;
				this.motionZ *= 0.3D;
			}

			this.motionX *= 0.7900000095367432D;
			this.motionY *= 0.749999988079071D;
			this.motionZ *= 0.7900000095367432D;

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
		}
		else
		{
			double dist = Math.sqrt(Math.pow(pullPrevPosX - pullEntity.posX, 2)+Math.pow(pullPrevPosY - pullEntity.posY, 2)+Math.pow(pullPrevPosZ - pullEntity.posZ, 2));
			if(dist > 0.05)
			{
				double angle = 0;
				if(!worldObj.isRemote)
					angle = setFacingAngle((pullEntity.rotationYaw-90) % 360);
				angle = getFacingAngle();
				double posX = pullEntity.posX+Math.cos(Math.toRadians(angle))*1.2;
				double posY = pullEntity.posY;
				double posZ = pullEntity.posZ+Math.sin(Math.toRadians(angle))*1.2;

				if(!worldObj.isRemote)
					this.setLocationAndAngles(posX, posY, posZ, pullEntity.rotationYaw, 0);
				else
				{
					this.setPositionAndRotation(posX, posY, posZ, pullEntity.rotationYaw, 0);
					isMoving = true;
				}

				pullPrevPosX = pullEntity.posX;
				pullPrevPosY = pullEntity.posY;
				pullPrevPosZ = pullEntity.posZ;

			}
			else
			{
				isMoving = false;
			}

			if(pullEntity != null && this.pullEntity.isSneaking())
			{
				setPullEntity(null);
			}
		}


		if (!this.worldObj.isRemote)
		{
			List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

			if (list != null && !list.isEmpty())
			{
				for (int k1 = 0; k1 < list.size(); ++k1)
				{
					Entity entity = (Entity)list.get(k1);

					if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityCart)
					{
						entity.applyEntityCollision(this);
					}
				}
			}

			if (this.riddenByEntity != null && this.riddenByEntity.isDead)
			{
				this.riddenByEntity = null;
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean isTeleport)
	{
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

}
