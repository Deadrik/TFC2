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
import net.minecraft.util.EnumParticleTypes;
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
		setSoundType(SoundType.PLANT);
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
	{
		worldIn.scheduleUpdate(pos, this, tickRate(worldIn));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(world.isRemote)
			return;
		IBlockState scanState;
		WoodType wood = (WoodType)state.getValue(META_PROPERTY);
		BlockPos scanPos;
		if (world.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5)))
		{
			for(int y = -4; y <= 4; y++)
			{
				for(int x = -4; x <= 4; x++)
				{
					for(int z = -4; z <= 4; z++)
					{
						scanPos = pos.add(x, y, z);
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

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		if ((worldIn.isRainingAt(pos.up())) && (!worldIn.getBlockState(pos.down()).isFullyOpaque()) && (rand.nextInt(15) == 1))
		{
			double d0 = pos.getX() + rand.nextFloat();
			double d1 = pos.getY() - 0.05D;
			double d2 = pos.getZ() + rand.nextFloat();
			worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		}
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
		if(state.getValue(META_PROPERTY) == WoodType.Palm)
			return state.withProperty(FANCY, false);

		return state.withProperty(FANCY, isTransparent);

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
