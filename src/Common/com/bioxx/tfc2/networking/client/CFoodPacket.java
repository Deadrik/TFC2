package com.bioxx.tfc2.networking.client;

import io.netty.buffer.ByteBuf;

import net.minecraft.util.IThreadListener;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.api.types.EnumFoodGroup;

public class CFoodPacket implements IMessage
{
	public float Grain;
	public float nutritionFruit;
	public float nutritionVeg;
	public float nutritionGrain;
	public float nutritionProtein;
	public float nutritionDairy;
	public float waterLevel;

	public CFoodPacket()
	{

	}

	public CFoodPacket(IFoodStatsTFC stats)
	{
		nutritionFruit = stats.getNutritionMap().get(EnumFoodGroup.Fruit);
		nutritionVeg = stats.getNutritionMap().get(EnumFoodGroup.Vegetable);
		nutritionGrain = stats.getNutritionMap().get(EnumFoodGroup.Grain);
		nutritionProtein = stats.getNutritionMap().get(EnumFoodGroup.Protein);
		nutritionDairy = stats.getNutritionMap().get(EnumFoodGroup.Dairy);
		waterLevel = stats.getWaterLevel();
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		buffer.writeFloat(nutritionFruit);
		buffer.writeFloat(nutritionVeg);
		buffer.writeFloat(nutritionGrain);
		buffer.writeFloat(nutritionProtein);
		buffer.writeFloat(nutritionDairy);
		buffer.writeFloat(waterLevel);
	}

	@Override
	public void fromBytes(ByteBuf buffer)
	{
		this.nutritionFruit = buffer.readFloat();
		this.nutritionVeg = buffer.readFloat();
		this.nutritionGrain = buffer.readFloat();
		this.nutritionProtein = buffer.readFloat();
		this.nutritionDairy = buffer.readFloat();
		this.waterLevel = buffer.readFloat();
	}

	public static class Handler implements IMessageHandler<CFoodPacket, IMessage> 
	{
		@Override
		public IMessage onMessage(final CFoodPacket message, MessageContext ctx) {
			IThreadListener mainThread = net.minecraft.client.Minecraft.getMinecraft(); //(WorldServer) ctx.getServerHandler().playerentityIn.world; // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(new Runnable() 
			{
				@Override
				public void run() 
				{
					IFoodStatsTFC stats = (IFoodStatsTFC)net.minecraft.client.Minecraft.getMinecraft().player.getFoodStats();
					stats.getNutritionMap().put(EnumFoodGroup.Fruit, message.nutritionFruit);
					stats.getNutritionMap().put(EnumFoodGroup.Vegetable, message.nutritionVeg);
					stats.getNutritionMap().put(EnumFoodGroup.Grain, message.nutritionGrain);
					stats.getNutritionMap().put(EnumFoodGroup.Protein, message.nutritionProtein);
					stats.getNutritionMap().put(EnumFoodGroup.Dairy, message.nutritionDairy);
					stats.setWaterLevel(message.waterLevel);
				}
			});
			return null; // no response in this case
		}
	}

}
