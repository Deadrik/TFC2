package com.bioxx.tfc2.items.pottery;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.IRegisterSelf;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.items.ItemTerra;
import com.bioxx.tfc2.tileentities.TilePitKiln;

public class ItemPotteryBase extends ItemTerra implements IRegisterSelf
{
	protected boolean displayMaterial = true;
	public ItemPotteryBase()
	{
		super();
		this.hasSubtypes = true;
		this.setCreativeTab(TFCTabs.TFCPottery);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		if(displayMaterial)
		{
			String[] name = new String[] {"global.clay", "global.ceramic"};
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate(name[is.getItemDamage()]));
		}
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn,
			BlockPos pos, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ)
	{
		// TODO Auto-generated method stub
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}

	@Override
	public String getPath()
	{
		return "pottery/";
	}

	public boolean isClay(ItemStack stack)
	{
		return true;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if(!world.isRemote)
		{
			if(isClay(player.getHeldItem(hand)) && Core.isTerrain(world.getBlockState(pos)))
			{
				world.setBlockState(pos.up(), TFCBlocks.PitKiln.getDefaultState());
				TilePitKiln te = (TilePitKiln) world.getTileEntity(pos.up());
				te.setInventorySlotContents(0, player.getHeldItem(hand).splitStack(1));
				return EnumActionResult.SUCCESS;
			}
		}

		return EnumActionResult.PASS;
	}

}
