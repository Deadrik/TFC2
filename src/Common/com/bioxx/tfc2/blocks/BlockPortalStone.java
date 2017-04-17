package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.PortalAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.PortalEnumType;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.world.WorldGen;

public class BlockPortalStone extends BlockTerra
{
	public static PropertyEnum META_PROPERTY = PropertyEnum.create("state", PortalEnumType.class);

	public BlockPortalStone()
	{
		super(Material.GROUND, META_PROPERTY);
		this.setCreativeTab(TFCTabs.TFCBuilding);
		this.setTickRandomly(true);
		setSoundType(SoundType.STONE);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, PortalEnumType.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((PortalEnumType)state.getValue(META_PROPERTY)).ordinal();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			updateTick(worldIn, pos, state, worldIn.rand);
		}
		return false;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) 
	{
		PortalEnumType portalstate = (PortalEnumType) state.getValue(META_PROPERTY);

		if(portalstate == PortalEnumType.Gate || portalstate == PortalEnumType.None)
			return;

		PortalAttribute pa = null;
		IslandMap map = null;

		if(worldIn.provider.getDimension() == 0)
		{
			map = WorldGen.getInstance().getIslandMap(pos.getX() >> 12, pos.getZ() >> 12);
			Center c = map.getClosestCenter(pos);
			pa = (PortalAttribute) c.getAttribute(Attribute.Portal);
			if(map.getIslandData().getPortalState(pa.direction) != portalstate)
			{
				worldIn.setBlockState(pos, state.withProperty(META_PROPERTY, map.getIslandData().getPortalState(pa.direction)));
			}
		}
		else if(worldIn.provider.getDimension() == 2)
		{
			BlockPos scaledPos = new BlockPos(pos.getX() * 8, pos.getY(), pos.getZ() * 8);
			map = WorldGen.getInstance().getIslandMap(scaledPos.getX() >> 12, scaledPos.getZ() >> 12);
			Center c = getPortalNeighbor(map.getClosestCenter(scaledPos));
			pa = (PortalAttribute) c.getAttribute(Attribute.Portal);
			if(map.getIslandData().getPortalState(pa.direction) != portalstate)
			{
				worldIn.setBlockState(pos, state.withProperty(META_PROPERTY, map.getIslandData().getPortalState(pa.direction)));
			}
		}

		if(map != null && pa != null)
		{
			PortalEnumType pState = map.getIslandData().getPortalState(pa.direction);
			if(toggleGate(worldIn, pos.down(), pState))
			{
				toggleGate(worldIn, pos.down(2), pState);
				toggleGate(worldIn, pos.down(3), pState);

				if(pa.direction == EnumFacing.EAST || pa.direction == EnumFacing.WEST)
				{
					toggleGate(worldIn, pos.north().down(1), pState);
					toggleGate(worldIn, pos.north().down(2), pState);
					toggleGate(worldIn, pos.north().down(3), pState);

					toggleGate(worldIn, pos.south().down(1), pState);
					toggleGate(worldIn, pos.south().down(2), pState);
					toggleGate(worldIn, pos.south().down(3), pState);
				}
				if(pa.direction == EnumFacing.NORTH || pa.direction == EnumFacing.SOUTH)
				{
					toggleGate(worldIn, pos.east().down(1), pState);
					toggleGate(worldIn, pos.east().down(2), pState);
					toggleGate(worldIn, pos.east().down(3), pState);

					toggleGate(worldIn, pos.west().down(1), pState);
					toggleGate(worldIn, pos.west().down(2), pState);
					toggleGate(worldIn, pos.west().down(3), pState);
				}
			}
		}
	}

	private Center getPortalNeighbor(Center closest)
	{
		if(!closest.hasAttribute(Attribute.Portal))
		{
			for(Center c : closest.neighbors)
			{
				if(c.hasAttribute(Attribute.Portal))
				{
					return c;
				}
			}
		}
		return closest;
	}

	private boolean toggleGate(World worldIn, BlockPos pos, PortalEnumType pState)
	{
		IBlockState gateState = worldIn.getBlockState(pos);
		if(pState == PortalEnumType.Enabled && gateState.getBlock() == TFCBlocks.PortalStone && gateState.getValue(META_PROPERTY) == PortalEnumType.Gate)
		{
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
			return true;
		}
		else if(pState != PortalEnumType.Enabled && gateState.getBlock() != TFCBlocks.PortalStone)
		{
			worldIn.setBlockState(pos, TFCBlocks.PortalStone.getDefaultState().withProperty(META_PROPERTY, PortalEnumType.Gate));
			return true;
		}

		return false;
	}

}
