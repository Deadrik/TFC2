package com.bioxx.tfc2.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.IGravityBlock;

public class EntityFallingBlockTFC extends EntityFallingBlock 
{
	BlockPos origPos;
	public EntityFallingBlockTFC(World worldIn, double x, double y, double z, IBlockState fallingBlockState) 
	{
		super(worldIn, x, y, z, fallingBlockState);
		origPos = new BlockPos(x, y, z);
	}

	@Override
	public void onUpdate()
	{
		Block block = this.fallTile.getBlock();
		IGravityBlock grav = null;
		if(block instanceof IGravityBlock)
			grav = (IGravityBlock)this.fallTile.getBlock();

		if (block.getMaterial(fallTile) == Material.AIR)
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
					if (this.worldObj.getBlockState(blockpos1).getBlock() != Blocks.PISTON_EXTENSION)
					{
						this.motionX *= 0.699999988079071D;
						this.motionZ *= 0.699999988079071D;
						this.motionY *= -0.5D;
						setDead();

						if (!this.canSetAsBlock)
						{
							boolean placed = false;
							BlockPos movePos;
							for(int i = 0; i < 6; i++)
							{
								movePos = blockpos1.add(0,i,0);
								if(movePos.getY() > origPos.getY())
									break;
								if(!placed && this.worldObj.canBlockBePlaced(block, blockpos1.add(0, i, 0), true, net.minecraft.util.EnumFacing.UP, (Entity)null, (ItemStack)null))
								{
									if((grav != null && !grav.canFallInto(this.worldObj, blockpos1.add(0, i, 0).down())) || (grav == null && !canFallInto(this.worldObj, blockpos1.add(0, i, 0).down())))
										placed = this.worldObj.setBlockState(blockpos1.add(0, i, 0), this.fallTile, 2);
								}
							}
							if(placed)
							{
								if(grav != null)
									grav.onEndFalling(this.worldObj, blockpos1);

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

	public boolean canFallInto(World worldIn, BlockPos pos)
	{
		if (worldIn.isAirBlock(pos)) return true;
		IBlockState state = worldIn.getBlockState(pos);
		Material material = state.getBlock().getMaterial(state);
		return (state.getBlock() == Blocks.FIRE) || (material == Material.AIR) || (material == Material.WATER) || (material == Material.LAVA) || state.getBlock().isReplaceable(worldIn, pos);
	}
}
