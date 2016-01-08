package com.bioxx.tfc2.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.tileentities.TileCrop;

public class ItemSeeds extends ItemTerra 
{
	Crop cropToPlant;
	public ItemSeeds(Crop c)
	{
		cropToPlant = c;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return false;

		IBlockState soil = worldIn.getBlockState(pos);
		if(side == EnumFacing.UP && soil.getBlock() == TFCBlocks.Farmland && worldIn.isAirBlock(pos.up()))
		{
			worldIn.setBlockState(pos.up(), TFCBlocks.Crop.getDefaultState());
			TileCrop tc = (TileCrop) worldIn.getTileEntity(pos.up());
			tc.setCropType(cropToPlant);
			tc.setFarmerID(playerIn);
		}

		return true;
	}
}
