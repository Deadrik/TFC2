package com.bioxx.tfc2.blocks.liquids;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;

import com.bioxx.tfc2.Core;

public class BlockFreshWaterStatic extends BlockFreshWater 
{

	public BlockFreshWaterStatic(Fluid fluid, Material material) 
	{
		super(fluid, material);
		this.setTickRandomly(true);
	}

	/*@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(LEVEL, meta);
	}

	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos blockIn)
	{
		this.updateLiquid(worldIn, pos, state);
	}

	private void updateLiquid(World worldIn, BlockPos pos, IBlockState state)
	{
		worldIn.setBlockState(pos, TFCBlocks.FreshWater.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		worldIn.scheduleUpdate(pos, TFCBlocks.FreshWater, this.tickRate(worldIn));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		//super.updateTick(world, pos, state, rand);
		world.setBlockState(pos, TFCBlocks.FreshWater.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		if(isGroundWater(world, pos) && world.provider.getDimension() == 0)
		{
			IslandMap map = Core.getMapForWorld(world, pos);
			NBTTagCompound nbt = map.getClosestCenter(pos).getCustomNBT();
			NBTTagCompound data;
			if(!nbt.hasKey("TFC2_Data"))
				nbt.setTag("TFC2_Data", new NBTTagCompound());
			data = nbt.getCompoundTag("TFC2_Data");
			byte[] hydrationArray = data.getByteArray("hydration");
			if(hydrationArray.length == 0)
			{
				hydrationArray = new byte[64];
			}
			int layer = (int)Math.floor(pos.getY()/4);
			hydrationArray[layer] = (byte)Math.min(hydrationArray[layer]+1, 255);

			if(layer > 0)
				hydrationArray[layer-1] = (byte)Math.min(hydrationArray[layer-1]+1, 255);
			if(layer < 63)
				hydrationArray[layer+1] = (byte)Math.min(hydrationArray[layer+1]+1, 255);

			data.setByteArray("hydration", hydrationArray);
		}
	}*/

	public boolean isGroundWater(World w, BlockPos pos)
	{
		if(Core.isSoil(w.getBlockState(pos.down(1))) || Core.isGravel(w.getBlockState(pos.down(1))))
		{
			return true;
		}
		return false;
	}
}
