package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockLeaves extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));
	public static PropertyBool FANCY = PropertyBool.create("fancy");
	private boolean isTransparent = true;

	public BlockLeaves()
	{
		super(Material.LEAVES, null);
		this.setCreativeTab(null);
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
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList list)
	{
		for(int l = 0; l < 16; l++)
			list.add(new ItemStack(itemIn, 1, l));
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	@Override
	public net.minecraft.pathfinding.PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return PathNodeType.OPEN;
	}

	@Override
	public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion)
	{
		return motion.crossProduct(new Vec3d(0.75, 1, 0.75));
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
		ret.add(new ItemStack(Items.STICK, 1, 0));
		return ret;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		world.scheduleUpdate(pos, this, tickRate(world));
	}

	@Override
	public void onNeighborChange(IBlockAccess worldIn, BlockPos pos, BlockPos blockIn)
	{
		((World)worldIn).scheduleUpdate(pos, this, tickRate((World)worldIn));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if(world.isRemote || state.getBlock() != this)
			return;
		IBlockState scanState;
		WoodType wood = (WoodType)state.getValue(getMetaProperty());
		BlockPos scanPos;

		if(wood == WoodType.Palm)
		{
			scanState = world.getBlockState(pos.down());
			if(scanState.getBlock() != TFCBlocks.LogNaturalPalm)
				world.setBlockToAir(pos);
			return;
		}

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
						if((state.getBlock() == TFCBlocks.Leaves && scanState.getBlock() == TFCBlocks.LogNatural && scanState.getValue(BlockLogNatural.WOOD) == wood) ||
								(state.getBlock() == TFCBlocks.Leaves2 && scanState.getBlock() == TFCBlocks.LogNatural2 && scanState.getValue(BlockLogNatural2.WOOD) == wood))
							return;
					}
				}
			}

			for(int y = -1; y <= 1; y++)
			{
				for(int x = -1; x <= 1; x++)
				{
					for(int z = -1; z <= 1; z++)
					{
						world.scheduleUpdate(pos.add(x, y, z), this, tickRate(world));
					}
				}
			}
			world.setBlockToAir(pos);
		}
	}

	protected IProperty getMetaProperty()
	{
		return META_PROPERTY;
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
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
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
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
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
