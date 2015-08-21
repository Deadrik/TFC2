package com.bioxx.tfc2.api.ore;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import com.bioxx.tfc2.api.types.OreType;

public class OreConfig 
{
	String oreName;
	VeinType vType;
	IBlockState state;
	int veinWidthMax;//Max width of the seam or blob
	int veinHeightMax;//Max height of the seam or blob
	int veinWidthMin;//Max width of the seam or blob
	int veinHeightMin;//Max height of the seam or blob
	int noiseVertical = 5;//How much the seam can move vertically between hexes. 1-10 is a sane range
	double noiseBiasVertical = 0.5;//Range (0-1) This will bias the vertical movement of a seam towards the up or down direction. 0 = 100% down, 1.0 = 100% up
	int noiseHorizontal = 5;//How much the seam can move horizontally between hexes. 1-5 is a good range
	int layerHexWidth = 1;//This is how many hexes wide that this seem should generate. Only used for large layer style ores such as coal.
	int minSeamLength = 10;//When deciding how long to make the seam, this is the minimum number of hexes to move.
	int maxSeamLength = 100;//When deciding how long to make the seam, this is the maximum number of hexes to move.

	int subSeamRarity = 1;//1 in X Centers will have an offshoot seam
	/**
	 * This is the maximum number of veins that will start per island. The actual amount will be between 1 and this number.
	 */
	int rarity = 5;

	public OreConfig(VeinType v, Block b, int m, String ore, int wMin, int wMax, int hMin, int hMax)
	{
		state = b.getStateFromMeta(m);
		veinWidthMax = wMax;
		veinHeightMax = hMax;
		veinWidthMin = wMin;
		veinHeightMin = hMin;
		vType = v;
		oreName = ore;
	}

	public OreConfig(VeinType v, Block b, OreType m, int wMin, int wMax, int hMin, int hMax)
	{
		this(v, b, m.getMeta(), m.getName(), wMin, wMax, hMin, hMax);
	}

	public String getOreName() {
		return oreName;
	}

	public IBlockState getOreBlockState() {
		return state;
	}

	public int getVeinWidthMax() {
		return veinWidthMax;
	}

	public int getVeinHeightMax() {
		return veinHeightMax;
	}

	public int getVeinWidthMin() {
		return veinWidthMin;
	}

	public int getVeinHeightMin() {
		return veinHeightMin;
	}

	public VeinType getVeinType() {
		return vType;
	}

	public int getNoiseVertical() {
		return noiseVertical;
	}

	public OreConfig setNoiseVertical(int noiseVertical) {
		this.noiseVertical = noiseVertical;
		return this;
	}

	public int getNoiseHorizontal() {
		return noiseHorizontal;
	}

	public OreConfig setNoiseHorizontal(int noiseHorizontal) {
		this.noiseHorizontal = noiseHorizontal;
		return this;
	}

	public int getLayerHexWidth() {
		return layerHexWidth;
	}

	public OreConfig setLayerHexWidth(int layerHexWidth) {
		this.layerHexWidth = layerHexWidth;
		return this;
	}

	public double getNoiseBiasVertical() {
		return noiseBiasVertical;
	}

	public void setNoiseBiasVertical(double noiseBiasVertical) {
		this.noiseBiasVertical = noiseBiasVertical;
	}

	public int getMinSeamLength() {
		return minSeamLength;
	}

	public OreConfig setMinSeamLength(int minSeamLength) {
		this.minSeamLength = minSeamLength; return this;
	}

	public int getMaxSeamLength() {
		return maxSeamLength;
	}

	public OreConfig setMaxSeamLength(int maxSeamLength) {
		this.maxSeamLength = maxSeamLength; return this;
	}

	public int getRarity() {
		return rarity;
	}

	public OreConfig setRarity(int rarity) {
		this.rarity = rarity;
		return this;
	}

	public int getSubSeamRarity() {
		return subSeamRarity;
	}

	public OreConfig setSubSeamRarity(int subSeamRarity) {
		this.subSeamRarity = subSeamRarity;
		return this;
	}

	public enum VeinType
	{
		Seam, Blob, Layer;
	}
}
