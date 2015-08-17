package com.bioxx.tfc2.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import com.bioxx.tfc2.TFCBlocks;

public abstract class BlockTerra extends Block
{
	/**
	 * This is the internal Property for metadata
	 */
	protected PropertyHelper META_PROP;

	private boolean showInCreative = true;

	protected BlockTerra()
	{
		this(Material.rock, null);
	}

	protected BlockTerra(Material material, PropertyHelper meta)
	{
		super(material);
		this.META_PROP = meta;
		/*if (META_PROP != null)
			this.setDefaultState(this.getBlockState().getBaseState().withProperty(META_PROP, (Comparable)META_PROP.getAllowedValues().toArray()[0]));*/
	}

	public void setShowInCreative(boolean b)
	{
		showInCreative = b;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		if(showInCreative)
		{
			if(hasMeta())
			{
				for(int l = 0; l < META_PROP.getAllowedValues().size(); l++)
					list.add(new ItemStack(itemIn, 1, l));
			}
			else
				super.getSubBlocks(itemIn, tab, list);
		}
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
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable)
	{
		IBlockState state = world.getBlockState(pos);
		IBlockState plant = plantable.getPlant(world, pos.offset(direction));
		net.minecraftforge.common.EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

		if (plantable instanceof BlockBush)
		{
			return true;
		}

		switch (plantType)
		{
		case Desert: return this == TFCBlocks.Sand || this == net.minecraft.init.Blocks.hardened_clay || this == net.minecraft.init.Blocks.stained_hardened_clay || this == TFCBlocks.Dirt;
		case Nether: return this == Blocks.soul_sand;
		case Crop:   return this == Blocks.farmland;
		case Cave:   return isSideSolid(world, pos, EnumFacing.UP);
		case Plains: return this == TFCBlocks.Grass || this == TFCBlocks.Dirt || this == net.minecraft.init.Blocks.farmland;
		case Water:  return getMaterial() == Material.water && ((Integer)state.getValue(BlockLiquid.LEVEL)) == 0;
		case Beach:
			boolean isBeach = this == TFCBlocks.Grass || this == TFCBlocks.Dirt || this == TFCBlocks.Sand;
			boolean hasWater = (world.getBlockState(pos.offsetEast()).getBlock().getMaterial() == Material.water ||
					world.getBlockState(pos.offsetWest()).getBlock().getMaterial() == Material.water ||
					world.getBlockState(pos.offsetNorth()).getBlock().getMaterial() == Material.water ||
					world.getBlockState(pos.offsetSouth()).getBlock().getMaterial() == Material.water);
			return isBeach && hasWater;
		}

		return false;
	}
}
