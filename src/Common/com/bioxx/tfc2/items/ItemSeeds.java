package com.bioxx.tfc2.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.api.interfaces.IRegisterSelf;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.tileentities.TileCrop;

public class ItemSeeds extends ItemTerra implements IRegisterSelf
{
	Crop[] cropToPlant = new Crop[]{Crop.Corn, Crop.Cabbage, Crop.Tomato, Crop.Wheat, Crop.Barley, Crop.Rye, Crop.Oat, Crop.Rice, 
			Crop.Corn, Crop.Corn, Crop.Corn, Crop.Corn, Crop.Corn, Crop.Corn, Crop.Corn, Crop.Corn, Crop.Corn, Crop.Corn};
	public ItemSeeds()
	{
		this.hasSubtypes = true;
		this.maxSubTypeMeta = 17;
		this.subTypeNames = new String[] {"seeds_corn", "seeds_cabbage", "seeds_tomato", "seeds_wheat", "seeds_barley", "seeds_rye", 
				"seeds_oat", "seeds_rice", "seeds_potato", "seeds_onion", "seeds_garlic", "seeds_carrot", "seeds_sugarcane", "seeds_yellowbellpepper", 
				"seeds_redbellpepper", "seeds_soybean", "seeds_greenbean", "seeds_squash"};
		this.setCreativeTab(TFCTabs.TFCDecoration);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = playerIn.getHeldItem(hand);
		if(worldIn.isRemote)
			return EnumActionResult.FAIL;

		IBlockState soil = worldIn.getBlockState(pos);
		if(facing == EnumFacing.UP && soil.getBlock() == TFCBlocks.Farmland && worldIn.isAirBlock(pos.up()))
		{
			//worldIn.setBlockState(pos.up(), TFCBlocks.Crop.getDefaultState());
			TileCrop tc = (TileCrop) worldIn.getTileEntity(pos.up());
			tc.setCropType(cropToPlant[stack.getItemDamage()]);
			tc.setFarmerID(playerIn);
		}

		return EnumActionResult.SUCCESS;
	}

	@Override
	public String[] getSubTypeNames() 
	{
		return subTypeNames;
	}

	@Override
	public String getPath()
	{
		return "Seeds/";
	}
}
