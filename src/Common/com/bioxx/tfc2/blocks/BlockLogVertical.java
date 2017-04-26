package com.bioxx.tfc2.blocks;

import java.util.Arrays;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.interfaces.ISupportBlock;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.terrain.BlockCollapsible;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public class BlockLogVertical extends BlockCollapsible implements ISupportBlock
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));

	public BlockLogVertical(Material m, PropertyHelper p)
	{
		super(m, p);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		setSoundType(SoundType.WOOD);
	}

	public BlockLogVertical()
	{
		this(Material.WOOD, META_PROPERTY);
	}
	/*******************************************************************************
	 * 1. Content 
	 *******************************************************************************/
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		if(facing == EnumFacing.DOWN || facing == EnumFacing.UP)
			return this.getStateFromMeta(meta);
		else
		{
			if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
			{
				if(meta < 8)
				{
					return TFCBlocks.LogHorizontal.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 1);
				}
				else
				{
					return TFCBlocks.LogHorizontal2.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 1);
				}
			}
			else
			{
				if(meta < 8)
				{
					return TFCBlocks.LogHorizontal.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 0);
				}
				else
				{
					return TFCBlocks.LogHorizontal2.getStateFromMeta(meta).withProperty(BlockLogHorizontal.ROT_PROPERTY, 0);
				}
			}
		}
	}

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
