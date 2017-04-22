package com.bioxx.tfc2.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.world.TeleporterPaths;

public class TeleportHandler 
{
	@SubscribeEvent
	public void handle(EntityTravelToDimensionEvent event)
	{
		int curDimension = event.getEntity().dimension;
		MinecraftServer minecraftserver = event.getEntity().getServer();
		WorldServer worldserver = minecraftserver.worldServerForDimension(curDimension);
		WorldServer worldserver1 = minecraftserver.worldServerForDimension(event.getDimension());

		World world = event.getEntity().getEntityWorld();
		TeleporterPaths teleporter = new TeleporterPaths(worldserver1);

		if(event.getEntity() instanceof EntityPlayerMP)
		{
			((EntityPlayerMP)event.getEntity()).invulnerableDimensionChange = true;
			minecraftserver.getPlayerList().transferPlayerToDimension((EntityPlayerMP)event.getEntity(), event.getDimension(), teleporter);
			event.setCanceled(true);
		}
		else
		{
			world.removeEntity(event.getEntity());
			event.getEntity().isDead = false;
			BlockPos blockpos;

			double d0 = event.getEntity().posX;
			double d1 = event.getEntity().posZ;
			double d2 = 8.0D;

			if (event.getDimension() == 2)
			{
				d0 = MathHelper.clamp(d0 / 8.0D, worldserver1.getWorldBorder().minX() + 16.0D, worldserver1.getWorldBorder().maxX() - 16.0D);
				d1 = MathHelper.clamp(d1 / 8.0D, worldserver1.getWorldBorder().minZ() + 16.0D, worldserver1.getWorldBorder().maxZ() - 16.0D);
			}
			else if (event.getDimension() == 0)
			{
				d0 = MathHelper.clamp(d0 * 8.0D, worldserver1.getWorldBorder().minX() + 16.0D, worldserver1.getWorldBorder().maxX() - 16.0D);
				d1 = MathHelper.clamp(d1 * 8.0D, worldserver1.getWorldBorder().minZ() + 16.0D, worldserver1.getWorldBorder().maxZ() - 16.0D);
			}

			d0 = (double)MathHelper.clamp((int)d0, -29999872, 29999872);
			d1 = (double)MathHelper.clamp((int)d1, -29999872, 29999872);
			float f = event.getEntity().rotationYaw;
			event.getEntity().setLocationAndAngles(d0, event.getEntity().posY, d1, 90.0F, 0.0F);


			teleporter.placeInPortal(event.getEntity(), event.getEntity().rotationYaw);

			blockpos = new BlockPos(event.getEntity());


			worldserver.updateEntityWithOptionalForce(event.getEntity(), false);

			Entity entity = EntityList.newEntity(event.getEntity().getClass(), worldserver1);

			if (entity != null)
			{
				entity.copyDataFromOld(event.getEntity());

				entity.moveToBlockPosAndAngles(blockpos, entity.rotationYaw, entity.rotationPitch);

				boolean flag = entity.forceSpawn;
				entity.forceSpawn = true;
				worldserver1.spawnEntity(entity);
				entity.forceSpawn = flag;
				worldserver1.updateEntityWithOptionalForce(entity, false);
			}

			event.getEntity().isDead = true;
			worldserver.resetUpdateEntityTick();
			worldserver1.resetUpdateEntityTick();
			event.setCanceled(true);
		}
	}
}
