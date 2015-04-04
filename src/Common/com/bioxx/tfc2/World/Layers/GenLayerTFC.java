package com.bioxx.tfc2.World.Layers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;

import com.bioxx.tfc2.World.TerrainTypes.TerrainType;

public abstract class GenLayerTFC extends GenLayer
{

	public GenLayerTFC(long p_i2125_1_) 
	{
		super(p_i2125_1_);
	}

	public static GenLayerTFC[] initialize(long seed, WorldType type, String options)
	{
		GenLayerTFC genlayer2 = genContinent();
		genlayer2.initWorldGenSeed(seed);
		return new GenLayerTFC[] {genlayer2};
	}

	public static GenLayerTFC genContinent()
	{
		GenLayerTFC islandsStart = new GenLayerIslandTFC(1L);
		drawImage(512, islandsStart, "IslandsStart");
		GenLayerTFC island = new GenLayerZoomTFC(2001L, islandsStart);
		drawImage(512, island, "Island Zoom");
		island = GenLayerZoomTFC.magnify(2002L, island, 1);
		drawImage(512, island, "Magnify");
		island = new GenLayerAddIslandTFC(2L, island);
		drawImage(512, island, "IslandAddland");
		island = new GenLayerRemoveOcean(2003L, island);
		drawImage(512, island, "Remove Ocean");
		island = GenLayerZoomTFC.magnify(2004L, island, 1);
		drawImage(512, island, "Magnify");
		island = new GenLayerZoomTFC(2005L, island);
		drawImage(512, island, "Island Zoom");
		island = new GenLayerAddIslandTFC(3L, island);
		drawImage(512, island, "IslandAddland");
		island = new GenLayerZoomTFC(2006L, island);
		drawImage(512, island, "Island Zoom");
		island = new GenLayerRemoveOcean(2007L, island);
		drawImage(512, island, "Remove Ocean");
		island = new GenLayerShore(5000L, island);
		drawImage(512, island, "Shore");
		island = new GenLayerZoomTFC(2009L, island);
		drawImage(512, island, "Island Zoom");
		island = new GenLayerZoomTFC(2009L, island);
		drawImage(512, island, "Island Zoom");
		island = new GenLayerZoomTFC(2009L, island);
		drawImage(512, island, "Island Zoom");
		island = new GenLayerZoomTFC(2009L, island);
		drawImage(512, island, "Island Zoom");
		island = new GenLayerZoomTFC(2010L, island);
		drawImage(512, island, "Island Zoom");
		return island;
	}

	static boolean shouldDraw = false;
	static int imageCount = 0;
	public static void drawImage(int size, GenLayerTFC genlayer, String name)
	{
		if(!shouldDraw)
			return;
		try 
		{
			imageCount++;
			File outFile = new File("Maps//"+imageCount+" "+name+".bmp");
			if(outFile.exists())
				return;
			int[] ints = genlayer.getInts(0, 0, size, size);
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".bmp");
			for(int x = 0; x < size; x++)
			{
				for(int z = 0; z < size; z++)
				{
					int height = ints[x*size+z]*64;
					graphics.setColor(TerrainType.getTerrain(ints[x*size+z]).getMapColor());	
					graphics.drawRect(x, z, 1, 1);
				}
			}
			System.out.println(name+".bmp");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}



}
