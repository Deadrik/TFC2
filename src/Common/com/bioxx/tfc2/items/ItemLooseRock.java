package com.bioxx.tfc2.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.interfaces.IRegisterSelf;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.core.TFCTabs;

public class ItemLooseRock extends ItemTerra implements IRegisterSelf
{
	public ItemLooseRock()
	{
		this.setShowInCreative(true);
		this.setHasSubtypes(true);
		this.maxSubTypeMeta = 15;
		this.setCreativeTab(TFCTabs.TFCMaterials);
		this.subTypeNames = Core.capitalizeStringArray(Global.STONE_ALL);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName();
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);

		if (is.getItemDamage() < Global.STONE_ALL.length)
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global." + Global.STONE_ALL[is.getItemDamage()]));
		else
			arraylist.add(TextFormatting.DARK_RED + Core.translate("global.unknown"));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);
		if(itemStackIn.getCount() < 2)
		{
			return new ActionResult(EnumActionResult.FAIL, itemStackIn);
		}

		PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(playerIn);

		pi.specialCraftingType = itemStackIn;
		pi.specialCraftingTypeAlternate = null;
		if(!worldIn.isRemote)
			playerIn.openGui(TFC.instance, 0, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());

		return new ActionResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	public String[] getSubTypeNames() 
	{
		return subTypeNames;
	}

	@Override
	public String getPath()
	{
		return "LooseRocks/";
	}
}
