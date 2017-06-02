package com.bioxx.tfc2.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.blocks.BlockSmallVessel;

public class TileSmallVessel extends TileTFC implements ITickable, IInventory
{
	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
	EnumFacing.Axis rotation = EnumFacing.Axis.Z;

	public TileSmallVessel()
	{

	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{

	}

	public IExtendedBlockState writeExtendedBlockState(IExtendedBlockState state) 
	{
		state = setInventoryDisplay(state);
		return state;
	}

	protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) 
	{
		PropertyItem.PropItems toDisplay = new PropertyItem.PropItems();
		ItemStack stack = getStackInSlot(0);
		float x = 0, z = 0;
		if(!stack.isEmpty()) 
		{
			PropertyItem.PItem item = getDisplayItem(stack, world, null, x+0.25f, z+0.25f, rotation);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		stack = getStackInSlot(1);
		if(!stack.isEmpty()) 
		{
			PropertyItem.PItem item = getDisplayItem(stack, world, null, x+0.25f, z-0.25f, rotation);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		stack = getStackInSlot(2);
		if(!stack.isEmpty()) 
		{
			PropertyItem.PItem item = getDisplayItem(stack, world, null, x-0.25f, z+0.25f, rotation);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		stack = getStackInSlot(3);
		if(!stack.isEmpty()) 
		{
			PropertyItem.PItem item = getDisplayItem(stack, world, null, x-0.25f, z-0.25f, rotation);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		// add inventory if needed
		return state.withProperty(BlockSmallVessel.INVENTORY, toDisplay);
	}

	@SideOnly(Side.CLIENT)
	public static PropertyItem.PItem getDisplayItem(ItemStack stack, World world, EntityLivingBase entity, float x, float z, EnumFacing.Axis axis) {
		if(stack == null)
			return null;

		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, entity);
		if(model == null || model.isBuiltInRenderer()) {
			// missing model so people don't go paranoid when their chests go missing
			model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
		}
		float rotation = 0;
		if(axis == EnumFacing.Axis.X)
		{
			rotation = (float)Math.PI /2f;
		}
		PropertyItem.PItem item = new PropertyItem.PItem(model, x,0,z, 0.45f, axis == EnumFacing.Axis.X ? (float) (Math.PI/2f) : 0);
		if(stack.getItem() instanceof ItemBlock) {
			item.y = -0.3125f;
			item.s = 0.375f;
			item.r = 0;
		}
		return item;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return false;
	}

	/***********************************************************************************
	 * 2. Getters and Setters
	 ***********************************************************************************/

	public NonNullList<ItemStack> getInventory()
	{
		return this.inventory;
	}

	public EnumFacing.Axis getRotation() {
		return rotation;
	}

	public void setRotation(EnumFacing.Axis axis)
	{
		rotation = axis;
	}

	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = nbt.getTagList("inventory", 10);
		inventory = Helper.readStackArrayFromNBTList(invList, getSizeInventory());
		rotation = EnumFacing.Axis.values()[nbt.getInteger("axis")];
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{

	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = Helper.writeStackArrayToNBTList(inventory);
		nbt.setTag("inventory", invList);
		nbt.setInteger("axis", rotation.ordinal());
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{

	}

	/*********************************************************
	 * IInventory Implementation
	 *********************************************************/
	@Override
	public String getName() {
		return "smallvessel";
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
		return this.inventory.size();
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
			boolean empty = true;
			for(ItemStack is : inventory)
			{
				if(!is.isEmpty())
					empty = false;
			}
			if(empty)
			{
				world.setBlockToAir(getPos());
			}
			else
				TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
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

			TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());//Is this needed?
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
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
		TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
		player.world.markBlockRangeForRenderUpdate(getPos(), getPos().add(1, 1, 1));
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {return true;}

	@Override
	public int getField(int id) 
	{
		return -1;
	}

	@Override
	public void setField(int id, int value) 
	{

	}

	@Override
	public int getFieldCount() {
		return 0;
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
	public boolean isEmpty() 
	{
		for(int i = 0; i < this.getSizeInventory(); i++)
		{
			if(this.getStackInSlot(i) != ItemStack.EMPTY)
				return false;
		}
		return true;
	}


}
