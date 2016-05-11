package com.bioxx.tfc2.handlers;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import net.minecraft.init.Blocks;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.dungeon.*;
import com.bioxx.jmapgen.dungeon.RoomSchematic.RoomType;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.events.IslandGenEvent;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.BlockPlanks;
import com.bioxx.tfc2.blocks.BlockStoneBrick;

public class CreateDungeonHandler 
{
	private boolean[] tempMap;
	@SubscribeEvent
	public void createDungeon(IslandGenEvent.Post event)
	{
		DungeonSchemManager dsm = DungeonSchemManager.getInstance();
		Random random = event.islandMap.mapRandom;

		Vector<Center> dungeonCenters = event.islandMap.getLandCenters();
		dungeonCenters = this.removeRiverCenters(dungeonCenters);
		dungeonCenters = event.islandMap.getCentersAbove(dungeonCenters, 0.3);
		dungeonCenters = event.islandMap.getCentersBelow(dungeonCenters, 0.6, false);
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

		int xStartLocal = 4+random.nextInt(8);
		int zStartLocal = 4+random.nextInt(8);

		//Elevation of the center
		int startElev = event.islandMap.convertHeightToMC(start.getElevation())+64;
		//this is the Y level where the dungeon will start
		int elev = startElev-30;

		Dungeon dungeon = new Dungeon(dsm.getRandomTheme(random), 16, xStartChunk-xStartLocal, elev, zStartChunk-zStartLocal);
		dungeon.blockMap.put("dungeon_wall", TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, event.islandMap.getParams().getSurfaceRock()));
		dungeon.blockMap.put("dungeon_floor", TFCBlocks.Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.getTypeFromString(event.islandMap.getParams().getCommonTree())));
		dungeon.blockMap.put("dungeon_ceiling", TFCBlocks.StoneBrick.getDefaultState().withProperty(BlockStoneBrick.META_PROPERTY, event.islandMap.getParams().getSurfaceRock()));
		dungeon.blockMap.put("dungeon_stairs_floor", TFCBlocks.StairsOak.getDefaultState());
		dungeon.blockMap.put("dungeon_stairs_wall", TFCBlocks.StairsOak.getDefaultState());
		dungeon.blockMap.put("dungeon_door", Blocks.OAK_DOOR.getDefaultState());

		TFC.log.info("Dungeon: " + start.point.toString());

		DungeonRoom dungeonEntrance = new DungeonRoom(dsm.getRandomEntrance(random, dungeon.getTheme()), new RoomPos(xStartLocal, 0, zStartLocal));
		dungeon.setRoom(xStartLocal, 0, zStartLocal, dungeonEntrance);
		LinkedList<DungeonRoom> queue = new LinkedList<DungeonRoom>();
		queue.add(dungeonEntrance);
		while(queue.peek() != null)
		{
			DungeonRoom room = queue.poll();
			boolean isRoomValid = true;
			for(DungeonDirection dir : room.schematic.getConnections())
			{
				RoomPos pos = room.getPosition().offset(dir);
				if(pos.getX() < 0 || pos.getZ() < 0 || pos.getX() >= dungeon.getSize() || pos.getZ() >= dungeon.getSize() || pos.getY() < 0 || pos.getY() >= 8)
					continue;
				//Have we already established a connection in this direction?
				if(isRoomValid && !room.hasConnection(dir))
				{
					/**
					 * If this room requires a matching room in a direction, then find the schematic,
					 * create a link for these rooms, and add the other room to the queue.
					 */
					if(room.schematic.getMatchingRoomMap().get(dir) != null)
					{
						String matching = room.schematic.getMatchingRoomMap().get(dir);
						RoomSchematic matchingSchem = dsm.getSchematic(matching.split("_")[0], matching);
						if(matchingSchem == null)
						{
							TFC.log.warn("[Dungeon Gen] Attempted to place a matching room with an invalid schem name.");
						}
						else
						{
							/**
							 * Make sure that there is not already a room in this direction. If there is
							 * then we should forget about placing this schematic in this location.
							 */
							if(dungeon.getRoom(room.getPosition().offset(dir)) == null)
							{
								DungeonRoom matchingRoom = new DungeonRoom(matchingSchem, room.getPosition().offset(dir));
								linkRooms(room, matchingRoom, dir);
								queue.add(matchingRoom);
								dungeon.setRoom(matchingRoom);
							}
							else
							{
								isRoomValid = false;
							}
						}
					}
					else
					{
						DungeonRoom neighbor = dungeon.getRoom(room.getPosition().offset(dir));
						/**
						 * Create a new random room in this direction
						 */
						if(neighbor == null)
						{
							RoomSchematic schem = null;

							if(random.nextDouble() < 0.05 && dungeon.getDungeonY() - (room.getPosition().getY()+1)*10 > 10)
								schem = dsm.getRandomRoomForDirection(random, dungeon.getTheme(), dir.getOpposite(), RoomType.Stairs);
							else
								schem = dsm.getRandomRoomForDirection(random, dungeon.getTheme(), dir.getOpposite());

							if(schem == null)
								continue;

							neighbor = new DungeonRoom(schem, room.getPosition().offset(dir));
							linkRooms(room, neighbor, dir);
						}
						else
						{
							if(neighbor.schematic.getConnections().contains(dir.getOpposite()))
							{
								linkRooms(room, neighbor, dir);
							}
						}

						if(neighbor != null)
						{
							queue.add(neighbor);
							dungeon.setRoom(neighbor);
						}
					}
				}
				if(!isRoomValid)
					break;
			}

			if(!isRoomValid)
			{
				room.clearConnections();
				dungeon.setRoom(room.getPosition(), null);
			}
		}

		event.islandMap.dungeons.add(dungeon);
	}

	void linkRooms(DungeonRoom room1, DungeonRoom room2, DungeonDirection room1_dir)
	{
		room1.addConnection(room1_dir, new RoomLink(room2, true));
		room2.addConnection(room1_dir.getOpposite(), new RoomLink(room1, false));
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
