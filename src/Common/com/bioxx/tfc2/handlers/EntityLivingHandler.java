package com.bioxx.tfc2.handlers;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.GameType;

import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.heat.ItemHeat;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.api.interfaces.IUpdateInInventory;
import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.Timekeeper;
import com.bioxx.tfc2.networking.client.CFoodPacket;
import com.bioxx.tfc2.potion.PotionTFC;
import com.bioxx.tfc2.world.WeatherManager;
import com.bioxx.tfc2.world.WorldGen;

public class EntityLivingHandler
{
	public static final AttributeModifier THIRST = new AttributeModifier(UUID.fromString("85b8dff6-3add-4aeb-89d0-b71d2fb33945"), "Thirsty", -0.5, 2);
	@SubscribeEvent
	public void onEntityLivingUpdate(LivingUpdateEvent event)
	{
		if (event.getEntityLiving() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();

			//If the player enters the portal realm then set them to adventure mode to prevent altering the world
			if(player.world.provider.getDimension() == 2 && !player.capabilities.isCreativeMode && !player.isSpectator())
				player.setGameType(GameType.ADVENTURE);
			else if(player.world.provider.getDimension() == 0 && !player.capabilities.isCreativeMode && !player.isSpectator())
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

			if(!player.world.isRemote)
			{
				int size = player.inventory.getSizeInventory();
				//Tick Item Updates
				for(int i = 0; i < size; i++)
				{
					ItemStack is = player.inventory.getStackInSlot(i);
					if(is != ItemStack.EMPTY)
					{
						if(is.getItem() instanceof IUpdateInInventory)
						{
							((IUpdateInInventory)is.getItem()).inventoryUpdate(player, is);
							if(is.getMaxStackSize() == 0)
								player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
						}
						if(is.getItem() instanceof IFood)
						{
							IFood food = (IFood)is.getItem();
							long time = Food.getDecayTimer(is)-player.world.getWorldTime();
							if(time < 0)
							{
								int expiredAmt = (int)Math.min(1+(time / Food.getExpirationTimer(is))* (-1), is.getMaxStackSize());
								expiredAmt = Math.max(expiredAmt, 0);
								is.shrink(expiredAmt);
								Food.setDecayTimer(is, Food.getDecayTimer(is)+Food.getExpirationTimer(is)*expiredAmt);
								if(is.getMaxStackSize() <= 0)
									player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);

								ItemStack out = food.onDecayed(is, player.world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
								if(out != null)
								{
									out.setCount(expiredAmt);
									player.inventory.addItemStackToInventory(out);
								}
							}
						}

						if(ItemHeat.Get(is) > 0)
						{
							ItemHeat.Decrease(is, 1);
						}
					}
				}

				updateEncumb(player);

				//Drain Nutrition
				NBTTagCompound tfcData = getEntityData(player.getEntityData());
				if(!tfcData.hasKey("nutritionDrainTimer"))
					tfcData.setLong("nutritionDrainTimer", Timekeeper.getInstance().getTotalTicks());
				long timer = tfcData.getLong("nutritionDrainTimer");
				IFoodStatsTFC food = (IFoodStatsTFC) player.getFoodStats();
				if(Timekeeper.getInstance().getTotalTicks() > timer)
				{
					tfcData.setLong("nutritionDrainTimer", timer += 1000);
					updateNutrition(tfcData, food, player);
					updateThirst(tfcData, food, player);
					updateHunger(tfcData, food, player);

					TFC.network.sendTo(new CFoodPacket(food), player);
				}
				player.getEntityData().setTag("TFC2Data", tfcData);
				if(food.getWaterLevel() < 5)
					setThirsty(player, true);
				else
					setThirsty(player, false);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onEntityLivingUpdateClient(LivingUpdateEvent event)
	{
		if (event.getEntity().getEntityWorld().isRemote && event.getEntityLiving() instanceof EntityPlayerSP)
		{
			EntityPlayerSP player = (EntityPlayerSP)event.getEntityLiving();
			updateEncumb(player);
		}
	}

	public void updateEncumb(EntityPlayer player)
	{
		float encumb = Core.getEncumbrance(player.inventory.mainInventory) / 80f;
		if(encumb >= 1.0)
		{
			if(player.isPotionActive(PotionTFC.ENCUMB_HEAVY_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_HEAVY_POTION);
			if(player.isPotionActive(PotionTFC.ENCUMB_MEDIUM_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_MEDIUM_POTION);

			if(player.isPotionActive(PotionTFC.ENCUMB_MAX_POTION))
				return;

			player.addPotionEffect(new PotionEffect(PotionTFC.ENCUMB_MAX_POTION, Integer.MAX_VALUE, 0, false, false));
		}
		else if(encumb >= 0.75)
		{
			if(player.isPotionActive(PotionTFC.ENCUMB_MAX_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_MAX_POTION);
			if(player.isPotionActive(PotionTFC.ENCUMB_MEDIUM_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_MEDIUM_POTION);

			if(player.isPotionActive(PotionTFC.ENCUMB_HEAVY_POTION))
				return;

			player.addPotionEffect(new PotionEffect(PotionTFC.ENCUMB_HEAVY_POTION, Integer.MAX_VALUE, 0, false, false));
		}
		else if(encumb >= 0.5)
		{
			if(player.isPotionActive(PotionTFC.ENCUMB_MAX_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_MAX_POTION);
			if(player.isPotionActive(PotionTFC.ENCUMB_HEAVY_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_HEAVY_POTION);

			if(player.isPotionActive(PotionTFC.ENCUMB_MEDIUM_POTION))
				return;

			player.addPotionEffect(new PotionEffect(PotionTFC.ENCUMB_MEDIUM_POTION, Integer.MAX_VALUE, 0, false, false));
		}
		else
		{
			if(player.isPotionActive(PotionTFC.ENCUMB_MAX_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_MAX_POTION);
			if(player.isPotionActive(PotionTFC.ENCUMB_HEAVY_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_HEAVY_POTION);
			if(player.isPotionActive(PotionTFC.ENCUMB_MEDIUM_POTION))
				player.removeActivePotionEffect(PotionTFC.ENCUMB_MEDIUM_POTION);
		}
	}

	public void updateNutrition(NBTTagCompound tfcData, IFoodStatsTFC food, EntityPlayer player)
	{
		//Nutrition drains at a rate of 0.03 per hour. this should give roughly 27 days until zero
		food.getNutritionMap().put(EnumFoodGroup.Fruit, food.getNutritionMap().get(EnumFoodGroup.Fruit)-0.03f);
		food.getNutritionMap().put(EnumFoodGroup.Vegetable, food.getNutritionMap().get(EnumFoodGroup.Vegetable)-0.03f);
		food.getNutritionMap().put(EnumFoodGroup.Grain, food.getNutritionMap().get(EnumFoodGroup.Grain)-0.03f);
		food.getNutritionMap().put(EnumFoodGroup.Protein, food.getNutritionMap().get(EnumFoodGroup.Protein)-0.03f);
		food.getNutritionMap().put(EnumFoodGroup.Dairy, food.getNutritionMap().get(EnumFoodGroup.Dairy)-0.03f);
	}

	public void updateThirst(NBTTagCompound tfcData, IFoodStatsTFC food, EntityPlayer player)
	{
		double temp = WeatherManager.getInstance().getTemperature(player.getPosition());
		if(player.isCreative())
			return;
		float thirst = 0.28f;
		if(temp > 20)
		{
			temp -= 20;
			thirst += thirst * (temp / 0.35);
		}
		food.setWaterLevel(Math.max(food.getWaterLevel()-thirst, 0));
	}

	public void updateHunger(NBTTagCompound tfcData, IFoodStatsTFC food, EntityPlayer player)
	{
		//Players suffer less natural hunger exhaustion as they increase in level
		player.getFoodStats().addExhaustion(1.0f - Math.min(player.experienceLevel / 100f, 0.95f));
	}

	public NBTTagCompound getEntityData(NBTTagCompound playerData)
	{
		if(playerData == null)
			return new NBTTagCompound();
		return playerData.getCompoundTag("TFC2Data");	
	}

	public static float getMaxHealth(EntityPlayer player)
	{
		IFoodStatsTFC food = (IFoodStatsTFC) player.getFoodStats();
		float total = food.getNutritionMap().get(EnumFoodGroup.Fruit) + food.getNutritionMap().get(EnumFoodGroup.Vegetable) +
				food.getNutritionMap().get(EnumFoodGroup.Grain) + food.getNutritionMap().get(EnumFoodGroup.Protein) +
				food.getNutritionMap().get(EnumFoodGroup.Dairy);

		total = total / 100;
		return Math.min(20+(player.experienceLevel * TFCOptions.healthGainRate), TFCOptions.healthGainCap) * total;
	}

	public void setThirsty(EntityPlayer player, boolean b)
	{
		if (b)
		{
			player.setSprinting(false);
			if(!player.isPotionActive(PotionTFC.THIRST_POTION))
				player.addPotionEffect(new PotionEffect(PotionTFC.THIRST_POTION, Integer.MAX_VALUE, 0, false, false));
		}
		else
		{
			player.removePotionEffect(PotionTFC.THIRST_POTION);
		}
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

		if(event.getEntity().world.isRemote)
			return;

		if (entity instanceof EntityPlayer)
		{
			//Removed on port
			/*EntityPlayer player = (EntityPlayer) entity;
			SkillStats skills = TFC_Core.getSkillStats(player);
			PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player);
			pi.tempSkills = skills;

			// Save the item in the back slot if keepInventory is set to true.
			if (entityIn.world.getGameRules().getGameRuleBooleanValue("keepInventory") && player.inventory instanceof InventoryPlayerTFC)
			{
				pi.tempEquipment = ((InventoryPlayerTFC) player.inventory).extraEquipInventory.clone();
			}*/
		}

		if(entity.getEntityData().hasKey("TFC2"))
		{
			/*NBTTagCompound nbt = entity.getEntityData().getCompoundTag("TFC2");
			if(nbt.getBoolean("isWild"))
			{
				IslandMap map = Core.getMapForWorld(event.getEntity().getEntityWorld(), event.getEntity().getPosition());
				SpawnEntry entry = map.getIslandData().animalEntries.get(nbt.getString("SpawnGroup"));
				entry.removeAnimal();
			}*/
		}
	}

	@SubscribeEvent
	public void onLivingDrop(LivingDropsEvent event)
	{
		boolean processed = false;
		if (!event.getEntity().world.isRemote && event.isRecentlyHit() && !(event.getEntity() instanceof EntityPlayer) && !(event.getEntity() instanceof EntityZombie))
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

	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent event)
	{
		if(event.getSource().getDamageType() == "player" && event.getSource().getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getSource().getEntity();
			ItemStack is = player.getHeldItemMainhand();
			if(is.isEmpty())
				event.setCanceled(true);
		}
	}
}
