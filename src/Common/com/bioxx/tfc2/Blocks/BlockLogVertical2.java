package com.bioxx.tfc2.Blocks;

import java.util.Arrays;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Interfaces.INeedOffset;
import com.bioxx.tfc2.api.Types.WoodType;

public class BlockLogVertical2 extends BlockTerra implements INeedOffset
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));

	public BlockLogVertical2()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta(meta + 16));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta() & 15;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
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
	public int convertMeta(int meta) 
	{
		if(meta < 16)
			return meta + 16;
		else
			return meta;
	}
}
