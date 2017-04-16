package com.bioxx.tfc2.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc2.api.types.EffectType;
import com.bioxx.tfc2.core.TFCTabs;

public class BlockEffect extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("effect", EffectType.class);

	public BlockEffect()
	{
		super(Material.FIRE, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setBlockBounds(0, 0, 0, 1, 0.001f, 1);
		this.fullBlock = false;
		this.setShowInCreative(false);
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		IBlockState block = world.getBlockState(pos);
		if(block.getValue(META_PROPERTY) == EffectType.Acid)
			return 4;
		else return getLightValue(state, world, pos);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, EffectType.getTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EffectType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
}
