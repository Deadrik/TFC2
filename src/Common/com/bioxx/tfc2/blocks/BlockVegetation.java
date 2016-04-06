package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.world.WorldGen;

public class BlockVegetation extends BlockTerra implements IPlantable
{
	public static final PropertyEnum META_PROPERTY = PropertyEnum.create("veg", VegType.class);
	/** Whether this fence connects in the northern direction */
	public static final PropertyBool IS_ON_STONE = PropertyBool.create("isonstone");

	public BlockVegetation()
	{
		super(Material.vine, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, VegType.Grass0).withProperty(IS_ON_STONE, Boolean.valueOf(false)));
		float f = 0.35F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
		checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!canBlockStay(worldIn, pos, state))
		{
			dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.air.getDefaultState(), 3);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		BlockPos down = pos.down();
		IBlockState soil = worldIn.getBlockState(down);
		if (state.getBlock() != this) 
			return canPlaceBlockOn(state, soil);
		return soil.getBlock().canSustainPlant(worldIn, down, EnumFacing.UP, this);
	}

	protected boolean canPlaceBlockOn(IBlockState state, IBlockState soil)
	{
		VegType veg = (VegType)state.getValue(META_PROPERTY);

		if(veg == VegType.DeadBush)
			return Core.isTerrain(soil);

		return Core.isSoil(soil);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		IBlockState state = world.getBlockState(pos);
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		VegType veg = (VegType)state.getValue(META_PROPERTY);
		if(veg == VegType.DoubleGrassBottom && plant.getValue(META_PROPERTY) == VegType.DoubleGrassTop)
			return true;
		if(veg == VegType.DoubleFernBottom && plant.getValue(META_PROPERTY) == VegType.DoubleFernTop)
			return true;

		return false;
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
	{
		VegType veg = (VegType)worldIn.getBlockState(pos).getValue(META_PROPERTY);
		if(veg == VegType.DeadBush)
			return 0xD8D8D8;
		int x = pos.getX() >> 12;
		int z = pos.getZ() >> 12;
		if(WorldGen.instance == null)
			return 0x55ff55;
		IslandMap m = WorldGen.instance.getIslandMap(x, z);
		double d0 = m.getParams().getIslandTemp().getMapTemp();
		double d1 = 0.5;

		if(worldIn instanceof ChunkCache)
			d1 = Core.getMoistureFromChunk((ChunkCache)worldIn, pos);
		if(m.getParams().hasFeature(Feature.Desert))
			d1 *= 0.25;
		return ColorizerGrass.getGrassColor(d0, d1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IBlockState state)
	{
		return this.getBlockColor();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
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

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		Block block = world.getBlockState(pos.up()).getBlock();
		return state.withProperty(IS_ON_STONE, world.getBlockState(pos.down()).getBlock() == TFCBlocks.Stone);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, VegType.getTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((VegType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
	{
		return null;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { META_PROPERTY, IS_ON_STONE});
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	@Override
	public int tickRate(World worldIn)
	{
		return 3;
	}

	public enum VegType implements IStringSerializable
	{
		Grass0("grass0", 0),
		Grass1("grass1", 1),
		DeadBush("deadbush", 2),
		DoubleGrassBottom("doublegrassbottom", 3),
		DoubleGrassTop("doublegrasstop", 4),
		Fern("fern", 5),
		DoubleFernBottom("doublefernbottom", 6),
		DoubleFernTop("doubleferntop", 7);

		private String name;
		private int meta;

		VegType(String s, int id)
		{
			name = s;
			meta = id;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getMeta()
		{
			return meta;
		}

		public static VegType getTypeFromMeta(int meta)
		{
			for(int i = 0; i < VegType.values().length; i++)
			{
				if(VegType.values()[i].meta == meta)
					return VegType.values()[i];
			}
			return null;
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) 
	{
		if(world.getBlockState(pos).getValue(META_PROPERTY) == VegType.DeadBush)
			return EnumPlantType.Desert;
		return EnumPlantType.Plains;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) 
	{
		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) 
			return getDefaultState();
		return state;
	}
}
