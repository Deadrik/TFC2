package com.bioxx.tfc2.items.pottery;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.tileentities.TileSmallVessel;

public class ItemPotteryVessel extends ItemPotteryBase
{
	public ItemPotteryVessel()
	{
		super();
		this.subTypeNames = new String[] {"clay_vessel", "ceramic_vessel", "ceramic_vessel_white"};
		this.maxSubTypeMeta =1;
		this.maxStackSize = 1;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		if(is.hasTagCompound() && is.getTagCompound().hasKey("Items"))
		{
			NBTTagList nbttaglist = is.getTagCompound().getTagList("Items", 10);
			for(int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound1.getByte("Slot");
				if(byte0 >= 0 && byte0 < 4)
				{
					ItemStack stackStored = new ItemStack(nbttagcompound1);
					arraylist.add(TextFormatting.DARK_GRAY + ""+stackStored.getCount() + "x " + stackStored.getDisplayName());
				}
			}
		}
		//arraylist.add(TextFormatting.DARK_GRAY + Core.translate(name[is.getItemDamage()]));
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
		else if(!world.isRemote && !player.isSneaking() && !isClay(player.getHeldItem(hand)))
		{
			player.openGui(TFC.instance, 7, world, pos.getX(), pos.getY(), pos.getZ());
		}


		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn)
	{
		if(!world.isRemote && !player.isSneaking() && !isClay(player.getHeldItemMainhand()))
		{
			player.openGui(TFC.instance, 7, world, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
			return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
		}
		return new ActionResult(EnumActionResult.PASS, player.getHeldItem(handIn));
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		//We dont want onItemUseFirst from ItemPotteryBase to fire
		if(this.isClay(player.getHeldItem(hand)))
			return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
		else
			return EnumActionResult.PASS;
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
