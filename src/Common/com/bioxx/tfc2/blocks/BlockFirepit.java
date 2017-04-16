package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.tileentities.TileFirepit;

public class BlockFirepit extends BlockTerra implements ITileEntityProvider
{
	public static final PropertyBool LIT = PropertyBool.create("lit");
	public static final PropertyEnum TOOL = PropertyEnum.create("tool", CookingTool.class);

	@ObjectHolder("harvestcraft:potItem")
	public static final Item potItem = null;
	@ObjectHolder("harvestcraft:skilletItem")
	public static final Item skilletItem = null;
	@ObjectHolder("harvestcraft:saucepanItem")
	public static final Item saucepanItem = null;


	public BlockFirepit()
	{
		super(Material.GRASS, LIT);
		this.setCreativeTab(null);
		this.isBlockContainer = true;
		setSoundType(SoundType.GROUND);
		this.setBlockBounds(0, 0, 0, 1, 0.3, 1);
		this.setBreaksWhenSuspended(true);
		this.setLightLevel(0.8f);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, 
			net.minecraft.util.EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		TileFirepit te = (TileFirepit)world.getTileEntity(pos);
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(!world.isRemote)
		{

			if(playerIn.isSneaking() && heldItem == ItemStack.EMPTY && te.hasCookingTool())
			{
				playerIn.inventory.addItemStackToInventory(te.getCookingTool());
				te.setCookingTool(ItemStack.EMPTY);
				te.ejectContents(false);
			}
			else if(heldItem.getItem() == potItem || heldItem.getItem() == skilletItem || heldItem.getItem() == saucepanItem)
			{
				if(!te.hasCookingTool())
				{
					te.setCookingTool(heldItem);
					playerIn.setHeldItem(hand, ItemStack.EMPTY);
				}
			}
			else if(heldItem.getItem() == TFCItems.Firestarter)
			{
				return false;
			}
			else if(te.hasCookingTool())
				playerIn.openGui(TFC.instance, 6, world, pos.getX(), pos.getY(), pos.getZ());
			else
				playerIn.openGui(TFC.instance, 5, world, pos.getX(), pos.getY(), pos.getZ());
		}

		//This should occur clientside
		if(heldItem.getItem() == TFCItems.Firestarter)
		{
			return false;
		}
		return true;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		IBlockState soil = worldIn.getBlockState(pos.down());
		return soil.getBlock().isSideSolid(soil, worldIn, pos.down(), EnumFacing.UP);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tileentity = worldIn.getTileEntity(pos);

		if (tileentity instanceof IInventory)
		{
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public Item getItemDropped(IBlockState paramIBlockState, Random paramRandom, int paramInt)
	{
		return null;//The firepit shouldn't drop itself as an item
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if(state.getValue(LIT) == true)
		{
			return 10;
		}
		return 0;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldObj, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if(state.getValue(LIT))
			entityIn.attackEntityFrom(DamageSource.IN_FIRE, 0.25F);
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		TileFirepit te = (TileFirepit)world.getTileEntity(pos);
		if(te.getField(TileFirepit.FIELD_FUEL_TIMER) > 0)
		{
			double x = rand.nextDouble() * 0.7;
			double z = rand.nextDouble() * 0.7;
			world.spawnParticle(EnumParticleTypes.FLAME, pos.getX()+0.15+x, pos.getY()+0.4, pos.getZ()+0.15+z, 0, 0.0, 0);
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX()+0.15+x, pos.getY()+0.4, pos.getZ()+0.15+z, 0, 0.02, 0);
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
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[]{LIT, TOOL}, new IUnlistedProperty[]{});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(LIT, meta == 1);
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getValue(LIT).booleanValue())
			return 1;
		else return 0;
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileFirepit te = (TileFirepit) world.getTileEntity(pos);
		if(te != null)
		{
			if(te.hasCookingTool())
			{
				if(te.getCookingTool().getItem() == potItem)
				{
					return state.withProperty(TOOL, CookingTool.Pot);
				}
				else if(te.getCookingTool().getItem() == skilletItem)
				{
					return state.withProperty(TOOL, CookingTool.Skillet);
				}
				else if(te.getCookingTool().getItem() == saucepanItem)
				{
					return state.withProperty(TOOL, CookingTool.SaucePan);
				}
			}
			else
			{
				return state.withProperty(TOOL, CookingTool.None);
			}
		}
		return state;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileFirepit();
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	public static enum CookingTool implements IStringSerializable
	{
		None("none"), Pot("pot"), Skillet("skillet"), SaucePan("saucepan");

		private String name;

		CookingTool(String n)
		{
			name = n;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
