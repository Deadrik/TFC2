package com.bioxx.tfc2.tileentities;

import java.util.ArrayList;
import java.util.UUID;

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
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.crafting.KilnManager;
import com.bioxx.tfc2.api.crafting.KilnManager.KilnEntry;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.blocks.BlockPitKiln;
import com.bioxx.tfc2.blocks.BlockPitKiln.FillType;
import com.bioxx.tfc2.core.Timekeeper;

public class TilePitKiln extends TileTFC implements ITickable, IInventory
{
	UUID potterID;
	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(1, ItemStack.EMPTY);
	private int kilnCookTime = 8000;
	private long lastScanTime = 0;
	public CraftResult recentCraftResult = new CraftResult();


	public TilePitKiln()
	{
		potterID = new UUID(0L, 0L);
	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		Timekeeper time = Timekeeper.getInstance();

		if(!world.isRemote)
		{
			//We're currently cooking
			if(recentCraftResult.result == ProcessEnum.WORKING)
			{
				//Scan to make sure that the pit is still valid
				ArrayList<BlockPos> ignoreList = new ArrayList<BlockPos>();
				//ignore this block for recursive scans
				ignoreList.add(getPos());
				ValidityScanResult result = isPitValid(ignoreList);
				if(result != ValidityScanResult.SUCCEED)
				{
					//Track this failure and the failure time.
					recentCraftResult.result = ProcessEnum.FAILED;
					recentCraftResult.finishTime = Timekeeper.getInstance().getTotalTicks();
					endCrafting(ProcessEnum.FAILED);
				}
			}

			//Current time has surpassed the finish time
			if(recentCraftResult.finishTime <= time.getTotalTicks() && recentCraftResult.result == ProcessEnum.WORKING)
			{
				endCrafting(ProcessEnum.SUCCEED);
			}
		}
	}

	public void startCrafting()
	{
		//Make sure we aren't working right now
		if(!getWorld().isRemote && recentCraftResult.result != ProcessEnum.WORKING)
		{
			//We reset our craftresult tracker to a working state
			recentCraftResult.result = ProcessEnum.WORKING;
			recentCraftResult.finishTime = Timekeeper.getInstance().getTotalTicks() + kilnCookTime;
			recentCraftResult.startTime = Timekeeper.getInstance().getTotalTicks();

			TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());

			if(world.getBlockState(getPos().north()).getBlock() == TFCBlocks.PitKiln)
			{
				((TilePitKiln) world.getTileEntity(getPos().north())).startCrafting();
			}
			if(world.getBlockState(getPos().south()).getBlock() == TFCBlocks.PitKiln)
			{
				((TilePitKiln) world.getTileEntity(getPos().south())).startCrafting();
			}
			if(world.getBlockState(getPos().east()).getBlock() == TFCBlocks.PitKiln)
			{
				((TilePitKiln) world.getTileEntity(getPos().east())).startCrafting();
			}
			if(world.getBlockState(getPos().west()).getBlock() == TFCBlocks.PitKiln)
			{
				((TilePitKiln) world.getTileEntity(getPos().west())).startCrafting();
			}
		}
	}

	public void endCrafting(ProcessEnum r)
	{
		switch(r)
		{
		case SUCCEED:
			if(recentCraftResult.result == ProcessEnum.WORKING)
			{
				//Create the final item
				KilnEntry entry = KilnManager.getInstance().matches(this.getStackInSlot(0));
				if(entry != null)
					this.setInventorySlotContents(0, entry.outStack.copy());

				//this.setInventorySlotContents(1, ItemStack.EMPTY);
				world.setBlockState(getPos(), world.getBlockState(getPos()).withProperty(BlockPitKiln.FILLTYPE, FillType.Charcoal).withProperty(BlockPitKiln.FILL, 2));
				world.setBlockToAir(getPos().up());
				recentCraftResult.result = ProcessEnum.SUCCEED;
			}
			break;
		case WORKING:
		case FAILED:
			if(recentCraftResult.finishTime > Timekeeper.getInstance().getTotalTicks())//important to prevent recursive error
			{
				if(world.getBlockState(getPos().north()) == TFCBlocks.PitKiln)
				{
					((TilePitKiln) world.getTileEntity(getPos().north())).endCrafting(ProcessEnum.FAILED);
				}
				if(world.getBlockState(getPos().south()) == TFCBlocks.PitKiln)
				{
					((TilePitKiln) world.getTileEntity(getPos().south())).endCrafting(ProcessEnum.FAILED);
				}
				if(world.getBlockState(getPos().east()) == TFCBlocks.PitKiln)
				{
					((TilePitKiln) world.getTileEntity(getPos().east())).endCrafting(ProcessEnum.FAILED);
				}
				if(world.getBlockState(getPos().west()) == TFCBlocks.PitKiln)
				{
					((TilePitKiln) world.getTileEntity(getPos().west())).endCrafting(ProcessEnum.FAILED);
				}
			}
			recentCraftResult.result = ProcessEnum.FAILED;
			break;

		}

		//Resets the selected recipe

		//Send a packet to reset the info for the client
		TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
	}

	public ValidityScanResult isPitValid(ArrayList<BlockPos> ignoreList)
	{
		ignoreList.add(getPos());
		long totalTicks = Timekeeper.getInstance().getTotalTicks();
		//If this kiln has already failed then we should skip scanning and return a failure result
		if(this.recentCraftResult.result == ProcessEnum.FAILED)
			return ValidityScanResult.FAILED_SELF;
		//Don't scan if we just started the cooking so that all neighboring pits have time to start or if we've scanned this block this tick
		if((this.recentCraftResult.result == ProcessEnum.WORKING && totalTicks < 100) || lastScanTime == totalTicks)
			return ValidityScanResult.SUCCEED;
		lastScanTime = Timekeeper.getInstance().getTotalTicks();
		IBlockState stateN = world.getBlockState(getPos().north());
		IBlockState stateNU = world.getBlockState(getPos().north().up());
		IBlockState stateS = world.getBlockState(getPos().south());
		IBlockState stateSU = world.getBlockState(getPos().south().up());
		IBlockState stateE = world.getBlockState(getPos().east());
		IBlockState stateEU = world.getBlockState(getPos().east().up());
		IBlockState stateW = world.getBlockState(getPos().west());
		IBlockState stateWU = world.getBlockState(getPos().west().up());

		//Do a simple check for terrain first
		ValidityScanResult northResult = Core.isTerrain(stateN) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;
		ValidityScanResult southResult = Core.isTerrain(stateS) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;
		ValidityScanResult eastResult = Core.isTerrain(stateE) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;
		ValidityScanResult westResult = Core.isTerrain(stateW) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;

		//check if we have soil on the second layer as well or if the neighbor is a kiln, we check for wood
		ValidityScanResult northUpResult = Core.isTerrain(stateNU) || (northResult != ValidityScanResult.SUCCEED && Core.isPlacedLog(stateNU)) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;
		ValidityScanResult southUpResult = Core.isTerrain(stateSU) || (southResult != ValidityScanResult.SUCCEED && Core.isPlacedLog(stateSU)) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;
		ValidityScanResult eastUpResult = Core.isTerrain(stateEU) || (eastResult != ValidityScanResult.SUCCEED && Core.isPlacedLog(stateEU)) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;
		ValidityScanResult westUpResult = Core.isTerrain(stateWU) || (westResult != ValidityScanResult.SUCCEED && Core.isPlacedLog(stateWU)) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;

		//Now if we have any failures in cardinal directions, check to see if its a Pitkiln
		boolean scanNorth = false, scanSouth = false, scanEast = false, scanWest = false, kilnNorth = false, kilnSouth = false, kilnEast = false, kilnWest = false;
		if(northResult != ValidityScanResult.SUCCEED && stateN.getBlock() == TFCBlocks.PitKiln)
		{
			kilnNorth = true;
			//Make sure that we are not doing a recursive scan by checking the ignore list for this pitkiln
			if(!ignoreList.contains(getPos().north()))
			{
				scanNorth = true;
			}
			else {northResult = ValidityScanResult.SUCCEED;}
		}
		if(southResult != ValidityScanResult.SUCCEED && stateS.getBlock() == TFCBlocks.PitKiln)
		{
			kilnSouth = true;
			if(!ignoreList.contains(getPos().south()))
			{
				scanSouth = true;
			}
			else {southResult = ValidityScanResult.SUCCEED;}
		}
		if(eastResult != ValidityScanResult.SUCCEED && stateE.getBlock() == TFCBlocks.PitKiln)
		{
			kilnEast = true;
			if(!ignoreList.contains(getPos().east()))
			{
				scanEast = true;
			}
			else {eastResult = ValidityScanResult.SUCCEED;}
		}
		if(westResult != ValidityScanResult.SUCCEED && stateW.getBlock() == TFCBlocks.PitKiln)
		{
			kilnWest = true;
			if(!ignoreList.contains(getPos().west()))
			{
				scanWest = true;

			}
			else {westResult = ValidityScanResult.SUCCEED;}
		}

		//If any direction is new pitkiln that doesnt exist in the ignore list then check its validity
		if(scanNorth)
		{
			northResult = ((TilePitKiln) world.getTileEntity(getPos().north())).isPitValid(ignoreList);
			if(northResult == ValidityScanResult.FAILED_SELF)
				northResult = ValidityScanResult.FAILED_OTHER;
			northUpResult = northResult;
		}
		if(scanSouth)
		{
			southResult = ((TilePitKiln) world.getTileEntity(getPos().south())).isPitValid(ignoreList);
			if(southResult == ValidityScanResult.FAILED_SELF)
				southResult = ValidityScanResult.FAILED_OTHER;
			southUpResult = southResult;
		}
		if(scanEast)
		{
			eastResult = ((TilePitKiln) world.getTileEntity(getPos().east())).isPitValid(ignoreList);
			if(eastResult == ValidityScanResult.FAILED_SELF)
				eastResult = ValidityScanResult.FAILED_OTHER;
			eastUpResult = eastResult;
		}
		if(scanWest)
		{
			westResult = ((TilePitKiln) world.getTileEntity(getPos().west())).isPitValid(ignoreList);
			if(westResult == ValidityScanResult.FAILED_SELF)
				westResult = ValidityScanResult.FAILED_OTHER;
			westUpResult = westUpResult == ValidityScanResult.FAILED_SELF ? westResult : westUpResult;
		}

		//If there is an airpocket above a nearby kiln but we're still early in the cooking process then ignore it so the palyer has time to fill it in.
		if(northUpResult == ValidityScanResult.FAILED_SELF && kilnNorth && northResult == ValidityScanResult.SUCCEED && totalTicks < recentCraftResult.startTime+500)
		{
			northUpResult = ValidityScanResult.SUCCEED;
		}
		if(southUpResult == ValidityScanResult.FAILED_SELF && kilnSouth && southResult == ValidityScanResult.SUCCEED && totalTicks < recentCraftResult.startTime+500)
		{
			southUpResult = ValidityScanResult.SUCCEED;
		}
		if(eastUpResult == ValidityScanResult.FAILED_SELF && kilnEast && eastResult == ValidityScanResult.SUCCEED && totalTicks < recentCraftResult.startTime+500)
		{
			eastUpResult = ValidityScanResult.SUCCEED;
		}
		if(westUpResult == ValidityScanResult.FAILED_SELF && kilnWest && westResult == ValidityScanResult.SUCCEED && totalTicks < recentCraftResult.startTime+500)
		{
			westUpResult = ValidityScanResult.SUCCEED;
		}

		ValidityScanResult upResult = (Core.isPlacedLog(world.getBlockState(getPos().up())) && Core.isTerrain(world.getBlockState(getPos().up(2)))) ? ValidityScanResult.SUCCEED : ValidityScanResult.FAILED_SELF;
		//If the upResult is a failure but we just recently started the cooking process then we ignore the result
		if(upResult != ValidityScanResult.SUCCEED &&  totalTicks < recentCraftResult.startTime+500)
			upResult = ValidityScanResult.SUCCEED;

		ValidityScanResult result = areAnyResultsFail(upResult, northResult, southResult, eastResult, westResult, 
				northUpResult, southUpResult, eastUpResult, westUpResult);
		return result;
	}

	private ValidityScanResult areAnyResultsFail(ValidityScanResult... results)
	{
		ValidityScanResult out = ValidityScanResult.SUCCEED;
		for(ValidityScanResult r : results)
			if(r != ValidityScanResult.SUCCEED)
			{
				if(r == ValidityScanResult.FAILED_SELF)
					out = r;
				else
					return r;
			}

		return out;
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
		if(stack != ItemStack.EMPTY) 
		{
			PropertyItem.PItem item = getDisplayItem(stack, world, null, x, z);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		// add inventory if needed
		return state.withProperty(BlockPitKiln.INVENTORY, toDisplay);
	}

	@SideOnly(Side.CLIENT)
	public static PropertyItem.PItem getDisplayItem(ItemStack stack, World world, EntityLivingBase entity, float x, float z) {
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

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return false;
	}

	/***********************************************************************************
	 * 2. Getters and Setters
	 ***********************************************************************************/

	public void setSmithID(EntityPlayer player)
	{
		potterID = EntityPlayer.getUUID(player.getGameProfile());
	}

	public EntityPlayer getSmith()
	{
		if(!world.isRemote)
			return world.getMinecraftServer().getPlayerList().getPlayerByUUID(potterID);
		else return TFC.proxy.getPlayer();
	}

	public NonNullList<ItemStack> getInventory()
	{
		return this.inventory;
	}

	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = nbt.getTagList("inventory", 10);
		inventory = Helper.readStackArrayFromNBTList(invList, getSizeInventory());
		recentCraftResult.startTime = nbt.getLong("startTime");
		recentCraftResult.result = ProcessEnum.values()[nbt.getInteger("result")];
		recentCraftResult.finishTime = nbt.getLong("endTime");
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{
		potterID = new UUID(nbt.getLong("potterID_least"), nbt.getLong("potterID_most"));
	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = Helper.writeStackArrayToNBTList(inventory);
		nbt.setTag("inventory", invList);
		nbt.setLong("startTime", recentCraftResult.startTime);
		nbt.setInteger("result", recentCraftResult.result.ordinal());
		nbt.setLong("endTime", recentCraftResult.finishTime);
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setLong("potterID_least", this.potterID.getLeastSignificantBits());
		nbt.setLong("potterID_most", this.potterID.getMostSignificantBits());

	}

	/*********************************************************
	 * IInventory Implementation
	 *********************************************************/
	@Override
	public String getName() {
		return "pitkiln";
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

	/*********************************************************
	 * Associated Classes
	 *********************************************************/
	public enum ProcessEnum
	{
		SUCCEED, FAILED, WORKING;
	}

	public enum ValidityScanResult
	{
		SUCCEED, FAILED_SELF, FAILED_OTHER;
	}

	public class CraftResult
	{
		public ProcessEnum result = ProcessEnum.FAILED;
		public long finishTime = 0;
		public long startTime = 0;
	}
}
