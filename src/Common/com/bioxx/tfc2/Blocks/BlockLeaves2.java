package com.bioxx.tfc2.Blocks;

import java.util.Arrays;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc2.api.Types.WoodType;

public class BlockLeaves2 extends BlockLeaves
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));
	public static PropertyBool IS_OUTER = PropertyBool.create("is_outer");
	private boolean isTransparent = true;

	public BlockLeaves2()
	{
		super();
		//this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, WoodType.Oak).withProperty(IS_OUTER, Boolean.valueOf(false)));
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY, IS_OUTER});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if(!isTransparent)
			return state.withProperty(IS_OUTER, false);

		if(world.getBlockState(pos.offsetNorth()).getBlock().getMaterial() != Material.leaves)
			return state.withProperty(IS_OUTER, true);
		if(world.getBlockState(pos.offsetSouth()).getBlock().getMaterial() != Material.leaves)
			return state.withProperty(IS_OUTER, true);
		if(world.getBlockState(pos.offsetEast()).getBlock().getMaterial() != Material.leaves)
			return state.withProperty(IS_OUTER, true);
		if(world.getBlockState(pos.offsetWest()).getBlock().getMaterial() != Material.leaves)
			return state.withProperty(IS_OUTER, true);
		if(world.getBlockState(pos.offsetUp()).getBlock().getMaterial() != Material.leaves)
			return state.withProperty(IS_OUTER, true);
		if(world.getBlockState(pos.offsetDown()).getBlock().getMaterial() != Material.leaves)
			return state.withProperty(IS_OUTER, true);

		return state.withProperty(IS_OUTER, false);

	}
}
