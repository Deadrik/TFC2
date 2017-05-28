package com.bioxx.tfc2.api.util;

import java.util.LinkedList;

import net.minecraft.util.math.BlockPos;

/***
 * Built this Custom List because BlockPos.equals as used in generic list types
 * did not seem to be properly comparing BlockPos Objects.
 * @author Bioxx
 *
 */
public class BlockPosList extends LinkedList<BlockPos> {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean contains(Object obj)
	{
		BlockPos compare = (BlockPos)obj;
		for(int i = 0; i < this.size(); i++)
		{
			BlockPos a = this.get(i);
			if(a != null && a.getX() == compare.getX() && a.getY() == compare.getY() && a.getZ() == compare.getZ())
				return true;
		}
		return false;
	}
}