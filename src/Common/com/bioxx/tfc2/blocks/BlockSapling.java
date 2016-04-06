package com.bioxx.tfc2.blocks;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Schematic;
import com.bioxx.tfc2.api.Schematic.SchemBlock;
import com.bioxx.tfc2.api.trees.TreeConfig;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.trees.TreeSchemManager;
import com.bioxx.tfc2.api.trees.TreeSchematic;
import com.bioxx.tfc2.api.types.WoodType;

public class BlockSapling extends BlockTerra implements IGrowable, IPlantable
{
	public static final PropertyEnum META_PROPERTY = PropertyEnum.create("type", WoodType.class, Arrays.copyOfRange(WoodType.values(), 0, 16));
	public BlockSapling()
	{
		super(Material.plants, META_PROPERTY);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setTickRandomly(true);
	}

	protected BlockSapling(PropertyHelper ph)
	{
		super(Material.plants, ph);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setTickRandomly(true);
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[]{META_PROPERTY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(META_PROPERTY, WoodType.getTypeFromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (!worldIn.isRemote)
		{
			super.updateTick(worldIn, pos, state, rand);

			if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0)
			{
				this.grow(worldIn, rand, pos, state);
			}
		}
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		return ((WoodType)state.getValue(META_PROPERTY)).getMeta();
	}

	@Override
	public boolean isFullCube()
	{
		return false;
	}

	@Override
	public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean p_176473_4_) 
	{
		return true;
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) 
	{
		return false;
	}

	@Override
	public void grow(World world, Random rand, BlockPos pos, IBlockState state) 
	{
		WoodType wood = (WoodType)state.getValue(META_PROPERTY);
		TreeSchemManager tsm = TreeRegistry.instance.managerFromString(wood.getName());
		TreeConfig tc = TreeRegistry.instance.treeFromString(wood.getName());

		int size = rand.nextInt(100 ) < 20 ? 2 : rand.nextInt(100 ) < 50 ? 1: 0;

		int rot = rand.nextInt(4);

		for(int i = size; i >= 0; i--)
		{
			TreeSchematic schem = tsm.getRandomSchematic(rand);
			int invalidCount = 0;
			int baseValidCount = 0;
			BlockPos scanPos;
			//validate the tree area
			for(SchemBlock b : schem.getBlockMap())
			{
				scanPos = rotatePos(pos, b.pos, rot);
				if(b.state.getBlock().getMaterial() == Material.wood)
				{
					if(!world.getBlockState(scanPos).getBlock().isReplaceable(world, scanPos))
						invalidCount++;

					if(b.pos.getY() == 0)
						if(Core.isTerrain(world.getBlockState(scanPos.down())))
							baseValidCount++;

				}
			}

			if(invalidCount > schem.getLogCount() / 10 || baseValidCount < schem.getBaseCount()*0.75)
				continue;


			for(SchemBlock b : schem.getBlockMap())
			{
				Process(world, rotatePos(pos, b.pos, rot), tc, schem, b.state);
			}
			break;
		}
	}

	private BlockPos rotatePos(BlockPos treePos, BlockPos localPos, int rot)
	{
		int localX = treePos.getX() + (localPos.getX() * -1) - 2;
		int localZ = treePos.getZ() + (localPos.getZ() * -1) - 2;
		int localY = treePos.getY() + localPos.getY();

		if(rot == 0)
		{
			localX = treePos.getX() + localPos.getX() + 1;
			localZ = treePos.getZ() + localPos.getZ() + 1;
		}
		else if(rot == 1)
		{
			localX = treePos.getX() + localPos.getZ();
			localZ = treePos.getZ() + (localPos.getX() * -1) - 2;
		}
		else if(rot == 2)
		{
			localX = treePos.getX()  + (localPos.getZ() * -1) -2;
			localZ = treePos.getZ() + localPos.getX();
		}

		return new BlockPos(localX, localY, localZ);
	}

	private void Process(World world, BlockPos blockPos, TreeConfig tc,
			Schematic schem, IBlockState state)
	{

		IBlockState block = tc.wood;
		IBlockState leaves = tc.leaves;

		if(state.getBlock().getMaterial() == Material.wood)
		{
			world.setBlockState(blockPos, block, 2);
		}
		else if(state.getBlock().getMaterial() == Material.leaves)
		{
			if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
			{
				world.setBlockState(blockPos, leaves, 2);
			}
		}
		else
		{
			world.setBlockState(blockPos, state);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer()
	{
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
	{
		return null;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Plains;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.getDefaultState();
	}
}
