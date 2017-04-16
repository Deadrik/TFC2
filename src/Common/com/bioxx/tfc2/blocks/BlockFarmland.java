package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.TFCTabs;

public class BlockFarmland extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockFarmland()
	{
		super(Material.GROUND, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		setSoundType(SoundType.GROUND);
		this.setTickRandomly(true);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(world.isRemote)
			return;

		if(!(world.getBlockState(pos.up()).getBlock() instanceof IPlantable))
		{
			if(!isFertile(world, pos) && rand.nextInt(100) == 0)
			{
				world.setBlockState(pos, TFCBlocks.Dirt.getDefaultState().withProperty(META_PROPERTY, state.getValue(META_PROPERTY)));
			}
		}
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if(plantType == EnumPlantType.Crop)
			return true;

		if(plantType == EnumPlantType.Plains)
			return true;

		if(plantable == TFCBlocks.Sapling)
			return true;

		return false;
	}

	@Override
	public boolean isFertile(World world, BlockPos pos)
	{

		IslandMap map = Core.getMapForWorld(world, pos);
		Center closest = map.getClosestCenter(pos);

		if(closest != null && closest.getCustomNBT() != null)
		{

			byte[] hydrationArray = closest.getCustomNBT().getByteArray("hydration");
			int hydraY = Math.min((int)Math.floor(pos.getY()/4), 64);
			boolean isIrrigated = hydrationArray.length == 0 ? false : (hydrationArray[hydraY] & 0xFF) > 100;

			return isIrrigated;
		}


		return false;
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { META_PROPERTY });
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, StoneType.getStoneTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((StoneType)state.getValue(META_PROPERTY)).getMeta();
	}
}
