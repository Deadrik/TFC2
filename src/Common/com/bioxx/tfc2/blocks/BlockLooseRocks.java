package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.TFCTabs;

public class BlockLooseRocks extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockLooseRocks()
	{
		super(Material.GROUND, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setBlockBounds(0.2f, 0, 0.2f, 0.8f, 0.1f, 0.8f);
		setSoundType(SoundType.STONE);
		setShowInCreative(false);
		this.setBreaksWhenSuspended(true);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return true;
		((World)worldIn).setBlockToAir(pos);
		Core.dropItem(worldIn, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, new ItemStack(getItemDropped(state, worldIn.rand, 0), 1, this.getMetaFromState(state)));
		return true;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

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

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(0.2, 0, 0.2, 0.8, 0.1, 0.8);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
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
