package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.interfaces.INeedOffset;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockSapling2 extends BlockSapling implements INeedOffset
{
	public static final PropertyEnum META_PROPERTY = PropertyEnum.create("type", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));
	public BlockSapling2()
	{
		super(META_PROPERTY);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta(meta+16));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta() & 15;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public int convertMetaToBlock(int meta) 
	{
		return meta & 15;
	}

	@Override
	public int convertMetaToItem(int meta) 
	{
		return meta + 16;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state)
	{
		WoodType wood = (WoodType)state.getValue(META_PROPERTY);
		this.grow_do(world, rand, pos, state, wood);
	}
}
