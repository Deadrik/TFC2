package com.bioxx.tfc2.blocks.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.IGravityBlock;
import com.bioxx.tfc2.api.interfaces.IWeightedBlock;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockDirt extends BlockCollapsible implements IWeightedBlock, IGravityBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockDirt()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
	{
		IBlockState state = world.getBlockState(pos);
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if(plantable == TFCBlocks.Sapling)
			return true;

		return false;
	}

	@Override
	public void onStartFalling(EntityFallingBlockTFC fallingEntity) 
	{
		fallingEntity.worldObj.playSoundAtEntity(fallingEntity, TFC_Sounds.FALLININGDIRTSHORT, 0.2f, 1.0f);
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
	public int getWeight(IBlockState myState) 
	{
		return 20;
	}

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos,IBlockState myState)
	{
		return 3;
	}

	@Override
	public boolean canBeSupportedBy(IBlockState myState, IBlockState otherState)
	{
		if(Core.isTerrain(otherState) && !Core.isSand(otherState))
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
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { META_PROPERTY });
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
