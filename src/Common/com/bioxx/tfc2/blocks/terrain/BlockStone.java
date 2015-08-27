package com.bioxx.tfc2.blocks.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.blocks.BlockTerra;

public class BlockStone extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);

	public BlockStone()
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
		return this.getDefaultState().withProperty(META_PROPERTY, StoneType.getStoneTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((StoneType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) 
	{
		IBlockState stateUp = worldIn.getBlockState(pos.up());
		IBlockState stateDown = worldIn.getBlockState(pos.down());
		IBlockState stateNorth = worldIn.getBlockState(pos.north());
		IBlockState stateSouth = worldIn.getBlockState(pos.south());
		IBlockState stateEast = worldIn.getBlockState(pos.east());
		IBlockState stateWest = worldIn.getBlockState(pos.west());

		if(!stateUp.getBlock().isSolidFullCube() && !stateDown.getBlock().isSolidFullCube() && !stateNorth.getBlock().isSolidFullCube()
				&& !stateSouth.getBlock().isSolidFullCube() && !stateEast.getBlock().isSolidFullCube() && !stateWest.getBlock().isSolidFullCube())
		{
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}
}
