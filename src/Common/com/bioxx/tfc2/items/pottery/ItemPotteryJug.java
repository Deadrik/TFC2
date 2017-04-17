package com.bioxx.tfc2.items.pottery;

import java.util.List;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.core.TFC_Sounds;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemPotteryJug extends ItemPotteryBase
{
	public ItemPotteryJug()
	{
		super();
		this.subTypeNames = new String[] {"clay_jug", "ceramic_jug", "water_jug"};
		this.maxSubTypeMeta = 2;
		this.maxStackSize = 1;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		if(!worldIn.isRemote)
		{
			EntityPlayer player = (EntityPlayer) entityLiving;
			if(stack.getItemDamage() == 2)
				Core.getPlayerFoodStats(player).restoreWater(player, 24000);
			
			if(stack.getItemDamage() > 1 && ((EntityPlayer)entityLiving).capabilities.isCreativeMode)
			{
				if(worldIn.rand.nextInt(50) == 0)
				{
					stack.shrink(1);
					entityLiving.playSound(TFC_Sounds.CERAMICBREAK, 0.7f, worldIn.rand.nextFloat() * 0.2f + 0.8f);
				}
				else
					stack.setItemDamage(1);
			}
		}
		return stack;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.DRINK;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		// TODO Auto-generated method stub
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		String[] name = new String[] {"global.clay", "global.ceramic", "fluid.freswater"};
		arraylist.add(TextFormatting.DARK_GRAY + Core.translate(name[is.getItemDamage()]));
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}

}
