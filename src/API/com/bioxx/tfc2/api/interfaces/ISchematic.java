package com.bioxx.tfc2.api.interfaces;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagList;

import com.bioxx.tfc2.api.Schematic.SchemBlock;

public interface ISchematic
{
	public boolean Load();
	/**
	 * @return Schematic "Height"
	 */
	public int getSizeY();

	public void setSizeY(int y);

	/**
	 * @return Schematic "Width"
	 */
	public int getSizeX();

	public void setSizeX(int x);

	/**
	 * @return Schematic "Length"
	 */
	public int getSizeZ();

	public void setSizeZ(int z);

	/**
	 * @return Schematic "TileEntities"
	 */
	public NBTTagList getTileEntities();

	public void setTileEntities(NBTTagList te);

	/**
	 * @return Schematic "Entities"
	 */
	public NBTTagList getEntities();

	public void setEntities(NBTTagList e);

	/**
	 * Gets the file path.
	 */
	public String getPath();

	/**
	 * Sets the file path for future reference.
	 * @param path The path to the schematic file
	 */
	public void setPath(String path);

	/**
	 * 
	 * @return Center of the schematic X Coordinate
	 */
	public int getCenterX();

	/**
	 * 
	 * @return Center of the schematic Z Coordinate
	 */
	public int getCenterZ();

	/**
	 * Used for lookups in the schematic list.
	 * @return File Index number
	 */
	public int getIndex();

	/**
	 * @return Returns an Arraylist containing every block in this schematic for iteration
	 */
	public ArrayList<SchemBlock> getBlockMap();	
}
