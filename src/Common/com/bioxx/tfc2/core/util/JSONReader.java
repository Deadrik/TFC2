package com.bioxx.tfc2.core.util;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JSONReader 
{

	public final String path;

	public JSONReader(String filepath)
	{
		path = filepath;
	}

	public void read()
	{
		read(null);
	}

	public void read(File file)
	{
		try
		{
			Gson gson = new Gson();
			InputStream stream;
			if(file == null)
				stream = this.getClass().getResourceAsStream(path);
			else
			{
				stream = Files.asByteSource(file).openStream();
			}

			InputStreamReader sr = new InputStreamReader(stream);
			JsonReader reader = new JsonReader(sr);
			process(reader);
			reader.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void process(JsonReader reader)
	{

	}

}
