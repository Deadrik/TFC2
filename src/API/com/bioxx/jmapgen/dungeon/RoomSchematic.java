package com.bioxx.jmapgen.dungeon;

import java.io.InputStreamReader;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import com.bioxx.tfc2.api.Schematic;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

public class RoomSchematic extends Schematic
{
	EnumSet<DungeonDirection> connections = EnumSet.noneOf(DungeonDirection.class);
	Map<String, IBlockState> templateMap = new HashMap<String, IBlockState>();
	/**
	 * This map allows us to guarantee that a certain schematic will always appear in next to this schematic. Good for stairs or very large rooms.
	 */
	Map<DungeonDirection, String> matchingRoomMap = new HashMap<DungeonDirection, String>();
	double chooseWeight = 1.0;
	RoomType roomType = RoomType.Normal;

	public RoomSchematic(String p, String f) 
	{
		super(p, f);
	}

	@Override
	public void PostProcess()
	{
		try
		{
			Gson gson = new Gson();
			InputStreamReader sr = new InputStreamReader(getClass().getResourceAsStream("/assets/tfc2/schematics/dungeons/" + this.filename + ".json"));
			JsonReader reader = new JsonReader(sr);

			RoomJSON r = gson.fromJson(reader, RoomJSON.class);

			for(String s : r.connections)
			{
				DungeonDirection rc = DungeonDirection.fromString(s);
				if(rc != null)
					connections.add(rc);
			}

			Iterator<String> iter = r.blockMap.iterator();
			while(iter.hasNext())
			{
				String key = iter.next();
				String[] template = key.split("\\|");
				String[] blockString = template[1].split(" ");
				int meta = (blockString.length == 2 ? Integer.parseInt(blockString[1]) : 0);
				templateMap.put(template[0], Block.getBlockFromName(blockString[0]).getStateFromMeta(meta));
			}

			iter = r.getMatchingRoomMap().iterator();
			while(iter.hasNext())
			{
				String key = iter.next();
				String[] template = key.split("\\|");
				String schemString = template[1];
				DungeonDirection dir = DungeonDirection.fromString(template[0]);
				matchingRoomMap.put(dir, schemString);
			}

			ArrayList<SchemBlock> outList = new ArrayList<SchemBlock>();
			for(SchemBlock block : this.getBlockMap())
			{
				if(block.state == templateMap.get("null"))
					continue;
				else
					block.pos = block.pos.down(r.floorY);

				outList.add(block);
			}
			this.blockMap = outList;

			roomType = RoomType.fromString(r.getRoomType());
			chooseWeight = r.getWeight();
		}
		catch(JsonSyntaxException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public ArrayList<SchemBlock> getProcessedBlockList(Dungeon dungeon)
	{
		ArrayList<SchemBlock> outList = new ArrayList<SchemBlock>();

		for(SchemBlock block : this.getBlockMap())
		{
			if(block.state == templateMap.get("null"))
				continue;
			else if(block.state == templateMap.get("dungeon_wall"))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_wall"), block.pos));
			}
			else if(block.state == templateMap.get("dungeon_floor"))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_floor"), block.pos));
			}
			else if(block.state == templateMap.get("dungeon_ceiling"))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_ceiling"), block.pos));
			}
			else if(block.state == templateMap.get("dungeon_stairs_floor"))
			{
				IBlockState state = dungeon.blockMap.get("dungeon_stairs_floor").getBlock().getStateFromMeta(block.state.getBlock().getMetaFromState(block.state));
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_stairs_floor"), block.pos));
			}
			else if(block.state == templateMap.get("dungeon_stairs_wall"))
			{
				IBlockState state = dungeon.blockMap.get("dungeon_stairs_wall").getBlock().getStateFromMeta(block.state.getBlock().getMetaFromState(block.state));
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_stairs_wall"), block.pos));
			}
			else if(block.state == templateMap.get("dungeon_door"))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_door"), block.pos));
			}
			else
			{
				outList.add(block);
			}
		}

		return outList;
	}

	public double getChooseWeight() {
		return chooseWeight;
	}

	public void setChooseWeight(double chooseWeight) {
		this.chooseWeight = chooseWeight;
	}

	public EnumSet<DungeonDirection> getConnections() {
		return connections;
	}

	public Map<String, IBlockState> getTemplateMap() {
		return templateMap;
	}

	public Map<DungeonDirection, String> getMatchingRoomMap() {
		return matchingRoomMap;
	}

	public RoomType getRoomType()
	{
		return roomType;
	}

	public static enum RoomType
	{
		Entrance("entrance"),Normal("normal"),Stairs("stairs");

		String name;

		RoomType(String s)
		{
			name = s;
		}

		public static RoomType fromString(String s)
		{
			for(RoomType r : RoomType.values())
			{
				if(r.name.equalsIgnoreCase(s))
					return r;
			}
			return null;
		}
	}

	public class RoomJSON {

		@SerializedName("blockmap")
		@Expose
		private List<String> blockMap = new ArrayList<String>();

		@SerializedName("floorY")
		@Expose
		private Integer floorY = 0;

		@SerializedName("connections")
		@Expose
		private List<String> connections = new ArrayList<String>();

		@SerializedName("weight")
		@Expose
		private double weight = 1.0;

		@SerializedName("matchingRoomMap")
		@Expose
		private List<String> matchingRoomMap = new ArrayList<String>();

		@SerializedName("roomType")
		@Expose
		private String roomType = "normal";

		/**
		 * 
		 * @return
		 * The blockMap
		 */
		public List<String> getBlockMap() {
			return blockMap;
		}

		/**
		 * 
		 * @param blockMap
		 * The blockMap
		 */
		public void setBlockMap(List<String> blockMap) {
			this.blockMap = blockMap;
		}

		/**
		 * 
		 * @return
		 * The floorY
		 */
		public Integer getFloorY() {
			return floorY;
		}

		/**
		 * 
		 * @param floorY
		 * The floorY
		 */
		public void setFloorY(Integer floorY) {
			this.floorY = floorY;
		}

		/**
		 * 
		 * @return
		 * The connections
		 */
		public List<String> getConnections() {
			return connections;
		}

		/**
		 * 
		 * @param connections
		 * The connections
		 */
		public void setConnections(List<String> connections) {
			this.connections = connections;
		}

		public double getWeight() {
			return weight;
		}

		public void setWeight(double weight) {
			this.weight = weight;
		}

		public List<String> getMatchingRoomMap() 
		{
			if(matchingRoomMap != null)
				return matchingRoomMap;
			return new ArrayList<String>();
		}

		public void setMatchingRoomMap(List<String> matchingRoomMap) {
			this.matchingRoomMap = matchingRoomMap;
		}

		public String getRoomType() {
			if(roomType != null)
				return roomType;
			return "normal";
		}

		public void setRoomType(String roomType) {
			this.roomType = roomType;
		}

	}
}