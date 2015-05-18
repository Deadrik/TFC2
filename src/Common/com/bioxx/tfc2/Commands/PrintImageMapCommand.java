package com.bioxx.tfc2.Commands;

import jMapGen.Map;
import jMapGen.Point;
import jMapGen.attributes.Attribute;
import jMapGen.attributes.RiverAttribute;
import jMapGen.graph.Center;
import jMapGen.graph.Corner;
import jMapGen.pathfinding.Path;
import jMapGen.pathfinding.PathNode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.Cache;
import com.bioxx.libnoise.module.modifier.Clamp;
import com.bioxx.libnoise.module.modifier.Curve;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.modifier.ScalePoint;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.tfc2.World.WorldGen;

public class PrintImageMapCommand extends CommandBase
{
	static Color[] colorMap = new Color[256];
	public PrintImageMapCommand()
	{
		for(int i = 0; i < 256; i++)
		{
			colorMap[i] = Color.getColor("", (i << 16) + (i << 8) + i);
		}
	}
	@Override
	public String getCommandName()
	{
		return "pi";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		MinecraftServer server = MinecraftServer.getServer();
		EntityPlayerMP player = null;
		try {
			player = getCommandSenderAsPlayer(sender);
		} catch (PlayerNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WorldServer world = server.worldServerForDimension(player.getEntityWorld().provider.getDimensionId());

		if(params.length >= 2)
		{
			String name = params[1];
			if(params[0].equals("elev"))
			{
				drawElevImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), world, name);
			}
			else if(params[0].equals("biome"))
			{
				drawMapImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), world, name);
			}
			else if(params[0].equals("canyon"))
			{
				int size = 4096;
				try 
				{
					File outFile = new File(name+".bmp");
					BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
					graphics.clearRect(0, 0, size, size);
					System.out.println(name+".bmp");
					float perc = 0.1f;
					float count = 0;
					int xM = ((int)Math.floor(player.posX) >> 12);
					int zM = ((int)Math.floor(player.posZ) >> 12);
					Map map = WorldGen.instance.getIslandMap(xM, zM);
					Point p;
					Center c;
					for(int z = 0; z < size; z++)
					{
						for(int x = 0; x < size; x++)
						{
							p = new Point(x,z);
							count++;
							c = map.getSelectedHexagon(p);
							if(c.hasAttribute(Attribute.gorgeUUID))
								graphics.setColor(Color.white);	
							else
								graphics.setColor(Color.black);	
							graphics.drawRect(x, z, 1, 1);
							if(count / (size*size) > perc)
							{
								System.out.println((int)(perc*100)+"%");
								perc+=0.1f;
							}
						}
					}
					System.out.println(name+".bmp Done!");
					ImageIO.write(outBitmap, "BMP", outFile);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			else if(params[0].equals("noise"))
			{
				int size = params.length >= 3 ? Integer.parseInt(params[2]) : 512;
				drawNoiseImage((int)Math.floor(player.posX), (int)Math.floor(player.posZ), size, world, name);
			}
			else if(params[0].equals("path"))
			{
				int size = 4096;
				try 
				{
					File outFile = new File(name+".bmp");
					BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
					Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
					graphics.clearRect(0, 0, size, size);
					System.out.println(name+".bmp");
					float perc = 0.1f;
					float count = 0;
					int xM = ((int)Math.floor(player.posX) >> 12);
					int zM = ((int)Math.floor(player.posZ) >> 12);
					Map map = WorldGen.instance.getIslandMap(xM, zM);
					Center closest = map.getSelectedHexagon(new Point((int)Math.floor(player.posX) & 4095, (int)Math.floor(player.posZ) & 4095));
					Vector<Center> land = map.landCenters(map.centers);
					Center end = land.get(world.rand.nextInt(land.size()));
					Path path = map.pathfinder.findPath(closest, end);
					if(path == null)
					{
						System.out.println("Failed to find path");
						return;
					}
					for(PathNode pn : path.path)
					{
						count++;
						graphics.setColor(Color.white);		
						if(pn.center == closest)
							graphics.setColor(Color.red);	
						if(pn.center == end)
							graphics.setColor(Color.blue);	
						Polygon poly = new Polygon();

						for(Corner cn : pn.center.corners)
						{
							poly.addPoint((int)cn.point.x, (int)cn.point.y);
						}
						graphics.fillPolygon(poly);

					}
					System.out.println(name+".bmp Done!");
					ImageIO.write(outBitmap, "BMP", outFile);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static void drawMapImage(int xCoord, int zCoord, World world, String name)
	{
		int size = 4096;
		try 
		{
			File outFile = new File(name+".bmp");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".bmp");
			float perc = 0.1f;
			float count = 0;
			int xM = (xCoord >> 12);
			int zM = (zCoord >> 12);
			Map map = WorldGen.instance.getIslandMap(xM, zM);
			Polygon poly;
			for(Center c : map.centers)
			{
				count++;
				graphics.setColor(c.biome.color);		
				poly = new Polygon();
				for(Corner cn : c.corners)
				{
					poly.addPoint((int)cn.point.x, (int)cn.point.y);
				}
				graphics.fillPolygon(poly);
				graphics.setColor(Color.black);	
				graphics.drawPolygon(poly);
			}

			for(Center c : map.centers)
			{
				if(c.hasAttribute(Attribute.riverUUID))
				{
					RiverAttribute a = (RiverAttribute) c.getAttribute(Attribute.riverUUID);
					if(a.getDownRiver() != null)
					{
						graphics.setColor(Color.cyan);	
						graphics.drawLine((int)c.point.x, (int)c.point.y, (int)a.getDownRiver().point.x, (int)a.getDownRiver().point.y);
					}
				}

			}
			System.out.println(name+".bmp Done!");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void drawElevImage(int xCoord, int zCoord, World world, String name)
	{
		int size = 4096;
		try 
		{
			File outFile = new File(name+".bmp");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".bmp");
			float perc = 0.1f;
			float count = 0;
			int xM = (xCoord >> 12);
			int zM = (zCoord >> 12);
			Map map = WorldGen.instance.getIslandMap(xM, zM);
			Polygon poly;
			for(Center c : map.centers)
			{
				count++;
				int elev = Math.max((int)(c.getElevation()*255), 0);
				graphics.setColor(colorMap[elev]);		
				poly = new Polygon();
				for(Corner cn : c.corners)
				{
					poly.addPoint((int)cn.point.x, (int)cn.point.y);
				}
				graphics.fillPolygon(poly);

			}
			System.out.println(name+".bmp Done!");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	public static void drawNoiseImage(int xCoord, int zCoord, int size, World world, String name)
	{
		try 
		{
			File outFile = new File(name+".bmp");
			BufferedImage outBitmap = new BufferedImage(size,size,BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) outBitmap.getGraphics();
			graphics.clearRect(0, 0, size, size);
			System.out.println(name+".bmp");
			float perc = 0.1f;
			float count = 0;

			Perlin pe = new Perlin();
			pe.setSeed (0);
			pe.setFrequency (0.03125);
			pe.setOctaveCount (4);
			pe.setNoiseQuality (NoiseQuality.BEST);

			ScalePoint sp = new ScalePoint();
			sp.setSourceModule(0, pe);
			sp.setxScale(.1);
			sp.setzScale(.1);

			//The scalebias makes our noise fit the range 0-1
			ScaleBias sb = new ScaleBias(sp);
			//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
			sb.setScale(0.25);
			//Next we offset by +0.5 which makes the noise 0-1
			sb.setBias(0.5);

			Curve curveModule = new Curve();
			curveModule.setSourceModule(0, sb);
			curveModule.AddControlPoint(0, 0);
			curveModule.AddControlPoint(0.35, 0.1);
			curveModule.AddControlPoint(0.75, 0.9);
			curveModule.AddControlPoint(1, 1);

			Clamp clampModule = new Clamp();
			clampModule.setSourceModule(0, curveModule);
			clampModule.setLowerBound(0);
			clampModule.setUpperBound(1);

			Cache cacheModule = new Cache();
			cacheModule.setSourceModule(0, clampModule);
			Plane heightPlane = new Plane(cacheModule);

			for(int z = 0; z < size; z++)
			{
				for(int x = 0; x < size; x++)
				{
					count++;
					double n = heightPlane.GetValue(xCoord-(size/2)+x, zCoord-(size/2)+z);
					if(n >= -2 && n <= 2)
					{
						int h = (int)(255*n);
						graphics.setColor(colorMap[h]);	
						graphics.drawRect(z, x, 1, 1);
						if(count / (size*size) > perc)
						{
							System.out.println((int)(perc*100)+"%");
							perc+=0.1f;
						}
					}
					else
					{
						//System.out.println("Error");
					}
				}
			}
			System.out.println(name+".bmp Done!");
			ImageIO.write(outBitmap, "BMP", outFile);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "";
	}

}
