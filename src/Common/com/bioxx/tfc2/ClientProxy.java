package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.bioxx.tfc2.Handlers.Client.KeyBindingHandler;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.Util.KeyBindings;

public class ClientProxy extends CommonProxy
{
	private static ModelResourceLocation freshwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "freshwater");
	private static ModelResourceLocation saltwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "saltwater");

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		Item fresh = Item.getItemFromBlock(TFCBlocks.FreshWater);
		Item salt = Item.getItemFromBlock(TFCBlocks.SaltWater);
		ModelBakery.addVariantName(fresh);
		ModelBakery.addVariantName(salt);
		ModelLoader.setCustomMeshDefinition(fresh, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return freshwaterLocation;
			}
		});
		ModelLoader.setCustomMeshDefinition(salt, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.FreshWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation func_178132_a(IBlockState state)//getModelResourceLocation
			{
				return freshwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.SaltWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation func_178132_a(IBlockState state)//getModelResourceLocation
			{
				return saltwaterLocation;
			}
		});
	}

	@Override
	public void registerRenderInformation()
	{
		MinecraftForge.EVENT_BUS.register(new RenderOverlayHandler());
		for(int l = 0; l < Global.STONE_ALL.length; l++)
		{
			String stone = Core.textConvert(Global.STONE_ALL[l]);

			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Dirt), l, new ModelResourceLocation(Reference.ModID + ":Dirt/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Grass), l, new ModelResourceLocation(Reference.ModID + ":Grass/" + stone + "/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Stone), l, new ModelResourceLocation(Reference.ModID + ":Stone/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Rubble), l, new ModelResourceLocation(Reference.ModID + ":Rubble/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Sand), l, new ModelResourceLocation(Reference.ModID + ":Sand/" + stone, "inventory"));
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Gravel), l, new ModelResourceLocation(Reference.ModID + ":Gravel/" + stone, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Dirt), Reference.ModID + ":Dirt/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Grass), Reference.ModID + ":Grass/" + stone + "/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Stone), Reference.ModID + ":Stone/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Rubble), Reference.ModID + ":Rubble/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Sand), Reference.ModID + ":Sand/" + stone);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Gravel), Reference.ModID + ":Gravel/" + stone);
		}
		for(int l = 0; l < Global.WOOD_STANDARD.length; l++)
		{
			String wood = Core.textConvert(Global.WOOD_STANDARD[l]);

			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Planks), l, new ModelResourceLocation(Reference.ModID + ":Wood/Planks/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Planks), Reference.ModID + ":Wood/Planks/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.Sapling), l, new ModelResourceLocation(Reference.ModID + ":Wood/Saplings/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.Sapling), Reference.ModID + ":Wood/Saplings/" + wood);
			registerItemMesh(Item.getItemFromBlock(TFCBlocks.LogVertical), l, new ModelResourceLocation(Reference.ModID + ":Wood/Logs/" + wood, "inventory"));
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogVertical), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogHorizontal), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogHorizontal2), Reference.ModID + ":Wood/Logs/" + wood);
			ModelBakery.addVariantName(Item.getItemFromBlock(TFCBlocks.LogNatural), Reference.ModID + ":Wood/Logs/" + wood);

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
