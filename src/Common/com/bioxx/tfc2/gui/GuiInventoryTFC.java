package com.bioxx.tfc2.gui;

import java.awt.Rectangle;
import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.core.PlayerInventory;

public class GuiInventoryTFC extends InventoryEffectRenderer
{
	private float xSizeLow;
	private float ySizeLow;
	private boolean hasEffect;
	protected static final ResourceLocation UPPER_TEXTURE = new ResourceLocation(Reference.ModID+":textures/gui/inventory.png");
	protected static final ResourceLocation UPPER_TEXTURE_2X2 = new ResourceLocation(Reference.ModID+":textures/gui/gui_inventory2x2.png");
	protected static final ResourceLocation EFFECTS_TEXTURE = new ResourceLocation(Reference.ModID+":textures/gui/inv_effects.png");
	protected EntityPlayer player;
	protected Slot activeSlot;

	public GuiInventoryTFC(EntityPlayer player)
	{
		super(player.inventoryContainer);
		this.allowUserInput = true;
		player.addStat(AchievementList.OPEN_INVENTORY, 1);
		xSize = 176;
		ySize = 102 + PlayerInventory.invYSize;
		this.player = player;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(player.getEntityData().hasKey("craftingTable"))
			Core.bindTexture(UPPER_TEXTURE);
		else
			Core.bindTexture(UPPER_TEXTURE_2X2);
		int k = this.guiLeft;
		int l = this.guiTop;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, 102);

		//Draw the player avatar
		GuiInventory.drawEntityOnScreen(k + 51, l + 75, 30, k + 51 - this.xSizeLow, l + 75 - 50 - this.ySizeLow, this.mc.player);

		PlayerInventory.drawInventory(this, width, height, ySize - PlayerInventory.invYSize);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		//this.fontRenderer.drawString(I18n.format("container.crafting", new Object[0]), 86, 7, 4210752);
	}

	@Override
	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen()
	{
		if (this.mc.playerController.isInCreativeMode())
			this.mc.displayGuiScreen(new GuiContainerCreative(player));
	}

	@Override
	public void initGui()
	{
		super.buttonList.clear();

		if (this.mc.playerController.isInCreativeMode())
		{
			this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.player));
		}
		else
			super.initGui();

		if (!this.mc.player.getActivePotionEffects().isEmpty())
		{
			//this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
			this.guiLeft = (this.width - this.xSize) / 2;
			this.hasEffect = true;
		}

		buttonList.clear();
		buttonList.add(new GuiInventoryButton(0, new Rectangle(guiLeft+176, guiTop + 3, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Inventory.Inventory"), new Rectangle(1,223,32,32)));
		buttonList.add(new GuiInventoryButton(1, new Rectangle(guiLeft+176, guiTop + 22, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Inventory.Skills"), new Rectangle(100,223,32,32)));
		buttonList.add(new GuiInventoryButton(2, new Rectangle(guiLeft+176, guiTop + 41, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Calendar.Calendar"), new Rectangle(34,223,32,32)));
		buttonList.add(new GuiInventoryButton(3, new Rectangle(guiLeft+176, guiTop + 60, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Inventory.Health"), new Rectangle(67,223,32,32)));
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		//Removed during port
		if (guibutton.id == 1)
			Minecraft.getMinecraft().displayGuiScreen(new GuiSkills(player));
		/*else if (guibutton.id == 2)
			Minecraft.getMinecraft().displayGuiScreen(new GuiCalendar(player));*/
		else if (guibutton.id == 3)
			Minecraft.getMinecraft().displayGuiScreen(new GuiHealth(player));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		super.drawScreen(par1, par2, par3);
		this.xSizeLow = par1;
		this.ySizeLow = par2;
		if(hasEffect)
			displayDebuffEffects();

		//removed during port
		/*for (int j1 = 0; j1 < this.inventorySlots.inventorySlots.size(); ++j1)
		{
			Slot slot = (Slot)this.inventorySlots.inventorySlots.get(j1);
			if (this.isMouseOverSlot(slot, par1, par2) && slot.func_111238_b())
				this.activeSlot = slot;
		}*/
	}

	protected boolean isMouseOverSlot(Slot par1Slot, int par2, int par3)
	{
		return this.isPointInRegion(par1Slot.xPos, par1Slot.yPos, 16, 16, par2, par3);
	}

	/**
	 * Displays debuff/potion effects that are currently being applied to the player
	 */
	private void displayDebuffEffects()
	{
		int var1 = this.guiLeft - 124;
		int var2 = this.guiTop;
		Collection var4 = this.mc.player.getActivePotionEffects();

		//Remvoed during port
		/*if (!var4.isEmpty())
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			int var6 = 33;

			if (var4.size() > 5)
				var6 = 132 / (var4.size() - 1);

			for (Iterator var7 = this.mc.player.getActivePotionEffects().iterator(); var7.hasNext(); var2 += var6)
			{
				PotionEffect var8 = (PotionEffect)var7.next();
				Potion var9 = Potion.potionTypes[var8.getPotionID()] instanceof TFCPotion ? 
						((TFCPotion) Potion.potionTypes[var8.getPotionID()]) : 
							Potion.potionTypes[var8.getPotionID()];
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						TFC_Core.bindTexture(EFFECTS_TEXTURE);
						this.drawTexturedModalRect(var1, var2, 0, 166, 140, 32);

						if (var9.hasStatusIcon())
						{
							int var10 = var9.getStatusIconIndex();
							this.drawTexturedModalRect(var1 + 6, var2 + 7, 0 + var10 % 8 * 18, 198 + var10 / 8 * 18, 18, 18);
						}

						String var12 = Core.translate(var9.getName());

						if (var8.getAmplifier() == 1)
							var12 = var12 + " II";
						else if (var8.getAmplifier() == 2)
							var12 = var12 + " III";
						else if (var8.getAmplifier() == 3)
							var12 = var12 + " IV";

						this.fontRenderer.drawStringWithShadow(var12, var1 + 10 + 18, var2 + 6, 16777215);
						String var11 = Potion.getDurationString(var8);
						this.fontRenderer.drawStringWithShadow(var11, var1 + 10 + 18, var2 + 6 + 10, 8355711);
			}
		}*/
	}

	private long spamTimer;
	@Override
	protected boolean checkHotbarKeys(int keycode)
	{
		/*if(this.activeSlot != null && this.activeSlot.slotNumber == 0 && this.activeSlot.getHasStack() &&
				this.activeSlot.getStack().getItem() instanceof IFood)
			return false;*/
		return super.checkHotbarKeys(keycode);
	}

	private int getEmptyCraftSlot()
	{
		if(this.inventorySlots.getSlot(4).getStack() == null)
			return 4;
		if(this.inventorySlots.getSlot(1).getStack() == null)
			return 1;
		if(this.inventorySlots.getSlot(2).getStack() == null)
			return 2;
		if(this.inventorySlots.getSlot(3).getStack() == null)
			return 3;
		if(player.getEntityData().hasKey("craftingTable"))
		{
			if(this.inventorySlots.getSlot(45).getStack() == null)
				return 45;
			if(this.inventorySlots.getSlot(46).getStack() == null)
				return 46;
			if(this.inventorySlots.getSlot(47).getStack() == null)
				return 47;
			if(this.inventorySlots.getSlot(48).getStack() == null)
				return 48;
			if(this.inventorySlots.getSlot(49).getStack() == null)
				return 49;
		}

		return -1;
	}
}
