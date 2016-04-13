package com.bioxx.tfc2.rendering;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;

public class MeshDef implements ItemMeshDefinition 
{
	public ModelResourceLocation[] rl;
	public MeshDef(ModelResourceLocation mrl)
	{
		rl = new ModelResourceLocation[1];
		rl[0] = mrl;
	}

	public MeshDef(ModelResourceLocation[] mrl)
	{
		rl = mrl;
	}

	public MeshDef()
	{
		Setup();
	}

	public void Setup()
	{

	}

	@Override
	public ModelResourceLocation getModelLocation(ItemStack stack) 
	{
		if(rl.length > 1 && stack.getItemDamage() < rl.length)
			return rl[stack.getItemDamage()];

		return rl[0];
	}

}
