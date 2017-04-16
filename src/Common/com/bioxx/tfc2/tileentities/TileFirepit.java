package com.bioxx.tfc2.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.heat.ItemHeat;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.blocks.BlockFirepit;
import com.bioxx.tfc2.containers.ContainerFakeFirepit;
import com.bioxx.tfc2.core.Timekeeper;

public class TileFirepit extends TileTFC implements ITickable, IInventory
{
	public static final int FIELD_FUEL_TIMER = 0;
	public static final int FIELD_FUELMAX_TIMER = 1;
	public static final int FIELD_COOKING_TIMER = 2;
	public static final int FIELD_COOKINGMAX_TIMER = 3;
	private static final int OUTPUT_SLOT = 10;
	private static final int FUEL_SLOT = 0;
	private static final int TOOL_SLOT = 1;

	private static final float MAXHEAT = 350f;

	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(11, ItemStack.EMPTY);
	ItemStack cookingTool = ItemStack.EMPTY;
	InventoryCrafting craftMatrix = new InventoryCrafting(new ContainerFakeFirepit(), 3, 3);

	private int cookingTimer = -1;
	private int cookingMaxTimer = -1;
	private int fuelTimer = 0;
	private int fuelMaxTimer = 0;


	public TileFirepit()
	{

	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		if(world.isRemote)
			return;
		Timekeeper time = Timekeeper.getInstance();

		if(this.hasCookingTool())
		{
			ItemStack output = CraftingManager.getInstance().findMatchingRecipe(craftMatrix, world);
			if(output == ItemStack.EMPTY)
			{
				cookingMaxTimer = -1;
				cookingTimer = cookingMaxTimer;
				this.setInventorySlotContents(OUTPUT_SLOT, output);
			}
			else if(!ItemStack.areItemsEqual(getStackInSlot(OUTPUT_SLOT), output))
			{
				this.setInventorySlotContents(OUTPUT_SLOT, output);
				cookingMaxTimer = 300;//For now we'll use a generic cooking time for all foods
				cookingTimer = cookingMaxTimer;
			}
		}
		else
		{
			//Handle non-cooking item heating
			if(fuelTimer > 0)
			{
				if(this.getStackInSlot(TOOL_SLOT) != ItemStack.EMPTY)
				{
					if(ItemHeat.Get(getStackInSlot(TOOL_SLOT)) < MAXHEAT)
						ItemHeat.Increase(getStackInSlot(TOOL_SLOT), 1.0f);
				}
			}
		}

		//If the fire is lit
		if(fuelTimer > 0)
		{
			fuelTimer--;

			if(cookingTimer > 0 && !this.getStackInSlot(OUTPUT_SLOT).isItemEqual(ItemStack.EMPTY))
			{
				cookingTimer--;
			}
		}
		else
		{
			if(fuelTimer <= 0 && isValidFuel(getStackInSlot(FUEL_SLOT)) && world.getBlockState(getPos()).getValue(BlockFirepit.LIT) == true)
			{
				fuelMaxTimer = Global.GetFirepitFuel(getStackInSlot(FUEL_SLOT));
				fuelTimer = fuelMaxTimer;
				getStackInSlot(FUEL_SLOT).shrink(1);
				world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockFirepit.LIT, true), 3);
			}
			else if(world.getBlockState(getPos()).getValue(BlockFirepit.LIT) == true)
			{
				world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockFirepit.LIT, false), 3);
			}

			if(fuelTimer <= 0 && cookingTimer > 0)
			{
				cookingTimer = cookingMaxTimer;//reset the cooking timer if the fuel runs out
			}
		}
	}

	public void light()
	{
		if(isValidFuel(getStackInSlot(FUEL_SLOT)))
			world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockFirepit.LIT, true), 3);
	}
	public boolean isValidFuel(ItemStack stack)
	{
		return Global.GetFirepitFuel(stack) > 0;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return false;
	}

	public void ejectContents(boolean ejectFuel)
	{
		if(ejectFuel)
		{
			InventoryHelper.spawnItemStack(world, getPos().getX(), getPos().getY(), getPos().getZ(), this.getStackInSlot(FUEL_SLOT));
			this.setInventorySlotContents(FUEL_SLOT, ItemStack.EMPTY);
		}
		else
		{
			for(int i = TOOL_SLOT; i < this.getSizeInventory(); i++)
			{
				if(i == OUTPUT_SLOT && cookingTimer > 0)
				{
					continue;
				}
				InventoryHelper.spawnItemStack(world, getPos().getX(), getPos().getY(), getPos().getZ(), this.getStackInSlot(i));
				this.setInventorySlotContents(i, ItemStack.EMPTY);
			}
		}

	}

	public boolean isFireLit()
	{
		return this.fuelTimer > 0;
	}

	/***********************************************************************************
	 * 2. Getters and Setters
	 ***********************************************************************************/

	public NonNullList<ItemStack> getInventory()
	{
		return this.inventory;
	}

	public boolean hasCookingTool()
	{
		if(ItemStack.areItemsEqual(this.getStackInSlot(TOOL_SLOT), new ItemStack(BlockFirepit.potItem)))
			return true;
		else if(ItemStack.areItemsEqual(this.getStackInSlot(TOOL_SLOT), new ItemStack(BlockFirepit.skilletItem)))
			return true;
		else if(ItemStack.areItemsEqual(this.getStackInSlot(TOOL_SLOT), new ItemStack(BlockFirepit.saucepanItem)))
			return true;
		return false;
	}

	public ItemStack getCookingTool()
	{
		return this.getStackInSlot(TOOL_SLOT);
	}

	public void setCookingTool(ItemStack tool)
	{
		this.setInventorySlotContents(TOOL_SLOT, tool);
		world.setBlockState(getPos(), this.getBlockType().getExtendedState(world.getBlockState(getPos()), world, getPos()));
	}

	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{

		cookingTimer = nbt.getInteger("cookingTimer");
		cookingMaxTimer = nbt.getInteger("cookingMaxTimer");
		fuelTimer = nbt.getInteger("fuelTimer");
		fuelMaxTimer = nbt.getInteger("fuelMaxTimer");
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = nbt.getTagList("inventory", 10);
		inventory = Helper.readStackArrayFromNBTList(invList, getSizeInventory());
		for(int i = TOOL_SLOT; i < 10; i++)
		{
			craftMatrix.setInventorySlotContents(1, inventory.get(i));
		}
	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{

		nbt.setInteger("cookingTimer", cookingTimer);
		nbt.setInteger("cookingMaxTimer", cookingMaxTimer);
		nbt.setInteger("fuelTimer", fuelTimer);
		nbt.setInteger("fuelMaxTimer", fuelMaxTimer);
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = Helper.writeStackArrayToNBTList(inventory);
		nbt.setTag("inventory", invList);

	}

	/*********************************************************
	 * IInventory Implementation
	 *********************************************************/
	@Override
	public String getName() {
		return "Firepit";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 11;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(index < getSizeInventory())
			return inventory.get(index);
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) 
	{
		if(inventory.get(index) != ItemStack.EMPTY)
		{
			if(inventory.get(index).getMaxStackSize() <= count)
			{
				ItemStack itemstack = inventory.get(index);
				inventory.set(index, ItemStack.EMPTY);
				TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
				return itemstack;
			}
			ItemStack itemstack1 = inventory.get(index).splitStack(count);
			if(inventory.get(index).getMaxStackSize() == 0)
				inventory.set(index, ItemStack.EMPTY);
			return itemstack1;
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		if(index < getSizeInventory())
		{
			ItemStack out = inventory.get(index);
			inventory.set(index, ItemStack.EMPTY);
			//TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
			return out;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
		if(index < getSizeInventory())
		{
			inventory.set(index, stack);
			//TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());//Is this needed?
			if(index == TOOL_SLOT)
				world.markBlockRangeForRenderUpdate(getPos(), getPos().south().east());
			if(index > 0 && index < 10)//Exclude the fuel slot (0) and the output slot (10)
				craftMatrix.setInventorySlotContents(index-1, stack);//Slot 0 is the fuel slot so we subtract one
		}
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) 
	{
		//TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
		player.world.markBlockRangeForRenderUpdate(getPos(), getPos().add(1, 1, 1));
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {return true;}

	@Override
	public int getField(int id) 
	{
		switch(id)
		{
		case FIELD_FUEL_TIMER: return this.fuelTimer;
		case FIELD_COOKING_TIMER: return this.cookingTimer;
		case FIELD_FUELMAX_TIMER: return this.fuelMaxTimer;
		case FIELD_COOKINGMAX_TIMER: return this.cookingMaxTimer;
		}
		return -1;
	}

	@Override
	public void setField(int id, int value) 
	{
		switch(id)
		{
		case FIELD_FUEL_TIMER: this.fuelTimer = value; break;
		case FIELD_COOKING_TIMER: this.cookingTimer = value; break;
		case FIELD_FUELMAX_TIMER: this.fuelMaxTimer = value; break;
		case FIELD_COOKINGMAX_TIMER: this.cookingMaxTimer = value; break;
		}
	}

	@Override
	public int getFieldCount() {
		return 4;
	}

	@Override
	public void clear() 
	{
		for(int i = 0; i < this.getSizeInventory(); i++)
		{
			this.setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i < this.getSizeInventory(); i++)
		{
			if(this.getStackInSlot(i) != ItemStack.EMPTY)
				return false;
		}
		return true;
	}

	/*********************************************************
	 * Associated Classes
	 *********************************************************/



}
