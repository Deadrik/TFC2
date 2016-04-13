package com.bioxx.jmapgen.dungeon;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.dungeon.Dungeon.DungeonDoor.DoorType;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.BlockStoneBrick;

public class Dungeon
{
	public Vector<DungeonLevel> levels;
	public Center entrance;
	private IslandMap islandMap;
	public IBlockState floorType;
	public IBlockState wallType;
	public IBlockState ceilingType;
	public int roomHeight = 3;
	public boolean usesEvenRooms = false;//this is for dungeons that should use double doors instead of single

	public Dungeon(IslandMap map)
	{
		levels = new Vector<DungeonLevel>();
		islandMap = map;
	}

	public void generate(long seed, Center start)
	{
		entrance = start;
		System.out.println("Dungeon at " + start.point.toString());
		Random r = new Random(seed);
		int numLevels = r.nextInt(4);
		DungeonRoom stairRoom = null;

		for(int i = 0; i < numLevels; i++)
		{
			DungeonLevel dl = new DungeonLevel();
			if(stairRoom != null)
				dl.generate(r, (int)stairRoom.getBoundingBox().getCenterX(), (int)stairRoom.getBoundingBox().getCenterZ(), this);
			else
				dl.generate(r, (int)entrance.point.getX(), (int)entrance.point.getZ(), this);

			levels.add(dl);
			dl.yLevel = 64+islandMap.convertHeightToMC(entrance.getElevation())-25 - (roomHeight+2) * i;
			while(stairRoom == null)
			{
				DungeonRoom room = dl.rooms.get(r.nextInt(dl.rooms.size()));
				if(room.roomType != RoomType.Hallway)
					stairRoom = room;
			}
		}

		//Choose what materials are to be used for dungeon construction
		if(r.nextBoolean())
		{
			if(r.nextBoolean())
				floorType = Core.getPlanks(WoodType.getTypeFromString(islandMap.getParams().getCommonTree()));
			else if(r.nextBoolean())
				floorType = Core.getPlanks(WoodType.getTypeFromString(islandMap.getParams().getUncommonTree()));
			else
				floorType = Core.getPlanks(WoodType.getTypeFromString(islandMap.getParams().getRareTree()));
		}
		else 
		{
			floorType = TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, islandMap.getParams().getSurfaceRock());
		}

		//Walls are always stone, not wood
		wallType = TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, islandMap.getParams().getSurfaceRock());

		if(r.nextBoolean())
		{
			if(r.nextBoolean() && WoodType.getTypeFromString(islandMap.getParams().getCommonTree()) != WoodType.Palm)
				ceilingType = Core.getPlanks(WoodType.getTypeFromString(islandMap.getParams().getCommonTree()));
			else if(r.nextBoolean() && WoodType.getTypeFromString(islandMap.getParams().getUncommonTree()) != WoodType.Palm)
				ceilingType = Core.getPlanks(WoodType.getTypeFromString(islandMap.getParams().getUncommonTree()));
			else if(WoodType.getTypeFromString(islandMap.getParams().getCommonTree()) != WoodType.Palm)
				ceilingType = Core.getPlanks(WoodType.getTypeFromString(islandMap.getParams().getRareTree()));
			else
				ceilingType = TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, islandMap.getParams().getSurfaceRock());
		}
		else 
		{
			ceilingType = TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, islandMap.getParams().getSurfaceRock());
		}
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList listNBT = new NBTTagList();
		for(DungeonLevel dr : levels)
		{
			listNBT.appendTag(dr.writeToNBT());
		}
		nbt.setTag("levels", listNBT);
		nbt.setInteger("entrance", entrance.index);

		nbt.setInteger("floorTypeBlock", Block.getIdFromBlock(floorType.getBlock()));
		nbt.setInteger("floorTypeMeta", floorType.getBlock().getMetaFromState(floorType));

		nbt.setInteger("wallTypeBlock", Block.getIdFromBlock(wallType.getBlock()));
		nbt.setInteger("wallTypeMeta", wallType.getBlock().getMetaFromState(wallType));

		nbt.setInteger("ceilingTypeBlock", Block.getIdFromBlock(ceilingType.getBlock()));
		nbt.setInteger("ceilingTypeMeta", ceilingType.getBlock().getMetaFromState(ceilingType));

		nbt.setInteger("roomHeight", roomHeight);
		nbt.setBoolean("usesEvenRooms", usesEvenRooms);
	}

	public void readFromNBT(IslandMap map, NBTTagCompound nbt)
	{
		NBTTagList list = nbt.getTagList("levels", 10);
		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound aNBT = list.getCompoundTagAt(i);
			DungeonLevel level = new DungeonLevel();
			level.readFromNBT(aNBT);
			levels.add(level);
		}
		entrance = map.centers.get(nbt.getInteger("entrance"));
		floorType = Block.getStateById(nbt.getInteger("floorTypeBlock")).getBlock().getStateFromMeta(nbt.getInteger("floorTypeMeta"));
		wallType = Block.getStateById(nbt.getInteger("wallTypeBlock")).getBlock().getStateFromMeta(nbt.getInteger("wallTypeMeta"));
		ceilingType = Block.getStateById(nbt.getInteger("ceilingTypeBlock")).getBlock().getStateFromMeta(nbt.getInteger("ceilingTypeMeta"));

		roomHeight = nbt.getInteger("roomHeight");
		usesEvenRooms = nbt.getBoolean("usesEvenRooms");
	}

	public static class DungeonLevel
	{
		public int yLevel = 50;
		int roomCount = 0;
		public Vector<DungeonRoom> rooms = new Vector<DungeonRoom>();
		public Vector<DungeonDoor> doors = new Vector<DungeonDoor>();
		DungeonRect boundingBox;


		public void generate(Random r, int x, int z, Dungeon dungeon)
		{
			LinkedList<AttachPoint> attachPoints = new LinkedList<AttachPoint>();
			int count = 0;
			// 1. Establish the entrance
			DungeonRect rect = new DungeonRect(-5+x, -5+z, 11, 11);
			boundingBox = rect;
			DungeonRoom room = new DungeonRoom(roomCount++, rect);
			rooms.add(room);
			attachPoints.addAll(room.attachPoints);
			// 3. Create a room
			while(attachPoints.size() > 0)
			{
				room = null;
				AttachPoint ap = attachPoints.poll();
				if(count < 20 || r.nextFloat() < 0.5f && count < 200)
				{
					count++;
					if(r.nextFloat() < 0.6)
						room = CreateHall(r, ap, attachPoints, dungeon); 
					else 
						room = CreateRoom(r, ap, attachPoints, dungeon);
				}
			}

			//Call this multiple times to get rid of orphan hallways
			for(int i = 0; i < 5; i++)
			{
				cleanOrphans();
			}

			//Try to create doors between rooms that are adjacent to one another
			for(DungeonRoom dr : rooms)
			{
				//while we're at it, we'll update the bounds for the bounding box
				int xm  = Math.min(boundingBox.getMinX(), dr.getBoundingBox().getMinX());
				int zm  = Math.min(boundingBox.getMinZ(), dr.getBoundingBox().getMinZ());
				int w  = Math.max(boundingBox.getMaxX() - boundingBox.getMinX(), dr.getBoundingBox().getMaxX() - boundingBox.getMinX());
				int h  = Math.max(boundingBox.getMaxZ() - boundingBox.getMinZ(), dr.getBoundingBox().getMaxZ() - boundingBox.getMinZ());

				boundingBox = new DungeonRect(xm, zm, w, h);

				for(AttachPoint ap : dr.attachPoints)
				{
					boolean isDoor = false;
					for(DungeonDoor door : dr.doors)
					{
						if(door.location.equals(ap.location))
							isDoor = true;
					}
					if(!isDoor)
					{
						DungeonRect scanPos = null;
						if(ap.direction == EnumFacing.NORTH)
							scanPos = new DungeonRect(ap.location.getX(), ap.location.getZ()-1, 1, 1);
						else if(ap.direction == EnumFacing.SOUTH)
							scanPos = new DungeonRect(ap.location.getX(), ap.location.getZ()+1, 1, 1);
						else if(ap.direction == EnumFacing.EAST)
							scanPos = new DungeonRect(ap.location.getX()+1, ap.location.getZ(), 1, 1);
						else if(ap.direction == EnumFacing.WEST)
							scanPos = new DungeonRect(ap.location.getX()-1, ap.location.getZ(), 1, 1);
						DungeonRoom collidedRoom = checkCollision(scanPos);
						if(collidedRoom != null)
						{
							boolean found = false;
							//Check if we've already created a door here
							for(DungeonDoor door : collidedRoom.doors)
							{
								if(door.doesBridgeRooms(collidedRoom, dr))
									found = true;
							}
							if(!found)
							{
								//Create a new door for these rooms
								DungeonDoor door = new DungeonDoor(ap.location, dr, collidedRoom);
								door.doorType = DoorType.Ruins2x2;
								if(dungeon.roomHeight > 3)
									door.doorType = DoorType.Ruins3x2;
								if(rooms.contains(collidedRoom))
									dr.addDoors(door);
								collidedRoom.addDoors(door);
								doors.add(door);
							}
						}
					}
				}
			}
		}

		public DungeonRect getBoundingBox()
		{
			return this.boundingBox;
		}

		public void cleanOrphans()
		{
			Vector<DungeonRoom> roomsNew = new Vector<DungeonRoom>();
			for(DungeonRoom dr : rooms)
			{
				boolean valid = true;

				if(dr.roomType == RoomType.Hallway && dr.doors.size() < 2)
				{
					valid = false;
					doors.remove(dr.doors.get(0));
					dr.doors.get(0).bridgedRooms[0].doors.remove(dr.doors.get(0));
					dr.doors.get(0).bridgedRooms[1].doors.remove(dr.doors.get(0));
				}
				if(valid)
				{
					roomsNew.add(dr);
				}
			}
			rooms = roomsNew;
		}

		public DungeonRoom CreateRoom(Random r, AttachPoint ap, LinkedList<AttachPoint> attachPoints, Dungeon dungeon)
		{
			int roomX = 7+r.nextInt(6);
			if((roomX & 1) == 0)
			{
				roomX += 1;
			}
			int roomZ = 7+r.nextInt(6);
			if((roomZ & 1) == 0)
			{
				roomZ += 1;
			}

			DungeonRoom room = null;
			DungeonRect dr = null;
			switch(ap.direction)
			{
			case EAST:
			{
				dr = new DungeonRect(ap.location.getX()+1, ap.location.getZ()-roomZ/2, roomX, roomZ);
				break;
			}
			case WEST:
			{
				dr = new DungeonRect(ap.location.getX()-roomX, ap.location.getZ()-roomZ/2, roomX, roomZ);
				break;
			}
			case NORTH:
			{
				dr = new DungeonRect(ap.location.getX()-roomX/2, ap.location.getZ()-roomZ, roomX, roomZ);
				break;
			}
			case SOUTH:
			{
				dr = new DungeonRect(ap.location.getX()-roomX/2, ap.location.getZ()+1, roomX, roomZ);
				break;
			}
			default:
				break;

			}
			//We need to make sure that our dungeon only generates within the confines of our dungeon area which is +-200 blocks from the center of our dungeon
			if(dr.getMinX() < dungeon.entrance.point.getX()-200 || dr.getMaxX() > dungeon.entrance.point.getX()+200 || 
					dr.getMinZ() < dungeon.entrance.point.getY()-200 || dr.getMaxZ() > dungeon.entrance.point.getY()+200)
				return null;

			if(checkCollision(dr.expand(1)) == null)
				room = new DungeonRoom(roomCount++,dr);
			if(room != null)
			{
				DungeonDoor door = new DungeonDoor(new BlockPos(ap.location.getX(), 0, ap.location.getZ()), ap.rootRoom, room);
				room.addDoors(door);
				ap.rootRoom.addDoors(door);
				attachPoints.addAll(room.attachPoints);
				doors.add(door);
				room.roomType = RoomType.Room;
				rooms.add(room);
			}

			return room;
		}

		public DungeonRoom CreateHall(Random r, AttachPoint ap, LinkedList<AttachPoint> attachPoints, Dungeon dungeon)
		{
			int hallWidth = 3;
			int hallLength = 9+r.nextInt(9);
			if((hallLength & 1) == 0)
			{
				hallLength += 1;
			}
			DungeonRoom room = null;
			DungeonRect dr = null;
			switch(ap.direction)
			{
			case EAST:
			{
				dr = new DungeonRect(ap.location.getX()+1, ap.location.getZ()-1, hallLength, hallWidth);
				break;
			}
			case WEST:
			{
				dr = new DungeonRect(ap.location.getX()-hallLength, ap.location.getZ()-1, hallLength, hallWidth);
				break;
			}
			case NORTH:
			{
				dr = new DungeonRect(ap.location.getX()-1, ap.location.getZ()-hallLength, hallWidth, hallLength);
				break;
			}
			case SOUTH:
			{
				dr = new DungeonRect(ap.location.getX()-1, ap.location.getZ()+1, hallWidth, hallLength);
				break;
			}
			default: 
				break;
			}

			//We need to make sure that our dungeon only generates within the confines of our dungeon area which is +-200 blocks from the center of our dungeon
			if(dr.getMinX() < dungeon.entrance.point.getX()-200 || dr.getMaxX() > dungeon.entrance.point.getX()+200 || 
					dr.getMinZ() < dungeon.entrance.point.getY()-200 || dr.getMaxZ() > dungeon.entrance.point.getY()+200)
				return null;

			if(checkCollision(dr.expand(1)) == null)
				room = new DungeonRoom(roomCount++,dr);

			if(room != null)
			{
				DungeonDoor door = new DungeonDoor(new BlockPos(ap.location.getX(), 0, ap.location.getZ()), ap.rootRoom, room);
				room.addDoors(door);
				ap.rootRoom.addDoors(door);
				attachPoints.addAll(room.attachPoints);
				doors.add(door);
				room.roomType = RoomType.Hallway;
				rooms.add(room);
			}

			return room;
		}

		public DungeonRoom checkCollision(DungeonRect dr)
		{
			for(DungeonRoom d : rooms)
			{
				for(DungeonRect r : d.rects)
				{
					if(r.intersects(dr))
						return d;
				}
			}
			return null;
		}

		public NBTTagCompound writeToNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound aNBT;
			NBTTagList listNBT = new NBTTagList();
			for(DungeonRoom dr : rooms)
			{
				listNBT.appendTag(dr.writeToNBT());
			}
			nbt.setTag("roomList", listNBT);

			//Save the doors
			listNBT = new NBTTagList();
			for(DungeonDoor dr : doors)
			{
				aNBT = dr.writeToNBT();
				listNBT.appendTag(aNBT);
			}
			nbt.setTag("doorList", listNBT);

			nbt.setInteger("yLevel", yLevel);
			nbt.setInteger("bbX", boundingBox.X);
			nbt.setInteger("bbZ", boundingBox.Z);
			nbt.setInteger("bbW", boundingBox.width);
			nbt.setInteger("bbH", boundingBox.height);

			return nbt;
		}

		public void readFromNBT(NBTTagCompound nbt)
		{
			NBTTagList list = nbt.getTagList("roomList", 10);
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound aNBT = list.getCompoundTagAt(i);
				DungeonRoom dr = new DungeonRoom(0);
				dr.readFromNBT(this, aNBT);
				this.rooms.add(dr);
			}

			list = nbt.getTagList("doorList", 10);
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound aNBT = list.getCompoundTagAt(i);
				DungeonDoor dr = new DungeonDoor(new BlockPos(aNBT.getInteger("posX"), aNBT.getInteger("posY"), aNBT.getInteger("posZ")), 
						getRoomByID(aNBT.getInteger("room0")), getRoomByID(aNBT.getInteger("room1")));
				dr.doorType = DungeonDoor.DoorType.values()[aNBT.getInteger("doorType")];
				//When we load the doors, we need to be sure to readd them to the list and add the door ref to each room
				this.doors.add(dr);
				dr.bridgedRooms[0].addDoors(dr);
				dr.bridgedRooms[1].addDoors(dr);
			}

			yLevel = nbt.getInteger("yLevel");
			this.boundingBox = new DungeonRect(nbt.getInteger("bbX"), nbt.getInteger("bbZ"), nbt.getInteger("bbW"), nbt.getInteger("bbH"));
		}

		public DungeonRoom getRoomByID(int id)
		{
			for(DungeonRoom dr : rooms)
			{
				if(dr.roomID == id)
					return dr;
			}
			return null;
		}
	}

	public static class DungeonDoor
	{
		public BlockPos location;
		public DoorType doorType;
		public DungeonRoom[] bridgedRooms = new DungeonRoom[2];

		public DungeonDoor()
		{

		}

		public DungeonDoor(BlockPos loc, DungeonRoom dr0, DungeonRoom dr1)
		{
			location = loc;
			bridgedRooms[0] = dr0;
			bridgedRooms[1] = dr1;
			doorType = DoorType.Standard;
		}

		public boolean doesBridgeRooms(DungeonRoom dr1, DungeonRoom dr2)
		{
			return (bridgedRooms[0] == dr1 && bridgedRooms[1] == dr2) || (bridgedRooms[0] == dr2 && bridgedRooms[1] == dr1);
		}

		public NBTTagCompound writeToNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger("room0", bridgedRooms[0].roomID);
			nbt.setInteger("room1", bridgedRooms[1].roomID);
			nbt.setInteger("posX", location.getX());
			nbt.setInteger("posY", location.getY());
			nbt.setInteger("posZ", location.getZ());
			nbt.setInteger("doorType", doorType.ordinal());
			return nbt;
		}

		public static enum DoorType
		{
			Standard, Tall, DoubleLeft, DoubleRight, TallDoubleLeft, TallDoubleRight, Ruins2x2, Ruins3x2;
		}

	}

	public static class AttachPoint
	{
		public BlockPos location;
		public EnumFacing direction;
		public DungeonRoom rootRoom;

		public AttachPoint(BlockPos loc, EnumFacing dir, DungeonRoom dr)
		{
			location = loc;
			direction = dir;
			rootRoom = dr;
		}
	}

	public static class DungeonRoom
	{
		public Vector<AttachPoint> attachPoints;
		public Vector<DungeonRect> rects;
		public Vector<DungeonDoor> doors;
		public boolean shouldCull = false;
		DungeonRect boundingBox;
		public RoomType roomType = RoomType.Room;
		public int roomID;

		public DungeonRoom(int id, DungeonRect... r)
		{
			roomID = id;
			rects = new Vector<DungeonRect>();
			doors = new Vector<DungeonDoor>();
			attachPoints = new Vector<AttachPoint>();
			for(DungeonRect d : r)
			{
				if(boundingBox == null)
					boundingBox = new DungeonRect(d.getMinX(), d.getMinZ(), d.width, d.height);

				addRect(d);
				addAttach(getNorthAttach(d));
				addAttach(getSouthAttach(d));
				addAttach(getEastAttach(d));
				addAttach(getWestAttach(d));
			}
		}

		public NBTTagCompound writeToNBT()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagCompound nbtr;
			nbt.setInteger("roomID", roomID);
			nbt.setInteger("roomType", roomType.ordinal());
			NBTTagList list = new NBTTagList();
			for(DungeonRect dr : rects)
			{
				nbtr = new NBTTagCompound();
				nbtr.setInteger("x", dr.X);
				nbtr.setInteger("z", dr.Z);
				nbtr.setInteger("width", dr.width);
				nbtr.setInteger("height", dr.height);
				list.appendTag(nbtr);
			}
			nbt.setTag("rectList", list);
			return nbt;
		}

		public void readFromNBT(DungeonLevel d, NBTTagCompound nbt)
		{
			this.roomID = nbt.getInteger("roomID");
			this.roomType = RoomType.values()[nbt.getInteger("roomType")];
			NBTTagList list = nbt.getTagList("rectList", 10);
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound aNBT = list.getCompoundTagAt(i);
				this.addRect(new DungeonRect(aNBT.getInteger("x"), aNBT.getInteger("z"), aNBT.getInteger("width"), aNBT.getInteger("height")));
			}
		}

		public void addAttach(AttachPoint ap)
		{
			DungeonRect d = new DungeonRect(ap.location.getX(), ap.location.getZ(), 1, 1);
			for(DungeonRect dr : rects)
			{
				if(d.intersects(dr))
					return;
			}
			attachPoints.add(ap);
		}

		public DungeonRect getBoundingBox()
		{
			return this.boundingBox;
		}

		public DungeonRoom addDoors(DungeonDoor... r)
		{
			for(DungeonDoor d : r)
			{
				doors.add(d);
			}
			return this;
		}

		public void addRect(DungeonRect r)
		{
			if(boundingBox == null)
				boundingBox = new DungeonRect(r.X, r.Z, r.X + r.width, r.Z + r.height);
			else
				boundingBox = new DungeonRect(Math.min(boundingBox.getMinX(), r.getMinX()), Math.min(boundingBox.getMinZ(), r.getMinZ()), 
						Math.max(boundingBox.getMaxX() - boundingBox.getMinX(), r.getMaxX() - boundingBox.getMinX()), 
						Math.max(boundingBox.getMaxZ() - boundingBox.getMinZ(), r.getMaxZ() - boundingBox.getMinZ()));
			rects.add(r);
		}

		public AttachPoint getNorthAttach(DungeonRect dr)
		{
			return new AttachPoint(new BlockPos(dr.getCenterX(), 0, dr.getMinZ()-1), EnumFacing.NORTH, this);
		}

		public AttachPoint getSouthAttach(DungeonRect dr)
		{
			return new AttachPoint(new BlockPos(dr.getCenterX(), 0, dr.getMaxZ()), EnumFacing.SOUTH, this);
		}

		public AttachPoint getEastAttach(DungeonRect dr)
		{
			return new AttachPoint(new BlockPos(dr.getMaxX(), 0, dr.getCenterZ()), EnumFacing.EAST, this);
		}

		public AttachPoint getWestAttach(DungeonRect dr)
		{
			return new AttachPoint(new BlockPos(dr.getMinX()-1, 0, dr.getCenterZ()), EnumFacing.WEST, this);
		}
	}

	public static enum RoomType
	{
		Room, Hallway;
	}

	public static class DungeonRect
	{
		public int X;
		public int Z;
		public int width;
		public int height;

		public DungeonRect(int x, int z, int sizex, int sizez)
		{
			X = x;
			Z = z;
			width = sizex;
			height = sizez;
		}

		public boolean intersects(DungeonRect r)
		{
			if(r.getMinX() < getMaxX() && 
					r.getMaxX() > getMinX()  && 
					r.getMinZ() < getMaxZ())
				return r.getMaxZ() > getMinZ();
				else return false;

			//return !( r.getMinX() > getMaxX() || r.getMaxX() <  getMinX() || r.getMinZ() > getMaxZ() || r.getMaxZ() < getMinZ());
		}

		public int getArea()
		{
			return width * height;
		}

		public int getDirection(DungeonRect r)
		{
			if(r.X <= getCenterX() && r.Z <= getCenterZ())
				return 0;
			else if(r.X >= getCenterX() && r.Z <= getCenterZ())
				return 1;
			else if(r.X >= getCenterX() && r.Z >= getCenterZ())
				return 2;
			else if(r.X <= getCenterX() && r.Z >= getCenterZ())
				return 3;
			else return -1;
		}

		public int getMinX()
		{
			return X;
		}

		public int getMaxX()
		{
			return X+width;
		}

		public int getMinZ()
		{
			return Z;
		}

		public int getMaxZ()
		{
			return Z+height;
		}

		public float getCenterX()
		{
			return X+width/2;
		}
		public float getCenterZ()
		{
			return Z+height/2;
		}

		public DungeonRect expand(int size)
		{

			return new DungeonRect(X+ (-size), Z+(-size), width+size*2, height+size*2);
		}

		public DungeonRect expand(int t, int b, int l, int r)
		{
			return new DungeonRect(X-l, Z-t, width+r, height+b);
		}
	}
}