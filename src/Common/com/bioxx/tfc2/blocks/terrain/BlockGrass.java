package com.bioxx.tfc2.blocks.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.blocks.BlockTerra;
import com.bioxx.tfc2.world.WorldGen;

public class BlockGrass extends BlockTerra
{
	public static final PropertyEnum META_PROPERTY = PropertyEnum.create("stone", StoneType.class);
	/** Whether this fence connects in the northern direction */
	public static final PropertyBool NORTH = PropertyBool.create("north");
	/** Whether this fence connects in the eastern direction */
	public static final PropertyBool EAST = PropertyBool.create("east");
	/** Whether this fence connects in the southern direction */
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	/** Whether this fence connects in the western direction */
	public static final PropertyBool WEST = PropertyBool.create("west");

	public BlockGrass()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, StoneType.Granite).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { META_PROPERTY, NORTH, SOUTH, EAST, WEST });
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public int tickRate(World worldIn)
	{
		return 3;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		Block block = world.getBlockState(pos.offsetUp()).getBlock();
		return state.withProperty(NORTH, world.getBlockState(pos.offsetNorth().offsetDown()).getBlock() == TFCBlocks.Grass).withProperty(
				SOUTH, world.getBlockState(pos.offsetSouth().offsetDown()).getBlock() == TFCBlocks.Grass).withProperty(
						EAST, world.getBlockState(pos.offsetEast().offsetDown()).getBlock() == TFCBlocks.Grass).withProperty(
								WEST, world.getBlockState(pos.offsetWest().offsetDown()).getBlock() == TFCBlocks.Grass);
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
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
	{
		int x = pos.getX() >> 12;
		int z = pos.getZ() >> 12;
		if(WorldGen.instance == null)
			return 0x55ff55;
		IslandMap m = WorldGen.instance.getIslandMap(x, z);
		double d0 = m.getParams().getIslandTemp().getTemp();
		double d1 = m.getClosestCenter(new Point(pos.getX(), pos.getZ()).toIslandCoord()).getMoistureRaw();
		return ColorizerGrass.getGrassColor(d0, d1);
		//return ColorizerGrass.getGrassColor(0.5, 1);
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
		return EnumWorldBlockLayer.CUTOUT_MIPPED;
	}

	/**
	 * Called when a plant grows on this block, only implemented for saplings using the WorldGen*Trees classes right now.
	 * Modder may implement this for custom plants.
	 * This does not use ForgeDirection, because large/huge trees can be located in non-representable direction,
	 * so the source location is specified.
	 * Currently this just changes the block to dirt if it was grass.
	 *
	 * Note: This happens DURING the generation, the generation may not be complete when this is called.
	 *
	 * @param world Current world
	 * @param pos Block position in world
	 * @param source Source plant's position in world
	 */
	@Override
	public void onPlantGrow(World world, BlockPos pos, BlockPos source)
	{
		IBlockState myState = world.getBlockState(pos);
		int meta = ((Integer)myState.getValue(BlockGrass.META_PROPERTY)).intValue();
		world.setBlockState(pos, TFCBlocks.Dirt.getDefaultState().withProperty(META_PROPERTY, meta), 2);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
	{
		IBlockState state = world.getBlockState(pos);
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if(plantable == TFCBlocks.Sapling)
			return true;
		if(plantable == TFCBlocks.Sapling2)
			return true;

		return false;
	}

}
