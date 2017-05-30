package com.bioxx.tfc2.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bioxx.tfc2.api.FoodRegistry.FoodGroupPair;
import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.api.util.JSONReader;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

public class FoodReader extends JSONReader 
{
	public ArrayList<FoodJSON> foodList = new ArrayList<FoodJSON>();
	public FoodReader(String filepath) {
		super(filepath);
	}


	@Override
	public void process(JsonReader reader)
	{
		try 
		{
			reader.beginArray();

			while(reader.hasNext())
			{
				foodList.add(readFood(reader));
			}
			reader.endArray();
			reader.close();

		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private FoodJSON readFood(JsonReader reader) throws IOException
	{
		String name = "";
		int meta = 0;
		long time = -1;
		List<FoodGroupPair> fg = new ArrayList<FoodGroupPair>();
		boolean edible = true;

		reader.beginObject();
		while(reader.hasNext())
		{
			String key = reader.nextName();
			if(key.equals("name"))
			{
				name = reader.nextString();
				if(name.contains(" "))
				{
					String[] s = name.split(" ");
					name = s[0];
					meta = Integer.parseInt(s[1]);
				}
			}
			else if(key.equals("decay"))
				time = reader.nextLong();
			else if(key.equals("foodgroup"))
			{
				if(reader.peek() == JsonToken.BEGIN_OBJECT)
				{
					reader.beginObject();
					while(reader.hasNext())
					{
						fg.add(new FoodGroupPair(EnumFoodGroup.valueOf(reader.nextName()), (float)reader.nextDouble()));
					}
					reader.endObject();
				}
				else
					fg.add(new FoodGroupPair(EnumFoodGroup.valueOf(reader.nextString()), 100f));
			}
			else if(key.equals("edible"))
				edible = reader.nextBoolean();
			else
				reader.skipValue();
		}
		reader.endObject();


		return new FoodJSON(name, meta, time, fg, edible);

	}

	public class FoodJSON 
	{
		public String itemName;
		public int itemMeta;
		public long decayTime;
		public List<FoodGroupPair> foodGroup = new ArrayList<FoodGroupPair>();
		public boolean isEdible;

		public FoodJSON(String name, int meta, long decay, List<FoodGroupPair> fg, boolean edible)
		{
			itemName = name;
			itemMeta = meta;
			decayTime = decay;
			foodGroup = fg;
			isEdible = edible;
		}

	}

}
