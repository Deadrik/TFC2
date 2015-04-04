package za.co.luma.geom;

/**
 * Class that represents a 2D point with int. This class is usefull for keeping tracks of indeces in a 2D array.
 */
public class Vector2DInt
{
	public int x, y;

	public Vector2DInt(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString()
	{
		return x + " " + y;
	}
}