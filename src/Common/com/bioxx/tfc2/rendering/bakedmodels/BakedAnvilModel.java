package com.bioxx.tfc2.rendering.bakedmodels;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.blocks.BlockAnvil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;

/**
 * This class was *mostly* borrowed from TiCon, thanks!
 */

public class BakedAnvilModel implements IPerspectiveAwareModel 
{
	private final IPerspectiveAwareModel standard;

	private final Map<String, IBakedModel> cache = Maps.newHashMap();
	private final VertexFormat format;
	private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

	public BakedAnvilModel(IPerspectiveAwareModel standard,VertexFormat format) {
		this.standard = standard;
		this.format = format;
		this.transforms = getTransforms(standard);
	}

	public static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> getTransforms(IPerspectiveAwareModel model) {
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> builder = ImmutableMap.builder();
		for(ItemCameraTransforms.TransformType type : ItemCameraTransforms.TransformType.values()) {
			TRSRTransformation transformation = new TRSRTransformation(model.handlePerspective(type).getRight());
			if(!transformation.equals(TRSRTransformation.identity())) {
				builder.put(type, TRSRTransformation.blockCenterToCorner(transformation));
			}
		}
		return builder.build();
	}

	protected IBakedModel getActualModel(IBlockState state, List<PropertyItem.PItem> items, EnumFacing facing) {
		IBakedModel bakedModel = standard;

		// add all the items to display on the table
		if(items != null && !items.isEmpty()) {
			BakedCompositeModel.Builder builder = new BakedCompositeModel.Builder();
			builder.add(bakedModel, null, 0);
			EnumFacing anvilFacing = state.getValue(BlockAnvil.FACING);
			for(PropertyItem.PItem item : items) 
			{
				float rot = 0;
				/*if(state.getValue(BlockAnvil.FACING) == EnumFacing.NORTH || state.getValue(BlockAnvil.FACING) == EnumFacing.SOUTH)
					builder.add(new TRSRBakedModel(item.model, item.x, item.y +0.15f, item.z, item.r, (float) (Math.PI), 0.7853981633974483f, item.s), null, 0);
				else 
					builder.add(new TRSRBakedModel(item.model, item.x, item.y + 0.15f, item.z, item.r, (float) (Math.PI), -0.7853981633974483f, item.s), null, 0);*/
				if(anvilFacing == EnumFacing.NORTH)
					rot = (float)Math.toRadians(0);
				else if(anvilFacing == EnumFacing.SOUTH)
					rot = (float)Math.toRadians(180);
				else if(anvilFacing == EnumFacing.EAST)
					rot = (float)Math.toRadians(270);
				else if(anvilFacing == EnumFacing.WEST)
					rot = (float)Math.toRadians(90);


				builder.add(new TRSRBakedModel(item.model, item.x, item.y + 0.15f, item.z, item.r, (float) (Math.PI), rot, item.s), null, 0);
			}

			bakedModel = builder.build(bakedModel);
		}

		if(facing != null) {
			bakedModel = new TRSRBakedModel(bakedModel, facing);
		}

		return bakedModel;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) 
	{
		List<PropertyItem.PItem> items = Collections.emptyList();
		EnumFacing face = EnumFacing.SOUTH;

		if(state instanceof IExtendedBlockState) {
			IExtendedBlockState extendedState = (IExtendedBlockState) state;

			if(/*Config.renderTableItems &&*/ extendedState.getUnlistedNames().contains(BlockAnvil.INVENTORY)) {
				if(extendedState.getValue(BlockAnvil.INVENTORY) != null) {
					items = extendedState.getValue(BlockAnvil.INVENTORY).items;
				}
			}

			// remove all world specific data
			// This is so that the next call to getQuads from the transformed TRSRModel doesn't do this again
			// otherwise table models inside table model items recursively calls this with the state of the original table
			state = extendedState.withProperty(BlockAnvil.INVENTORY, PropertyItem.PropItems.EMPTY);
		}

		// models are symmetric, no need to rotate if there's nothing on it where rotation matters, so we just use default
		if(items == null) {
			return standard.getQuads(state, side, rand);
		}

		// the model returned by getActualModel should be a simple model with no special handling
		return getActualModel(state, items, face).getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return standard.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return standard.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return standard.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return standard.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return standard.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return TableItemOverrideList.INSTANCE;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> pair = standard.handlePerspective(cameraTransformType);
		return Pair.of(this, pair.getRight());
	}

	private static class TableItemOverrideList extends ItemOverrideList {

		static TableItemOverrideList INSTANCE = new TableItemOverrideList();

		private TableItemOverrideList() {
			super(ImmutableList.<ItemOverride>of());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			if(originalModel instanceof BakedAnvilModel) {
				return ((BakedAnvilModel) originalModel).getActualModel(null, Collections.<PropertyItem.PItem>emptyList(), null);
			}

			return originalModel;
		}
	}

	public static TextureAtlasSprite getTextureFromBlock(Block block, int meta) {
		IBlockState state = block.getStateFromMeta(meta);
		return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
	}

}
