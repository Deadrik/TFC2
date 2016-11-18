package com.bioxx.tfc2.handlers;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.GameType;

import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnEntry;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.api.interfaces.IUpdateInInventory;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.world.WorldGen;

public class EntityLivingHandler
{
	@SubscribeEvent
	public void onEntityLivingUpdate(LivingUpdateEvent event)
	{
		if (event.getEntityLiving() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();

			//If the player enters the portal realm then set them to adventure mode to prevent altering the world
			if(player.worldObj.provider.getDimension() == 2 && !player.capabilities.isCreativeMode && !player.isSpectator())
				player.setGameType(GameType.ADVENTURE);
			else if(player.worldObj.provider.getDimension() == 0 && !player.capabilities.isCreativeMode && !player.isSpectator())
			{
				IslandMap map = WorldGen.getInstance().getIslandMap((int)player.posX >> 12, (int)player.posZ >> 12);
				if(map.getIslandData().isIslandUnlocked && !player.isSpectator())
					player.setGameType(GameType.SURVIVAL);
				else if(!player.isSpectator())
					player.setGameType(GameType.ADVENTURE);
			}


			//Set Max Health
			float newMaxHealth = getMaxHealth(player);
			float oldMaxHealth = (float)player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
			if(oldMaxHealth != newMaxHealth)
			{
				player.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newMaxHealth);
			}

			if(!player.worldObj.isRemote)
			{
				//Tick Item Updates
				for(int i = 0; i < player.inventory.mainInventory.length; i++)
				{
					ItemStack is = player.inventory.mainInventory[i];
					if(is != null)
					{
						if(is.getItem() instanceof IUpdateInInventory)
						{
							((IUpdateInInventory)is.getItem()).inventoryUpdate(player, is);
							if(is.stackSize == 0)
								player.inventory.mainInventory[i] = null;
						}
						if(is.getItem() instanceof IFood)
						{
							IFood food = (IFood)is.getItem();
							long time = Food.getDecayTimer(is)-player.worldObj.getWorldTime();
							if(time < 0)
							{
								int expiredAmt = (int)Math.min(1+(time / Food.getExpirationTimer(is))* (-1), is.stackSize);
								is.stackSize-=expiredAmt;
								Food.setDecayTimer(is, Food.getDecayTimer(is)+Food.getExpirationTimer(is)*expiredAmt);
								if(is.stackSize <= 0)
									player.inventory.mainInventory[i] = null;

								ItemStack out = food.onDecayed(is, player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
								if(out != null)
								{
									out.stackSize = expiredAmt;
									player.inventory.addItemStackToInventory(out);
								}
							}
						}
					}
				}

			}
			else
			{

			}
		}
	}

	public static float getMaxHealth(EntityPlayer player)
	{
		//Expand on this later
		return 20;
		/*return Math.min(20+(player.experienceLevel * TFCOptions.healthGainRate),
				TFCOptions.healthGainCap) * Core.getPlayerFoodStats(player).getNutritionHealthModifier() *
				(1+0.2f * Core.getPlayerFoodStats(player).nutrDairy);*/
	}

	public void setThirsty(EntityPlayer player, boolean b)
	{
		//Removed on port
		/*IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

		if (iattributeinstance.getModifier(TFCAttributes.THIRSTY_UUID) != null)
		{
			iattributeinstance.removeModifier(TFCAttributes.THIRSTY);
		}

		if (b)
		{
			iattributeinstance.applyModifier(TFCAttributes.THIRSTY);
		}*/
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void handleFOV(FOVUpdateEvent event)
	{
		EntityPlayer player = event.getEntity();

		// Calculate FOV based on the variable draw speed of the bow depending on player armor.
		//Removed on port
		/*if (player.isUsingItem() && player.getItemInUse().getItem() instanceof ItemCustomBow)
		{
			float fov = 1.0F;
			int duration = player.getItemInUseDuration();
			float speed = ItemCustomBow.getUseSpeed(player);
			float force = duration / speed;

			if (force > 1.0F)
			{
				force = 1.0F;
			}
			else
			{
				force *= force;
			}

			fov *= 1.0F - force * 0.15F;
			event.newfov = fov;
		}*/
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if(event.getEntity().worldObj.isRemote)
			return;

		if (entity instanceof EntityPlayer)
		{
			//Removed on port
			/*EntityPlayer player = (EntityPlayer) entity;
			SkillStats skills = TFC_Core.getSkillStats(player);
			PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player);
			pi.tempSkills = skills;

			// Save the item in the back slot if keepInventory is set to true.
			if (entity.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory") && player.inventory instanceof InventoryPlayerTFC)
			{
				pi.tempEquipment = ((InventoryPlayerTFC) player.inventory).extraEquipInventory.clone();
			}*/
		}

		if(entity.getEntityData().hasKey("TFC2"))
		{
			NBTTagCompound nbt = entity.getEntityData().getCompoundTag("TFC2");
			if(nbt.getBoolean("isWild"))
			{
				IslandMap map = Core.getMapForWorld(event.getEntity().getEntityWorld(), event.getEntity().getPosition());
				SpawnEntry entry = map.getIslandData().animalEntries.get(nbt.getString("SpawnGroup"));
				entry.removeAnimal();
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrop(LivingDropsEvent event)
	{
		boolean processed = false;
		if (!event.getEntity().worldObj.isRemote && event.isRecentlyHit() && !(event.getEntity() instanceof EntityPlayer) && !(event.getEntity() instanceof EntityZombie))
		{
			if(event.getSource().getSourceOfDamage() instanceof EntityPlayer || event.getSource().isProjectile())
			{
				boolean foundFood = false;
				processed = true;
				ArrayList<EntityItem> drop = new ArrayList<EntityItem>();
				EntityPlayer p = null;
				if(event.getSource().getSourceOfDamage() instanceof EntityPlayer)
					p = (EntityPlayer)event.getSource().getSourceOfDamage();
				//Removed on port
				/*else if(event.source.getSourceOfDamage() instanceof EntityProjectileTFC)
				{
					EntityProjectileTFC proj = (EntityProjectileTFC)event.source.getSourceOfDamage();
					if(proj.shootingEntity instanceof EntityPlayer)
						p = (EntityPlayer)proj.shootingEntity;
				}*/
				//Removed on port
				/*for(EntityItem ei : event.drops)
				{
					ItemStack is = ei.getEntityItem();
					if (is.getItem() instanceof IFood)
					{
						if(p == null)
							continue;
						foundFood = true;

						int sweetMod = ((ItemFoodTFC) is.getItem()).getTasteSweetMod(is);
						int sourMod = ((ItemFoodTFC) is.getItem()).getTasteSourMod(is);
						int saltyMod = ((ItemFoodTFC) is.getItem()).getTasteSaltyMod(is);
						int bitterMod = ((ItemFoodTFC) is.getItem()).getTasteBitterMod(is);
						int umamiMod = ((ItemFoodTFC) is.getItem()).getTasteSavoryMod(is);

						float oldWeight = Food.getWeight(is);
						Food.setWeight(is, 0);
						float newWeight = oldWeight * (TFC_Core.getSkillStats(p).getSkillMultiplier(Global.SKILL_BUTCHERING)+0.01f);
						while (newWeight >= Global.FOOD_MIN_DROP_WEIGHT)
						{
							float fw = Helper.roundNumber(Math.min(Global.FOOD_MAX_WEIGHT, newWeight), 10);
							if (fw < Global.FOOD_MAX_WEIGHT)
								newWeight = 0;
							newWeight -= fw;

							ItemStack result = ItemFoodTFC.createTag(new ItemStack(is.getItem(), 1), fw);

							if(sweetMod != 0) result.getTagCompound().setInteger("tasteSweetMod", sweetMod);
							if(sourMod != 0) result.getTagCompound().setInteger("tasteSourMod", sourMod);
							if(saltyMod != 0) result.getTagCompound().setInteger("tasteSaltyMod", saltyMod);
							if(bitterMod != 0) result.getTagCompound().setInteger("tasteBitterMod", bitterMod);
							if(umamiMod != 0) result.getTagCompound().setInteger("tasteUmamiMod", umamiMod);	

							drop.add(new EntityItem(event.getEntity().worldObj, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, result));
						}
					}
					else
					{
						drop.add(ei);
					}
				}
				event.drops.clear();*/
				event.getDrops().addAll(drop);
				if(foundFood && p != null)
				{
					//Removed on port
					//TFC_Core.getSkillStats(p).increaseSkill(Global.SKILL_BUTCHERING, 1);
				}
			}
		}
		//Removed on port
		/*if (!processed && !(event.getEntity() instanceof EntityPlayer) && !(event.getEntity() instanceof EntityZombie))
		{
			ArrayList<EntityItem> drop = new ArrayList<EntityItem>();
			for(EntityItem ei : event.drops)
			{
				if (!(ei.getEntityItem().getItem() instanceof IFood))
				{
					drop.add(ei);
				}
			}
			event.drops.clear();
			event.drops.addAll(drop);
		}*/
	}
}
