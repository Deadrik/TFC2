package com.bioxx.tfc2.world.hexgen;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.Spline3D;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.PortalAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.Schematic.SchemBlock;
import com.bioxx.tfc2.api.types.PortalEnumType;
import com.bioxx.tfc2.blocks.BlockPortal;
import com.bioxx.tfc2.blocks.BlockPortalStone;

public class WorldGenPortalsHex extends WorldGenHex
{
	public WorldGenPortalsHex()
	{

	}

	@Override
	public void generate(Random random, IslandMap map, Center closest, World world) 
	{
		super.generate(random, map, closest, world);

		if(world.provider.getDimension() == 0)
		{
			if(closest != null && closest.hasAttribute(Attribute.Portal))
			{
				PortalAttribute attr = (PortalAttribute) closest.getAttribute(Attribute.Portal);
				Point p = new Point(centerX, centerZ);
				BlockPos bp = centerPos;

				/*
				 * This should hopefully prevent stacking portals from occuring if the world saves but the islandmap does not
				 */
				if(findPortal(world, bp.add(0, map.convertHeightToMC(closest.elevation)+Global.SEALEVEL, 0)) != null)
				{
					return;
				}

				bp = world.getTopSolidOrLiquidBlock(bp);
				BlockPos portalPos = bp;
				BuildPortalSchem(world, closest, portalPos, map, false);
				//Once we generate the portal structure, just end this generator. 
				//We dont want to potentially generate it 256 times
				return;
			}
		}
	}

	private BlockPos findPortal(World world, BlockPos pos)
	{
		for(int x = -64; x < 65; x++)
		{
			for(int z = -64; z < 65; z++)
			{
				for(int y = -20; y < 20; y++)
				{
					IBlockState state = world.getBlockState(pos.add(x, y, z));
					if(state.getBlock() == TFCBlocks.Portal && (Boolean)state.getValue(BlockPortal.CENTER) == true)
					{
						return pos.add(x, y, z).down();
					}
				}
			}
		}
		return null;
	}

	public static void BuildPortalSchem(World world, Center c, BlockPos portalPos, IslandMap map, boolean flip) {
		PortalAttribute attr = (PortalAttribute) c.getAttribute(Attribute.Portal);
		//TODO: Generate portal structure here

		BlockPos localPos;
		IBlockState state;
		EnumFacing.Axis axis = EnumFacing.Axis.X;
		EnumFacing dir = attr.direction;
		if(flip)
			dir = dir.getOpposite();

		AxisAlignedBB aabb = Core.PortalSchematic.getBoundingBox(portalPos);
		ArrayList<BlockPos> clearArea = Core.getBlockPosInAABB(aabb.offset(-4, 1, -5));
		for(BlockPos pos : clearArea)
		{
			if(Core.isTerrain(world.getBlockState(pos)))
				world.setBlockToAir(pos);
		}

		for(SchemBlock b : Core.PortalSchematic.getBlockMap())
		{
			localPos = b.pos;
			state = b.state;
			int localX = portalPos.getX() + localPos.getX() * -1;
			int localZ = portalPos.getZ() + localPos.getZ() * -1;
			int localY = portalPos.getY() + localPos.getY();

			if(dir == EnumFacing.SOUTH)
			{
				localX = portalPos.getX() + localPos.getX();
				localZ = portalPos.getZ() + localPos.getZ();
			}
			else if(dir == EnumFacing.EAST)
			{
				localX = portalPos.getX() + localPos.getZ();
				localZ = portalPos.getZ() + localPos.getX() * -1;
				axis = EnumFacing.Axis.Z;
			}
			else if(dir == EnumFacing.WEST)
			{
				localX = portalPos.getX()  + localPos.getZ() * -1;
				localZ = portalPos.getZ() + localPos.getX();
				axis = EnumFacing.Axis.Z;
			}
			localPos = new BlockPos(localX, localY, localZ);

			if(state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT) == BlockStone.EnumType.STONE)
			{
				state = TFCBlocks.PortalStone.getDefaultState().withProperty(BlockPortalStone.META_PROPERTY, PortalEnumType.None);
			}
			else if(state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT) == BlockStone.EnumType.GRANITE)
			{
				if(map.getIslandData().getPortalState(dir) != PortalEnumType.Enabled)
					state = TFCBlocks.PortalStone.getDefaultState().withProperty(BlockPortalStone.META_PROPERTY, PortalEnumType.Gate);
				else
					state = Blocks.AIR.getDefaultState();
			}
			else if(state.getBlock() == Blocks.PLANKS)
			{
				state = TFCBlocks.PortalStone.getDefaultState().withProperty(BlockPortalStone.META_PROPERTY, map.getIslandData().getPortalState(dir));
			}
			else if(state.getBlock() == Blocks.STAINED_GLASS)
			{
				state = TFCBlocks.Portal.getDefaultState().withProperty(BlockPortal.AXIS, axis).withProperty(BlockPortal.CENTER, false);
			}
			else if(state.getBlock() == Blocks.GLASS)
			{
				state = TFCBlocks.Portal.getDefaultState().withProperty(BlockPortal.AXIS, axis).withProperty(BlockPortal.CENTER, true);
				if(world.provider.getDimension() == 0)
				{
					c.getCustomNBT().setInteger("PortalX", localPos.getX());
					c.getCustomNBT().setInteger("PortalY", localPos.getY());
					c.getCustomNBT().setInteger("PortalZ", localPos.getZ());
				}
			}

			if(state.getBlock() != Blocks.AIR)
			{
				world.setBlockState(localPos, state, 2);
			}
		}
	}

	Center getCenterInChunk(IslandMap map, int x, int z)
	{
		Point p = new Point(x, z).toIslandCoord();
		Center c = map.getClosestCenter(p);
		Point p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		p = new Point(x+15, z).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		p = new Point(x, z+15).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		p = new Point(x+15, z+15).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		return null;
	}

	public static void BuildPath(World world, BlockPos start, BlockPos End, Spline3D spline)
	{
		for(double len = 0; len <= 1; len += 0.001)
		{
			BlockPos pos = spline.getPoint(len);
			int radius = 3;
			if(len == 0 || len >= 0.998)
				radius = 14;
			for(int x = -radius; x <= radius; x++)
			{
				for(int z = -radius; z <= radius; z++)
				{
					double dist = pos.distanceSqToCenter(pos.getX()+x+0.5, pos.getY()+0.5, pos.getZ()+z+0.5);
					if(dist < radius * radius)
					{
						if(world.isAirBlock(pos.add(x, 0, z)))
							world.setBlockState(pos.add(x, 0, z), TFCBlocks.PortalStone.getDefaultState());
					}
				}
			}
		}
	}

}
