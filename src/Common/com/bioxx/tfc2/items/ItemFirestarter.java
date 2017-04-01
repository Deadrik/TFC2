package com.bioxx.tfc2.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.blocks.BlockFirepit;
import com.bioxx.tfc2.tileentities.TileFirepit;

public class ItemFirestarter extends ItemTerra
{
	public ItemFirestarter()
	{
		this.setShowInCreative(true);
		this.setCreativeTab(CreativeTabs.TOOLS);
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
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			List<EntityItem> list = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)));
			List<EntityItem> uselist = new ArrayList<EntityItem>();
			int needStones = 4;
			int needSticks = 4;
			int needLogs= 1;
			NonNullList<ItemStack> oreList=null;

			for(EntityItem ei : list)
			{
				Item i = ei.getEntityItem().getItem();
				if(needStones > 0 && i == TFCItems.LooseRock)
				{
					needStones -= ei.getEntityItem().getCount();
					uselist.add(ei);
				}

				if(needSticks > 0)
				{
					oreList=OreDictionary.getOres("stickWood");
					for(ItemStack is : oreList)
						if(needSticks > 0 && OreDictionary.itemMatches(is, ei.getEntityItem(), false))
						{
							needSticks -= ei.getEntityItem().getCount();
							uselist.add(ei);
						}
				}
				if(needLogs > 0)
				{
					oreList=OreDictionary.getOres("logWood");
					for(ItemStack is : oreList)
						if(needLogs > 0 && OreDictionary.itemMatches(is, ei.getEntityItem(), false))
						{
							needLogs -= ei.getEntityItem().getCount();
							uselist.add(ei);
						}
				}
			}

			ItemStack logStack = null;

			if(needStones <= 0 && needSticks <= 0 && needLogs <= 0)
			{
				needStones = 4;
				needSticks = 4;
				needLogs= 1;

				for(EntityItem ei : uselist)
				{
					if(needStones > 0 && ei.getEntityItem().getItem() == TFCItems.LooseRock)
					{
						int remove = Math.min(ei.getEntityItem().getCount(), needStones);
						needStones -= remove;
						ei.getEntityItem().shrink(remove);
						if(ei.getEntityItem().getCount() == 0)
							ei.setDead();
					}
					else if(needSticks > 0)
					{
						oreList=OreDictionary.getOres("stickWood");
						for(ItemStack is : oreList)
							if(needSticks > 0 && OreDictionary.itemMatches(is, ei.getEntityItem(), false))
							{
								int remove = Math.min(ei.getEntityItem().getCount(), needSticks);
								needSticks -= remove;
								ei.getEntityItem().shrink(remove);
								if(ei.getEntityItem().getCount() == 0)
									ei.setDead();
							}
					}
					else if(logStack == null)
					{
						oreList=OreDictionary.getOres("logWood");
						for(ItemStack is : oreList)
						{
							if(OreDictionary.itemMatches(is, ei.getEntityItem(), false))
							{
								logStack = ei.getEntityItem().copy();
								ei.setDead();
							}
						}
					}
				}

				if(logStack != null)
				{
					worldIn.setBlockState(pos.up(), TFCBlocks.Firepit.getDefaultState().withProperty(BlockFirepit.LIT, true));
					TileFirepit te = (TileFirepit)worldIn.getTileEntity(pos.up());
					te.setInventorySlotContents(0, logStack);
				}
			}
		}
		return EnumActionResult.PASS;
	}
}
