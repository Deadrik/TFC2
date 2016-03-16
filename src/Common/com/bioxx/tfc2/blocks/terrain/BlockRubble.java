package com.bioxx.tfc2.blocks.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.IWeightedBlock;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.blocks.BlockGravity;
import com.bioxx.tfc2.blocks.BlockWoodSupport;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockRubble extends BlockGravity implements IWeightedBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockRubble()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public void onStartFalling(EntityFallingBlockTFC fallingEntity) 
	{
		fallingEntity.worldObj.playSoundAtEntity(fallingEntity, TFC_Sounds.FALLININGROCKSHORT, 0.8f, 0.5f+(fallingEntity.worldObj.rand.nextFloat()*0.3f));
	}

	@Override
	public void onEndFalling(World worldIn, BlockPos pos)
	{
		IBlockState otherState = worldIn.getBlockState(pos.north());
		if(otherState.getBlock() instanceof BlockWoodSupport && worldIn.rand.nextInt(100) < 25)
		{
			((BlockWoodSupport)otherState.getBlock()).createFallingEntity(worldIn, pos.north(), otherState);
		}
		otherState = worldIn.getBlockState(pos.south());
		if(otherState.getBlock() instanceof BlockWoodSupport && worldIn.rand.nextInt(100) < 25)
		{
			((BlockWoodSupport)otherState.getBlock()).createFallingEntity(worldIn, pos.south(), otherState);
		}
		otherState = worldIn.getBlockState(pos.east());
		if(otherState.getBlock() instanceof BlockWoodSupport && worldIn.rand.nextInt(100) < 25)
		{
			((BlockWoodSupport)otherState.getBlock()).createFallingEntity(worldIn, pos.east(), otherState);
		}
		otherState = worldIn.getBlockState(pos.west());
		if(otherState.getBlock() instanceof BlockWoodSupport && worldIn.rand.nextInt(100) < 25)
		{
			((BlockWoodSupport)otherState.getBlock()).createFallingEntity(worldIn, pos.west(), otherState);
		}
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
		return new BlockState(this, new IProperty[]{META_PROPERTY});
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

	@Override
	public int getSlideHeight()
	{
		return 2;
	}

	@Override
	public float getSlideChance()
	{
		return 0.5f;
	}

	@Override
	public int getWeight(IBlockState myState) {
		return 40;
	}
}
