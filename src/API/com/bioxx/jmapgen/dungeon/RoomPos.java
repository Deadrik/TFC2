package com.bioxx.jmapgen.dungeon;

import net.minecraft.util.math.BlockPos;

public class RoomPos extends BlockPos 
{

	public RoomPos(int x, int y, int z) 
	{
		super(x, y, z);
	}

	public RoomPos(BlockPos pos) 
	{
		super(pos.getX(), pos.getY(), pos.getZ());
	}

	public RoomPos add(RoomPos pos)
	{
		return new RoomPos(this.getX()+pos.getX(), this.getY()+pos.getY(), this.getZ()+pos.getZ());
	}

	public RoomPos offset(DungeonDirection facing)
	{

		switch(facing)
		{
		case DOWN:
			return new RoomPos(this.down());
		case EAST:
			return new RoomPos(this.east());
		case NORTH:
			return new RoomPos(this.north());
		case SOUTH:
			return new RoomPos(this.south());
		case UP:
			return new RoomPos(this.up());
		case WEST:
			return new RoomPos(this.west());
		default:
			return this;

		}
	}

}
