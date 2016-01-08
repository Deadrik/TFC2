package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.BlockFarmland;
import com.bioxx.tfc2.blocks.terrain.BlockDirt;
import com.google.common.collect.Sets;

public class ItemHoe extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {});

	public ItemHoe(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return false;

		IBlockState soil = worldIn.getBlockState(pos);
		if(side == EnumFacing.UP && Core.isSoil(soil))
		{
			worldIn.setBlockState(pos, TFCBlocks.Farmland.getDefaultState().withProperty(BlockFarmland.META_PROPERTY, soil.getValue(BlockDirt.META_PROPERTY)));
		}

		return true;
	}

}
