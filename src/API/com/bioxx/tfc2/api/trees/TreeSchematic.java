package com.bioxx.tfc2.api.trees;

import java.util.ArrayList;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import com.bioxx.tfc2.api.Schematic;
import com.bioxx.tfc2.api.types.WoodType;

public class TreeSchematic extends Schematic
{
	private int size;
	private WoodType type;
	private int baseCount = 0;
	private int logCount = 0;

	public TreeSchematic(String p, String f, WoodType w)
	{
		super(p, f);
		type = w;
	}

	@Override
	public void PostProcess()
	{
		ArrayList<SchemBlock> map = new ArrayList<SchemBlock>();
		for(SchemBlock b : blockMap)
		{
			if(b.state.getBlock() != Blocks.AIR)
			{
				if(b.pos.getY() == 0)
					baseCount++;
				map.add(b);
				if(b.state.getBlock().getMaterial(b.state) == Material.WOOD)
					logCount++;
			}

		}
		blockMap = map;

		int num = filename.indexOf('_');
		String s = filename.substring(0, num);
		if(s.equals("large"))
			size = 2;
		else if(s.equals("normal"))
			size = 1;
		else
			size = 0;

		aabb = new AxisAlignedBB(0, 0, 0, width, height, length);
	}

	@Override
	public AxisAlignedBB getBoundingBox(BlockPos pos)
	{
		return aabb.addCoord(pos.getX()-getCenterX()+1, pos.getY(), pos.getZ()-getCenterZ()+1);
	}

	public int getBaseCount()
	{
		return this.baseCount;
	}

	public int getLogCount()
	{
		return this.logCount;
	}

	public int getGrowthStage()
	{
		return size;
	}

	public WoodType getWoodType()
	{
		return type;
	}
}
