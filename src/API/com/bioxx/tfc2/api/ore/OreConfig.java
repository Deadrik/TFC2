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
	int noiseVertical = 20;//How much the seam can move vertically between hexes. 1-30 is a sane range
	double noiseBiasVertical = 0.5;//Range (0-1) This will bias the vertical movement of a seam towards the up or down direction. 0 = 100% down, 1.0 = 100% up
	int noiseHorizontal = 8;//How much the seam can move horizontally between hexes. 1-8 is a good range
	int layerHexWidth = 1;//This is how many hexes wide that this seem should generate. Only used for large layer style ores such as coal.
	int minSeamLength = 5;//When deciding how long to make the seam, this is the minimum number of hexes to move.
	int maxSeamLength = 30;//When deciding how long to make the seam, this is the maximum number of hexes to move.
	double pathBias = 0.5;//Range (0-1) This causes the seam to prefer moving either left or right from the previous hex. 0 = Left, 1 = Right
	int subSeamRarity = 1;//1 in X Centers will have an offshoot seam
	/**
	 * This value is means that 1 in X centers will have an ore seam start. If an island has 16000 centers and rarity is 100, then there should be ~16 seams of this ore.
	 */
	int rarity = 200;

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
		state = b.getStateFromMeta(m.getMeta());
		veinWidthMax = hMax;
		veinHeightMax = wMax;
		vType = v;
		oreName = m.getName();
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

	public void setMinSeamLength(int minSeamLength) {
		this.minSeamLength = minSeamLength;
	}

	public int getMaxSeamLength() {
		return maxSeamLength;
	}

	public void setMaxSeamLength(int maxSeamLength) {
		this.maxSeamLength = maxSeamLength;
	}

	public double getPathBias() {
		return pathBias;
	}

	public void setPathBias(double pathBias) {
		this.pathBias = pathBias;
	}

	public int getRarity() {
		return rarity;
	}

	public void setRarity(int rarity) {
		this.rarity = rarity;
	}

	public int getSubSeamRarity() {
		return subSeamRarity;
	}

	public void setSubSeamRarity(int subSeamRarity) {
		this.subSeamRarity = subSeamRarity;
	}

	public enum VeinType
	{
		Seam, Blob, Layer;
	}
}
