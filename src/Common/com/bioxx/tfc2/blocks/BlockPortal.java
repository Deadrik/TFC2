package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.world.TeleporterPaths;

public class BlockPortal extends BlockTerra
{

	public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, new EnumFacing.Axis[] {EnumFacing.Axis.X, EnumFacing.Axis.Z});
	public static final PropertyBool CENTER = PropertyBool.create("center");

	public BlockPortal()
	{
		super(Material.GROUND, null);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setSoundType(SoundType.STONE);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		super.updateTick(worldIn, pos, state, rand);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	/*@Override
	public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
	{
		EnumFacing.Axis axis = (EnumFacing.Axis)worldIn.getBlockState(pos).getValue(AXIS);
		float f = 0.125F;
		float f1 = 0.125F;

		if (axis == EnumFacing.Axis.X)
		{
			f = 0.5F;
		}

		if (axis == EnumFacing.Axis.Z)
		{
			f1 = 0.5F;
		}

		this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
	}*/

	public static int getMetaForAxis(EnumFacing.Axis axis)
	{
		return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	/**
	 * Called When an Entity Collided with the Block
	 */
	@Override
	public void onEntityCollidedWithBlock(World worldObj, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if (!entityIn.isRiding() && !entityIn.isBeingRidden() && !worldObj.isRemote)
		{
			MinecraftServer minecraftserver = worldObj.getMinecraftServer();
			if(worldObj.provider.getDimension() == 0)
			{
				if(entityIn instanceof EntityPlayerMP)
					((EntityPlayerMP)entityIn).mcServer.getPlayerList().transferPlayerToDimension((EntityPlayerMP)entityIn, 2, new TeleporterPaths(minecraftserver.worldServerForDimension(2)));
				else
					entityIn.changeDimension(2);
			}
			else if(worldObj.provider.getDimension() == 2)
			{
				if(entityIn instanceof EntityPlayerMP)
					((EntityPlayerMP)entityIn).mcServer.getPlayerList().transferPlayerToDimension((EntityPlayerMP)entityIn, 0, new TeleporterPaths(minecraftserver.worldServerForDimension(0)));
				else
					entityIn.changeDimension(0);
			}
		}
	}

	private void travelToDimension(Entity entityIn, int dimensionId)
	{
		if (!entityIn.worldObj.isRemote && !entityIn.isDead)
		{

			MinecraftServer minecraftserver = entityIn.worldObj.getMinecraftServer();
			int j = entityIn.dimension;
			WorldServer worldserver = minecraftserver.worldServerForDimension(j);
			WorldServer worldserver1 = minecraftserver.worldServerForDimension(dimensionId);
			entityIn.dimension = dimensionId;

			if (j == 2 && dimensionId == 2)
			{
				worldserver1 = minecraftserver.worldServerForDimension(0);
				entityIn.dimension = 0;
			}

			entityIn.worldObj.removeEntity(entityIn);
			entityIn.isDead = false;

			minecraftserver.getPlayerList().transferEntityToWorld(entityIn, j, worldserver, worldserver1, new TeleporterPaths(worldserver1));

			Entity entity = EntityList.createEntityByName(EntityList.getEntityString(entityIn), worldserver1);

			if (entity != null)
			{
				//entity.copyDataFromOld(entityIn);
				worldserver1.spawnEntityInWorld(entity);
			}

			entityIn.isDead = true;

			worldserver.resetUpdateEntityTick();
			worldserver1.resetUpdateEntityTick();
		}
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X).withProperty(CENTER, (meta >> 3) == 1 ? true : false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.TRANSLUCENT;
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return getMetaForAxis((EnumFacing.Axis)state.getValue(AXIS)) + ((Boolean) state.getValue(CENTER) ? 7 : 0);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return null;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {AXIS, CENTER});
	}
}
