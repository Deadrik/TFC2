package com.bioxx.tfc2.api.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.tfc2.entity.EntityFallingBlockTFC;

public interface IGravityBlock 
{
	/**
	 * @return Minimum cliffheight required for this block to slide down to lower elevation. -1 disables sliding
	 */
	public int getSlideHeight();

	/**
	 * @return Chance that a block will slide [0.0 - 1.0]
	 */
	public float getSlideChance();

	public void onStartFalling(EntityFallingBlockTFC fallingEntity);

	public void onEndFalling(World worldIn, BlockPos pos);

	public boolean canFallInto(World worldIn, BlockPos pos);
}
