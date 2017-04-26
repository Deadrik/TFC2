package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.interfaces.INeedOffset;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;
import com.bioxx.tfc2.core.TFCTabs;

public class BlockWoodSupport extends BlockCollapsible implements ISupportBlock, INeedOffset
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 8));
	public static PropertyBool SPAN = PropertyBool.create("isspan");
	public static PropertyBool NORTH_CONNECTION = PropertyBool.create("north");
	public static PropertyBool SOUTH_CONNECTION = PropertyBool.create("south");
	public static PropertyBool EAST_CONNECTION = PropertyBool.create("east");
	public static PropertyBool WEST_CONNECTION = PropertyBool.create("west");

	public BlockWoodSupport() 
	{
		this(Material.WOOD, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROPERTY, WoodType.Oak).
				withProperty(SPAN, Boolean.valueOf(false)).
				withProperty(NORTH_CONNECTION, Boolean.valueOf(false)).
				withProperty(EAST_CONNECTION, Boolean.valueOf(false)).
				withProperty(SOUTH_CONNECTION, Boolean.valueOf(false)).
				withProperty(WEST_CONNECTION, Boolean.valueOf(false)));
	}

	protected BlockWoodSupport(Material material, PropertyHelper meta)
	{
		super(material, meta);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		compressionBreak = true;
		this.collapseType = CollapsibleType.Structure;
		setSoundType(SoundType.WOOD);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos, IBlockState myState)
	{
		return ((WoodType)myState.getValue(META_PROPERTY)).getSupportRange();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public void createFallingEntity(World world, BlockPos pos, IBlockState state)
	{
		world.setBlockToAir(pos);
		EntityItem ei = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.STICK, 1+world.rand.nextInt(3)));
		world.spawnEntity(ei);
	}

	@Override
	public boolean recievesHorizontalSupport(IBlockState myState, IBlockAccess world, BlockPos pos, EnumFacing facing)
	{
		if(myState.getBlock() instanceof BlockWoodSupport)
		{
			myState = getActualState(myState, world, pos);
			if((Boolean)myState.getValue(SPAN))
			{
				if(facing == EnumFacing.NORTH && (Boolean)myState.getValue(NORTH_CONNECTION))
					return true;
				if(facing == EnumFacing.SOUTH && (Boolean)myState.getValue(SOUTH_CONNECTION))
					return true;
				if(facing == EnumFacing.EAST && (Boolean)myState.getValue(EAST_CONNECTION))
					return true;
				if(facing == EnumFacing.WEST && (Boolean)myState.getValue(WEST_CONNECTION))
					return true;
			}
			return false;
		}
		else return super.recievesHorizontalSupport(myState, world, pos, facing);
	}

	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		if(side == EnumFacing.UP)
			return true;

		if((Boolean)state.getValue(NORTH_CONNECTION))
			return true;
		if((Boolean)state.getValue(SOUTH_CONNECTION))
			return true;
		if((Boolean)state.getValue(EAST_CONNECTION))
			return true;
		if((Boolean)state.getValue(WEST_CONNECTION))
			return true;

		return false;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) 
	{
		super.onBlockAdded(world, pos, state);
		state = getActualState(state, world, pos);
		if(world.isRemote || !(Boolean)state.getValue(SPAN))
			return;

		//Here we do our checks to make sure that this beam is not too long.
		int range = getNaturalSupportRange(world, pos, state);
		boolean foundColumn = false;
		IBlockState otherState;

		if(!foundColumn && (Boolean)state.getValue(NORTH_CONNECTION))
		{
			for(int i = 1; i <= range; i++)
			{
				otherState = world.getBlockState(pos.north(i));
				if(otherState.getBlock() instanceof BlockWoodSupport)
				{
					otherState = otherState.getBlock().getActualState(otherState, world, pos.north(i));
					if(!(Boolean)otherState.getValue(SPAN))
					{foundColumn = true; break;}
				}
			}
		}
		if(!foundColumn && (Boolean)state.getValue(SOUTH_CONNECTION))
		{
			for(int i = 1; i <= range; i++)
			{
				otherState = world.getBlockState(pos.south(i));
				if(otherState.getBlock() instanceof BlockWoodSupport)
				{
					otherState = otherState.getBlock().getActualState(otherState, world, pos.south(i));
					if(!(Boolean)otherState.getValue(SPAN))
					{foundColumn = true; break;}
				}
			}
		}

		if(!foundColumn && (Boolean)state.getValue(EAST_CONNECTION))
		{
			for(int i = 1; i <= range; i++)
			{
				otherState = world.getBlockState(pos.east(i));
				if(otherState.getBlock() instanceof BlockWoodSupport)
				{
					otherState = otherState.getBlock().getActualState(otherState, world, pos.east(i));
					if(!(Boolean)otherState.getValue(SPAN))
					{foundColumn = true; break;}
				}
			}
		}

		if(!foundColumn && (Boolean)state.getValue(WEST_CONNECTION))
		{
			for(int i = 1; i <= range; i++)
			{
				otherState = world.getBlockState(pos.west(i));
				if(otherState.getBlock() instanceof BlockWoodSupport)
				{
					otherState = otherState.getBlock().getActualState(otherState, world, pos.west(i));
					if(!(Boolean)otherState.getValue(SPAN))
					{foundColumn = true; break;}
				}
			}
		}


		if(!foundColumn)
		{
			world.setBlockToAir(pos);
			int meta = this.getMetaFromState(state);
			Core.dropItem(world, pos, new ItemStack(this.getItemDropped(state, world.rand, 0), 1, convertMetaToItem(meta)));
		}

	}

	public boolean canStay(World world, BlockPos pos, IBlockState state)
	{
		return false;
	}

	@Override
	public int convertMetaToBlock(int meta) 
	{
		return meta & 7;
	}

	@Override
	public int convertMetaToItem(int meta) 
	{
		return meta & 7;
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos blockIn)
	{
		IBlockState state = getActualState(world.getBlockState(pos), world, pos);
		if(state.getValue(SPAN))
		{
			if(state.getValue(EAST_CONNECTION) != state.getValue(WEST_CONNECTION) || state.getValue(NORTH_CONNECTION) != state.getValue(SOUTH_CONNECTION))
			{
				this.createFallingEntity((World) world, pos, state);
				return;
			}
			else if(!state.getValue(EAST_CONNECTION) && !state.getValue(WEST_CONNECTION) && !state.getValue(NORTH_CONNECTION) && !state.getValue(SOUTH_CONNECTION))
			{
				this.createFallingEntity((World) world, pos, state);
				return;
			}
		}
		super.onNeighborChange(world, pos, blockIn);
	}

	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/
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
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return state.withProperty(NORTH_CONNECTION, world.getBlockState(pos.north()).getBlock() instanceof BlockWoodSupport).
				withProperty(SOUTH_CONNECTION, world.getBlockState(pos.south()).getBlock() instanceof BlockWoodSupport).
				withProperty(EAST_CONNECTION, world.getBlockState(pos.east()).getBlock() instanceof BlockWoodSupport).
				withProperty(WEST_CONNECTION, world.getBlockState(pos.west()).getBlock() instanceof BlockWoodSupport).
				withProperty(SPAN, !canBeSupportedBy(state, world.getBlockState(pos.down())));
	}
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY, SPAN, NORTH_CONNECTION, SOUTH_CONNECTION, EAST_CONNECTION, WEST_CONNECTION});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta((meta & 7))).withProperty(SPAN, (meta & 8) == 0 ? false : true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta() + ((Boolean)state.getValue(SPAN) ? 8 : 0);
	}
}
