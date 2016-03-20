package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;

public class BlockLogHorizontal extends BlockCollapsible implements ISupportBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 8));
	public static PropertyInteger ROT_PROPERTY =  PropertyInteger.create("rotation", 0, 1);

	public BlockLogHorizontal()
	{
		super(Material.ground, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setShowInCreative(false);
	}

	protected BlockLogHorizontal(Material material, PropertyHelper meta)
	{
		super(material, meta);
	}

	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
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
		EntityItem ei = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.stick, 1+world.rand.nextInt(3)));
		world.spawnEntityInWorld(ei);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(TFCBlocks.LogVertical);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}
	/*******************************************************************************
	 * 2. Rendering 
	 *******************************************************************************/
	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/
	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY, ROT_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta((meta & 7))).withProperty(ROT_PROPERTY, (meta & 8) >> 3);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int wood = (((WoodType)state.getValue(META_PROPERTY)).getMeta() & 7);
		int rot = (((Integer)state.getValue(ROT_PROPERTY)) << 3);
		return wood + rot;
	}
}
