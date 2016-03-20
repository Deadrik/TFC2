package com.bioxx.tfc2.blocks.terrain;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;

import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.blocks.BlockGravity;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockGravel extends BlockGravity
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockGravel()
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
		fallingEntity.worldObj.playSoundAtEntity(fallingEntity, TFC_Sounds.FALLININGDIRTSHORT, 0.7f, 0.80f);
	}

	@Override
	public int getSlideHeight()
	{
		return 1;
	}

	@Override
	public float getSlideChance()
	{
		return 1.0f;
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
}
