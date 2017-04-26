package com.bioxx.tfc2.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;
import com.bioxx.tfc2.core.TFCTabs;

public class BlockThatch extends BlockCollapsible
{
	public BlockThatch()
	{
		super(Material.GRASS, null);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setSoundType(SoundType.PLANT);
		this.lightOpacity = 255;
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos,IBlockState myState)
	{
		return 6;
	}

	@Override
	public void createFallingEntity(World world, BlockPos pos, IBlockState state)
	{
		world.setBlockToAir(pos);
		EntityItem ei = new EntityItem(world, pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, new ItemStack(TFCItems.Straw, 1+world.rand.nextInt(3)));
		world.spawnEntity(ei);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(!world.isRemote && pos.up().equals(fromPos) && world.isAirBlock(pos.down()))
		{
			if(world.getBlockState(fromPos).getBlock() instanceof BlockGravity)
				world.destroyBlock(pos, true);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		entityIn.motionX *= 0.1D;
		entityIn.motionZ *= 0.1D;
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		if(side == EnumFacing.UP || side == EnumFacing.DOWN)
			return false;
		return true;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/
	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return null;
	}

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
}
