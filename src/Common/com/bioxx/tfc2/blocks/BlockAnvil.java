package com.bioxx.tfc2.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.properties.PropertyItem;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.rendering.particles.ParticleAnvil;
import com.bioxx.tfc2.tileentities.TileAnvil;

public class BlockAnvil extends BlockTerra implements ITileEntityProvider
{
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyItem INVENTORY = new PropertyItem();

	public static final AxisAlignedBB AABB_EW = new AxisAlignedBB(0.19,0,0.0625,0.81,0.6925,0.9375);
	public static final AxisAlignedBB AABB_NS = new AxisAlignedBB(0.0625,0,0.19,0.9375,0.6925,0.81);

	public BlockAnvil()
	{
		super(Material.GRASS, FACING);
		this.setCreativeTab(TFCTabs.TFCDevices);
		this.isBlockContainer = true;
		setSoundType(SoundType.GROUND);
		this.setBreaksWhenSuspended(true);
	}

	/*******************************************************************************
	 * 1. Content
	 *******************************************************************************/

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, 
			net.minecraft.util.EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		EnumFacing facing = state.getValue(BlockAnvil.FACING);
		TileAnvil te = (TileAnvil)world.getTileEntity(pos);
		if(!world.isRemote && te.getTimer() <= 0)
			playerIn.openGui(TFC.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
		else if(te.getSmith() == playerIn && side == EnumFacing.UP)
		{
			int subX = 0;
			int subZ = 0;

			if(facing == EnumFacing.EAST || facing == EnumFacing.WEST)
			{
				hitX -= 0.25f;
				hitZ -= 0.125f;

				subX = (int)Math.floor(hitZ/0.125f);
				subZ = (int)Math.floor(hitX/0.125f);
			}
			else
			{
				hitZ -= 0.25f;
				hitX -= 0.125f;

				subX = (int)Math.floor(hitX/0.125f);
				subZ = (int)Math.floor(hitZ/0.125f);
			}

			TFC.log.info("Hit: " + subX + "," + subZ + " | " + TileAnvil.getStrikePointIndex(subX, subZ));

			te.hitStrikePoint(TileAnvil.getStrikePointIndex(subX, subZ));

			//get the targeted sub block coords
			/*double subX = hitX/8D;
			double subZ = hitZ/8D;

			if(facing == EnumFacing.EAST || facing == EnumFacing.WEST)
			{
				subX = (hitZ+2)/8D; subZ = (hitX+1)/8D;
			}

			te.setStrikePoint((int)subX, (int)subZ, null);*/
		}
		return true;
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
	{
		IBlockState soil = worldIn.getBlockState(pos.down());
		return soil.getBlock().isSideSolid(soil, worldIn, pos.down(), EnumFacing.UP);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList list)
	{
		list.add(new ItemStack(itemIn, 1, 0));
	}

	/*******************************************************************************
	 * 2. Rendering
	 *******************************************************************************/

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
	{
		TileAnvil te = (TileAnvil)world.getTileEntity(pos);

		/*for (int x = 0; x < 6; x++)
		{
			for (int z = 0; z < 4; z++)
			{
				AnvilStrikePoint point = te.getStrikePoint(x, z);
				if(point != null && ! point.hasSpawnedParticle())
				{
					double xPos = 0.125 + (Math.floor(x*2D)) / 16D + 0.0625D;
					double yPos = 0.71;
					double zPos = 0.25 + (Math.floor(z*2D)) / 16D + 0.0625D;

					if(state.getValue(FACING) == EnumFacing.EAST || state.getValue(FACING) == EnumFacing.WEST)
					{
						double temp = xPos;
						xPos = zPos;
						zPos = temp;
					}
					ParticleAnvil particle;
					if(point.getType() == AnvilStrikeType.CRITICAL)
						particle = new ParticleStrikeCrit(world, pos.getX()+xPos, pos.getY()+yPos, pos.getZ()+zPos);
					else
						particle = new ParticleStrike(world, pos.getX()+xPos, pos.getY()+yPos, pos.getZ()+zPos);

					long time = Timekeeper.getInstance().getTotalTicks();
					particle.setMaxAge(point.getLifeTime());

					net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(particle);

					point.setSpawnedParticle(true);
				}
			}
		}*/

		/*if(rand.nextInt(10) == 0)
		{
			net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleStrikeCrit(world, pos.getX()+Math.floor(4+rand.nextInt(4)*2)/16f+0.0625f, pos.getY()+0.71, pos.getZ()+Math.floor(4+rand.nextInt(4)*2)/16f+0.0625f));
		}
		else if(rand.nextInt(8) == 0)
		{
			net.minecraft.client.Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleStrike(world, pos.getX()+Math.floor(4+rand.nextInt(4)*2)/16f+0.0625f, pos.getY()+0.71, pos.getZ()+Math.floor(4+rand.nextInt(4)*2)/16f+0.0625f));
		}*/
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType()
	{
		return Block.EnumOffsetType.NONE;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		if(state.getValue(FACING) == EnumFacing.NORTH || state.getValue(FACING) == EnumFacing.SOUTH)
			return AABB_NS;
		return AABB_EW;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		if(state.getValue(FACING) == EnumFacing.NORTH || state.getValue(FACING) == EnumFacing.SOUTH)
			return AABB_NS;
		return AABB_EW;
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

	/*******************************************************************************
	 * 3. Blockstate 
	 *******************************************************************************/

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		TileAnvil te = (TileAnvil) world.getTileEntity(pos);
		if(te != null)
			return te.writeExtendedBlockState((IExtendedBlockState) state);
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new ExtendedBlockState(this, new IProperty[]{FACING}, new IUnlistedProperty[]{INVENTORY});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
		return i;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) 
	{
		return new TileAnvil();
	}

	@Override
	public Item getItemDropped(IBlockState paramIBlockState, Random paramRandom, int paramInt)
	{
		return Item.getItemFromBlock(this);
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return true;
	}

	/*******************************************************************************
	 * Particles 
	 *******************************************************************************/

	public static class ParticleStrike extends ParticleAnvil
	{
		static final ResourceLocation TEX = Core.CreateRes(Reference.ModID+":textures/particles/strike.png");
		protected ParticleStrike(World worldIn, double posXIn, double posYIn, double posZIn) 
		{
			super(worldIn, posXIn, posYIn, posZIn);
		}

		@Override
		protected ResourceLocation getTexture() 
		{
			return TEX;
		}

	}

	public static class ParticleStrikeCrit extends ParticleAnvil
	{
		static final ResourceLocation TEX = Core.CreateRes(Reference.ModID+":textures/particles/strike_crit.png");
		protected ParticleStrikeCrit(World worldIn, double posXIn, double posYIn, double posZIn) 
		{
			super(worldIn, posXIn, posYIn, posZIn);
		}

		@Override
		protected ResourceLocation getTexture() 
		{
			return TEX;
		}

	}
}
