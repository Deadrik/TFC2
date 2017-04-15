package com.bioxx.tfc2.blocks;

import java.util.Arrays;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.INeedOffset;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockLogVertical2 extends BlockLogVertical implements INeedOffset
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));

	public BlockLogVertical2()
	{
		super(Material.GROUND, META_PROPERTY);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		meta %= 16;
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta(meta + 16));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta() & 15;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		if(facing == EnumFacing.DOWN || facing == EnumFacing.UP)
			return this.getStateFromMeta(meta);
		else
		{
			if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
			{
				return TFCBlocks.LogHorizontal3.getStateFromMeta(meta).withProperty(BlockLogHorizontal3.ROT_PROPERTY, 1);
			}
			else
			{
				return TFCBlocks.LogHorizontal3.getStateFromMeta(meta).withProperty(BlockLogHorizontal3.ROT_PROPERTY, 0);
			}
		}
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
	public int damageDropped(IBlockState state)
	{
		return getMetaFromState(state) + 16;
	}
}
