package com.bioxx.tfc2.core;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.fml.common.network.ByteBufUtils;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.SkillsManager;
import com.bioxx.tfc2.api.SkillsManager.Skill;

public class PlayerSkillData 
{
	private Map<Skill, Float> skillsMap;
	private EntityPlayer player;

	public PlayerSkillData(EntityPlayer p)
	{
		player = p;
		skillsMap = new HashMap<Skill, Float>();
		for(Skill s : SkillsManager.instance.getSkillsArray())
		{
			setSkill(s.skillName, 0);
		}
	}

	public void increaseSkill(String skillName)
	{
		increaseSkill(skillName, 1.0f);
	}

	public void increaseSkill(String skillName, float externalMultiplier)
	{
		Skill sk = SkillsManager.instance.getSkill(skillName);
		float incAmount = (sk.skillMultiplier*sk.skillFlat)*externalMultiplier;
		float curAmount = getSkill(skillName);
		setSkill(skillName, curAmount+incAmount);
	}

	public void setSkill(String skillName, float amount)
	{
		Skill sk = SkillsManager.instance.getSkill(skillName);
		if(sk != null)
			skillsMap.put(sk, amount);
	}

	public float getSkill(String skillName)
	{
		Skill sk = SkillsManager.instance.getSkill(skillName);
		if(sk != null)
			return skillsMap.get(sk);
		else return 0f;
	}

	public SkillRank getSkillRank(String skillName)
	{
		float raw = getSkill(skillName);
		if(raw < 1000)
		{
			return SkillRank.Novice;
		}
		else if(raw < 5000)
		{
			return SkillRank.Adept;
		}
		else if(raw < 10000)
		{
			return SkillRank.Expert;
		}
		else
		{
			return SkillRank.Master;
		}
	}

	public float getPercToNextRank(String skillName)
	{
		float raw = getSkill(skillName);
		if(raw < 1000)
		{
			return raw/1000f;
		}
		else if(raw < 5000)
		{
			return (raw-1000f)/1000f;
		}
		else if(raw < 10000)
		{
			return (raw-5000f)/5000f;
		}
		else
		{
			return 1.0f;
		}
	}

	public void readNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("skillCompound"))
		{
			NBTTagCompound skillCompound = nbt.getCompoundTag("skillCompound");
			for(String skill : skillCompound.getKeySet())
			{
				setSkill(skill, skillCompound.getInteger(skill));
			}
		}
	}

	/**
	 * Writes food stats to an NBT object.
	 */
	public void writeNBT(NBTTagCompound nbt)
	{
		NBTTagCompound skillCompound = new NBTTagCompound();
		Object[] keys = skillsMap.keySet().toArray();
		for(Object o : keys)
		{
			Skill k = (Skill)o;
			float f = skillsMap.get(k);
			skillCompound.setFloat(k.skillName, f);
		}
		nbt.setTag("skillCompound", skillCompound);
	}

	public void toOutBuffer(ByteBuf buffer)
	{
		Object[] keys = skillsMap.keySet().toArray();
		buffer.writeInt(keys.length);
		for(Object o : keys)
		{
			Skill k = (Skill)o;
			float f = skillsMap.get(k);
			ByteBufUtils.writeUTF8String(buffer, k.skillName);
			buffer.writeFloat(f);
		}
	}

	public static enum SkillRank
	{
		Novice("gui.skill.novice"), Adept("gui.skill.adept"), Expert("gui.skill.expert"), Master("gui.skill.master");

		String name;
		private SkillRank(String local)
		{
			name = local;
		}

		public String getUnlocalizedName()
		{
			return name;
		}

		public String getLocalizedName()
		{
			return Core.translate(name);
		}
	}


}
