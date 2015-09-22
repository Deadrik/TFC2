package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockLeaves2 extends BlockLeaves
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 16, 19));

	private boolean isTransparent = true;

	public BlockLeaves2()
	{
		super();
		this.META_PROP = META_PROPERTY;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[]{META_PROPERTY, BlockLeaves.FANCY}, new IUnlistedProperty[]{ B3DLoader.B3DFrameProperty.instance });
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean fancy = true;
		if(!isTransparent)
			fancy = false;
		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			fancy = false;

		return state.withProperty(BlockLeaves.FANCY, fancy);
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) 
	{
		if(state.getValue(META_PROPERTY) == WoodType.Palm)
		{
			B3DLoader.B3DState newState = new B3DLoader.B3DState(null, 1);
			return ((IExtendedBlockState) state).withProperty(B3DLoader.B3DFrameProperty.instance, newState);
		}

		return state;
	}

	private boolean isSameLeaf(IBlockAccess world, BlockPos pos, IBlockState state)
	{
		if((world.getBlockState(pos).getBlock() == state.getBlock() && 
				world.getBlockState(pos).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			return true;
		}
		return false;
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		for(int l = 16; l < 19; l++)
			list.add(new ItemStack(itemIn, 1, l));
	}

	@Override
	public int quantityDropped(IBlockState state, int i, Random random)
	{
		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			return 0;
		return 1;
	}

	@Override
	public float getBlockHardness(World worldIn, BlockPos pos)
	{
		if(worldIn.getBlockState(pos).getValue(META_PROPERTY) == WoodType.Palm)
			return -1.0f;
		return this.blockHardness;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) 
	{
		if(worldIn.getBlockState(pos).getValue(META_PROPERTY) == WoodType.Palm && worldIn.getBlockState(pos.down()).getBlock() != TFCBlocks.LogNatural2)
		{
			worldIn.setBlockState(pos, Blocks.air.getDefaultState());
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta((meta & 15) + 16));
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
}
