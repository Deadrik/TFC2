package com.bioxx.tfc2.items.pottery;

import com.bioxx.tfc2.api.interfaces.IRegisterSelf;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.items.ItemTerra;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPotteryBase extends ItemTerra implements IRegisterSelf
{
	public ItemPotteryBase()
	{
		super();
		this.hasSubtypes = true;
		this.setCreativeTab(TFCTabs.TFCPottery);
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
		return null;
	}

	@Override
	public String getPath()
	{
		return "pottery/";
	}

}
