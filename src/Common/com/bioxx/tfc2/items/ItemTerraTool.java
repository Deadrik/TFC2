package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;

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

	@Override
	public int getMaxDamage(ItemStack stack)
	{
		return this.getMaxDamage() - (stack.hasTagCompound() ? stack.getTagCompound().getInteger("reducedDur") : 0);
	}

	public ItemStack onRepair(ItemStack is)
	{
		if(!is.hasTagCompound())
		{
			is.setTagCompound(new NBTTagCompound());
		}
		is.getTagCompound().setInteger("reducedDur", Math.max((is.hasTagCompound() ? is.getTagCompound().getInteger("reducedDur") : 0)*2, 1));
		return is;
	}
}
