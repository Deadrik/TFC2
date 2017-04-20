package com.bioxx.tfc2.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.tileentities.TilePitKiln;
import com.bioxx.tfc2.tileentities.TilePitKiln.ProcessEnum;

public class BlockPitKiln extends BlockTerra implements ITileEntityProvider
{
	public static PropertyInteger FILL = PropertyInteger.create("fill", 0, 4);
	public static PropertyEnum FILLTYPE = PropertyEnum.create("filltype", FillType.class);
	public static final PropertyItem INVENTORY = new PropertyItem();

	public BlockPitKiln()
	{
		super(Material.GROUND, FILL);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setTickRandomly(true);
		setSoundType(SoundType.STONE);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!world.isRemote && hand == EnumHand.MAIN_HAND)
		{
			int fill = state.getValue(FILL);
			if(player.getHeldItem(hand).getItem() == TFCItems.Straw)
			{
				if((state.getValue(FILLTYPE) == FillType.Straw && fill < 4) || fill == 0)
				{
					world.setBlockState(pos, state.withProperty(FILL, fill+1).withProperty(FILLTYPE, FillType.Straw));
					player.getHeldItem(hand).shrink(1);
					return true;
				}
			}
			else if(player.getHeldItem(hand).isEmpty())
			{
				if(state.getValue(FILLTYPE) == FillType.Straw && fill > 0)
				{
					Core.giveItem(world, player, pos, new ItemStack(TFCItems.Straw));
					world.setBlockState(pos, state.withProperty(FILL, fill-1));
					return true;
				}
				else if(fill == 0)
				{
					TilePitKiln te = (TilePitKiln) world.getTileEntity(pos);
					Core.giveItem(world, player, pos, te.getStackInSlot(0));
					te.setInventorySlotContents(0, ItemStack.EMPTY);
					world.setBlockToAir(pos);
				}
			}
		}
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		checkAndDropBlock((World) worldIn, pos, worldIn.getBlockState(pos));
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		if (!canBlockStay(worldIn, pos, state))
		{
			TilePitKiln te = (TilePitKiln) worldIn.getTileEntity(pos);
			int fill = state.getValue(FILL);
			if(state.getValue(BlockPitKiln.FILLTYPE) == FillType.Straw)
			{
				Core.dropItem(worldIn, pos, new ItemStack(TFCItems.Straw, fill));
			}
			else if(state.getValue(BlockPitKiln.FILLTYPE) == FillType.Charcoal)
			{
				Core.dropItem(worldIn, pos, new ItemStack(Items.COAL, fill, 1));
			}
			if(!te.getStackInSlot(0).isEmpty())
				Core.dropItem(worldIn, pos, te.getStackInSlot(0));

			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		BlockPos down = pos.down();
		IBlockState soil = worldIn.getBlockState(down);
		return Core.isTerrain(soil);
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
	{
		player.addStat(StatList.getBlockStats(this));
		player.addExhaustion(0.005F);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
	{
		if(state.getValue(FILL) > 0 && state.getValue(FILLTYPE) == FillType.Charcoal && 
				ForgeHooks.isToolEffective(worldIn, pos, player.getHeldItemMainhand()))
		{
			Core.giveItem(worldIn, player, pos, new ItemStack(Items.COAL, 1, 1));
			worldIn.setBlockState(pos, state.withProperty(FILL, state.getValue(FILL)-1));
		}
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		this.onBlockHarvested(world, pos, state, player);
		if(state.getValue(FILLTYPE) == FillType.Straw || state.getValue(FILL) > 0)
			return false;
		else if(state.getValue(FILL) == 0)
		{
			return false;
		}
		return world.setBlockState(pos, net.minecraft.init.Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		TilePitKiln te = (TilePitKiln)world.getTileEntity(pos);
		if(te.recentCraftResult.result == ProcessEnum.WORKING)
		{
			double x = 0;
			double z = 0;
			double y = 1;

			if(!world.isAirBlock(pos.up()))
				y = 2;
			if(!world.isAirBlock(pos.up(2)))
				y = 3;

			if(y < 3)
			{
				x = rand.nextDouble() * 0.7;
				z = rand.nextDouble() * 0.7;
				world.spawnParticle(EnumParticleTypes.FLAME, pos.getX()+0.15+x, pos.getY()+y, pos.getZ()+0.15+z, 0, 0.02, 0);
				x = rand.nextDouble() * 0.7;
				z = rand.nextDouble() * 0.7;
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX()+0.15+x, pos.getY()+y, pos.getZ()+0.15+z, 0, 0.02, 0);
			}
			x = rand.nextDouble() * 0.7;
			z = rand.nextDouble() * 0.7;
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX()+0.15+x, pos.getY()+y, pos.getZ()+0.15+z, 0, 0.02, 0);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		int fill = state.getValue(FILL);
		return new AxisAlignedBB(0,0,0,1,0.25*fill,1);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		int fill = state.getValue(FILL);
		return new AxisAlignedBB(0,0,0,1,0.25*fill,1);
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


	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TilePitKiln te = (TilePitKiln) world.getTileEntity(pos);
		if(te != null)
			return te.writeExtendedBlockState((IExtendedBlockState) state);
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[]{FILL, FILLTYPE}, new IUnlistedProperty[]{INVENTORY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FILL, meta & 7).withProperty(FILLTYPE, FillType.values()[meta >> 3]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FILL) + ((state.getValue(FILLTYPE) == FillType.Straw ? 0 : 1) << 3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TilePitKiln();
	}

	@Override
	public Item getItemDropped(IBlockState paramIBlockState, Random paramRandom, int paramInt)
	{
		return null;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	public static enum FillType implements IStringSerializable
	{
		Straw("straw"), Charcoal("charcoal");

		String name;

		FillType(String s)
		{
			name = s;
		}


		@Override
		public String getName() {
			return name;
		}
	}

}
