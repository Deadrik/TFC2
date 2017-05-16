package com.bioxx.tfc2.api.animals;

import com.bioxx.tfc2.api.types.Gender;

public interface IGenderedAnimal 
{
	public Gender getGender();
	public void setGender(Gender g);
}
