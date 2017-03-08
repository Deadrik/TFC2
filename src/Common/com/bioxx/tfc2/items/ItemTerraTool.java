package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import com.google.common.collect.Multimap;

public class ItemTerraTool extends ItemTool 
{

	public ItemTerraTool(ToolMaterial mat, Set effective)
	{
		this(mat.getDamageVsEntity(), 1.0f, mat, effective);
	}

	protected ItemTerraTool(float attackDamage, float attackSpeed, ToolMaterial material, Set effectiveBlocks) 
	{
		super(attackDamage, attackSpeed, material, effectiveBlocks);
	}

	@Override
	public Item setUnlocalizedName(String unlocalizedName)
	{
		this.setRegistryName(unlocalizedName);
		return super.setUnlocalizedName(unlocalizedName);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier 2", (double)this.damageVsEntity, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
		}

		return multimap;
	}
}
