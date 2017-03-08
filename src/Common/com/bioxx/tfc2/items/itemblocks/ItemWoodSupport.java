package com.bioxx.tfc2.items.itemblocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.INeedOffset;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.BlockWoodSupport;

public class ItemWoodSupport extends ItemWood
{
	public ItemWoodSupport(Block b)
	{
		super(b);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		int meta = is.getItemDamage();
		meta = ((INeedOffset)block).convertMetaToItem(meta);
		arraylist.add(TextFormatting.DARK_GRAY + "Span Range: " + WoodType.getTypeFromMeta(meta).getSupportRange());
		/*if(block instanceof ISupportBlock)
		{

			arraylist.add(TextFormatting.DARK_GRAY + "Max Cross Load: " + WoodType.getTypeFromMeta(meta).getRupture());
			arraylist.add(TextFormatting.DARK_GRAY + "Max Free Load: " + WoodType.getTypeFromMeta(meta).getCompression());
		}*/

	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos clickedPos, EnumFacing clickedSide, EntityPlayer player, ItemStack is)
	{
		if(super.canPlaceBlockOnSide(world, clickedPos, clickedSide, player, is))
		{
			BlockWoodSupport block = (BlockWoodSupport) this.block;
			IBlockState otherState = world.getBlockState(clickedPos);
			if(clickedSide == EnumFacing.UP || clickedSide == EnumFacing.DOWN)
			{
				return block.canBeSupportedBy(block.getDefaultState(), world.getBlockState(clickedPos));
			}
			else
			{
				//If we are placing this block on the side of another block then it needs to be a support beam block.
				if(otherState.getBlock() instanceof BlockWoodSupport)
				{
					BlockWoodSupport otherBlock = (BlockWoodSupport) otherState.getBlock();
					otherState = otherBlock.getActualState(otherState, world, clickedPos);
					if(!otherState.getValue(BlockWoodSupport.SPAN))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos origPos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = playerIn.getHeldItem(hand);
		IBlockState iblockstate = worldIn.getBlockState(origPos);
		Block block = iblockstate.getBlock();
		BlockPos pos = origPos.offset(side);

		if (stack.getMaxStackSize() == 0)
		{
			return EnumActionResult.FAIL;
		}
		if (!playerIn.canPlayerEdit(pos, side, stack))
		{
			return EnumActionResult.FAIL;
		}
		if (worldIn.mayPlace(this.block, pos, false, side, (Entity)null))
		{
			int meta = getMetadata(stack.getMetadata());
			IBlockState scanState;
			//Scan out to make sure there is another beam in the facing direction 
			int range = WoodType.getTypeFromMeta(stack.getItemDamage()).getSupportRange();
			BlockPos scanPos;
			int foundRange = -1;
			if(side == EnumFacing.UP || side == EnumFacing.DOWN)
			{
				foundRange = 1;
			}
			for(int i = 1; i <= range && foundRange == -1; i++)
			{
				scanPos = origPos.offset(side, i);
				scanState = worldIn.getBlockState(scanPos);
				scanState = scanState.getBlock().getActualState(scanState, worldIn, scanPos);
				if(scanState.getBlock() instanceof BlockWoodSupport)
				{
					if(!scanState.getValue(BlockWoodSupport.SPAN))
					{
						foundRange = i;
						break;
					}
				}
				else if(scanState.getBlock().isReplaceable(worldIn, pos))
				{
					continue;
				}
				else
				{
					break;
				}
			}

			if(foundRange != -1 && stack.getMaxStackSize() >= foundRange)
			{
				for(int i = 1; i <= foundRange; i++)
				{
					scanPos = origPos.offset(side, i);
					IBlockState iblockstate1 = this.block.getStateForPlacement(worldIn, scanPos.offset(side.getOpposite()), side, hitX, hitY, hitZ, meta, playerIn);
					if (placeBlockAt(stack, playerIn, worldIn, scanPos, side, hitX, hitY, hitZ, iblockstate1))
					{
						worldIn.playSound(scanPos.getX() + 0.5F, scanPos.getY() + 0.5F, scanPos.getZ() + 0.5F, this.block.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (this.block.getSoundType().getVolume() + 1.0F) / 2.0F, this.block.getSoundType().getPitch() * 0.8F, true);
						stack.shrink(1);
					}
				}
			}

			return EnumActionResult.SUCCESS;
		}


		return EnumActionResult.PASS;
	}
}
