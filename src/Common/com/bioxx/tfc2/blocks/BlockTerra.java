package com.bioxx.tfc2.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.INeedOffset;

public abstract class BlockTerra extends Block
{
	/**
	 * This is the internal Property for metadata
	 */
	protected PropertyHelper META_PROP;
	private boolean showInCreative = true;
	protected AxisAlignedBB blockAABB;
	private boolean breaksWhenSuspended = false;

	protected BlockTerra()
	{
		this(Material.ROCK, null);
		blockAABB = FULL_BLOCK_AABB;
	}

	protected BlockTerra(Material material, PropertyHelper meta)
	{
		super(material);
		this.META_PROP = meta;
		/*if (META_PROP != null)
			this.setDefaultState(this.getBlockState().getBaseState().withProperty(META_PROP, (Comparable)META_PROP.getAllowedValues().toArray()[0]));*/
		blockAABB = FULL_BLOCK_AABB;
	}

	public void setBreaksWhenSuspended(boolean b)
	{
		breaksWhenSuspended = b;
	}

	/**
	 * @return Does this block break when there is nothing solid underneath it to hold it up?
	 */
	public boolean getBreaksWhenSuspended()
	{
		return breaksWhenSuspended;
	}

	public void setShowInCreative(boolean b)
	{
		showInCreative = b;
	}

	public void setBlockBounds(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax)
	{
		blockAABB = new AxisAlignedBB(xMin, yMin, zMin, xMax, yMax, zMax);
	}

	@Override
	public Block setUnlocalizedName(String name)
	{

		try{this.setRegistryName(name);}
		catch(Exception e){}
		return super.setUnlocalizedName(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList list)
	{
		if(showInCreative)
		{
			if(hasMeta())
			{
				if(itemIn instanceof ItemBlock && ((ItemBlock)itemIn).block instanceof INeedOffset)
				{
					for(int l = 0; l < META_PROP.getAllowedValues().size(); l++)
						list.add(new ItemStack(itemIn, 1, ((INeedOffset)(((ItemBlock)itemIn).block)).convertMetaToItem(l)));
				}
				else
				{
					for(int l = 0; l < META_PROP.getAllowedValues().size(); l++)
						list.add(new ItemStack(itemIn, 1, l));
				}
			}
			else
				super.getSubBlocks(itemIn, tab, list);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return blockAABB;
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		if (META_PROP != null)
			return getMetaFromState(state);
		return super.damageDropped(state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return hasMeta() ? this.getDefaultState().withProperty(META_PROP, (Comparable)META_PROP.getAllowedValues().toArray()[meta]) : super.getStateFromMeta(meta);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return hasMeta() ? ((Integer)state.getValue(META_PROP)).intValue() : super.getMetaFromState(state);
	}

	public final boolean hasMeta()
	{
		return META_PROP != null;
	}

	/**
	 * Determines if this block can support the passed in plant, allowing it to be planted and grow.
	 * Some examples:
	 *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
	 *   Cacti checks if its a cacti, or if its sand
	 *   Nether types check for soul sand
	 *   Crops check for tilled soil
	 *   Caves check if it's a solid surface
	 *   Plains check if its grass or dirt
	 *   Water check if its still water
	 *
	 * @param world The current world
	 * @param pos Block position in world
	 * @param direction The direction relative to the given position the plant wants to be, typically its UP
	 * @param plantable The plant that wants to check
	 * @return True to allow the plant to be planted/stay.
	 */
	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
	{
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		/*if (plantable instanceof BlockBush)
		{
			return true;
		}*/

		switch (plantType)
		{
		case Desert: return this == TFCBlocks.Sand || this == net.minecraft.init.Blocks.HARDENED_CLAY || this == net.minecraft.init.Blocks.STAINED_HARDENED_CLAY || this == TFCBlocks.Dirt;
		case Nether: return this == Blocks.SOUL_SAND;
		case Crop:   return this == TFCBlocks.Farmland;
		case Cave:   return isSideSolid(state, world, pos, EnumFacing.UP);
		case Plains: return this == TFCBlocks.Grass || this == TFCBlocks.Dirt || this == TFCBlocks.Farmland;
		case Water:  return getMaterial(state) == Material.WATER && ((Integer)state.getValue(BlockLiquid.LEVEL)) == 0;
		case Beach:
			boolean isBeach = this == TFCBlocks.Grass || this == TFCBlocks.Dirt || this == TFCBlocks.Sand;
			boolean hasWater = (world.getBlockState(pos.east()).getBlock().getMaterial(world.getBlockState(pos.east())) == Material.WATER ||
					world.getBlockState(pos.west()).getBlock().getMaterial(world.getBlockState(pos.west())) == Material.WATER ||
					world.getBlockState(pos.north()).getBlock().getMaterial(world.getBlockState(pos.north())) == Material.WATER ||
					world.getBlockState(pos.south()).getBlock().getMaterial(world.getBlockState(pos.south())) == Material.WATER);
			return isBeach && hasWater;
		}

		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		if(this.getBreaksWhenSuspended())
		{
			IBlockState under = world.getBlockState(pos.down());
			if(!under.getBlock().isSideSolid(under, world, pos.down(), EnumFacing.UP))
			{
				this.dropBlockAsItem(world, pos, state, 0);
				//Break this block
				world.setBlockToAir(pos);
			}
		}
		else
			super.neighborChanged(state, world, pos, blockIn, fromPos);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		List<ItemStack> ret = super.getDrops(world, pos, state, fortune);
		if(this.isBlockContainer)
		{
			TileEntity te = world.getTileEntity(pos);
			if(te instanceof IInventory)
			{
				for(int i = 0; i < ((IInventory)te).getSizeInventory(); i++)
				{
					ret.add(((IInventory)te).getStackInSlot(i));
				}
			}
		}
		return ret;
	}
}
