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

public class BlockLogNatural extends BlockTerra
{
	public static PropertyEnum WOOD = PropertyEnum.create("wood", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));

	public BlockLogNatural()
	{
		super(Material.GROUND, WOOD);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		setSoundType(SoundType.WOOD);
		this.setShowInCreative(false);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{WOOD});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(WOOD, WoodType.getTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(WOOD)).getMeta();
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(TFCBlocks.LogVertical);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(WOOD)).getMeta();
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

	public static BlockPosList getTreeForCut(World world, BlockPos pos)
	{
		BlockPosList outList = new BlockPosList();

		IBlockState scanState = world.getBlockState(pos);
		if(!Core.isNaturalLog(scanState))
			return outList;

		WoodType baseWoodType = getWoodType(scanState);

		BlockPosList queue = new BlockPosList();
		queue.add(pos);

		BlockPos scanPos;
		while(!queue.isEmpty())
		{
			scanPos = queue.pop();
			scanState = world.getBlockState(scanPos);

			if(baseWoodType == WoodType.Palm && scanState.getBlock() == TFCBlocks.Leaves2 && scanState.getValue(BlockLeaves2.META_PROPERTY) == baseWoodType)
				outList.add(scanPos);

			if(Core.isNaturalLog(scanState) && getWoodType(scanState) == baseWoodType) //Leaves2 is needed for the palm top (maybe there is a better way to do this?)
			{
				if(!outList.contains(scanPos))
					outList.add(scanPos);
				//Iterable<BlockPos> list = BlockPos.getAllInBox(scanPos.add(-1, 0, -1), scanPos.add(1, 1, 1));
				for(int x = -1; x <= 1; x++)
				{
					for(int z = -1; z <= 1; z++)
					{
						for(int y = -1; y <= 1; y++)
						{
							if(x == 0 && y == 0 && z == 0)
								continue;
							BlockPos p = scanPos.add(x, y, z);
							if(Core.isNaturalLog(world.getBlockState(p)) 
									&& !queue.contains(p)&& !outList.contains(p))
								queue.add(p);
						}
					}
				}
			}
		}

		return outList;
	}

	public static WoodType getWoodType(IBlockState state)
	{
		if(state.getBlock() == TFCBlocks.LogNatural)
			return (WoodType)state.getValue(BlockLogNatural.WOOD);
		else if(state.getBlock() == TFCBlocks.LogNatural2)
			return (WoodType)state.getValue(BlockLogNatural2.WOOD);
		else if(state.getBlock() == TFCBlocks.LogNaturalPalm)
			return (WoodType)state.getValue(BlockLogNaturalPalm.WOOD);

		return null;
	}
}
