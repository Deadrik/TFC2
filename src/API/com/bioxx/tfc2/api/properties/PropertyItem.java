package com.bioxx.tfc2.api.properties;

import java.util.List;

import net.minecraft.client.renderer.block.model.IBakedModel;

import net.minecraftforge.common.property.IUnlistedProperty;

import com.bioxx.tfc2.api.properties.PropertyItem.PropItems;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * This class was borrowed from TiCon, thanks!
 */
public class PropertyItem implements IUnlistedProperty<PropItems> {

	@Override
	public String getName() {
		return "Items";
	}

	@Override
	public boolean isValid(PropItems value) {
		return value != null && value.items != null;
	}

	@Override
	public Class<PropItems> getType() {
		return PropItems.class;
	}

	@Override
	public String valueToString(PropItems value) {
		return value.toString();
	}

	public static class PropItems {
		public static final PropItems EMPTY = new PropItems();
		static {
			EMPTY.items = ImmutableList.of();
		}

		public List<PItem> items = Lists.newLinkedList();
	}

	public static class PItem {
		public final IBakedModel model;
		public float x,y,z;
		public float s;
		public float r;

		public PItem(IBakedModel model) {
			this(model, 0,0,0);
		}

		public PItem(IBakedModel model, float x, float y, float z) {
			this(model, x,y,z, 1, 0);
		}

		public PItem(IBakedModel model, float x, float y, float z, float s, float r) {
			this.model = model;
			this.x = x;
			this.y = y;
			this.z = z;
			this.s = s;
			this.r = r;
		}
	}
}
