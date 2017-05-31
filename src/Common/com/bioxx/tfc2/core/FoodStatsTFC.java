package com.bioxx.tfc2.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.EnumFoodGroup;

public class FoodStatsTFC
{
	/** The player's food level. This measures how much food the player can handle.*/
	public float stomachLevel = 100;
	private float stomachMax = 100.0f;
	private float prevFoodLevel = 100;

	public float nutrFruit = 1.0f;
	public float nutrVeg = 1.0f;
	public float nutrGrain = 1.0f;
	public float nutrDairy = 0.0f;
	public float nutrProtein = 1.0f;
	private boolean sendUpdate = true;

	public long soberTime;

	/**This is how full the player is from the food that they've eaten. 
	 * It could also be how happy they are with what they've eaten*/
	private float satisfaction;

	private float foodExhaustionLevel;
	//private float waterExhaustionLevel;

	/** The player's food timer value. */
	public long foodTimer;
	public long foodHealTimer;

	public float waterLevel = 100f;//TFC_Time.DAY_LENGTH*2;
	public long waterTimer;

	public EntityPlayer player;
	private long nameSeed = Long.MIN_VALUE;
	private boolean satFruit;
	private boolean satVeg;
	private boolean satGrain;
	private boolean satProtein;
	private boolean satDairy;

	public FoodStatsTFC(EntityPlayer player)
	{
		this.player = player;
		//waterTimer = Math.max(TFC_Time.getTotalTicks(),TFC_Time.startTime);
		//foodTimer = Math.max(TFC_Time.getTotalTicks(),TFC_Time.startTime);
		//foodHealTimer = Math.max(TFC_Time.getTotalTicks(),TFC_Time.startTime);
	}	

	/**
	 * Handles the food game logic.
	 */
	public void onUpdate(EntityPlayer player)
	{
		if(!player.world.isRemote)
		{
			Timekeeper time = Timekeeper.instance;
			/*
			 * Standard filling reduction based upon time.
			 */
			if (time.getTotalTicks() - this.foodTimer >= Timekeeper.HOUR_LENGTH && !player.capabilities.isCreativeMode)
			{
				//Increase our timer
				this.foodTimer += Timekeeper.HOUR_LENGTH;

				float drainMult = 1.0f;

				//We dont want the player to starve to death while sleeping
				if(player.isPlayerSleeping())
				{
					drainMult = 0.50f;
				}

				//Water
				if(player.isSprinting())
					waterLevel -= 5;
				if(!player.capabilities.isCreativeMode)
					waterLevel -= 1;

				//Food
				float hunger = (1 + foodExhaustionLevel) * drainMult;
				if(this.satisfaction >= hunger)
				{
					satisfaction -= hunger; 
					hunger = 0;
					foodExhaustionLevel = 0;
				}
				else
				{
					hunger -= satisfaction; 
					satisfaction = 0;
					foodExhaustionLevel = 0;
				}
				this.stomachLevel = Math.max(this.stomachLevel - hunger, 0);

				if(satisfaction == 0)
				{
					satProtein = false; satFruit = false; satVeg = false; satDairy = false; satGrain = false;
				}

				/*
				 * Reduce nutrients
				 */
				if (this.stomachLevel <= 0)
				{
					reduceNutrition(0.0024F);//3x penalty for starving
				}
				else if(this.satisfaction <= 0)
				{
					reduceNutrition(0.0008F);
				}
				else
				{
					if(this.satProtein)
						this.addNutrition(EnumFoodGroup.Protein, this.satisfaction*((1-this.nutrProtein)/100), false);
					if(this.satGrain)
						this.addNutrition(EnumFoodGroup.Grain, this.satisfaction*((1-this.nutrGrain)/100), false);
					if(this.satVeg)
						this.addNutrition(EnumFoodGroup.Vegetable, this.satisfaction*((1-this.nutrVeg)/100), false);
					if(this.satFruit)
						this.addNutrition(EnumFoodGroup.Fruit, this.satisfaction*((1-this.nutrFruit)/100), false);
					if(this.satDairy)
						this.addNutrition(EnumFoodGroup.Dairy, this.satisfaction*((1-this.nutrDairy)/100), false);
				}
				sendUpdate = true;
			}

			if(!player.capabilities.isCreativeMode)
			{
				for(;waterTimer < time.getTotalTicks();  waterTimer++)
				{
					//Reduce the player's water for normal living
					waterLevel -= 1;
					if(waterLevel < 0)
						waterLevel = 0;
					/*if(!Core.isPlayerInDebugMode(player) && waterLevel == 0 && temp > 35)
						player.attackEntityFrom(new DamageSource("heatStroke").setDamageBypassesArmor().setDamageIsAbsolute(), 2);*/
				}
			}

			//Heal or hurt the player based on hunger.
			if (time.getTotalTicks() - this.foodHealTimer >= Timekeeper.HOUR_LENGTH/2)
			{
				this.foodHealTimer += Timekeeper.HOUR_LENGTH/2;

				if (this.stomachLevel >= this.getMaxStomach(player)/4 && player.shouldHeal())
				{
					//Player heals 1% per 30 in game minutes
					player.heal((int) (player.getMaxHealth() * 0.01f));
				}
				else if (this.stomachLevel <= 0 && getNutritionHealthModifier() < 0.85f && !Core.isPlayerInDebugMode(player) && player.getSleepTimer() == 0)
				{
					//Players loses health at a rate of 5% per 30 minutes if they are starving
					//Disabled so that the penalty for not eating is now entirely based upon nutrition.
					//player.attackEntityFrom(DamageSource.starve, Math.max((int) (player.getMaxHealth() * 0.05f), 10));
				}
			}
		}
	}

	protected void reduceNutrition(float amount) 
	{
		nutrFruit = Math.max(this.nutrFruit - (amount + foodExhaustionLevel), 0);
		nutrVeg = Math.max(this.nutrVeg - (amount + foodExhaustionLevel), 0);
		nutrGrain = Math.max(this.nutrGrain - (amount + foodExhaustionLevel), 0);
		nutrProtein = Math.max(this.nutrProtein - (amount + foodExhaustionLevel), 0);
		nutrDairy = Math.max(this.nutrDairy - (amount + foodExhaustionLevel), 0);

		sendUpdate = true;
	}

	public int getMaxWater(EntityPlayer player)
	{
		return 100;//return TFC_Time.DAY_LENGTH * 2 + 200 * player.experienceLevel;
	}

	public float getMaxStomach(EntityPlayer player)
	{
		return this.stomachMax;
	}

	/**
	 * Get the player's food level.
	 */
	public float getFoodLevel()
	{
		return this.stomachLevel;
	}

	@SideOnly(Side.CLIENT)
	public float getPrevFoodLevel()
	{
		return this.prevFoodLevel ;
	}

	/**
	 * If foodLevel is not max.
	 */
	public boolean needFood()
	{
		return this.stomachLevel < getMaxStomach(this.player) && (getMaxStomach(this.player) - stomachLevel) > 0.1;
	}

	public boolean needDrink()
	{
		return this.waterLevel < getMaxWater(this.player) - 500;
	}

	/**
	 * Reads food stats from an NBT object.
	 */
	public void readNBT(NBTTagCompound par1NBTTagCompound)
	{
		if (par1NBTTagCompound.hasKey("foodCompound"))
		{
			NBTTagCompound foodCompound = par1NBTTagCompound.getCompoundTag("foodCompound");
			this.waterLevel = foodCompound.getFloat("waterLevel");
			this.stomachLevel = foodCompound.getFloat("foodLevel");
			this.foodTimer = foodCompound.getLong("foodTickTimer");
			this.foodHealTimer = foodCompound.getLong("foodHealTimer");
			this.waterTimer = foodCompound.getLong("waterTimer");
			this.soberTime = foodCompound.getLong("soberTime");
			this.satisfaction = foodCompound.getFloat("foodSaturationLevel");
			this.foodExhaustionLevel = foodCompound.getFloat("foodExhaustionLevel");
			this.nutrFruit = foodCompound.getFloat("nutrFruit");
			this.nutrVeg = foodCompound.getFloat("nutrVeg");
			this.nutrGrain = foodCompound.getFloat("nutrGrain");
			this.nutrProtein = foodCompound.getFloat("nutrProtein");
			this.nutrDairy = foodCompound.getFloat("nutrDairy");
			this.sendUpdate = foodCompound.getBoolean("shouldSendUpdate");
			this.satFruit = foodCompound.getBoolean("satFruit");
			this.satVeg = foodCompound.getBoolean("satVeg");
			this.satGrain = foodCompound.getBoolean("satGrain");
			this.satProtein = foodCompound.getBoolean("satProtein");
			this.satDairy = foodCompound.getBoolean("satDairy");
		}
	}

	/**
	 * Writes food stats to an NBT object.
	 */
	public void writeNBT(NBTTagCompound nbt)
	{
		NBTTagCompound foodNBT = new NBTTagCompound();
		foodNBT.setFloat("waterLevel", this.waterLevel);
		foodNBT.setFloat("foodLevel", this.stomachLevel);
		foodNBT.setLong("foodTickTimer", this.foodTimer);
		foodNBT.setLong("foodHealTimer", this.foodHealTimer);
		foodNBT.setLong("waterTimer", this.waterTimer);
		foodNBT.setLong("soberTime", this.soberTime);
		foodNBT.setFloat("foodSaturationLevel", this.satisfaction);
		foodNBT.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
		foodNBT.setFloat("nutrFruit", nutrFruit);
		foodNBT.setFloat("nutrVeg", nutrVeg);
		foodNBT.setFloat("nutrGrain", nutrGrain);
		foodNBT.setFloat("nutrProtein", nutrProtein);
		foodNBT.setFloat("nutrDairy", nutrDairy);
		foodNBT.setBoolean("shouldSendUpdate", sendUpdate);
		foodNBT.setBoolean("satFruit", satFruit);
		foodNBT.setBoolean("satVeg", satVeg);
		foodNBT.setBoolean("satGrain", satGrain);
		foodNBT.setBoolean("satProtein", satProtein);
		foodNBT.setBoolean("satDairy", satDairy);
		nbt.setTag("foodCompound", foodNBT);
	}

	public void addFoodExhaustion(float par1)
	{
		this.foodExhaustionLevel = par1;
	}

	/*public void addWaterExhaustion(float par1)
	{
		this.waterExhaustionLevel = par1;
	}*/

	public float getSatisfaction()
	{
		return this.satisfaction;
	}

	public void setFoodLevel(float par1)
	{
		if(par1 != this.stomachLevel)
			sendUpdate = true;
		this.stomachLevel = par1;

	}

	public void setSatisfaction(float par1, int[] fg)
	{
		this.satisfaction = Math.min(par1, 10);
	}

	public long getPlayerFoodSeed()
	{
		/*if(nameSeed == Long.MIN_VALUE)
		{
			long seed = 0;
			byte[] nameBytes = player.getCommandSenderName().getBytes();
			for(byte b : nameBytes)
				seed+=b;
			nameSeed = seed + player.world.getSeed();
		}
		return nameSeed;*/
		return 0;
	}

	public float getNutritionHealthModifier()
	{
		float nMod = 0.00f;
		nMod += 0.1f * nutrFruit;
		nMod += 0.4f * nutrVeg;
		nMod += 0.3f * nutrGrain;
		nMod += 0.2f * nutrProtein;
		return Math.max(nMod, 0.05f);
	}

	public static float getMaxHealth(EntityPlayer player)
	{
		/*return Math.min(20+(player.experienceLevel * TFCOptions.healthGainRate),
				TFCOptions.healthGainCap) * Core.getPlayerFoodStats(player).getNutritionHealthModifier() * (1+0.2f * Core.getPlayerFoodStats(player).nutrDairy);
		 */
		return 0;
	}

	/**
	 * 
	 * @return return true if the itemstack should be consumed, else return false
	 */
	public static boolean reduceFood(ItemStack is, float amount)
	{
		/*if(is.hasTagCompound())
		{
			float weight = is.getTagCompound().getFloat("foodWeight");
			float decay = is.getTagCompound().hasKey("foodDecay") ? is.getTagCompound().getFloat("foodDecay") : 0;
			if(decay >= 0 && (weight - decay) - amount <= 0)
				return true;
			else if(decay <= 0 && weight - amount <= 0)
				return true;
			else
			{
				is.getTagCompound().setFloat("foodWeight", Helper.roundNumber(weight - amount, 10));
				//is.getTagCompound().setFloat("foodDecay", Helper.roundNumber(decay - amount, 10));
			}
		}*/
		return false;
	}

	public void addNutrition(EnumFoodGroup fg, float foodAmt)
	{
		addNutrition(fg, foodAmt, true);
	}

	public void addNutrition(EnumFoodGroup fg, float foodAmt, boolean shouldDoMath)
	{
		float amount = foodAmt;
		if(shouldDoMath)
			amount = foodAmt/5f/50f;//converts it to 5% if it is 5oz of food
		switch(fg)
		{
		case Dairy:
			this.nutrDairy = Math.min(nutrDairy + amount, 1.0f);
			break;
		case Fruit:
			this.nutrFruit = Math.min(nutrFruit + amount, 1.0f);
			break;
		case Grain:
			this.nutrGrain = Math.min(nutrGrain + amount, 1.0f);
			break;
		case Protein:
			this.nutrProtein = Math.min(nutrProtein + amount, 1.0f);
			break;
		case Vegetable:
			this.nutrVeg = Math.min(nutrVeg + amount, 1.0f);
			break;
		default:
			break;
		}
	}

	public boolean shouldSendUpdate()
	{
		return sendUpdate;
	}

	public void restoreWater(EntityPlayer player, int w)
	{
		this.waterLevel = Math.min(this.waterLevel + w, this.getMaxWater(player));
		sendUpdate = true;
		this.writeNBT(player.getEntityData());
	}

	public void resetTimers()
	{
		//waterTimer = TFC_Time.getTotalTicks();
		//foodTimer = TFC_Time.getTotalTicks();
		//foodHealTimer = TFC_Time.getTotalTicks();
	}

	public void consumeAlcohol()
	{
		//TODO: Add a parameter for alcohol strength
		/*if(soberTime <= TFC_Time.getTotalTicks())
			soberTime = TFC_Time.getTotalTicks() + player.world.rand.nextInt(1000) + 400;
		else
			soberTime += player.world.rand.nextInt(1000) + 400;
		sendUpdate = true;*/
	}

}
