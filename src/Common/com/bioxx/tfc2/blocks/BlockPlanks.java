package com.bioxx.tfc2.blocks;

import java.util.Arrays;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;

public class BlockPlanks extends BlockCollapsible implements ISupportBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));

	public BlockPlanks()
	{
		super(Material.GROUND, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setSoundType(SoundType.WOOD);
	}

	public BlockPlanks(Material m, PropertyHelper p)
	{
		super(m, p);
		setSoundType(SoundType.WOOD);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public IBlockState getFallBlockType(IBlockState myState)
	{
		return TFCBlocks.Rubble.getDefaultState();
	}

	@Override
	public int getNaturalSupportRange(IBlockAccess world, BlockPos pos,IBlockState myState)
	{
		return 7;
	}

	@Override
	public boolean canBeSupportedBy(IBlockState myState, IBlockState otherState)
	{
		if(otherState.getBlock() == this || Core.isSoil(otherState) || Core.isStone(otherState) || otherState.getBlock() instanceof ISupportBlock)
			return true;
		return false;
	}

	@Override
	public void createFallingEntity(World world, BlockPos pos, IBlockState state)
	{
		world.setBlockToAir(pos);
		EntityItem ei = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.STICK, 1+world.rand.nextInt(3)));
		world.spawnEntity(ei);
	}

	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY});
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
}
