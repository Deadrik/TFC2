package com.bioxx.tfc2.blocks.vanilla;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.core.Timekeeper;
import com.bioxx.tfc2.tileentities.TileTorch;

public class BlockTorchTFC extends BlockTorch implements ITileEntityProvider
{
	private final boolean isOn;

	public BlockTorchTFC(boolean isOn)
	{
		super();
		this.setTickRandomly(true);
		this.isOn = isOn;

		if (isOn)
		{
			this.setLightLevel(0.9375F);
		}
		else
		{
			this.setLightLevel(0.0F);
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
	{
		super.onBlockAdded(worldIn, pos, state);

		if (!worldIn.isRemote)
		{
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof TileTorch)
			{
				((TileTorch) te).setTimer((int) Timekeeper.getInstance().getTotalHours());
			}
		}
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		super.updateTick(worldIn, pos, state, rand);

		if (!worldIn.isRemote && this.isOn)
		{
			if (TFCOptions.torchBurnTime != 0 && worldIn.getTileEntity(pos) instanceof TileTorch)
			{
				TileTorch te = (TileTorch) worldIn.getTileEntity(pos);
				boolean timerExpired = Timekeeper.getInstance().getTotalHours() > te.getTimer() + TFCOptions.torchBurnTime;
				boolean isWet = worldIn.isRaining() && worldIn.canBlockSeeSky(pos);

				if (timerExpired || isWet)
				{
					int meta = this.getMetaFromState(state);
					worldIn.setBlockState(pos, TFCBlocks.TorchOff.getStateFromMeta(meta), 3);
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!worldIn.isRemote)
		{
			ItemStack is = playerIn.inventory.getCurrentItem();
			Item item = is != null ? is.getItem() : null;

			// Making new torches. Keep meta check just in case there are some burned out torches that haven't converted yet.
			if (item == Items.stick)
			{
				playerIn.inventory.consumeInventoryItem(Items.stick);
				EntityItem ei = playerIn.entityDropItem(new ItemStack(TFCBlocks.TorchOn), 1);
				ei.setNoPickupDelay();
			}
			// Refreshing torch timer, or re-lighting burned out torches that haven't converted yet.
			else if (item == Item.getItemFromBlock(TFCBlocks.TorchOn))
			{
				TileTorch te = (TileTorch) worldIn.getTileEntity(pos);
				te.setTimer((int) Timekeeper.getInstance().getTotalHours());
				if (!isOn)
				{
					int meta = this.getMetaFromState(state);
					worldIn.setBlockState(pos, TFCBlocks.TorchOn.getStateFromMeta(meta), 3);
				}
			}
			// Extinguish the torch
			else
			{
				int meta = this.getMetaFromState(state);
				worldIn.setBlockState(pos, TFCBlocks.TorchOff.getStateFromMeta(meta), 3);
			}
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		int currentHour = (int) Timekeeper.getInstance().getTotalHours();
		return new TileTorch(currentHour);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 *  
	 * @param fortune the level of the Fortune enchantment on the player's tool
	 */
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		if (this.isOn)
		{
			return Item.getItemFromBlock(TFCBlocks.TorchOn);
		}
		else
		{
			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World worldIn, BlockPos pos)
	{
		if (this.isOn)
		{
			return Item.getItemFromBlock(TFCBlocks.TorchOn);
		}
		else
		{
			return null;
		}
	}

	@Override
	protected ItemStack createStackedBlock(IBlockState state)
	{
		if (this.isOn)
		{
			return new ItemStack(TFCBlocks.TorchOn);
		}
		else
		{
			return null;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (this.isOn)
		{
			super.randomDisplayTick(worldIn, pos, state, rand);
		}
	}

	@Override
	public boolean isAssociatedBlock(Block other)
	{
		return other == TFCBlocks.TorchOn || other == TFCBlocks.TorchOff;
	}

	@Override
	public boolean isReplaceable(World worldIn, BlockPos pos)
	{
		return true;
	}
}
