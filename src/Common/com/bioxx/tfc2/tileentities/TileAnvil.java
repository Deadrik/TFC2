package com.bioxx.tfc2.tileentities;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.blocks.BlockAnvil;
import com.bioxx.tfc2.core.Timekeeper;
import com.bioxx.tfc2.networking.client.CAnvilStrikePacket;

public class TileAnvil extends TileTFC implements ITickable, IInventory
{
	UUID smithID;
	ItemStack[] inventory;
	AnvilStrikePoint[] hitArray;

	public TileAnvil()
	{
		smithID = new UUID(0L, 0L);
		inventory = new ItemStack[3];
		inventory[0] = new ItemStack(Items.IRON_INGOT);
		//inventory[1] = new ItemStack(Items.GOLD_INGOT);
		hitArray = new AnvilStrikePoint[24];
	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		Timekeeper time = Timekeeper.getInstance();
		if(!worldObj.isRemote && time.getTotalTicks()%50 == 0)
		{
			AnvilStrikePoint p = new AnvilStrikePoint();
			p.setBirthTime(time.getTotalTicks());
			if(worldObj.rand.nextFloat() < 0.25)
				p.setType(AnvilStrikeType.CRITICAL);
			else
				p.setType(AnvilStrikeType.HIT);
			p.setLifeTime(100);

			int x = worldObj.rand.nextInt(6);
			int z = worldObj.rand.nextInt(4);

			if(this.getStrikePoint(x, z) == null)
			{
				this.setStrikePoint(x, z, p);
				sendSmithingPacket(x, z, p);
			}


		}

		for(int i = 0; i < 24; i++)
		{
			AnvilStrikePoint p = hitArray[i];
			if(p != null && p.getBirthTime()+p.getLifeTime() < time.getTotalTicks())
			{
				hitArray[i] = null;
			}

		}

	}

	public void sendSmithingPacket(int x, int z, AnvilStrikePoint point)
	{
		//worldObj.getMinecraftServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 200, getWorld().provider.getDimension(), this.getDescriptionPacket());
		EntityPlayerMP player = worldObj.getMinecraftServer().getPlayerList().getPlayerByUUID(smithID);
		if(player != null)
			TFC.network.sendTo(new CAnvilStrikePacket(this.getPos(), getStrikePointIndex(x, z), point), player);
	}

	public IExtendedBlockState writeExtendedBlockState(IExtendedBlockState state) 
	{

		state = setInventoryDisplay(state);

		return state;
	}

	protected IExtendedBlockState setInventoryDisplay(IExtendedBlockState state) 
	{
		PropertyItem.PropItems toDisplay = new PropertyItem.PropItems();
		EnumFacing facing = state.getValue(BlockAnvil.FACING);
		ItemStack stack = getStackInSlot(0);
		float x = 0, z = 0;
		if(stack != null) 
		{
			if(getStackInSlot(1) != null)
			{
				if(facing == EnumFacing.NORTH)
					x = 0.25f;
				else if(facing == EnumFacing.SOUTH)
					x = -0.25f;
				else if(facing == EnumFacing.EAST)
					z= 0.25f;
				else
					z= -0.25f;
			}
			PropertyItem.PItem item = getAnvilItem(stack, worldObj, null, x, z);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		stack = getStackInSlot(1);
		x = 0; z = 0;
		if(stack != null) 
		{
			if(getStackInSlot(0) != null)
			{
				if(facing == EnumFacing.NORTH)
					x = -0.25f;
				else if(facing == EnumFacing.SOUTH)
					x = 0.25f;
				else if(facing == EnumFacing.EAST)
					z= -0.25f;
				else
					z = 0.25f;
			}
			PropertyItem.PItem item = getAnvilItem(stack, worldObj, null, x, z);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		// add inventory if needed
		return state.withProperty(BlockAnvil.INVENTORY, toDisplay);
	}

	@SideOnly(Side.CLIENT)
	public static PropertyItem.PItem getAnvilItem(ItemStack stack, World world, EntityLivingBase entity, float x, float z) {
		if(stack == null)
			return null;

		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, entity);
		if(model == null || model.isBuiltInRenderer()) {
			// missing model so people don't go paranoid when their chests go missing
			model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getModelManager().getMissingModel();
		}

		PropertyItem.PItem item = new PropertyItem.PItem(model, x,0,z, 0.5f, (float) (Math.PI/2));
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

	public void setStrikePoint(int index, AnvilStrikePoint point)
	{
		hitArray[index] = point;
	}

	public void setStrikePoint(int x, int z, AnvilStrikePoint point)
	{
		setStrikePoint(getStrikePointIndex(x, z), point);
	}

	public AnvilStrikePoint getStrikePoint(int index)
	{
		return hitArray[index];
	}

	public AnvilStrikePoint getStrikePoint(int x, int z)
	{
		return getStrikePoint(getStrikePointIndex(x, z));
	}

	public static int getStrikePointIndex(int x, int z)
	{
		return z * 6 + x;
	}

	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = nbt.getTagList("inventory", 10);
		inventory = Helper.readStackArrayFromNBTList(invList, getSizeInventory());
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{
		smithID = new UUID(nbt.getLong("farmerID_least"), nbt.getLong("farmerID_most"));
	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = Helper.writeStackArrayToNBTList(inventory);
		nbt.setTag("inventory", invList);
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setLong("farmerID_least", this.smithID.getLeastSignificantBits());
		nbt.setLong("farmerID_most", this.smithID.getMostSignificantBits());
	}

	/*********************************************************
	 * IInventory Implementation
	 *********************************************************/
	@Override
	public String getName() {
		return "Anvil";
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

	/*********************************************************
	 * Associated Classes
	 *********************************************************/
	public static class AnvilStrikePoint
	{
		long birthTime;
		int lifeTime;
		AnvilStrikeType type;
		//Used on Client only
		boolean spawnedParticle = false;

		public long getBirthTime() {
			return birthTime;
		}
		public void setBirthTime(long timer) {
			this.birthTime = timer;
		}
		public AnvilStrikeType getType() {
			return type;
		}
		public void setType(AnvilStrikeType type) {
			this.type = type;
		}
		public boolean hasSpawnedParticle() {
			return spawnedParticle;
		}
		public void setSpawnedParticle(boolean spawnedParticle) {
			this.spawnedParticle = spawnedParticle;
		}
		public int getLifeTime() {
			return lifeTime;
		}
		public void setLifeTime(int lifeTime) {
			this.lifeTime = lifeTime;
		}
	}

	public enum AnvilStrikeType
	{
		HIT, CRITICAL;
	}
}
