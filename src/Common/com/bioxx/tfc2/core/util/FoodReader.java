package com.bioxx.tfc2.core.util;

import java.io.IOException;
import java.util.ArrayList;

import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.google.gson.stream.JsonReader;

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
		String fg = "None";

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
				fg = reader.nextString();
			else
				reader.skipValue();
		}
		reader.endObject();


		return new FoodJSON(name, meta, time, fg);

	}

	public class FoodJSON 
	{
		public String itemName;
		public int itemMeta;
		public long decayTime;
		public EnumFoodGroup foodGroup;

		public FoodJSON(String name, int meta, long decay, String fg)
		{
			itemName = name;
			itemMeta = meta;
			decayTime = decay;
			foodGroup = EnumFoodGroup.valueOf(fg);
		}

	}

}
