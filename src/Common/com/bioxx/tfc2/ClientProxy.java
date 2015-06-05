package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.bioxx.tfc2.Handlers.Client.KeyBindingHandler;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.Util.KeyBindings;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerRenderInformation()
	{
		MinecraftForge.EVENT_BUS.register(new RenderOverlayHandler());
		for(int l = 0; l < Global.STONE_ALL.length; l++)
		{
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Dirt), l, new ModelResourceLocation(Reference.ModID + ":Dirt/" + Global.STONE_ALL[l], "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Grass), l, new ModelResourceLocation(Reference.ModID + ":Grass/" + Global.STONE_ALL[l]+"/"+Global.STONE_ALL[l], "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Stone), l, new ModelResourceLocation(Reference.ModID + ":Stone/" + Global.STONE_ALL[l], "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Rubble), l, new ModelResourceLocation(Reference.ModID + ":Rubble/" + Global.STONE_ALL[l], "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Sand), l, new ModelResourceLocation(Reference.ModID + ":Sand/" + Global.STONE_ALL[l], "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Gravel), l, new ModelResourceLocation(Reference.ModID + ":Gravel/" + Global.STONE_ALL[l], "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Dirt), Reference.ModID + ":Dirt/" + Global.STONE_ALL[l]);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Grass), Reference.ModID + ":Grass/" + Global.STONE_ALL[l]+"/"+Global.STONE_ALL[l]);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Stone), Reference.ModID + ":Stone/" + Global.STONE_ALL[l]);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Rubble), Reference.ModID + ":Rubble/" + Global.STONE_ALL[l]);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Sand), Reference.ModID + ":Sand/" + Global.STONE_ALL[l]);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Gravel), Reference.ModID + ":Gravel/" + Global.STONE_ALL[l]);
		}
		for(int l = 0; l < Global.WOOD_STANDARD.length; l++)
		{
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Planks), l, new ModelResourceLocation(Reference.ModID + ":Wood/Planks/" + Global.WOOD_STANDARD[l], "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Planks), Reference.ModID + ":Wood/Planks/" + Global.WOOD_STANDARD[l]);

		}
	}

	private void registerItemMesh(Item i, int meta, ModelResourceLocation mrl)
	{
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(i, meta, mrl);
	}

	@Override
	public File getMinecraftDir()
	{
		return Minecraft.getMinecraft().mcDataDir;
	}

	@Override
	public void registerKeys()
	{
		//KeyBindings.addKeyBinding(KeyBindingHandler.Key_CombatMode);
		//KeyBindings.addIsRepeating(false);
		//ClientRegistry.registerKeyBinding(KeyBindingHandler.Key_ToolMode);
		//ClientRegistry.registerKeyBinding(KeyBindingHandler.Key_LockTool);
		ClientRegistry.registerKeyBinding(KeyBindingHandler.Key_CombatMode);
		//uploadKeyBindingsToGame();
	}

	@Override
	public void registerKeyBindingHandler()
	{
		FMLCommonHandler.instance().bus().register(new KeyBindingHandler());
	}

	@Override
	public void uploadKeyBindingsToGame()
	{
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		KeyBinding[] tfcKeyBindings = KeyBindings.gatherKeyBindings();
		KeyBinding[] allKeys = new KeyBinding[settings.keyBindings.length + tfcKeyBindings.length];
		System.arraycopy(settings.keyBindings, 0, allKeys, 0, settings.keyBindings.length);
		System.arraycopy(tfcKeyBindings, 0, allKeys, settings.keyBindings.length, tfcKeyBindings.length);
		settings.keyBindings = allKeys;
		settings.loadOptions();
	}

}
