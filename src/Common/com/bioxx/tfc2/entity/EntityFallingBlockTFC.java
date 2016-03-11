package com.bioxx.tfc2.entity;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.blocks.BlockGravity;

public class EntityFallingBlockTFC extends EntityFallingBlock 
{
	public EntityFallingBlockTFC(World worldIn, double x, double y, double z,
			IBlockState fallingBlockState) 
	{
		super(worldIn, x, y, z, fallingBlockState);

	}

	@Override
	public void onUpdate()
	{
		BlockGravity block = (BlockGravity)this.fallTile.getBlock();

		if (block.getMaterial() == Material.air)
		{
			setDead();
		}
		else
		{
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;

			if (this.fallTime++ == 0)
			{
				BlockPos blockpos = new BlockPos(this);

				if (this.worldObj.getBlockState(blockpos).getBlock() == block)
				{
					this.worldObj.setBlockToAir(blockpos);
				}
				/*else if (!this.worldObj.isRemote)
				{
					setDead();
					return;
				}*/
			}

			this.motionY -= 0.03999999910593033D;
			moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.9800000190734863D;
			this.motionY *= 0.9800000190734863D;
			this.motionZ *= 0.9800000190734863D;

			if (!this.worldObj.isRemote)
			{
				BlockPos blockpos1 = new BlockPos(this);

				if (this.onGround)
				{
					if (this.worldObj.getBlockState(blockpos1).getBlock() != Blocks.piston_extension)
					{
						this.motionX *= 0.699999988079071D;
						this.motionZ *= 0.699999988079071D;
						this.motionY *= -0.5D;
						setDead();

						if (!this.canSetAsBlock)
						{
							if ((this.worldObj.canBlockBePlaced(block, blockpos1, true, net.minecraft.util.EnumFacing.UP, (Entity)null, (ItemStack)null)) && (!block.canFallInto(this.worldObj, blockpos1.down())) && (this.worldObj.setBlockState(blockpos1, this.fallTile, 3)))
							{
								block.onEndFalling(this.worldObj, blockpos1);

								if ((this.tileEntityData != null) && ((block instanceof net.minecraft.block.ITileEntityProvider)))
								{
									TileEntity tileentity = this.worldObj.getTileEntity(blockpos1);

									if (tileentity != null)
									{
										NBTTagCompound nbttagcompound = new NBTTagCompound();
										tileentity.writeToNBT(nbttagcompound);

										for (String s : this.tileEntityData.getKeySet())
										{
											NBTBase nbtbase = this.tileEntityData.getTag(s);

											if ((!s.equals("x")) && (!s.equals("y")) && (!s.equals("z")))
											{
												nbttagcompound.setTag(s, nbtbase.copy());
											}
										}

										tileentity.readFromNBT(nbttagcompound);
										tileentity.markDirty();
									}
								}
							}
							else if ((this.shouldDropItem) && (this.worldObj.getGameRules().getBoolean("doEntityDrops")))
							{
								entityDropItem(new ItemStack(block, 1, block.damageDropped(this.fallTile)), 0.0F);
							}
						}
					}
				}
				else if (((this.fallTime > 100) && (!this.worldObj.isRemote) && ((blockpos1.getY() < 1) || (blockpos1.getY() > 256))) || (this.fallTime > 600))
				{
					if ((this.shouldDropItem) && (this.worldObj.getGameRules().getBoolean("doEntityDrops")))
					{
						entityDropItem(new ItemStack(block, 1, block.damageDropped(this.fallTile)), 0.0F);
					}

					setDead();
				}
			}
		}
	}
}
