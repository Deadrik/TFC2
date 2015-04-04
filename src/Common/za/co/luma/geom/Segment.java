package za.co.luma.geom;

/**
 * This class represents a finite line segment, that is, the segment between two
 * points.
 * 
 * @author Herman Tulleken (herman@luma.co.za)
 */
public class Segment
{
	private Vector2DDouble start;
	private Vector2DDouble end;

	/**
	 * Construct a new segment from two given points.
	 */
	public Segment(Vector2DDouble start, Vector2DDouble end)
	{
		super();
		this.start = start;
		this.end = end;
	}

	/**
	 * Gets the end point of this segment.
	 * 
	 */
	public Vector2DDouble getEnd()
	{
		return end;
	}

	/**
	 * Sets the end point of this segment.
	 */
	public void setEnd(Vector2DDouble end)
	{
		this.end = end;
	}

	/**
	 * Gets the start point of this segment.
	 * 
	 */
	public Vector2DDouble getStart()
	{
		return start;
	}

	/**
	 * Sets the start point for this segment.
	 * 
	 */
	public void setStart(Vector2DDouble start)
	{
		this.start = start;
	}

	
	/**
	 * Returns "[start, end]".
	 */
	@Override
	public String toString()
	{
		return "[" + start + ", " + end + "]";
	}

}
