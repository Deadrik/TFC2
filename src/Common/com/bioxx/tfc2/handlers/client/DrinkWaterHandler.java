package com.bioxx.tfc2.handlers.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.bioxx.tfc2.TFCBlocks;

public class DrinkWaterHandler 
{
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickEmpty event)
	{
		if(event.getSide() == Side.CLIENT)
		{
			RayTraceResult result = rayTrace(event.getEntityPlayer(), 5, 1.0f);
			if(result != null && result.typeOfHit == Type.MISS)
			{
				BlockPos blockpos = result.getBlockPos();
				IBlockState state = event.getWorld().getBlockState(blockpos);
				if(state.getBlock() == TFCBlocks.FreshWater)
				{
					Minecraft.getMinecraft().playerController.processRightClickBlock((EntityPlayerSP)event.getEntityPlayer(), 
							(WorldClient)event.getWorld(), event.getItemStack(), blockpos, result.sideHit, result.hitVec, event.getHand());
				}
			}
			else if(result != null && result.typeOfHit == Type.BLOCK)
			{
				BlockPos blockpos = result.getBlockPos().offset(result.sideHit);
				IBlockState state = event.getWorld().getBlockState(blockpos);
				if(state.getBlock() == TFCBlocks.FreshWater || state.getBlock() == TFCBlocks.FreshWaterStatic)
				{
					Minecraft.getMinecraft().playerController.processRightClickBlock((EntityPlayerSP)event.getEntityPlayer(), 
							(WorldClient)event.getWorld(), event.getItemStack(), blockpos, result.sideHit, result.hitVec, event.getHand());
				}
			}
		}
	}

	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getSide() == Side.CLIENT)
		{
			if(event.getHitVec() == null || event.getItemStack() != null)
			{
				return;
			}
			BlockPos blockpos = event.getPos().offset(event.getFace());
			IBlockState state = event.getWorld().getBlockState(blockpos);
			if(state.getBlock() == TFCBlocks.FreshWater || state.getBlock() == TFCBlocks.FreshWaterStatic)
			{
				Minecraft.getMinecraft().playerController.processRightClickBlock((EntityPlayerSP)event.getEntityPlayer(), 
						(WorldClient)event.getWorld(), event.getItemStack(), blockpos, event.getFace(), event.getHitVec(), event.getHand());
			}
		}
	}

	public RayTraceResult rayTrace(EntityPlayer player, double blockReachDistance, float partialTicks)
	{
		Vec3d vec3d = player.getPositionEyes(partialTicks);
		Vec3d vec3d1 = player.getLook(partialTicks);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * blockReachDistance, vec3d1.yCoord * blockReachDistance, vec3d1.zCoord * blockReachDistance);
		return player.worldObj.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}
}
