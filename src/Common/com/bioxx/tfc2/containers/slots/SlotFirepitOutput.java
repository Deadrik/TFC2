package com.bioxx.tfc2.containers.slots;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.math.MathHelper;

import com.bioxx.tfc2.tileentities.TileFirepit;

public class SlotFirepitOutput extends Slot
{
	private final EntityPlayer player;
	private int removeCount;

	public SlotFirepitOutput(EntityPlayer player, IInventory iinventory, int i, int j, int k)
	{
		super(iinventory, i, j, k);
		this.player = player;
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
	{
		if(this.inventory instanceof TileFirepit)
		{
			TileFirepit tile = (TileFirepit)inventory;
			return tile.getField(TileFirepit.FIELD_COOKING_TIMER) <= 0;
		}
		return false;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return false;
	}

	@Override
	public ItemStack decrStackSize(int amount)
	{
		if (this.getHasStack())
		{
			this.removeCount += Math.min(amount, this.getStack().getCount());
		}

		return super.decrStackSize(amount);
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
	{
		this.onCrafting(stack);
		super.onTake(thePlayer, stack);
		return stack;
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	@Override
	protected void onCrafting(ItemStack stack, int amount)
	{
		this.removeCount += amount;
		this.onCrafting(stack);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
	 */
	@Override
	protected void onCrafting(ItemStack stack)
	{
		stack.onCrafting(this.player.world, this.player, this.removeCount);

		if (!this.player.world.isRemote)
		{
			int i = this.removeCount;
			float f = FurnaceRecipes.instance().getSmeltingExperience(stack);

			if (f == 0.0F)
			{
				i = 0;
			}
			else if (f < 1.0F)
			{
				int j = MathHelper.floor((float)i * f);

				if (j < MathHelper.ceil((float)i * f) && Math.random() < (double)((float)i * f - (float)j))
				{
					++j;
				}

				i = j;
			}

			while (i > 0)
			{
				int k = EntityXPOrb.getXPSplit(i);
				i -= k;
				this.player.world.spawnEntity(new EntityXPOrb(this.player.world, this.player.posX, this.player.posY + 0.5D, this.player.posZ + 0.5D, k));
			}
		}

		this.removeCount = 0;

		net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerSmeltedEvent(player, stack);

		if (stack.getItem() == Items.IRON_INGOT)
		{
			this.player.addStat(AchievementList.ACQUIRE_IRON);
		}

		if (stack.getItem() == Items.COOKED_FISH)
		{
			this.player.addStat(AchievementList.COOK_FISH);
		}
	}
}
