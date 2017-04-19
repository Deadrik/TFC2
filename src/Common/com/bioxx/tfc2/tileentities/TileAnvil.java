package com.bioxx.tfc2.tileentities;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC.RecipeType;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.blocks.BlockAnvil;
import com.bioxx.tfc2.core.Timekeeper;
import com.bioxx.tfc2.networking.client.CAnvilStrikePacket;
import com.bioxx.tfc2.networking.server.SAnvilCraftingPacket;

public class TileAnvil extends TileTFC implements ITickable, IInventory
{
	UUID smithID;
	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
	AnvilStrikePoint[] hitArray;
	int anvilRecipeIndex = -1;
	int craftingTimer = 0;
	int craftingProgress = 0;
	AnvilStrikeType strikeTarget;
	ArrayList<Integer> availableHitPoints = new ArrayList<Integer>();

	public TileAnvil()
	{
		smithID = new UUID(0L, 0L);
		hitArray = new AnvilStrikePoint[24];

	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		Timekeeper time = Timekeeper.getInstance();
		if(craftingTimer > 0)
			craftingTimer--;

		generateStrikePoints();

		resetStrikePoints();

		if(craftingTimer == 0 && craftingProgress >= 10)
			endCrafting(CraftingResult.SUCCEED);
		else if(craftingTimer == 0)
			endCrafting(CraftingResult.FAILED);
	}

	protected void generateStrikePoints()
	{
		Timekeeper time = Timekeeper.getInstance();
		if(!world.isRemote && craftingTimer > 0 && craftingTimer % 10 == 0)
		{
			AnvilStrikePoint p = new AnvilStrikePoint();

			p.setBirthTime(time.getTotalTicks());
			//Create Specials
			if(world.rand.nextFloat() < 0.25)
			{
				if(world.rand.nextFloat() < 0.25)
				{
					int type = world.rand.nextInt(3);
					if(type == 0)
						p.setType(AnvilStrikeType.EFF_CRIT);
					else if(type == 1)
						p.setType(AnvilStrikeType.DUR_CRIT);
					else if(type == 2)
						p.setType(AnvilStrikeType.DAM_CRIT);
					else if(type == 3)
						p.setType(AnvilStrikeType.SPD_CRIT);

					p.setLifeTime(50);
				}
				else
				{
					int type = world.rand.nextInt(3);
					if(type == 0)
						p.setType(AnvilStrikeType.EFF_NORM);
					else if(type == 1)
						p.setType(AnvilStrikeType.DUR_NORM);
					else if(type == 2)
						p.setType(AnvilStrikeType.DAM_NORM);
					else if(type == 3)
						p.setType(AnvilStrikeType.SPD_NORM);

					p.setLifeTime(100);
				}
			}
			else
			{
				int type = world.rand.nextInt(7);
				if(type == 0)
					p.setType(AnvilStrikeType.HIT_LIGHT);
				else if(type == 1)
					p.setType(AnvilStrikeType.HIT_MEDIUM);
				else if(type == 2)
					p.setType(AnvilStrikeType.HIT_HEAVY);
				else if(type == 3)
					p.setType(AnvilStrikeType.HIT_BEND);
				else if(type == 4)
					p.setType(AnvilStrikeType.HIT_UPSET);
				else if(type == 5)
					p.setType(AnvilStrikeType.HIT_PUNCH);
				else if(type == 6)
					p.setType(AnvilStrikeType.HIT_SHRINK);

				p.setLifeTime(100);
			}


			int xz = world.rand.nextInt(availableHitPoints.size());
			int index = availableHitPoints.remove(xz);

			if(this.getStrikePoint(index) == null)
			{
				this.setStrikePoint(index, p);
				sendSmithingPacket(index, p);
			}
		}
	}

	protected void resetStrikePoints()
	{
		Timekeeper time = Timekeeper.getInstance();
		if(craftingTimer <= 0)
		{
			for(int i = 0; i < 24; i++)
			{
				AnvilStrikePoint p = hitArray[i];
				if(p != null)
				{
					resetStrikePoint(i);
				}
			}
		}
		else
		{
			for(int i = 0; i < 24; i++)
			{
				AnvilStrikePoint p = hitArray[i];
				if(p != null && p.getBirthTime()+p.getLifeTime() < time.getTotalTicks())
				{
					resetStrikePoint(i);
				}
			}
		}
	}

	public boolean resetStrikePoint(int index)
	{
		if(index < 24 && this.getStrikePoint(index) != null)
		{
			this.setStrikePoint(index, null);
			this.availableHitPoints.add(index);
			return true;
		}
		return false;
	}

	public void hitStrikePoint(int index)
	{
		craftingProgress++;
		resetStrikePoint(index);
	}

	public void endCrafting(CraftingResult r)
	{
		craftingTimer = -1;//This effectively turns off crafting stuff when we're done

		switch(r)
		{
		case SUCCEED:
			//Create the final item
			this.setInventorySlotContents(1, ItemStack.EMPTY);
			this.setInventorySlotContents(0, CraftingManagerTFC.getInstance().getRecipeList(RecipeType.ANVIL).get(anvilRecipeIndex).getRecipeOutput().copy());
			//world.setBlockState(pos, this.blockType.getExtendedState(world.getBlockState(pos), world, pos), 0x2);
			break;
		case FAILED:
			//Do something if the smith fails
			break;
		}

		anvilRecipeIndex = -1;//Resets the selected recipe

		//Send a packet to reset the info for the client
	}

	public void sendSmithingPacket(int xz, AnvilStrikePoint point)
	{
		EntityPlayerMP player = world.getMinecraftServer().getPlayerList().getPlayerByUUID(smithID);
		if(player != null)
			TFC.network.sendTo(new CAnvilStrikePacket(this.getPos(), xz, point), player);
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
		if(stack != ItemStack.EMPTY) 
		{
			if(getStackInSlot(1) != ItemStack.EMPTY)
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
			PropertyItem.PItem item = getAnvilItem(stack, world, null, x, z);
			if(item != null) {
				toDisplay.items.add(item);
			}
		}
		stack = getStackInSlot(1);
		x = 0; z = 0;
		if(stack != ItemStack.EMPTY) 
		{
			if(getStackInSlot(0) != ItemStack.EMPTY)
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
			PropertyItem.PItem item = getAnvilItem(stack, world, null, x, z);
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

	@SideOnly(Side.CLIENT)
	public void selectRecipe(int index)
	{
		this.anvilRecipeIndex = index;
	}

	public void startCrafting(UUID id)
	{
		if(anvilRecipeIndex == -1)
			return;
		if(world.isRemote)
		{
			TFC.network.sendToServer(new SAnvilCraftingPacket(this.getPos(),this.getAnvilRecipeIndex(), true, id));
			this.craftingTimer = 500;
		}
		else
		{
			if(craftingTimer <= 0)
			{
				this.smithID = id;
				this.craftingTimer = 500;
				for(int i = 0; i < 24; i++)
				{
					availableHitPoints.add(i);
				}
			}
		}
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
		smithID = EntityPlayer.getUUID(player.getGameProfile());
	}

	public EntityPlayer getSmith()
	{
		if(!world.isRemote)
			return world.getMinecraftServer().getPlayerList().getPlayerByUUID(smithID);
		else return TFC.proxy.getPlayer();
	}

	public void setStrikePoint(int index, AnvilStrikePoint point)
	{
		hitArray[index] = point;
		if(point == null)
			availableHitPoints.add(index);
	}

	public void setStrikePoint(int x, int z, AnvilStrikePoint point)
	{
		setStrikePoint(getStrikePointIndex(x, z), point);
	}

	public AnvilStrikePoint getStrikePoint(int index)
	{
		if(index < 0 || index > 23) return null;
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

	public int getAnvilRecipeIndex() {
		return anvilRecipeIndex;
	}

	public void setAnvilRecipeIndex(int anvilRecipeIndex) {
		this.anvilRecipeIndex = anvilRecipeIndex;
	}

	public int getTimer() {
		return craftingTimer;
	}

	public void setTimer(int timer) {
		this.craftingTimer = timer;
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
		if(id == 0)
			return this.getAnvilRecipeIndex();
		return -1;
	}

	@Override
	public void setField(int id, int value) 
	{
		if(id == 0)
		{
			this.setAnvilRecipeIndex(value);
			if(world.isRemote)
			{
				TFC.network.sendToServer(new SAnvilCraftingPacket(this.getPos(), this.getAnvilRecipeIndex(), false, this.smithID));
			}
		}
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		for(int i = 0; i < this.getSizeInventory(); i++)
		{
			this.setInventorySlotContents(i, ItemStack.EMPTY);
		}
	}

	/*********************************************************
	 * Associated Classes
	 *********************************************************/
	public static class AnvilStrikePoint
	{
		long birthTime;
		int lifeTime;
		AnvilStrikeType type;

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
		public int getLifeTime() {
			return lifeTime;
		}
		public void setLifeTime(int lifeTime) {
			this.lifeTime = lifeTime;
		}
	}

	public enum AnvilStrikeType
	{
		EFF_NORM(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/eff_norm.png")), 
		EFF_CRIT(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/eff_crit.png")),
		DUR_NORM(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/dur_norm.png")), 
		DUR_CRIT(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/dur_crit.png")),
		SPD_NORM(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/spd_norm.png")), 
		SPD_CRIT(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/spd_crit.png")),
		DAM_NORM(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/dam_norm.png")), 
		DAM_CRIT(Core.CreateRes(Reference.ModID+":textures/blocks/anvil/dam_crit.png")), 
		HIT_LIGHT(Core.CreateRes(Reference.ModID+":textures/gui/anvil_hit_light.png")), 
		HIT_MEDIUM(Core.CreateRes(Reference.ModID+":textures/gui/anvil_hit_medium.png")), 
		HIT_HEAVY(Core.CreateRes(Reference.ModID+":textures/gui/anvil_hit_heavy.png")), 
		HIT_BEND(Core.CreateRes(Reference.ModID+":textures/gui/anvil_bend.png")), 
		HIT_UPSET(Core.CreateRes(Reference.ModID+":textures/gui/anvil_upset.png")), 
		HIT_PUNCH(Core.CreateRes(Reference.ModID+":textures/gui/anvil_punch.png")), 
		HIT_SHRINK(Core.CreateRes(Reference.ModID+":textures/gui/anvil_shrink.png"));

		ResourceLocation texture;

		AnvilStrikeType(ResourceLocation image)
		{
			texture = image;
		}

		public ResourceLocation getTexture() 
		{
			return texture;
		}
	}

	public enum CraftingResult
	{
		SUCCEED, FAILED;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}
}
