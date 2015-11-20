package com.bioxx.tfc2.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.types.StoneType;

public class BlockLooseRocks extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockLooseRocks()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setBlockBounds(0.2f, 0, 0.2f, 0.8f, 0.1f, 0.8f);
		setShowInCreative(false);
	}

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

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
	{

	}

	@Override
	public Item getItemDropped(IBlockState paramIBlockState, Random paramRandom, int paramInt)
	{
		return TFCItems.LooseRock;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return getMetaFromState(state);
	}
}
