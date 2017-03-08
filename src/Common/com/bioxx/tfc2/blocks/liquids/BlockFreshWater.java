package com.bioxx.tfc2.blocks.liquids;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.networking.client.CFoodPacket;

public class BlockFreshWater extends BlockFluidClassic {

	public BlockFreshWater(Fluid fluid, Material material) 
	{
		super(fluid, material);
		this.setTickRate(3);
	}

	/*@Override
	public Block setUnlocalizedName(String name)
	{
		this.setRegistryName(name);
		return super.setUnlocalizedName(name);
	}

	@Override
	public float getFluidHeightForRender(IBlockAccess world, BlockPos pos)
	{
		IBlockState here = world.getBlockState(pos);
		IBlockState up = world.getBlockState(pos.down(densityDir));
		if (here.getBlock() instanceof BlockFluidBase)
		{
			if (up.getBlock().getMaterial(up).isLiquid() || up.getBlock() instanceof IFluidBlock)
			{
				return 1;
			}

			if (getMetaFromState(here) == getMaxRenderHeightMeta())
			{
				return 0.875F;
			}
		}
		return !here.getBlock().getMaterial(here).isSolid() && up.getBlock() == this ? 1 : this.getQuantaPercentage(world, pos) * 0.875F;
	}

	@Override
	protected boolean canFlowInto(IBlockAccess world, BlockPos pos)
	{
		if (world.isAirBlock(pos)) return true;
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == this || block == TFCBlocks.SaltWater || block == TFCBlocks.SaltWaterStatic || block == TFCBlocks.FreshWaterStatic)
		{
			return true;
		}

		if (displacements.containsKey(block))
		{
			return displacements.get(block);
		}

		Material material = block.getMaterial(state);
		if (material.blocksMovement()  ||
				material == Material.WATER ||
				material == Material.LAVA  ||
				material == Material.PORTAL)
		{
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE)
		{
			return true;
		}

		if (this.density > density)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean isSourceBlock(IBlockAccess world, BlockPos pos)
	{
		IBlockState b = world.getBlockState(pos);
		return world.getBlockState(pos).getBlock() instanceof BlockFluidBase && 
				((Integer)world.getBlockState(pos).getValue(LEVEL)).intValue() == 0;
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		super.updateTick(world, pos, state, rand);
		int myLevel = ((Integer)state.getValue(LEVEL)).intValue();
		if(state.getBlock() == TFCBlocks.FreshWater && ((Integer)state.getValue(LEVEL)).intValue() > 0)
		{
			int count = 0;
			if(isSourceBlock(world, pos.north())) 
				count++;
			if(isSourceBlock(world, pos.south())) 
				count++;
			if(isSourceBlock(world, pos.east())) 
				count++;
			if(isSourceBlock(world, pos.west())) 
				count++;

			if(count > 1)
			{
				world.setBlockState(pos, TFCBlocks.FreshWaterStatic.getDefaultState().withProperty(LEVEL, 0));
			}
		}
		else
		{
			world.setBlockState(pos, TFCBlocks.FreshWater.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand)
	{
		double d0 = (double)pos.getX();
		double d1 = (double)pos.getY();
		double d2 = (double)pos.getZ();


		int i = ((Integer)state.getValue(LEVEL)).intValue();

		if (i > 0 && i < 8)
		{
			if (rand.nextInt(64) == 0)
			{
				worldIn.playSound(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.AMBIENT, rand.nextFloat() * 0.25F + 0.75F, rand.nextFloat() * 1.0F + 0.5F, false);
			}
		}
		else if (rand.nextInt(10) == 0)
		{
			worldIn.spawnParticle(EnumParticleTypes.SUSPENDED, d0 + (double)rand.nextFloat(), d1 + (double)rand.nextFloat(), d2 + (double)rand.nextFloat(), 0.0D, 0.0D, 0.0D, new int[0]);
		}

		if (rand.nextInt(10) == 0 && worldIn.getBlockState(pos.down()).getBlock().isOpaqueCube(worldIn.getBlockState(pos.down())))
		{
			Material material = worldIn.getBlockState(pos.down(2)).getBlock().getMaterial(worldIn.getBlockState(pos.down(2)));

			if (!material.blocksMovement() && !material.isLiquid())
			{
				double d3 = d0 + (double)rand.nextFloat();
				double d5 = d1 - 1.05D;
				double d7 = d2 + (double)rand.nextFloat();

				worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d3, d5, d7, 0.0D, 0.0D, 0.0D, new int[0]);
			}
		}
	}
	 */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(worldIn.isRemote)
			return false;

		IFoodStatsTFC food = (IFoodStatsTFC)playerIn.getFoodStats();
		food.setWaterLevel((Math.min(food.getWaterLevel()+0.1f, 20)));
		TFC.network.sendTo(new CFoodPacket(food), (EntityPlayerMP) playerIn);

		return true;
	}
}
