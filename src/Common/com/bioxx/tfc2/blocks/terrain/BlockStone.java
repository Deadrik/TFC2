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

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.StoneType;

public class BlockStone extends BlockCollapsable
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

		if(stateUp.getBlock() != this && stateDown.getBlock() != this && stateNorth.getBlock() != this
				&& stateSouth.getBlock() != this && stateEast.getBlock() != this && stateWest.getBlock() != this)
		{
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
		else
		{
			super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
		}
	}

	@Override
	public IBlockState getFallBlockType(IBlockState myState)
	{
		return TFCBlocks.Rubble.getDefaultState().withProperty(BlockRubble.META_PROPERTY, myState.getValue(META_PROPERTY));
	}

	@Override
	public int getNaturalSupportRange(IBlockState myState)
	{
		StoneType stone = myState.getValue(BlockStone.META_PROPERTY);
		switch(stone)
		{
		case Granite:
		case Gabbro:
		case Diorite:
			return 7;
		case Andesite:
		case Basalt:
		case Dacite:
		case Rhyolite:
			return 6;
		case Gneiss:
		case Blueschist:
		case Marble:
		case Schist:
			return 5;
		case Chert:
		case Claystone:
		case Dolomite:
		case Limestone:
		case Shale:
			return 4;
		}
		return 5;
	}

	@Override
	public boolean canSupport(IBlockState myState, IBlockState otherState)
	{
		if(otherState.getBlock() == this)
			return true;
		return false;
	}
}
