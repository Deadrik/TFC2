package com.bioxx.tfc2.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.network.IGuiHandler;

import com.bioxx.tfc2.containers.*;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.entity.EntityCart;
import com.bioxx.tfc2.tileentities.TileAnvil;
import com.bioxx.tfc2.tileentities.TileFirepit;


public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) 
	{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(player);
		switch(id)
		{
		case 0://knapping
		{
			return new ContainerSpecialCrafting(player.inventory, pi.specialCraftingTypeAlternate == null ? pi.specialCraftingType : null, world, x, y, z);
		}
		case 1://cart
		{
			return new ContainerCart(player.inventory, ((EntityCart)pi.entityForInventory).cartInv, world, x, y, z);
		}
		case 2://cart
		{
			return new ContainerAnvil(player.inventory, (TileAnvil)te, world, x, y, z);
		}
		case 3://Skills Screen
			return new ContainerSkills(player);
		case 4://Health Screen
			return new ContainerSkills(player);
		case 5://firepit
		{
			return new ContainerFirepit(player.inventory, (TileFirepit)te, world, x, y, z);
		}
		case 6://cooking pot
		{
			return new ContainerCookingPot(player.inventory, (TileFirepit)te, world, x, y, z);
		}
		case 7://small vessel
		{
			return new ContainerVessel(player.inventory, world);
		}
		default:
		{
			return null;
		}
		}
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}
}
