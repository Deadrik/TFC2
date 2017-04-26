package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockLogHorizontal extends BlockCollapsible implements ISupportBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 8));
	public static PropertyInteger ROT_PROPERTY =  PropertyInteger.create("rotation", 0, 1);

	public BlockLogHorizontal()
	{
		this(Material.GROUND, META_PROPERTY);
	}

	protected BlockLogHorizontal(Material material, PropertyHelper meta)
	{
		super(material, meta);
		setSoundType(SoundType.WOOD);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setShowInCreative(false);
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
	public void createFallingEntity(World world, BlockPos pos, IBlockState state)
	{
		if(world.rand.nextFloat() < 0.4)
		{
			world.setBlockToAir(pos);
			EntityItem ei = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.STICK, 1+world.rand.nextInt(3)));
			world.spawnEntity(ei);
		}
		else
		{
			int x = 0;
			int z = 0;
			if(world.rand.nextFloat() < 0.25)
			{
				x = -1 + world.rand.nextInt(3);
				z = -1 + world.rand.nextInt(3);
			}
			world.setBlockToAir(pos);
			EntityFallingBlockTFC entityfallingblock = new EntityFallingBlockTFC(world, pos.getX() + 0.5D + x, pos.getY(), pos.getZ() + 0.5D + z, state);
			world.spawnEntity(entityfallingblock);
		}
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
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY, ROT_PROPERTY});
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
