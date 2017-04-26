package com.bioxx.tfc2.blocks.terrain;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.BlockVegetation.VegType;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockStone extends BlockCollapsible
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockStone()
	{
		super(Material.ROCK, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setSoundType(SoundType.STONE);
		scanDepth = 10;
		collapseType = CollapsibleType.Nature;
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos blockIn)
	{
		IBlockState stateUp = worldIn.getBlockState(pos.up());
		IBlockState stateDown = worldIn.getBlockState(pos.down());
		IBlockState stateNorth = worldIn.getBlockState(pos.north());
		IBlockState stateSouth = worldIn.getBlockState(pos.south());
		IBlockState stateEast = worldIn.getBlockState(pos.east());
		IBlockState stateWest = worldIn.getBlockState(pos.west());

		if(stateUp.getBlock() != this && stateDown.getBlock() != this && stateNorth.getBlock() != this
				&& stateSouth.getBlock() != this && stateEast.getBlock() != this && stateWest.getBlock() != this)
		{
			dropBlockAsItem((World) worldIn, pos, worldIn.getBlockState(pos), 0);
			((World)worldIn).setBlockToAir(pos);
		}
		else
		{
			super.onNeighborChange(worldIn, pos, blockIn);
		}
	}

	@Override
	protected void onCreateFallingEntity(EntityFallingBlockTFC entity, IBlockState state, World world, BlockPos pos)
	{
		if(world.rand.nextFloat() < 0.5)
			world.setBlockState(pos, TFCBlocks.Gravel.getDefaultState().withProperty(META_PROPERTY, state.getValue(META_PROPERTY)));
	}

	@Override
	public IBlockState getFallBlockType(IBlockState myState)
	{
		return TFCBlocks.Rubble.getDefaultState().withProperty(BlockRubble.META_PROPERTY, myState.getValue(META_PROPERTY));
	}

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos, IBlockState myState)
	{
		int range = 5;
		StoneType stone = (StoneType)myState.getValue(BlockStone.META_PROPERTY);
		switch(stone)
		{
		case Granite:
		case Gabbro:
		case Diorite:
			range = 7; break;
		case Andesite:
		case Basalt:
		case Dacite:
		case Rhyolite:
			range = 6; break;
		case Gneiss:
		case Blueschist:
		case Marble:
		case Schist:
			range = 5; break;
		case Chert:
		case Claystone:
		case Dolomite:
		case Limestone:
		case Shale:
			range = 4; break;
		}
		return range;
	}

	@Override
	public boolean canBeSupportedBy(IBlockState myState, IBlockState otherState)
	{
		if(otherState.getBlock() == this || otherState.getBlock() instanceof ISupportBlock || otherState.getBlock() == Blocks.BEDROCK || Core.isSoil(otherState))
			return true;
		return false;
	}

	@Override
	protected boolean hasSupport(World world, BlockPos pos, IBlockState state)
	{
		scanDepth = getDepthScanRangeScaled(world, pos);
		boolean natural = super.hasSupport(world, pos, state);
		scanDepth = 10;
		return natural;
	}

	protected int getDepthScanRangeScaled(World world, BlockPos pos)
	{
		int worldElev = world.getTopSolidOrLiquidBlock(pos).getY();
		return Math.max((int)Math.floor(scanDepth * ((float)pos.getY() / (float)worldElev)), 2);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if(plantable == TFCBlocks.Vegetation && (VegType)plant.getValue(BlockVegetation.META_PROPERTY) == VegType.Grass)
			return true;
		return false;
	}

	/*******************************************************************************
	 * 1. Rendering 
	 *******************************************************************************/
	/*******************************************************************************
	 * 1. Blockstate 
	 *******************************************************************************/
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY});
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
