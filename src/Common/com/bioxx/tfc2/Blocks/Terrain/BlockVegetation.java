package com.bioxx.tfc2.Blocks.Terrain;

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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.Blocks.BlockTerra;

public class BlockVegetation extends BlockTerra
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
		float f = 0.4F;
		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] { META_PROPERTY, IS_ON_STONE});
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
		return state.withProperty(IS_ON_STONE, world.getBlockState(pos.offsetDown()).getBlock() == TFCBlocks.Stone);
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
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return ColorizerGrass.getGrassColor(0.5D, 1.0D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass)
	{
		return worldIn.getBiomeGenForCoords(pos).func_180627_b(pos);
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
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
	{
		return null;
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

	public enum VegType implements IStringSerializable
	{
		Grass0("grass", 0);

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
}
