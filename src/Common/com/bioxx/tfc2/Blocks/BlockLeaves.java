package com.bioxx.tfc2.Blocks;

import java.util.Arrays;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.Point;
import com.bioxx.tfc2.World.WorldGen;
import com.bioxx.tfc2.api.Types.WoodType;

public class BlockLeaves extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));
	public static PropertyBool IS_OUTER = PropertyBool.create("is_outer");
	public static PropertyBool EAST = PropertyBool.create("east");
	public static PropertyBool WEST = PropertyBool.create("west");
	public static PropertyBool NORTH = PropertyBool.create("north");
	public static PropertyBool SOUTH = PropertyBool.create("south");
	public static PropertyBool EAST_DOWN = PropertyBool.create("downeast");
	public static PropertyBool WEST_DOWN = PropertyBool.create("downwest");
	public static PropertyBool NORTH_DOWN = PropertyBool.create("downnorth");
	public static PropertyBool SOUTH_DOWN = PropertyBool.create("downsouth");
	private boolean isTransparent = true;

	public BlockLeaves()
	{
		super(Material.leaves, null);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.META_PROP = META_PROPERTY;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY, IS_OUTER, EAST, NORTH, SOUTH, WEST, EAST_DOWN, NORTH_DOWN, SOUTH_DOWN, WEST_DOWN});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean outer = true;
		boolean east = false;
		boolean west = false;
		boolean north = false;
		boolean south = false;
		boolean east_down = false;
		boolean west_down = false;
		boolean north_down = false;
		boolean south_down = false;

		if(!isTransparent)
			outer = false;

		//North
		if(world.getBlockState(pos.offsetNorth()).getBlock() != state.getBlock())
		{
			north = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetNorth().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetNorth().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			north_down = true;
			north = true;
		}

		//South
		if(world.getBlockState(pos.offsetSouth()).getBlock() != state.getBlock())
		{
			south = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetSouth().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetSouth().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			south_down = true;
			south = true;
		}

		//East
		if(world.getBlockState(pos.offsetEast()).getBlock() != state.getBlock())
		{
			east = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetEast().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetEast().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			east = true;
			east_down = true;
		}

		//West
		if(world.getBlockState(pos.offsetWest()).getBlock() != state.getBlock())
		{
			west = true;
			outer = true;
		}

		if((world.getBlockState(pos.offsetWest().offsetDown()).getBlock() == state.getBlock() && 
				world.getBlockState(pos.offsetWest().offsetDown()).getValue(META_PROPERTY) == state.getValue(META_PROPERTY)))
		{
			west = true;
			west_down = true;
		}

		if(world.getBlockState(pos.offsetUp()).getBlock() != this)
			outer = true;
		if(world.getBlockState(pos.offsetDown()).getBlock() != this)
			outer = true;

		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			outer = false;

		return state.withProperty(IS_OUTER, outer).withProperty(EAST, east).withProperty(WEST, west).withProperty(NORTH, north).withProperty(SOUTH, south).
				withProperty(EAST_DOWN, east_down).withProperty(WEST_DOWN, west_down).withProperty(NORTH_DOWN, north_down).withProperty(SOUTH_DOWN, south_down);

	}

	@Override
	public boolean isOpaqueCube()
	{
		return !this.isTransparent;
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
		double d1 = m.getSelectedHexagon(new Point(pos.getX(), pos.getZ())).moisture;

		if(d1 < 0.25)
		{
			IBlockState state = worldIn.getBlockState(pos);
			if(state.getValue(META_PROPERTY) == WoodType.Acacia)
				d1 = 0.25;
		}

		return ColorizerGrass.getGrassColor(d0, d1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(IBlockState state)
	{
		return this.getBlockColor();
	}

	/**
	 * Pass true to draw this block using fancy graphics, or false for fast graphics.
	 */
	@SideOnly(Side.CLIENT)
	public void setGraphicsLevel(boolean p_150122_1_)
	{
		this.isTransparent = p_150122_1_;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer()
	{
		return this.isTransparent ? EnumWorldBlockLayer.CUTOUT_MIPPED : EnumWorldBlockLayer.SOLID;
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
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}
}
