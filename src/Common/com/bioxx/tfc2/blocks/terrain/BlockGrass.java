package com.bioxx.tfc2.blocks.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.world.WorldGen;

public class BlockGrass extends BlockCollapsible
{
	public static final PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool SPARSE = PropertyBool.create("sparse");

	public BlockGrass()
	{
		super(Material.GROUND, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		setSoundType(SoundType.GROUND);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, StoneType.Granite).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(SPARSE, false));
		this.collapseType = CollapsibleType.Nature;
		this.setShowInCreative(false);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(world.isRemote)
			return;

		if(world.getBlockState(pos.north()).getBlock() == TFCBlocks.Dirt && world.isAirBlock(pos.north().up()) && rand.nextInt(10) == 0)
		{
			world.setBlockState(pos.north(), this.getDefaultState().withProperty(META_PROPERTY, world.getBlockState(pos.north()).getValue(BlockDirt.META_PROPERTY)));
		}

		if(world.getBlockState(pos.south()).getBlock() == TFCBlocks.Dirt && world.isAirBlock(pos.south().up()) && rand.nextInt(10) == 0)
		{
			world.setBlockState(pos.south(), this.getDefaultState().withProperty(META_PROPERTY, world.getBlockState(pos.south()).getValue(BlockDirt.META_PROPERTY)));
		}

		if(world.getBlockState(pos.east()).getBlock() == TFCBlocks.Dirt && world.isAirBlock(pos.east().up()) && rand.nextInt(10) == 0)
		{
			world.setBlockState(pos.east(), this.getDefaultState().withProperty(META_PROPERTY, world.getBlockState(pos.east()).getValue(BlockDirt.META_PROPERTY)));
		}

		if(world.getBlockState(pos.west()).getBlock() == TFCBlocks.Dirt && world.isAirBlock(pos.west().up()) && rand.nextInt(10) == 0)
		{
			world.setBlockState(pos.west(), this.getDefaultState().withProperty(META_PROPERTY, world.getBlockState(pos.west()).getValue(BlockDirt.META_PROPERTY)));
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		((World)worldIn).scheduleUpdate(pos, this, tickRate((World)worldIn));
	}

	@Override
	public void onPlantGrow(IBlockState state, World world, BlockPos pos, BlockPos source)
	{
		StoneType stone = (StoneType)state.getValue(BlockGrass.META_PROPERTY);
		world.setBlockState(pos, TFCBlocks.Dirt.getDefaultState().withProperty(META_PROPERTY, stone), 2);
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if(plant.getBlock() == Blocks.SAPLING)
			return false;//This may break some cross mod compatability but for now its needed to prevent vanilla and some pam trees from generating

		if(plantType == EnumPlantType.Plains)
			return true;

		if(plant.getBlock() == TFCBlocks.VegDesert)
			return true;
		return false;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(TFCBlocks.Dirt);
	}

	@Override
	public int tickRate(World worldIn)
	{
		return 3;
	}

	@Override
	public IBlockState getFallBlockType(IBlockState myState)
	{
		return TFCBlocks.Dirt.getDefaultState().withProperty(BlockRubble.META_PROPERTY, myState.getValue(META_PROPERTY));
	}

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos, IBlockState myState)
	{
		return 3;
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/


	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] { META_PROPERTY, NORTH, SOUTH, EAST, WEST, SPARSE });
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		Block block = world.getBlockState(pos.up()).getBlock();
		IBlockState out = state.withProperty(NORTH, world.getBlockState(pos.north().down()).getBlock() == TFCBlocks.Grass).withProperty(
				SOUTH, world.getBlockState(pos.south().down()).getBlock() == TFCBlocks.Grass).withProperty(
						EAST, world.getBlockState(pos.east().down()).getBlock() == TFCBlocks.Grass).withProperty(
								WEST, world.getBlockState(pos.west().down()).getBlock() == TFCBlocks.Grass);
		IslandMap map = WorldGen.getInstance().getIslandMap(pos.getX() >> 12, pos.getZ() >> 12);
		if(map.getParams().getIslandMoisture().equals(Moisture.LOW) &&
				!map.getClosestCenter(pos).getMoisture().equals(Moisture.MAX))
			out = out.withProperty(SPARSE, true);
		return out;
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

}
