package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.api.util.KeyBindings;
import com.bioxx.tfc2.blocks.BlockLeaves;
import com.bioxx.tfc2.blocks.BlockLeaves2;
import com.bioxx.tfc2.blocks.BlockVegDesert;
import com.bioxx.tfc2.blocks.BlockVegDesert.DesertVegType;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.BlockVegetation.VegType;
import com.bioxx.tfc2.core.RegistryItemQueue;
import com.bioxx.tfc2.entity.*;
import com.bioxx.tfc2.handlers.client.*;
import com.bioxx.tfc2.rendering.MeshDef;
import com.bioxx.tfc2.rendering.model.*;
import com.bioxx.tfc2.rendering.tesr.AnvilTESR;
import com.bioxx.tfc2.tileentities.TileAnvil;
import com.bioxx.tfc2.world.WorldGen;

public class ClientProxy extends CommonProxy
{
	private static ModelResourceLocation freshwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "freshwater");
	private static ModelResourceLocation saltwaterLocation = new ModelResourceLocation(Reference.getResID() + "liquids", "saltwater");

	@Override
	public void preInit(FMLPreInitializationEvent event)
	{
		super.preInit(event);

		setupBlockMeshes();

		//Entities
		RenderingRegistry.registerEntityRenderingHandler(EntityCart.class, new IRenderFactory<EntityCart>() { 
			@Override
			public Render<? super EntityCart> createRenderFor(RenderManager manager) {
				return new RenderCart(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityBear.class, new IRenderFactory<EntityBear>() { 
			@Override
			public Render<? super EntityBear> createRenderFor(RenderManager manager) {
				return new RenderBear(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityBearPanda.class, new IRenderFactory<EntityBearPanda>() { 
			@Override
			public Render<? super EntityBearPanda> createRenderFor(RenderManager manager) {
				return new RenderBearPanda(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityLion.class, new IRenderFactory<EntityLion>() { 
			@Override
			public Render<? super EntityLion> createRenderFor(RenderManager manager) {
				return new RenderLion(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityTiger.class, new IRenderFactory<EntityTiger>() { 
			@Override
			public Render<? super EntityTiger> createRenderFor(RenderManager manager) {
				return new RenderTiger(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityRhino.class, new IRenderFactory<EntityRhino>() { 
			@Override
			public Render<? super EntityRhino> createRenderFor(RenderManager manager) {
				return new RenderRhino(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityElephant.class, new IRenderFactory<EntityElephant>() { 
			@Override
			public Render<? super EntityElephant> createRenderFor(RenderManager manager) {
				return new RenderElephant(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityMammoth.class, new IRenderFactory<EntityMammoth>() { 
			@Override
			public Render<? super EntityMammoth> createRenderFor(RenderManager manager) {
				return new RenderMammoth(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityBoar.class, new IRenderFactory<EntityBoar>() { 
			@Override
			public Render<? super EntityBoar> createRenderFor(RenderManager manager) {
				return new RenderBoar(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityBison.class, new IRenderFactory<EntityBison>() { 
			@Override
			public Render<? super EntityBison> createRenderFor(RenderManager manager) {
				return new RenderBison(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityFoxRed.class, new IRenderFactory<EntityFoxRed>() { 
			@Override
			public Render<? super EntityFoxRed> createRenderFor(RenderManager manager) {
				return new RenderFoxRed(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityFoxArctic.class, new IRenderFactory<EntityFoxArctic>() { 
			@Override
			public Render<? super EntityFoxArctic> createRenderFor(RenderManager manager) {
				return new RenderFoxArctic(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityFoxDesert.class, new IRenderFactory<EntityFoxDesert>() { 
			@Override
			public Render<? super EntityFoxDesert> createRenderFor(RenderManager manager) {
				return new RenderFoxDesert(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityHippo.class, new IRenderFactory<EntityHippo>() { 
			@Override
			public Render<? super EntityHippo> createRenderFor(RenderManager manager) {
				return new RenderHippo(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityBigCat.class, new IRenderFactory<EntityBigCat>() { 
			@Override
			public Render<? super EntityBigCat> createRenderFor(RenderManager manager) {
				return new RenderBigCat(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntitySabertooth.class, new IRenderFactory<EntitySabertooth>() { 
			@Override
			public Render<? super EntitySabertooth> createRenderFor(RenderManager manager) {
				return new RenderSabertooth(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityElk.class, new IRenderFactory<EntityElk>() { 
			@Override
			public Render<? super EntityElk> createRenderFor(RenderManager manager) {
				return new RenderElk(manager);
			}
		});

		//TESR
		ClientRegistry.bindTileEntitySpecialRenderer(TileAnvil.class, new AnvilTESR());

	}

	@Override
	public void init(FMLInitializationEvent event)
	{
		super.init(event);
		MinecraftForge.EVENT_BUS.register(new RenderOverlayHandler());
		MinecraftForge.EVENT_BUS.register(new ClientRenderHandler());
		MinecraftForge.EVENT_BUS.register(new BackgroundMusicHandler());
		MinecraftForge.EVENT_BUS.register(new AnvilHighlightHandler());

		setupBlockColors();

		//Disable vanilla UI elements
		GuiIngameForge.renderHealth = false;
		GuiIngameForge.renderArmor = false;
		GuiIngameForge.renderExperiance = false;
	}

	@Override
	public void postInit(FMLPostInitializationEvent event)
	{
		super.postInit(event);
		MinecraftForge.EVENT_BUS.register(new ModelBakeHandler());
		MinecraftForge.EVENT_BUS.register(new SmallVesselHighlightHandler());
	}

	private void registerItemMesh(Item i, ModelResourceLocation mrl)
	{
		ModelLoader.setCustomMeshDefinition(i, new MeshDef(mrl));
	}

	private void registerItemMesh(Item i, int meta, ModelResourceLocation mrl)
	{
		ModelLoader.setCustomModelResourceLocation(i, meta, mrl);
	}

	private void setupBlockColors()
	{

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor()
		{
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
			{
				if(worldIn == null || pos == null || WorldGen.getInstance() == null)
					return 0x55ff55;
				int x = pos.getX() >> 12;
				int z = pos.getZ() >> 12;
				IslandMap m = WorldGen.getInstance().getIslandMap(x, z);
				double d0 = m.getParams().getIslandTemp().getMapTemp();
				double d1 = m.getClosestCenter(pos).getMoistureRaw() * m.getParams().getIslandMoisture().getMoisture();

				if(m.getParams().hasFeature(Feature.Desert))
				{
					d1 *= 0.25;
					d0 *= 1.5;
				}

				if(d1 < 0.25)
				{
					if(state.getBlock() == TFCBlocks.Leaves && state.getValue(BlockLeaves.META_PROPERTY) == WoodType.Acacia)
						d1 = 0.25;
				}
				return ColorizerGrass.getGrassColor(Math.min(d0, 1), Math.min(d1, 1));
			}
		}, new Block[] { TFCBlocks.Leaves, TFCBlocks.Leaves2});

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor()
		{
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
			{
				if(worldIn == null ||pos == null || WorldGen.getInstance() == null || state.getBlock() != TFCBlocks.Vegetation)
					return 0x55ff55;
				VegType veg = (VegType)state.getValue(BlockVegetation.META_PROPERTY);

				int x = pos.getX() >> 12;
				int z = pos.getZ() >> 12;

				IslandMap m = WorldGen.getInstance().getIslandMap(x, z);
				double d0 = m.getParams().getIslandTemp().getMapTemp();
				double d1 = m.getClosestCenter(pos).getMoistureRaw() * m.getParams().getIslandMoisture().getMoisture();

				if(m.getParams().hasFeature(Feature.Desert))
				{
					d1 *= 0.25;
					d0 *= 1.5;
				}
				return ColorizerGrass.getGrassColor(Math.min(d0, 1), Math.min(d1, 1));
			}
		}, new Block[] { TFCBlocks.Vegetation});

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor()
		{
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
			{
				if(worldIn == null ||pos == null || WorldGen.getInstance() == null || state.getBlock() != TFCBlocks.VegDesert)
					return 0x55ff55;
				DesertVegType veg = (DesertVegType)state.getValue(BlockVegDesert.META_PROPERTY);
				if(veg == DesertVegType.DeadBush)
					return 0xD8D8D8;
				int x = pos.getX() >> 12;
				int z = pos.getZ() >> 12;

				IslandMap m = WorldGen.getInstance().getIslandMap(x, z);
				double d0 = m.getParams().getIslandTemp().getMapTemp();
				double d1 = m.getClosestCenter(pos).getMoistureRaw() * m.getParams().getIslandMoisture().getMoisture();
				/*if(m.getParams().hasFeature(Feature.Desert))
					d1 *= 0.25;*/
				return ColorizerGrass.getGrassColor(Math.min(d0, 1), Math.min(d1, 1));
			}
		}, new Block[] { TFCBlocks.VegDesert});

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor()
		{
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
			{
				if(worldIn == null ||pos == null || WorldGen.getInstance() == null)
					return 0x55ff55;
				int x = pos.getX() >> 12;
				int z = pos.getZ() >> 12;
				IslandMap m = WorldGen.getInstance().getIslandMap(x, z);
				double d0 = m.getParams().getIslandTemp().getMapTemp();
				double d1 = m.getClosestCenter(pos).getMoistureRaw() * m.getParams().getIslandMoisture().getMoisture();

				if(m.getParams().hasFeature(Feature.Desert))
				{
					d1 *= 0.25;
					d0 *= 1.5;
				}
				return ColorizerGrass.getGrassColor(Math.min(d0, 1), Math.min(d1, 1));
			}
		}, new Block[] { TFCBlocks.Grass});
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
		OBJLoader.INSTANCE.addDomain(Reference.ModID);

		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.LooseRocks), 0, new ModelResourceLocation(Reference.ModID + ":loose_rock", "inventory"));

		Item anvilItem = Item.getItemFromBlock(TFCBlocks.Anvil);
		ModelLoader.setCustomModelResourceLocation(anvilItem, 0, new ModelResourceLocation(Reference.ModID + ":anvil"));
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
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Planks2), "Wood/Planks/", WoodType.getNamesArray(), 16, 19);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Sapling2), "Wood/Saplings/", WoodType.getNamesArray(), 16, 19);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.LogVertical2), "Wood/Logs/", WoodType.getNamesArray(), 16, 19);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.Leaves2), "Wood/Leaves/", WoodType.getNamesArray(), 16, 18);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.Leaves2), 18, new ModelResourceLocation(Reference.ModID + ":leaves_palm", "inventory"));
		//registerVariantModel(Item.getItemFromBlock(TFCBlocks.Ore), "Ore/", OreType.getNamesArray(), 0, 13);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.StoneBrick), "StoneBrick/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.StoneSmooth), "StoneSmooth/", Global.STONE_ALL, 0, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.SupportBeam), "Wood/SupportBeams/", WoodType.getNamesArray(), 0, 8);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.SupportBeam2), "Wood/SupportBeams/", WoodType.getNamesArray(), 8, 16);
		registerVariantModel(Item.getItemFromBlock(TFCBlocks.SupportBeam3), "Wood/SupportBeams/", WoodType.getNamesArray(), 16, 19);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.TorchOn),0,new ModelResourceLocation(Reference.ModID + ":torch_on", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.TorchOff),0,new ModelResourceLocation(Reference.ModID + ":torch_off", "inventory"));
		for(Block b : TFCBlocks.stairsList)
		{
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b),0,new ModelResourceLocation(Reference.ModID + ":Wood/Stairs/"+Core.getUnlocalized(b.getUnlocalizedName()), "inventory"));
		}
		RegistryItemQueue.getInstance().registerMeshes();
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(TFCBlocks.Thatch), 0, new ModelResourceLocation(Reference.ModID + ":thatch", "inventory"));
	}

	private void registerVariantModel(Item item, String path, String[] variantNames, int metaStart, int metaEnd)
	{
		for(int meta = metaStart; meta < metaEnd; meta++)
		{
			String vName = Core.textConvert(variantNames[meta]);
			ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Reference.ModID + ":" + path + vName, "inventory");
			ModelLoader.setCustomModelResourceLocation(item, meta, itemModelResourceLocation);
			//ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(new ResourceLocation(Reference.ModID, item.getRegistryName().getResourcePath()), vName));
		}
	}

	@Override
	@Deprecated
	/**
	 * Should not be used clientside
	 */
	public void sendToAllNear(World world, BlockPos pos, int range, Packet<?> packet)
	{
		if(world.isRemote)
			return;
		super.sendToAllNear(world, pos, range, packet);
	}

	@Override
	public EntityPlayer getPlayer()
	{
		return Minecraft.getMinecraft().player;
	}
}
