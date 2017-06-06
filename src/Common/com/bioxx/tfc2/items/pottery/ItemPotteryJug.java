package com.bioxx.tfc2.items.pottery;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.networking.client.CFoodPacket;

public class ItemPotteryJug extends ItemPotteryBase
{
	public ItemPotteryJug()
	{
		super();
		this.subTypeNames = new String[] {"clay_jug", "ceramic_jug", "water_jug"};
		this.maxSubTypeMeta = 2;
		this.maxStackSize = 1;
		displayMaterial = false;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving)
	{
		if(!worldIn.isRemote)
		{
			EntityPlayer player = (EntityPlayer) entityLiving;

			if(IsWaterJug(stack))
			{
				IFoodStatsTFC food = (IFoodStatsTFC)player.getFoodStats();
				food.setWaterLevel(20);
				TFC.network.sendTo(new CFoodPacket(food), (EntityPlayerMP) player);
			}

			if(IsWaterJug(stack) && !((EntityPlayer)entityLiving).capabilities.isCreativeMode)
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
		if(IsWaterJug(stack))
			return EnumAction.DRINK;
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		if(IsWaterJug(stack))
			return 20;
		return 10;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if(IsWaterJug(itemstack))
		{
			playerIn.setActiveHand(handIn);
			return new ActionResult(EnumActionResult.SUCCESS, itemstack);
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		return EnumActionResult.PASS;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		String[] name = new String[] {"global.clay", "global.ceramic", "fluid.freshwater"};
		arraylist.add(TextFormatting.DARK_GRAY + Core.translate(name[is.getItemDamage()]));
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}

	public static boolean IsWaterJug(ItemStack stack)
	{
		if(stack.getItem() == TFCItems.PotteryJug)
			return stack.getItemDamage() == 2;
		return false;
	}

	public static boolean IsCeramicJug(ItemStack stack)
	{
		if(stack.getItem() == TFCItems.PotteryJug)
			return stack.getItemDamage() == 1;
		return false;
	}

	@Override
	public boolean isClay(ItemStack stack)
	{
		return stack.getItemDamage() == 0;
	}

}
