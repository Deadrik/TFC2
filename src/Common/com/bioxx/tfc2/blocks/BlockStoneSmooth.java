package com.bioxx.tfc2.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;
import com.bioxx.tfc2.blocks.terrain.BlockRubble;
import com.bioxx.tfc2.core.TFCTabs;

public class BlockStoneSmooth extends BlockCollapsible implements ISupportBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockStoneSmooth()
	{
		super(Material.GROUND, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		setSoundType(SoundType.STONE);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/

	@Override
	public IBlockState getFallBlockType(IBlockState myState)
	{
		return TFCBlocks.Rubble.getDefaultState().withProperty(BlockRubble.META_PROPERTY, myState.getValue(META_PROPERTY));
	}

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos, IBlockState myState)
	{
		return 6;
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
