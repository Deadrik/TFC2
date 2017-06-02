package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.rendering.bakedmodels.BakedAnvilModel;
import com.bioxx.tfc2.rendering.bakedmodels.BakedPitKilnModel;
import com.bioxx.tfc2.rendering.bakedmodels.BakedSmallVesselModel;

public class ModelBakeHandler 
{
	ModelResourceLocation locPitKilnCharcoal = new ModelResourceLocation(Reference.ModID + ":pitkiln", "fill=0,fillType=charcoal");
	ModelResourceLocation locPitKilnStraw = new ModelResourceLocation(Reference.ModID + ":pitkiln", "fill=0,fillType=straw");
	ModelResourceLocation locSmallVessel = new ModelResourceLocation(Reference.ModID + ":smallvessel","normal");
	ModelResourceLocation locAnvilN = new ModelResourceLocation(Reference.ModID + ":anvil", "facing=north");
	ModelResourceLocation locAnvilS = new ModelResourceLocation(Reference.ModID + ":anvil", "facing=south");
	ModelResourceLocation locAnvilE = new ModelResourceLocation(Reference.ModID + ":anvil", "facing=east");
	ModelResourceLocation locAnvilW = new ModelResourceLocation(Reference.ModID + ":anvil", "facing=west");


	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) 
	{
		// tool tables
		replaceAnvilModel(locAnvilN, Core.CreateRes(Reference.ModID + ":block/anvil"), event);
		replaceAnvilModel(locAnvilS, Core.CreateRes(Reference.ModID + ":block/anvil"), event);
		replaceAnvilModel(locAnvilE, Core.CreateRes(Reference.ModID + ":block/anvil"), event);
		replaceAnvilModel(locAnvilW, Core.CreateRes(Reference.ModID + ":block/anvil"), event);

		replacePitKilnModel(locPitKilnCharcoal, Core.CreateRes(Reference.ModID + ":block/pitkiln/pitkiln_0"), event);
		replacePitKilnModel(locPitKilnStraw, Core.CreateRes(Reference.ModID + ":block/pitkiln/pitkiln_0"), event);

		replaceSmallVesselModel(locSmallVessel, Core.CreateRes(Reference.ModID + ":block/smallvessel"), event);

		// silence the missing-model message for the default itemblock
		//event.getModelRegistry().putObject(locAnvil, event.getModelRegistry().getObject(locAnvil));
	}

	public static void replaceAnvilModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
		try {
			IModel model = ModelLoaderRegistry.getModel(modelLocation);
			IBakedModel standard = event.getModelRegistry().getObject(modelVariantLocation);
			if(standard instanceof IPerspectiveAwareModel) {
				IBakedModel finalModel = new BakedAnvilModel((IPerspectiveAwareModel) standard, DefaultVertexFormats.BLOCK);

				event.getModelRegistry().putObject(modelVariantLocation, finalModel);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void replacePitKilnModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
		try {
			IModel model = ModelLoaderRegistry.getModel(modelLocation);
			IBakedModel standard = event.getModelRegistry().getObject(modelVariantLocation);
			if(standard instanceof IPerspectiveAwareModel) {
				IBakedModel finalModel = new BakedPitKilnModel((IPerspectiveAwareModel) standard, DefaultVertexFormats.BLOCK);

				event.getModelRegistry().putObject(modelVariantLocation, finalModel);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void replaceSmallVesselModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
		try {
			IModel model = ModelLoaderRegistry.getModel(modelLocation);
			IBakedModel standard = event.getModelRegistry().getObject(modelVariantLocation);
			if(standard instanceof IPerspectiveAwareModel) {
				IBakedModel finalModel = new BakedSmallVesselModel((IPerspectiveAwareModel) standard, DefaultVertexFormats.BLOCK);

				event.getModelRegistry().putObject(modelVariantLocation, finalModel);
			}

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
