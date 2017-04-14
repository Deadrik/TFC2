package com.bioxx.tfc2.handlers;

import java.util.*;

import net.minecraft.init.Blocks;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.dungeon.*;
import com.bioxx.jmapgen.dungeon.RoomSchematic.RoomType;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.events.IslandGenEvent;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.BlockStoneBrick;
import com.bioxx.tfc2.blocks.BlockStoneSmooth;

public class CreateDungeonHandler 
{
	private boolean[] tempMap;
	@SubscribeEvent
	public void createDungeon(IslandGenEvent.Post event)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			return;
		DungeonSchemManager dsm = DungeonSchemManager.getInstance();
		Random random = event.islandMap.mapRandom;

		Vector<Center> dungeonCenters = event.islandMap.getLandCenters();
		dungeonCenters = this.removeRiverCenters(dungeonCenters);
		dungeonCenters = event.islandMap.getCentersAbove(dungeonCenters, 0.3);
		dungeonCenters = event.islandMap.getCentersBelow(dungeonCenters, 0.6, false);

		if(dungeonCenters.size() == 0)
			return;

		//Find a suitable location for the entrance
		Center start = null;
		int counter = 0;
		while(start == null)
		{
			start = dungeonCenters.get(random.nextInt(dungeonCenters.size()));
			//pick a relatively flat area.
			if(counter > 2500 || Math.abs(start.getAverageElevation() - start.getElevation()) <= 0.08)
			{
				break;
			}
			start = null;
			counter++;
		}

		//We want our dungeon to have its entrance in this chunk.
		int xStartChunk = ((int)(start.point.x) >> 4);
		int zStartChunk = ((int)(start.point.y) >> 4);

		//Elevation of the center
		int startElev = event.islandMap.convertHeightToMC(start.getElevation())+64;
		//this is the Y level where the dungeon will start
		int elev = startElev-30;

		DungeonTheme dungeonTheme = dsm.getRandomTheme(random);
		Dungeon dungeon = new Dungeon(dungeonTheme.getThemeName(), xStartChunk, elev, zStartChunk);
		dungeon.blockMap.put("dungeon_wall", TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, event.islandMap.getParams().getSurfaceRock()));
		dungeon.blockMap.put("dungeon_floor", Core.getPlanks(WoodType.getTypeFromString(event.islandMap.getParams().getCommonTree())));
		dungeon.blockMap.put("dungeon_ceiling", TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, event.islandMap.getParams().getSurfaceRock()));
		dungeon.blockMap.put("dungeon_smoothstone", TFCBlocks.StoneSmooth.getDefaultState().withProperty(BlockStoneSmooth.META_PROPERTY, event.islandMap.getParams().getSurfaceRock()));
		dungeon.blockMap.put("dungeon_stairs_floor", TFCBlocks.StairsOak.getDefaultState());
		dungeon.blockMap.put("dungeon_stairs_wall", TFCBlocks.StairsOak.getDefaultState());
		dungeon.blockMap.put("dungeon_door", Blocks.OAK_DOOR.getDefaultState());

		while(true)
		{
			genDungeon(event.islandMap, dungeonTheme, random, xStartChunk, zStartChunk, dungeon);
			if(dungeon.getRoomCount() > 30)
				break;
			dungeon.resetDungeonMap();
		}
		TFC.log.info("Dungeon: " + start.point.toString() + " | Size : " + dungeon.getRoomCount());
		event.islandMap.dungeons.add(dungeon);
	}

	private void genDungeon(IslandMap map, DungeonTheme dungeonTheme, Random random, int xStartChunk, int zStartChunk, Dungeon dungeon) 
	{
		DungeonRoom dungeonEntrance = new DungeonRoom(dungeonTheme.getRandomEntrance(random), dungeon.dungeonStart);
		dungeon.setRoom(xStartChunk, 0, zStartChunk, dungeonEntrance);
		LinkedList<DungeonRoom> queue = new LinkedList<DungeonRoom>();
		queue.add(dungeonEntrance);
		while(queue.peek() != null)
		{
			DungeonRoom room = queue.poll();
			if(room == null || room.getSchematic() == null)
				continue;
			boolean isRoomValid = true;
			boolean addedRoom = false;
			for(DungeonDirection dir : room.getSchematic().getConnections())
			{
				RoomPos pos = room.getPosition().offset(dir);
				//Have we already established a connection in this direction?
				if(isRoomValid && !room.hasConnection(dir))
				{
					DungeonRoom neighbor = dungeon.getRoom(room.getPosition().offset(dir));
					/**
					 * Create a new random room in this direction
					 */
					if(neighbor == null && checkElevation(map, dungeon, room.getPosition().offset(dir)))
					{
						RoomSchematic schem = null;
						double dist = room.getPosition().offset(dir).distanceSq(dungeon.dungeonStart);
						if(dist > 256)
							schem = dungeonTheme.getRandomRoomSingleDirection(random, dir.getOpposite());
						else if(random.nextDouble() < 0.25 && room.getPosition().getY() > 16)
							schem = dungeonTheme.getRandomRoomForDirection(random, dir.getOpposite(), RoomType.Stairs);
						else
							schem = dungeonTheme.getRandomRoomForDirection(random, dir.getOpposite());

						if(schem == null)
							continue;

						neighbor = new DungeonRoom(schem, room.getPosition().offset(dir));
						linkRooms(room, neighbor, dir);
						addedRoom = true;

						if(!neighbor.getSchematic().getSetPieceMap().isEmpty())
						{
							if(checkSetPiece(map, dungeon, neighbor.getPosition(), neighbor.getSchematic().getSetPieceMap()))
							{
								Iterator<RoomPos> iter = neighbor.getSchematic().getSetPieceMap().keySet().iterator();
								while(iter.hasNext())
								{
									RoomPos setPos = iter.next();
									String s = neighbor.getSchematic().getSetPieceMap().get(setPos);
									setPos = pos.add(setPos);
									DungeonRoom setpieceRoom = new DungeonRoom(dungeonTheme.getSchematic(s), setPos);
									dungeon.setRoom(setPos, setpieceRoom);
									queue.add(setpieceRoom);
								}
							}
							else
							{
								neighbor.clearConnections(dungeon);
								neighbor = null;
							}
						}

					}
					else if(neighbor != null)//A room already exists in this neighbor location
					{
						//If the neighbor can connect to this room then link them
						if(neighbor.getSchematic().getConnections().contains(dir.getOpposite()))
						{
							linkRooms(room, neighbor, dir);
						}
					}

					if(neighbor != null && addedRoom)
					{
						queue.add(neighbor);
						dungeon.setRoom(neighbor);
					}
				}

				if(!isRoomValid)
					break;
			}

			if(!isRoomValid)
			{
				room.clearConnections(dungeon);
				requeueNeighbors(queue, dungeon, room);
				dungeon.setRoom(room.getPosition(), null);
			}
		}
	}

	boolean checkElevation(IslandMap map, Dungeon dungeon, RoomPos pos)
	{
		Center closest = map.getClosestCenter(new Point((pos.getX() << 4)+8, (pos.getZ() << 4)+8));
		if(pos.getY()+14 > map.convertHeightToMC(closest.getElevation())+64)//we do 14 instead of 10 to make sure that the schematic is a bit deeper underground
			return false;
		if(pos.getY() > dungeon.dungeonStart.getY())
			return false;
		return true;
	}

	boolean checkSetPiece(IslandMap islandMap, Dungeon dungeon, RoomPos startPos, Map<RoomPos, String> map)
	{
		Iterator<RoomPos> iter = map.keySet().iterator();
		while(iter.hasNext())
		{
			RoomPos pos = iter.next();
			RoomPos pos2 = startPos.add(pos);
			if(dungeon.getRoom(pos2) != null)
				return false;
			if(!checkElevation(islandMap, dungeon, pos2))
				return false;
		}

		return true;
	}

	void requeueNeighbors(LinkedList<DungeonRoom> queue, Dungeon dungeon, DungeonRoom room)
	{
		DungeonRoom other = dungeon.getRoom(room.getPosition().offset(DungeonDirection.NORTH));
		if(other != null)
			queue.add(other);
		other = dungeon.getRoom(room.getPosition().offset(DungeonDirection.SOUTH));
		if(other != null)
			queue.add(other);
		other = dungeon.getRoom(room.getPosition().offset(DungeonDirection.EAST));
		if(other != null)
			queue.add(other);
		other = dungeon.getRoom(room.getPosition().offset(DungeonDirection.WEST));
		if(other != null)
			queue.add(other);
		other = dungeon.getRoom(room.getPosition().offset(DungeonDirection.UP));
		if(other != null)
			queue.add(other);
		other = dungeon.getRoom(room.getPosition().offset(DungeonDirection.DOWN));
		if(other != null)
			queue.add(other);
	}

	void linkRooms(DungeonRoom room1, DungeonRoom room2, DungeonDirection room1_dir)
	{
		room1.addConnection(room1_dir, new RoomLink(true));
		room2.addConnection(room1_dir.getOpposite(), new RoomLink(false));
	}

	public Vector<Center> removeRiverCenters(Vector<Center> list)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : list)
		{
			if(!c.hasAttribute(Attribute.River))
				out.add(c);
		}
		return out;
	}
}
