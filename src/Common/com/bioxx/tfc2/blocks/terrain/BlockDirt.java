package com.bioxx.tfc2.blocks.terrain;

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

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.interfaces.IGravityBlock;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockDirt extends BlockCollapsible implements IGravityBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockDirt()
	{
		super(Material.GROUND, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		setSoundType(SoundType.GROUND);
		this.collapseType = CollapsibleType.Nature;
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if(plantType == EnumPlantType.Plains)
			return true;
		return false;
	}

	@Override
	public void onStartFalling(EntityFallingBlockTFC fallingEntity) 
	{
		Core.playSoundAtEntity(fallingEntity, TFC_Sounds.FALLININGDIRTSHORT, 0.2f, 1.0f);
	}

	@Override
	public int getSlideHeight()
	{
		return 1;
	}

	@Override
	public float getSlideChance()
	{
		return 0.75f;
	}

	@Override
	public void onEndFalling(World worldIn, BlockPos pos) 
	{

	}

	@Override
	public boolean canFallInto(World worldIn, BlockPos pos) 
	{
		return false;
	}

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos,IBlockState myState)
	{
		return 3;
	}

	@Override
	public boolean canBeSupportedBy(IBlockState myState, IBlockState otherState)
	{
		if((Core.isTerrain(otherState) && !Core.isSand(otherState)) || otherState.getBlock() instanceof ISupportBlock)
			return true;
		return false;
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/



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
