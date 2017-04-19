package com.bioxx.tfc2.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.blocks.BlockFirepit;
import com.bioxx.tfc2.blocks.BlockPitKiln;
import com.bioxx.tfc2.blocks.BlockPitKiln.FillType;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.tileentities.TileFirepit;
import com.bioxx.tfc2.tileentities.TilePitKiln;

public class ItemFirestarter extends ItemTerra
{
	public ItemFirestarter()
	{
		this.setShowInCreative(true);
		this.setMaxDamage(10000);
		this.setMaxStackSize(1);
		this.setCreativeTab(TFCTabs.TFCTools);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return super.getUnlocalizedName();
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote)
		{
			//onUse(worldIn, player, pos);
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		playerIn.setActiveHand(handIn);
		return new ActionResult(EnumActionResult.SUCCESS, itemstack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack is)
	{
		return EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack is)
	{
		return 2000;
	}

	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count)
	{
		World world = player.world;



		int duration = getMaxItemUseDuration(stack) - count;
		int chance = getMaxItemUseDuration(stack) - duration;
		int rand = world.rand.nextInt(chance);

		BlockPos pos = null;
		RayTraceResult result = rayTrace(player, 3, 0);
		if(result != null && result.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			pos = result.getBlockPos();			
		}
		else return;

		if(!world.isRemote)
		{
			if(count < 200)
				return;

			if(count % 10 == 1)
				world.playSound(null, pos, TFC_Sounds.FIRESTARTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
			if(rand < 20 && pos != null)
				onUse(world, player, stack, pos, count);

			if(stack.getItemDamage()+duration > stack.getMaxDamage())
			{
				stack.damageItem(duration, player);
				player.stopActiveHand();
			}
		}
		else
		{
			if(count > 0 )
				world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX()+world.rand.nextDouble(), pos.getY()+1, pos.getZ()+world.rand.nextDouble(), 0, 0.01, 0);
		}
	}

	public RayTraceResult rayTrace(EntityLivingBase player, double blockReachDistance, float partialTicks)
	{
		Vec3d vec3d = player.getPositionEyes(partialTicks);
		Vec3d vec3d1 = player.getLook(partialTicks);
		Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * blockReachDistance, vec3d1.yCoord * blockReachDistance, vec3d1.zCoord * blockReachDistance);
		return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	protected void onUse(World world, EntityLivingBase player, ItemStack stack, BlockPos pos, int count)
	{
		int duration = getMaxItemUseDuration(stack) - count;
		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() == TFCBlocks.PitKiln && state.getValue(BlockPitKiln.FILLTYPE) == FillType.Straw && state.getValue(BlockPitKiln.FILL) == 4)
		{
			TilePitKiln te = (TilePitKiln) world.getTileEntity(pos);
			te.startCrafting();
		}
		else
		{
			List<EntityItem> list = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos.up(), pos.up().add(1, 1, 1)));
			List<EntityItem> uselist = new ArrayList<EntityItem>();
			int needStones = 4;
			int needSticks = 4;
			int needStraw = 4;
			int needLogs= 1;
			NonNullList<ItemStack> oreList=null;

			stack.damageItem(count, player);

			if(world.getBlockState(pos).getBlock() == TFCBlocks.Firepit)
			{
				TileFirepit te = (TileFirepit)world.getTileEntity(pos);
				te.light();
				return;
			}

			for(EntityItem ei : list)
			{
				Item i = ei.getEntityItem().getItem();
				if(needStones > 0 && i == TFCItems.LooseRock)
				{
					needStones -= ei.getEntityItem().getCount();
					uselist.add(ei);
				}

				if(needSticks > 0)
				{
					oreList=OreDictionary.getOres("stickWood");
					for(ItemStack is : oreList)
						if(needSticks > 0 && OreDictionary.itemMatches(is, ei.getEntityItem(), false))
						{
							needSticks -= ei.getEntityItem().getCount();
							uselist.add(ei);
						}
				}

				if(needStraw > 0)
				{
					if(needStraw > 0 && ei.getEntityItem().getItem() == TFCItems.Straw)
					{
						needStraw -= ei.getEntityItem().getCount();
						uselist.add(ei);
					}
				}

				if(needLogs > 0)
				{
					oreList=OreDictionary.getOres("logWood");
					for(ItemStack is : oreList)
						if(needLogs > 0 && OreDictionary.itemMatches(is, ei.getEntityItem(), false))
						{
							needLogs -= ei.getEntityItem().getCount();
							uselist.add(ei);
							break;
						}
				}
			}

			ItemStack logStack = null;

			if(needStones <= 0 && needSticks <= 0 && needLogs <= 0 && needStraw <= 0)
			{
				needStones = 4;
				needSticks = 4;
				needStraw = 4;
				needLogs= 1;

				for(EntityItem ei : uselist)
				{
					if(needStones > 0 && ei.getEntityItem().getItem() == TFCItems.LooseRock)
					{
						int remove = Math.min(ei.getEntityItem().getCount(), needStones);
						needStones -= remove;
						ei.getEntityItem().shrink(remove);
						if(ei.getEntityItem().getCount() == 0)
							ei.setDead();
					}
					if(needSticks > 0)
					{
						oreList=OreDictionary.getOres("stickWood");
						for(ItemStack is : oreList)
							if(needSticks > 0 && OreDictionary.itemMatches(is, ei.getEntityItem(), false))
							{
								int remove = Math.min(ei.getEntityItem().getCount(), needSticks);
								needSticks -= remove;
								ei.getEntityItem().shrink(remove);
								if(ei.getEntityItem().getCount() == 0)
									ei.setDead();
							}
					}
					if(needStraw > 0)
					{
						if(needStraw > 0 && ei.getEntityItem().getItem() == TFCItems.Straw)
						{
							int remove = Math.min(ei.getEntityItem().getCount(), needStraw);
							needStraw -= remove;
							needStraw -= ei.getEntityItem().getCount();
							ei.getEntityItem().shrink(remove);
							if(ei.getEntityItem().getCount() == 0)
								ei.setDead();
						}
					}
					if(logStack == null)
					{
						oreList=OreDictionary.getOres("logWood");
						for(ItemStack is : oreList)
						{
							if(OreDictionary.itemMatches(is, ei.getEntityItem(), false))
							{
								logStack = ei.getEntityItem().copy();
								ei.setDead();
								break;
							}
						}
					}
				}

				if(logStack != null)
				{
					world.setBlockState(pos.up(), TFCBlocks.Firepit.getDefaultState().withProperty(BlockFirepit.LIT, true));
					TileFirepit te = (TileFirepit)world.getTileEntity(pos.up());
					te.setInventorySlotContents(0, logStack);
				}
			}
		}
		player.stopActiveHand();
	}
}
