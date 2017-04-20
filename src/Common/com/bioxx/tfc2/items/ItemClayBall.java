package com.bioxx.tfc2.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class ItemClayBall extends ItemTerra 
{
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);

		if(itemStackIn.getCount() < 5)
		{
			return new ActionResult(EnumActionResult.FAIL, itemStackIn);
		}

		PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(playerIn);
		pi.specialCraftingType = new ItemStack(itemStackIn.getItem());
		pi.specialCraftingTypeAlternate = new ItemStack(itemStackIn.getItem());
		if(!worldIn.isRemote)
			playerIn.openGui(TFC.instance, 0, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());

		return new ActionResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
	}
}
