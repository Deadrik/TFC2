package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockLeaves extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));
	public static PropertyBool FANCY = PropertyBool.create("fancy");
	private boolean isTransparent = true;

	public BlockLeaves()
	{
		super(Material.LEAVES, null);
		this.setCreativeTab(CreativeTabs.DECORATIONS);
		this.setHardness(0.2F);
		this.setLightOpacity(1);
		this.META_PROP = META_PROPERTY;
		setSoundType(SoundType.GROUND);
		this.setTickRandomly(true);
		this.setShowInCreative(false);
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
	public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion)
	{
		return motion.crossProduct(new Vec3d(0.75, 1, 0.75));
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
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;//!this.isTransparent;
	}

	@Override
	public boolean isVisuallyOpaque()
	{
		return false;
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
	public BlockRenderLayer getBlockLayer()
	{
		return this.isTransparent ? BlockRenderLayer.CUTOUT_MIPPED : BlockRenderLayer.SOLID;
	}

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY, FANCY});
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
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return NULL_AABB;
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
