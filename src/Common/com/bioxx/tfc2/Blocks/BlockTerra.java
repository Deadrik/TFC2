package com.bioxx.tfc2.Blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public abstract class BlockTerra extends Block
{
	private final PropertyInteger META_PROP;

	protected BlockTerra()
	{
		this(Material.rock, null);
	}

	protected BlockTerra(Material material, PropertyInteger meta)
	{
		super(material);
		this.META_PROP = meta;
		if (META_PROP != null)
			this.setDefaultState(this.blockState.getBaseState().withProperty(META_PROP, (Comparable)META_PROP.getAllowedValues().toArray()[0]));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		if(hasMeta())
		{
			for(int l = 0; l < META_PROP.getAllowedValues().size(); l++)
				list.add(new ItemStack(itemIn, 1, l));
		}
		else
			super.getSubBlocks(itemIn, tab, list);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		if (META_PROP != null)
			return getMetaFromState(state);
		return super.damageDropped(state);
	}

	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer)
	{
		onBlockPlacedBy(world, pos, state, placer, null);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
	{
		//Debug Message
		if(world.isRemote)
		{
			int metadata = this.getMetaFromState(state);
			System.out.println("Meta="+(new StringBuilder()).append(getUnlocalizedName()).append(":").append(metadata).toString());
		}
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

	@Override
	public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos)
	{
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
		{
			int metadata = this.getMetaFromState(state);
			System.out.println("Meta = "+(new StringBuilder()).append(getUnlocalizedName()).append(":").append(metadata).toString());
		}
		return super.onBlockActivated(world, pos, state, playerIn, side, hitX, hitY, hitZ);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
	{
		super.harvestBlock(world, player, pos, state, te);
		//TFC_Core.addPlayerExhaustion(player, 0.001f);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable)
	{
		/*Block plant = plantable.getPlant(world, pos.offsetUp()).getBlock();
		EnumPlantType plantType = plantable.getPlantType(world, pos.offsetUp());

		if (plant == Blocks.cactus && this == Blocks.cactus)
		{
			return true;
		}

		if (plant == Blocks.reeds && this == Blocks.reeds)
		{
			return true;
		}

		switch (plantType)
		{
		case Cave:   return isSideSolid(world, x, y, z, UP);
		case Plains: return TFC_Core.isSoil(this);
		case Water:  return world.getBlock(x, y, z).getMaterial() == Material.water && world.getBlockMetadata(x, y, z) == 0;
		case Beach:
			boolean isBeach = TFC_Core.isSand(this) || TFC_Core.isGravel(this);
			boolean hasWater = (world.getBlock(x - 1, y, z    ).getMaterial() == Material.water ||
					world.getBlock(x + 1, y, z    ).getMaterial() == Material.water ||
					world.getBlock(x,     y, z - 1).getMaterial() == Material.water ||
					world.getBlock(x,     y, z + 1).getMaterial() == Material.water);
			return isBeach && hasWater;
		default: return false;
		}*/

		return super.canSustainPlant(world, pos, direction, plantable);
	}
}
