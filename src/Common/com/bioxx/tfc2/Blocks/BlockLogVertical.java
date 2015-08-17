package com.bioxx.tfc2.blocks;

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
import com.bioxx.tfc2.api.types.WoodType;

public class BlockLogVertical extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));

	public BlockLogVertical()
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
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
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
				if(meta < 8)
				{
					return TFCBlocks.LogHorizontal.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 1);
				}
				else
				{
					return TFCBlocks.LogHorizontal2.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 1);
				}
			}
			else
			{
				if(meta < 8)
				{
					return TFCBlocks.LogHorizontal.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 0);
				}
				else
				{
					return TFCBlocks.LogHorizontal2.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 0);
				}
			}
		}
	}
}
