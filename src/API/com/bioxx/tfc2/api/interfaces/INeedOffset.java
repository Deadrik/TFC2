package com.bioxx.tfc2.api.interfaces;

public interface INeedOffset 
{
	/**
	 * Converts Item Damage to Block Metadata
	 */
	public int convertMetaToBlock(int meta);
	/**
	 * Converts block metadata (0-15) into Item metadata
	 * @param meta This metadata usually comes from a blocks getMetaFromState method
	 */
	public int convertMetaToItem(int meta);
}
