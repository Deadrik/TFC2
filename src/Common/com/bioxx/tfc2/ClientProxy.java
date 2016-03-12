package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;

import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.types.OreType;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.api.util.KeyBindings;
import com.bioxx.tfc2.blocks.BlockLeaves2;
import com.bioxx.tfc2.core.RegistryItemQueue;
import com.bioxx.tfc2.entity.*;
import com.bioxx.tfc2.handlers.client.*;
import com.bioxx.tfc2.rendering.MeshDef;
import com.bioxx.tfc2.rendering.model.*;

public class ClientProxy extends CommonProxy
{
	private static ModelResourceLocation freshwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "freshwater");
	private static ModelResourceLocation saltwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "saltwater");

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);
		setupBlockMeshes();
	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new RenderOverlayHandler());
		MinecraftForge.EVENT_BUS.register(new ClientRenderHandler());
		MinecraftForge.EVENT_BUS.register(new BackgroundMusicHandler());


		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityCart.class, new RenderCart(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityBear.class, new RenderBear(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityBearPanda.class, new RenderBearPanda(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityLion.class, new RenderLion(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTiger.class, new RenderTiger(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityRhino.class, new RenderRhino(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityElephant.class, new RenderElephant(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityMammoth.class, new RenderMammoth(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityBoar.class, new RenderBoar(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityBison.class, new RenderBison(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityFoxRed.class, new RenderFoxRed());
		RenderingRegistry.registerEntityRenderingHandler(EntityFoxArctic.class, new RenderFoxArctic());
		RenderingRegistry.registerEntityRenderingHandler(EntityFoxDesert.class, new RenderFoxDesert());
		RenderingRegistry.registerEntityRenderingHandler(EntityHippo.class, new RenderHippo());

		//Disable vanilla UI elements
		GuiIngameForge.renderHealth = false;
		GuiIngameForge.renderArmor = false;
		GuiIngameForge.renderExperiance = false;
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);

	}

	private void registerItemMesh(Item i, ModelResourceLocation mrl)
	{
		ModelLoader.setCustomMeshDefinition(i, new MeshDef(mrl));
	}

	private void registerItemMesh(Item i, int meta, ModelResourceLocation mrl)
	{
		ModelLoader.setCustomModelResourceLocation(i, meta, mrl);
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

	@Override
	public boolean isClientSide()
	{
		return true;
	}

	@Override
	public void registerGuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(TFC.instance, new GuiHandler());
		// Register Gui Event Handler
		MinecraftForge.EVENT_BUS.register(new GuiHandler());
	}

	//Keep at the bottom of the file so its not a nuisence
	private void setupBlockMeshes() 
	{
		OBJLoader.instance.addDomain(Reference.ModID);

		//Setup Liquid Blocks
		Item fresh = Item.getItemFromBlock(TFCBlocks.FreshWater);
		Item salt = Item.getItemFromBlock(TFCBlocks.SaltWater);
		Item saltstatic = Item.getItemFromBlock(TFCBlocks.SaltWaterStatic);
		Item freshstatic = Item.getItemFromBlock(TFCBlocks.FreshWaterStatic);
		/*ModelBakery.addVariantName(fresh);
		ModelBakery.addVariantName(salt);
		ModelBakery.addVariantName(saltstatic);
		ModelBakery.addVariantName(freshstatic);*/
		/*ModelLoader.setCustomMeshDefinition(fresh, new ItemMeshDefinition()
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
		ModelLoader.setCustomMeshDefinition(saltstatic, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomMeshDefinition(freshstatic, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return freshwaterLocation;
			}
		});*/
		ModelLoader.setCustomStateMapper(TFCBlocks.FreshWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return freshwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.SaltWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.SaltWaterStatic, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return saltwaterLocation;
			}
		});
		ModelLoader.setCustomStateMapper(TFCBlocks.FreshWaterStatic, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return freshwaterLocation;
			}
		});
		//End Liquids

		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.LooseRocks), 0, new ModelResourceLocation(Reference.ModID + ":loose_rock", "inventory"));

		//Change the StateMapper for this block so that it will point to a different file for a specific Property
		StateMapperBase ignoreState = new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState iBlockState) 
			{
				if(iBlockState.getValue(BlockLeaves2.META_PROPERTY) == WoodType.Palm)
					return new ModelResourceLocation("tfc2:leaves_palm");
				else return new ModelResourceLocation("tfc2:leaves2");

			}
		};
		ModelLoader.setCustomStateMapper(TFCBlocks.Leaves2, ignoreState);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.FreshWater),0,freshwaterLocation);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.FreshWaterStatic),0,freshwaterLocation);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.SaltWater),0,saltwaterLocation);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.SaltWaterStatic),0,saltwaterLocation);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Dirt), "Dirt/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Grass), "Grass/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Stone), "Stone/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Rubble), "Rubble/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Sand), "Sand/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Gravel), "Gravel/", Global.STONE_ALL, 0, 16);
		//registerVariantModel(Item.getItemFromBlock(TFCBlocks.LooseRocks), "LooseRocks/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Planks), "Wood/Planks/", WoodType.getNamesArray(), 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Sapling), "Wood/Saplings/", WoodType.getNamesArray(), 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.LogVertical), "Wood/Logs/", WoodType.getNamesArray(), 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Leaves), "Wood/Leaves/", WoodType.getNamesArray(), 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Planks2), "Wood/Planks/", WoodType.getNamesArray(), 16, 18);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Sapling2), "Wood/Saplings/", WoodType.getNamesArray(), 16, 19);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.LogVertical2), "Wood/Logs/", WoodType.getNamesArray(), 16, 19);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Leaves2), "Wood/Leaves/", WoodType.getNamesArray(), 16, 18);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.Leaves2), 18, new ModelResourceLocation(Reference.ModID + ":leaves_palm", "inventory"));
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Ore), "Ore/", OreType.getNamesArray(), 0, 13);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.StoneBrick), "StoneBrick/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.StoneSmooth), "StoneSmooth/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.SupportBeam), "Wood/SupportBeams/", WoodType.getNamesArray(), 0, 8);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.SupportBeam2), "Wood/SupportBeams/", WoodType.getNamesArray(), 8, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.SupportBeam3), "Wood/SupportBeams/", WoodType.getNamesArray(), 16, 18);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.TorchOn),0,new ModelResourceLocation(Reference.ModID + ":torch_on", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.TorchOff),0,new ModelResourceLocation(Reference.ModID + ":torch_off", "inventory"));
		RegistryItemQueue.getInstance().registerMeshes();
	}

	private void registerVariantModel(Item item, String path, String[] variantNames, int metaStart, int metaEnd)
	{
		for(int meta = metaStart; meta < metaEnd; meta++)
		{
			String vName = Core.textConvert(variantNames[meta]);
			ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Reference.ModID + ":" + path + vName, "inventory");
			ModelLoader.setCustomModelResourceLocation(item, meta, itemModelResourceLocation);
		}
	}
}
