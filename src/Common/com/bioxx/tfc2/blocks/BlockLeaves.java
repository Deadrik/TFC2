package com.bioxx.tfc2.blocks;

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

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.world.WorldGen;

public class BlockLeaves extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));
	public static PropertyBool IS_OUTER = PropertyBool.create("is_outer");
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
		return new BlockState(this, new IProperty[]{META_PROPERTY, IS_OUTER});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean outer = true;

		if(!isTransparent)
			outer = false;

		//North
		if(world.getBlockState(pos.north()).getBlock() != state.getBlock())
			outer = true;

		//South
		if(world.getBlockState(pos.south()).getBlock() != state.getBlock())
			outer = true;

		//East
		if(world.getBlockState(pos.east()).getBlock() != state.getBlock())
			outer = true;

		//West
		if(world.getBlockState(pos.west()).getBlock() != state.getBlock())
			outer = true;

		if(world.getBlockState(pos.up()).getBlock() != this)
			outer = true;
		if(world.getBlockState(pos.down()).getBlock() != this)
			outer = true;

		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			outer = false;

		return state.withProperty(IS_OUTER, outer);

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
		double d1 = m.getClosestCenter(new Point(pos.getX(), pos.getZ())).getMoistureRaw();

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
