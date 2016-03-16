package com.bioxx.tfc2.blocks;

import java.util.Arrays;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

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
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY});
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
}
