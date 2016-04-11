package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.world.WorldGen;

public class BlockLeaves extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));
	public static PropertyBool FANCY = PropertyBool.create("fancy");
	private boolean isTransparent = true;

	public BlockLeaves()
	{
		super(Material.leaves, null);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.META_PROP = META_PROPERTY;
		this.setTickRandomly(true);
	}
	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	@Override
	public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion)
	{
		return motion.crossProduct(new Vec3(0.75, 1, 0.75));
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return null;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		world.scheduleUpdate(pos, this, tickRate(world));
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		world.scheduleUpdate(pos, this, tickRate(world));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(world.isRemote)
			return;
		IBlockState scanState;
		WoodType wood = (WoodType)state.getValue(META_PROPERTY);
		for(int y = -5; y <= 5; y++)
		{
			for(int x = -5; x <= 5; x++)
			{
				for(int z = -5; z <= 5; z++)
				{
					scanState = world.getBlockState(pos.add(x, y, z));
					if(Core.isNaturalLog(scanState) && scanState.getValue(META_PROPERTY) == wood)
						return;
				}
			}
		}
		world.scheduleUpdate(pos.north(), this, tickRate(world));
		world.scheduleUpdate(pos.south(), this, tickRate(world));
		world.scheduleUpdate(pos.east(), this, tickRate(world));
		world.scheduleUpdate(pos.west(), this, tickRate(world));
		world.setBlockToAir(pos);
	}

	@Override
	public int tickRate(World worldIn)
	{
		return 5;
	}

	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/

	@Override
	public boolean isOpaqueCube()
	{
		return false;//!this.isTransparent;
	}

	@Override
	public boolean isVisuallyOpaque()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor()
	{
		return ColorizerFoliage.getFoliageColor(0.5D, 1.0D);
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
		double d0 = m.getParams().getIslandTemp().getMapTemp();
		double d1 = 0.5;

		if(worldIn instanceof ChunkCache)
			d1 = Core.getMoistureFromChunk((ChunkCache)worldIn, pos);

		if(m.getParams().hasFeature(Feature.Desert))
			d1 *= 0.25;

		if(d1 < 0.25)
		{
			IBlockState state = worldIn.getBlockState(pos);
			if(state.getValue(META_PROPERTY) == WoodType.Acacia)
				d1 = 0.25;
		}
		return ColorizerFoliage.getFoliageColor(d0, d1);
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

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/
	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY, FANCY});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		boolean fancy = true;

		if(!isTransparent)
			fancy = false;

		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			fancy = false;

		return state.withProperty(FANCY, fancy);

	}



	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity)
	{

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
