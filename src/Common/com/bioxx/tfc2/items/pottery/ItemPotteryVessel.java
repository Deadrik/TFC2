package com.bioxx.tfc2.items.pottery;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.tileentities.TileSmallVessel;

public class ItemPotteryVessel extends ItemPotteryBase
{
	public ItemPotteryVessel()
	{
		super();
		this.subTypeNames = new String[] {"clay_vessel", "ceramic_vessel"};
		this.maxSubTypeMeta = 1;
		this.maxStackSize = 1;
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}

	@Override
	public boolean isClay(ItemStack stack)
	{
		return stack.getItemDamage() == 0;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world,
			BlockPos pos, EnumHand hand, EnumFacing facing, float hitX,
			float hitY, float hitZ)
	{
		if(facing == EnumFacing.UP && !world.isRemote && player.isSneaking() && !isClay(player.getHeldItem(hand)))
		{
			IBlockState downState = world.getBlockState(pos);
			if(downState.getBlock() != TFCBlocks.SmallVessel && downState.isSideSolid(world, pos, EnumFacing.UP))
			{
				int index = getIndex(hitX, hitZ);
				world.setBlockState(pos.up(), TFCBlocks.SmallVessel.getDefaultState());
				TileSmallVessel tile = (TileSmallVessel) world.getTileEntity(pos.up());
				tile.setRotation(player.getHorizontalFacing().getAxis());
				tile.setInventorySlotContents(index, player.inventory.removeStackFromSlot(player.inventory.currentItem));
			}
			else if(downState.getBlock() == TFCBlocks.SmallVessel)
			{
				int index = getIndex(hitX, hitZ);
				TileSmallVessel tile = (TileSmallVessel) world.getTileEntity(pos);
				if(tile.getStackInSlot(index).isEmpty())
				{
					tile.setInventorySlotContents(index, player.getHeldItemMainhand());
				}
			}
		}

		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	private int getIndex(float hitX, float hitZ)
	{
		int index = 0;
		if(hitX < 0.5 && hitZ < 0.5)
			index = 0;
		else if(hitX < 0.5 && hitZ >= 0.5)
			index = 1;
		else if(hitX >= 0.5 && hitZ < 0.5)
			index = 2;
		else if(hitX >= 0.5 && hitZ >= 0.5)
			index = 3;
		return index;
	}
}
