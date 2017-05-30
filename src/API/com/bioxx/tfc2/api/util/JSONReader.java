package com.bioxx.tfc2.api.util;

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

	public boolean read()
	{
		return read(null);
	}

	public boolean read(File file)
	{
		try
		{
			Gson gson = new Gson();
			InputStream stream;
			if(file == null)
				stream = this.getClass().getResourceAsStream(path);
			else if(file.exists())
			{
				stream = Files.asByteSource(file).openStream();
			}
			else
			{
				return false;
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
		return true;
	}

	public void process(JsonReader reader)
	{

	}

}
