package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.api.util.BlockPosList;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.items.ItemAxe;

public class BlockLogNaturalPalm extends BlockTerra
{
	public static PropertyEnum WOOD = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 18, 19));

	public BlockLogNaturalPalm()
	{
		super(Material.GROUND, WOOD);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setBlockBounds(0.125f, 0, 0.125f, 0.875f, 1f, 0.875f);
		this.setDefaultState(this.blockState.getBaseState().withProperty(WOOD, WoodType.Palm));
		this.setShowInCreative(false);
		setSoundType(SoundType.WOOD);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{WOOD});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(WOOD, WoodType.getTypeFromMeta(meta+16));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(WOOD)).getMeta() & 15;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(TFCBlocks.LogVertical2);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(WOOD)).getMeta();
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

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		if(world.isRemote)
			return true;

		//get our item parameters
		ItemStack stack = player.getHeldItemMainhand();
		int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
		int maxCut = 0;
		if(stack.getItem() instanceof ItemAxe)
		{
			maxCut = ((ItemAxe)stack.getItem()).maxTreeSize;
		}
		else return false;

		//create the map of our tree
		BlockPosList tree = BlockLogNatural.getTreeForCut(world, pos);
		int count = tree.size();

		//if the tree has too many blocks then prevent chopping
		if(count > maxCut)
		{
			player.sendMessage(new TextComponentTranslation(Core.translate("gui.axe.treetoobig")));
			return false;
		}
		else if(count > stack.getMaxDamage() - stack.getItemDamage())
		{
			player.sendMessage(new TextComponentTranslation(Core.translate("gui.axe.needsrepair")));
			return false;
		}
		else
		{
			for(BlockPos p : tree)
			{
				IBlockState s = world.getBlockState(p);
				this.onBlockHarvested(world, pos, s, player);
				world.setBlockToAir(p);
				s.getBlock().dropBlockAsItem(world, p, s, fortune);
			}
		}
		stack.damageItem(count-1, player);

		return true;
	}
}
