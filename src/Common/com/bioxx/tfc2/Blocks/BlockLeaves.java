package com.bioxx.tfc2.Blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jMapGen.Map;
import com.bioxx.jMapGen.Point;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.World.WorldGen;
import com.bioxx.tfc2.api.Types.WoodType;

public class BlockLeaves extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class);
	private boolean isTransparent = true;

	public BlockLeaves()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		//this.setShowInCreative(false);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY});
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
		Map m = WorldGen.instance.getIslandMap(x, z);
		double d0 = m.islandParams.getIslandTemp().getTemp();
		double d1 = m.getSelectedHexagon(new Point(pos.getX(), pos.getZ())).moisture;
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
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(TFCBlocks.LogVertical);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}
}
