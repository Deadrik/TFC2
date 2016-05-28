package com.bioxx.tfc2.tileentities;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.blocks.BlockAnvil;
import com.bioxx.tfc2.core.Timekeeper;

public class TileAnvil extends TileTFC implements ITickable, IInventory
{
	UUID smithID;
	ItemStack[] inventory;

	public TileAnvil()
	{
		smithID = new UUID(0L, 0L);
		inventory = new ItemStack[3];
		inventory[0] = new ItemStack(TFCItems.StoneAxeHead);
	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		Timekeeper time = Timekeeper.getInstance();
		if(!worldObj.isRemote && time.getTotalTicks()%100 == 0)
			worldObj.getMinecraftServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 200, getWorld().provider.getDimension(), this.getDescriptionPacket());
	}

	public IExtendedBlockState writeExtendedBlockState(IExtendedBlockState state) 
	{

		state = setInventoryDisplay(state);

		return state;
	}

	protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) 
	{
		PropertyItem.PropItems toDisplay = new PropertyItem.PropItems();
		if(getStackInSlot(0) != null) {
			ItemStack stack = getStackInSlot(0);
			PropertyItem.PItem item = getTableItem(stack, worldObj, null);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		// add inventory if needed
		return state.withProperty(BlockAnvil.INVENTORY, toDisplay);
	}

	@SideOnly(Side.CLIENT)
	public static PropertyItem.PItem getTableItem(ItemStack stack, World world, EntityLivingBase entity) {
		if(stack == null)
			return null;

		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, entity);
		if(model == null || model.isBuiltInRenderer()) {
			// missing model so people don't go paranoid when their chests go missing
			model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
		}

		PropertyItem.PItem item = new PropertyItem.PItem(model, 0,-0.46875f,0, 0.8f, (float) (Math.PI/2));
		if(stack.getItem() instanceof ItemBlock) {
			item.y = -0.3125f;
			item.s = 0.375f;
			item.r = 0;
		}
		return item;
	}

	/***********************************************************************************
	 * 2. Getters and Setters
	 ***********************************************************************************/

	public void setSmithID(EntityPlayer player)
	{
		smithID = EntityPlayer.getUUID(player.getGameProfile());
	}


	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{

	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{
		smithID = new UUID(nbt.getLong("farmerID_least"), nbt.getLong("farmerID_most"));
	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = new NBTTagList();
		invList.appendTag(inventory[0].writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setLong("farmerID_least", this.smithID.getLeastSignificantBits());
		nbt.setLong("farmerID_most", this.smithID.getMostSignificantBits());
	}

	@Override
	public String getName() {
		return null;
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
		return 3;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(index < getSizeInventory())
			return inventory[index];
		return null;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) 
	{
		if(index < getSizeInventory() && inventory[index] != null)
			inventory[index].stackSize--;
		return inventory[index];
	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		if(index < getSizeInventory())
		{
			ItemStack out = inventory[index];
			inventory[index] = null;
			return out;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
		if(index < getSizeInventory())
		{
			inventory[index] = stack;
		}

	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) 
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {return true;}

	@Override
	public int getField(int id) {
		return 0;
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
	public void clear() {

	}
}
