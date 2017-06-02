package com.bioxx.tfc2.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.bioxx.tfc2.TFC;

public class Helper 
{
	public static int combineCoords(int x, int y)
	{
		short xs = (short)x;
		short ys = (short)y;
		return (xs << 16) | (ys & 0xFFFF);
	}

	public static int getXCoord(int c)
	{
		return (short)(c >> 16);
	}
	public static int getYCoord(int c)
	{
		return (short)(c & 0xffff);
	}

	/**
	 * @param angle Any angle in degrees
	 * @return Returns the original angle after making sure that it is bounded between 0 and 360 degrees
	 */
	public static double normalizeAngle(double angle)
	{
		angle = angle % 360;
		if(angle < 0)
			angle += 360;

		return angle;
	}

	public static double dist2dSq(BlockPos a, BlockPos b)
	{
		double d0 = a.getX() - b.getX();
		double d2 = a.getZ() - b.getZ();

		return d0 * d0 + d2 * d2;
	}

	public static NBTTagList writeStackArrayToNBTList(NonNullList<ItemStack> list)
	{
		NBTTagList invList = new NBTTagList();
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i) != ItemStack.EMPTY)
			{
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte)i);
				list.get(i).writeToNBT(tag);
				invList.appendTag(tag);
			}
		}
		return invList;
	}

	public static NonNullList<ItemStack> readStackArrayFromNBTList(NBTTagList list, int size)
	{
		NonNullList<ItemStack> out = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);

		for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			byte byte0 = tag.getByte("Slot");
			if(byte0 >= 0 && byte0 < size)
				out.set(byte0, new ItemStack(tag));
		}
		return out;
	}

	/**
	 * Rotates a Vec3d around an arbitrary rotation point along an axis with a rotation in radians
	 */
	public static Vec3d rotateVertex(Vec3d origin, Vec3d src, Vec3d axis, double rotation)
	{
		double q0 = 1;
		double q1 = 0;
		double q2 = 0;
		double q3 = 0;
		double norm = axis.lengthVector();
		if (norm == 0) {
			throw new ArithmeticException("zero norm for rotation axis");
		}

		double halfAngle = -0.5 * rotation;
		double coeff = Math.sin(halfAngle) / norm;

		q0 = Math.cos (halfAngle);
		q1 = coeff * axis.xCoord;
		q2 = coeff * axis.yCoord;
		q3 = coeff * axis.zCoord;

		return origin.add(applyTo(src.subtract(origin), q0, q1, q2, q3));

	}

	/** Apply the rotation to a vector.
	 * @param u vector to apply the rotation to
	 * @return a new vector which is the image of u by the rotation
	 */
	public static Vec3d applyTo(Vec3d u, double q0, double q1, double q2, double q3) 
	{

		double x = u.xCoord;
		double y = u.yCoord;
		double z = u.zCoord;

		double s = q1 * x + q2 * y + q3 * z;

		return new Vec3d(2 * (q0 * (x * q0 - (q2 * z - q3 * y)) + s * q1) - x,
				2 * (q0 * (y * q0 - (q3 * x - q1 * z)) + s * q2) - y,
				2 * (q0 * (z * q0 - (q1 * y - q2 * x)) + s * q3) - z);

	}

	public static List<String> getResourceFiles( String path ) throws IOException 
	{
		List<String> filenames = new ArrayList<String>();

		InputStream in = TFC.instance.getClass().getResourceAsStream( path );
		BufferedReader br = new BufferedReader( new InputStreamReader( in ) );

		String resource = br.readLine();
		if(resource == null)
			TFC.log.warn("Helper -> No Resources Found at " + path + " | " + in.available() + " bytes");
		while( resource != null ) 
		{
			filenames.add( resource );
			resource = br.readLine();
		}

		return filenames;
	}

	public BlockPos Lerp(BlockPos start, BlockPos end, float percent)
	{
		BlockPos b = end.add(start);
		return start.add(new BlockPos(percent*b.getX(), percent*b.getY(), percent*b.getZ()));
	}

	/**
	 * This is a 2d equation using X and Z coordinates
	 */
	public static BlockPos getPerpendicularPoint(BlockPos A, BlockPos B, float distance)
	{
		BlockPos M = divide(A.add(B), 2);
		BlockPos p = A.subtract(B);
		BlockPos n = new BlockPos(-p.getZ(),p.getY(), p.getX());
		float norm_length = (float) Math.sqrt((n.getX() * n.getX()) + (n.getZ() * n.getZ()));
		n = divide(n, norm_length);
		return M.add(multiply(n, distance));
	}

	public static BlockPos divide(BlockPos A, float divisor)
	{
		return new BlockPos(A.getX()/divisor, A.getY()/divisor, A.getZ()/divisor);
	}

	public static BlockPos multiply(BlockPos A, float mult)
	{
		return new BlockPos(A.getX()*mult, A.getY()*mult, A.getZ()*mult);
	}
}
