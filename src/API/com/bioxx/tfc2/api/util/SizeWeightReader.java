package com.bioxx.tfc2.api.util;

import java.io.IOException;
import java.util.ArrayList;

import com.bioxx.tfc2.api.types.EnumSize;
import com.bioxx.tfc2.api.types.EnumWeight;
import com.google.gson.stream.JsonReader;

public class SizeWeightReader extends JSONReader 
{
	public ArrayList<SizeWeightJSON> list = new ArrayList<SizeWeightJSON>();
	public SizeWeightReader(String filepath) {
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
				list.add(read(reader));
			}
			reader.endArray();
			reader.close();

		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private SizeWeightJSON read(JsonReader reader) throws IOException
	{
		String name = "";
		int meta = -1;
		EnumSize size = EnumSize.VERYSMALL;
		EnumWeight weight = EnumWeight.VERYLIGHT;

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
			else if(key.equals("size"))
			{
				size = EnumSize.fromString(reader.nextString());
			}
			else if(key.equals("weight"))
			{
				weight = EnumWeight.fromString(reader.nextString());
			}
			else
				reader.skipValue();
		}
		reader.endObject();


		return new SizeWeightJSON(name, meta, size, weight);

	}

	public static class SizeWeightJSON 
	{
		public String itemName;
		public int itemMeta;
		public EnumSize size;
		public EnumWeight weight;

		public SizeWeightJSON(String name, int meta, EnumSize s, EnumWeight w)
		{
			itemName = name;
			itemMeta = meta;
			size = s;
			weight = w;
		}

	}

}
