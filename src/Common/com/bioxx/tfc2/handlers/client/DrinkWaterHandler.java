package com.bioxx.tfc2.handlers.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.items.pottery.ItemPotteryJug;
import com.bioxx.tfc2.networking.client.CFoodPacket;

public class DrinkWaterHandler 
{
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickEmpty event)
	{
		if(event.getSide() == Side.CLIENT)
		{
			/*
			 * We perform the Raytrace again, but this time we allow water blocks to be traced. If we get a hit on a water 
			 * block then the client will send the RightClick event to the server and the server will attempt to drink the water
			 */
			RayTraceResult result = rayTrace(event.getEntityPlayer(), 5, 1.0f);
			if(result != null && result.typeOfHit == Type.MISS)
			{
				BlockPos blockpos = result.getBlockPos();
				IBlockState state = event.getWorld().getBlockState(blockpos);
				if(state.getBlock() == Blocks.WATER && Core.isFreshWater(event.getWorld(), result.getBlockPos()))
				{
					Minecraft.getMinecraft().playerController.processRightClickBlock((EntityPlayerSP)event.getEntityPlayer(), 
							(WorldClient)event.getWorld(), blockpos, result.sideHit, result.hitVec, event.getHand());
				}
			}
			else if(result != null && result.typeOfHit == Type.BLOCK)
			{
				BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
				IBlockState state = event.getWorld().getBlockState(blockpos);
				if(state.getBlock() == Blocks.WATER && Core.isFreshWater(event.getWorld(), result.getBlockPos()))
				{
					Minecraft.getMinecraft().playerController.processRightClickBlock((EntityPlayerSP)event.getEntityPlayer(), 
							(WorldClient)event.getWorld(), blockpos, result.sideHit, result.hitVec, event.getHand());
				}
			}
		}
	}

	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getSide() == Side.CLIENT)
		{
			/*if(event.getHitVec() == null || event.getItemStack() != null)
			{
				return;
			}
			BlockPos blockpos = event.getPos().offset(event.getFace());
			IBlockState state = event.getWorld().getBlockState(blockpos);
			if(state.getBlock() == Blocks.WATER && Core.isFreshWater(event.getWorld(), blockpos))
			{
				Minecraft.getMinecraft().playerController.processRightClickBlock((EntityPlayerSP)event.getEntityPlayer(), 
						(WorldClient)event.getWorld(), event.getItemStack(), blockpos, event.getFace(), event.getHitVec(), event.getHand());
			}*/
		}
		else
		{
			BlockPos blockpos = event.getPos().offset(event.getFace());
			IBlockState state = event.getWorld().getBlockState(blockpos);
			if((state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.FLOWING_WATER) && Core.isFreshWater(event.getWorld(), blockpos))
			{
				if(event.getEntityPlayer().getHeldItem(event.getHand()) == ItemStack.EMPTY)
				{
					IFoodStatsTFC food = (IFoodStatsTFC)event.getEntityPlayer().getFoodStats();
					food.setWaterLevel((Math.min(food.getWaterLevel()+0.1f, 20)));
					TFC.network.sendTo(new CFoodPacket(food), (EntityPlayerMP) event.getEntityPlayer());
				}
				else if(ItemPotteryJug.IsCeramicJug(event.getEntityPlayer().getHeldItem(event.getHand())))
				{
					event.getEntityPlayer().getHeldItem(event.getHand()).setItemDamage(2);
				}
			}
		}
	}

	public RayTraceResult rayTrace(EntityPlayer player, double blockReachDistance, float partialTicks)
	{
		Vec3d vec3d = player.getPositionEyes(partialTicks);
		Vec3d vec3d1 = player.getLook(partialTicks);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * blockReachDistance, vec3d1.yCoord * blockReachDistance, vec3d1.zCoord * blockReachDistance);
		return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}
}
