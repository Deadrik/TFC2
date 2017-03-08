package com.bioxx.tfc2.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class EntityCart extends Entity 
{
	public EntityLivingBase pullEntity;
	public InventoryCart cartInv;
	double pullPrevPosX, pullPrevPosY, pullPrevPosZ;
	public boolean isMoving = false;

	protected static final DataParameter<Boolean> IS_BEING_PULLED = EntityDataManager.createKey(EntityCart.class, DataSerializers.BOOLEAN);
	protected static final DataParameter<Float> ROTATION = EntityDataManager.createKey(EntityCart.class, DataSerializers.FLOAT);

	public EntityCart(World worldIn) 
	{
		super(worldIn);
		this.setSize(1.0F, 1.0F);
		cartInv = new InventoryCart("Cart", false, 27);
	}
	@Override
	protected void entityInit() {
		getDataManager().set(IS_BEING_PULLED, false);
		getDataManager().set(ROTATION, this.rotationYaw);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound tagCompund) {
		this.setFacingAngle(this.rotationYaw);
		NBTTagList nbttaglist = tagCompund.getTagList("Items", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbt1 = nbttaglist.getCompoundTagAt(i);
			int j = nbt1.getByte("Slot") & 255;

			if (j < cartInv.getSizeInventory())
			{
				cartInv.setInventorySlotContents(j, new ItemStack(nbt1));
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tagCompound) 
	{
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.cartInv.getSizeInventory(); ++i)
		{
			ItemStack itemstack = this.cartInv.getStackInSlot(i);

			if (itemstack != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				itemstack.writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		tagCompound.setTag("Items", nbttaglist);

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
	public AxisAlignedBB getEntityBoundingBox()
	{
		AxisAlignedBB aabb = super.getEntityBoundingBox();
		if(isBeingPulled())
			aabb = aabb.expand(-0.15, -0.55, -0.15);
		if(this.isBeingRidden())
			aabb = aabb.expand(-0.15, -0.55, -0.15);

		return aabb;
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
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
	{
		if (this.isBeingRidden() && getPassengers().get(0) instanceof EntityPlayer && getPassengers().get(0) != player)
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
			else if(this.getPassengers().size() < 1 && (angle > revAngle - 25 && angle < revAngle + 25))
			{
				player.startRiding(this);
				this.noClip = true;
			}
			else
			{
				PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player).entityForInventory = this;
				if(!world.isRemote)
				{
					player.openGui(TFC.instance, 1, world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
				}
			}

			return true;
		}
	}

	@Override
	public double getMountedYOffset()
	{
		return (double)this.height * 0.5D - 0.1D;
	}

	/*@Override
	public void updateRiderPosition()
	{
		if (isBeingRidden())
		{
			double posX = this.posX+Math.cos(Math.toRadians(getFacingAngle()))*0;
			double posZ = this.posZ+Math.sin(Math.toRadians(getFacingAngle()))*0;

			this.riddenByEntity.setPosition(posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(),  posZ);
		}
	}*/

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
			getDataManager().set(IS_BEING_PULLED, true);
		}
		else
		{
			this.rotationPitch = 0;
			getDataManager().set(IS_BEING_PULLED, false);
		}
		pullEntity = entity;
	}

	public boolean isBeingPulled()
	{
		return getDataManager().get(IS_BEING_PULLED);
	}

	public float getFacingAngle()
	{
		return getDataManager().get(ROTATION);
	}

	public float setFacingAngle(float f)
	{
		getDataManager().set(ROTATION, f);
		return f;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		if (this.isBeingRidden())
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

			this.move(MoverType.SELF,this.motionX, this.motionY, this.motionZ);
		}
		else
		{
			double dist = Math.sqrt(Math.pow(pullPrevPosX - pullEntity.posX, 2)+Math.pow(pullPrevPosY - pullEntity.posY, 2)+Math.pow(pullPrevPosZ - pullEntity.posZ, 2));
			if(dist > 0.05)
			{
				double angle = 0;
				if(!world.isRemote)
					angle = setFacingAngle((pullEntity.rotationYaw-90) % 360);
				angle = getFacingAngle();
				double posX = pullEntity.posX+Math.cos(Math.toRadians(angle))*1.2;
				double posY = pullEntity.posY;
				double posZ = pullEntity.posZ+Math.sin(Math.toRadians(angle))*1.2;

				if(!world.isRemote)
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


		if (!this.world.isRemote)
		{
			List list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));

			if (list != null && !list.isEmpty())
			{
				for (int k1 = 0; k1 < list.size(); ++k1)
				{
					Entity entity = (Entity)list.get(k1);

					if (!getPassengers().contains(entity) && entity.canBePushed() && entity instanceof EntityCart)
					{
						entity.applyEntityCollision(this);
					}
				}
			}

			if (this.isBeingRidden() && this.getPassengers().get(0).isDead)
			{
				getPassengers().remove(0);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean isTeleport)
	{
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

}
