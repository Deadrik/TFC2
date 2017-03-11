package com.bioxx.jmapgen.dungeon;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.Schematic;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

public class RoomSchematic extends Schematic
{
	String theme;
	EnumSet<DungeonDirection> connections = EnumSet.noneOf(DungeonDirection.class);
	Map<String, IBlockState> templateMap = new HashMap<String, IBlockState>();
	Map<RoomPos, String> setPieceMap = new HashMap<RoomPos, String>();

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
			InputStream stream = this.getClass().getResourceAsStream(this.path.replace(".schematic", ".json"));
			InputStreamReader sr = new InputStreamReader(stream);
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

			iter = r.getSetPieceMap().iterator();
			while(iter.hasNext())
			{
				String key = iter.next();
				String[] template = key.split("\\|");
				String[] directions = template[0].split("\\-\\>");
				String schemString = template[1];
				RoomPos pos = new RoomPos(0,0,0);
				for(String d : directions)
				{
					DungeonDirection dir = DungeonDirection.fromString(d);
					pos = pos.offset(dir);
				}
				setPieceMap.put(pos, schemString);
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
		catch(NullPointerException ex)
		{
			TFC.log.error("NullPointerException while loading "+this.filename);
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
			if(matchesTranslation(block.state, templateMap.get("null")))
				continue;
			else if(block.state.getBlock() == Blocks.AIR)
			{
				outList.add(block);
			}
			else if(matchesTranslation(block.state, templateMap.get("dungeon_wall")))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_wall"), block.pos));
			}
			else if(matchesTranslation(block.state, templateMap.get("dungeon_ceiling")))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_ceiling"), block.pos));
			}
			else if(matchesTranslation(block.state, templateMap.get("dungeon_floor")))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_floor"), block.pos));
			}
			else if(block.state.getBlock() == templateMap.get("dungeon_stairs_floor").getBlock())
			{
				IBlockState state = dungeon.blockMap.get("dungeon_stairs_floor");
				state = state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(block.state));
				outList.add(new SchemBlock(state, block.pos));
			}
			else if(block.state.getBlock() == templateMap.get("dungeon_stairs_wall").getBlock())
			{
				IBlockState state = dungeon.blockMap.get("dungeon_stairs_wall");
				state = state.getBlock().getStateFromMeta(state.getBlock().getMetaFromState(block.state));
				outList.add(new SchemBlock(state, block.pos));
			}
			else if(matchesTranslationBlock(block.state, templateMap.get("dungeon_door")))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_door"), block.pos));
			}
			else if(matchesTranslation(block.state, templateMap.get("dungeon_smoothstone")))
			{
				outList.add(new SchemBlock(dungeon.blockMap.get("dungeon_smoothstone"), block.pos));
			}
			else
			{

				boolean found = false;
				for(String s : templateMap.keySet())
				{
					String[] blockString = s.split(" ");
					int meta = (blockString.length == 2 ? Integer.parseInt(blockString[1]) : 0);

					if(templateMap.get(s) == block.state || (meta == -1 && templateMap.get(s).getBlock() == block.state.getBlock()))
					{
						Block b = Block.getBlockFromName(blockString[0]);
						if(b == null)
						{
							TFC.log.warn("Block not found for dungeon generation: "+blockString[0]);
							break;
						}
						/*if((block.state.getBlock().getMaterial(block.state) == Material.WATER || block.state.getBlock().getMaterial(block.state) == Material.LAVA))
						{
							if(block.state.getBlock().getMetaFromState(block.state) != 0)
							{
								outList.add(new SchemBlock(Blocks.AIR.getDefaultState(), block.pos));
								found = true;
								break;
							}
						}*/
						if(meta == -1)
						{
							meta = block.state.getBlock().getMetaFromState(block.state);
							outList.add(new SchemBlock(b.getStateFromMeta(meta), block.pos));
							found = true;
							break;
						}

						if(b != null)
						{
							outList.add(new SchemBlock(b.getStateFromMeta(meta), block.pos));
							found = true;
							break;
						}
					}
				}
				if(!found)
					outList.add(block);
			}
		}

		return outList;
	}

	private boolean matchesTranslation(IBlockState state, IBlockState blockMapState)
	{
		if(blockMapState == null)
			return false;

		if(blockMapState == state)
			return true;

		return false;
	}

	private boolean matchesTranslationBlock(IBlockState state, IBlockState blockMapState)
	{
		if(blockMapState == null)
			return false;

		if(blockMapState.getBlock()== state.getBlock())
			return true;

		if(blockMapState == state)
			return true;

		return false;
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

	public Map<RoomPos, String> getSetPieceMap() {
		return setPieceMap;
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

		@SerializedName("floory")
		@Expose
		private Integer floorY = 0;

		@SerializedName("connections")
		@Expose
		private List<String> connections = new ArrayList<String>();

		@SerializedName("weight")
		@Expose
		private double weight = 1.0;

		@SerializedName("roomtype")
		@Expose
		private String roomType = "normal";

		@SerializedName("setpiecemap")
		@Expose
		private List<String> setPieceMap = new ArrayList<String>();

		/**
		 * 
		 * @return
		 * The blockMap
		 */
		public List<String> getBlockMap() 
		{
			return blockMap;
		}

		/**
		 * 
		 * @param blockMap
		 * The blockMap
		 */
		public void setBlockMap(List<String> blockMap) 
		{
			this.blockMap = blockMap;
		}

		/**
		 * 
		 * @return
		 * The floorY
		 */
		public Integer getFloorY() 
		{
			return floorY;
		}

		/**
		 * 
		 * @param floorY
		 * The floorY
		 */
		public void setFloorY(Integer floorY) 
		{
			this.floorY = floorY;
		}

		/**
		 * 
		 * @return
		 * The connections
		 */
		public List<String> getConnections() 
		{
			return connections;
		}

		/**
		 * 
		 * @param connections
		 * The connections
		 */
		public void setConnections(List<String> connections) 
		{
			this.connections = connections;
		}

		public double getWeight() 
		{
			return weight;
		}

		public void setWeight(double weight) 
		{
			this.weight = weight;
		}

		public String getRoomType() 
		{
			if(roomType != null)
				return roomType;
			return "normal";
		}

		public void setRoomType(String roomType) 
		{
			this.roomType = roomType;
		}

		public List<String> getSetPieceMap() 
		{
			if(setPieceMap != null)
				return setPieceMap;
			return new ArrayList<String>();
		}

		public void setSetPieceMap(List<String> setPieceMap) 
		{
			this.setPieceMap = setPieceMap;
		}

	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
}