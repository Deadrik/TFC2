package com.bioxx.tfc2.api;

import java.util.ArrayList;
import java.util.List;

public class SkillsManager 
{
	private List<Skill> skillsArray = new ArrayList<Skill>();

	public static SkillsManager instance = new SkillsManager();

	public SkillsManager()
	{

	}

	public void registerSkill(Skill skill)
	{
		skillsArray.add(skill);
	}

	public void registerSkill(String name, float rate)
	{
		skillsArray.add(new Skill(name, rate));
	}

	public List<Skill> getSkillsArray()
	{
		return this.skillsArray;
	}

	public Skill getSkill(String name)
	{
		for(Skill s : skillsArray)
			if(s.skillName.equalsIgnoreCase(name))
				return s;
		return null;
	}

	public static class Skill
	{
		public String skillName;
		public float skillMultiplier = 1.0f;
		public float skillFlat = 1.0f;

		public Skill(String n)
		{
			skillName = n;
		}

		public Skill(String n, float r)
		{
			this(n);
			skillMultiplier = r;
		}

		public Skill(String n, float r, float f)
		{
			this(n);
			skillMultiplier = r;
			skillFlat = f;
		}
	}
}
