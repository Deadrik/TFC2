package com.bioxx.tfc2.api.interfaces;

import java.util.HashMap;

import com.bioxx.tfc2.api.types.EnumFoodGroup;

public interface IFoodStatsTFC 
{
	public HashMap<EnumFoodGroup, Float> getNutritionMap();
	public float getWaterLevel();
	public void setWaterLevel(float f);
}
