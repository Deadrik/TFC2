package za.co.luma.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A SegmentSet is an iterable collection of Segments.
 * 
 * @author Herman Tulleken
 */
public class SegmentSet implements Iterable<Segment>
{
	List<Segment> segments;
	
	/** 
	 * Adss all the Segments in the given collection to this SegmentSet.
	 */
	public boolean addAll(Collection<? extends Segment> c)
	{
		return segments.addAll(c);
	}
	
	/**
	 * Add all the segments in the given SegmentSet to this SegmentSet.
	 */
	public boolean addAll(SegmentSet set)
	{
		boolean added = true;
		
		for(Segment segment : set )
		{
			added &= add(segment);
		}
		
		//Bug.pr(segments);
		
		return added;
	}

	/**
	 * Creates a new empty SegmentSet.
	 *
	 */
	public SegmentSet()
	{
		segments = new ArrayList<Segment>();	
	}

	/**
	 * Adds a new Segment to this SegementSet.
	 * 
	 * @param segment The segment to add
	 * 
	 * @return whether the Segment has been successfully added.
	 */
	public boolean add(Segment segment)
	{
		return segments.add(segment);
	}

	/**
	 * Removes all Segments from this SegmentSet.
	 */
	public void clear()
	{
		segments.clear();
	}

	/**
	 * Returns an ierator for this SegmentSet.
	 */
	public Iterator<Segment> iterator()
	{
		return segments.iterator();
	}

	/**
	 * Removes a Segment from this SegmentSet.
	 * 
	 * @param segment The segment to remove.
	 * 
	 * @return
	 */
	public boolean remove(Object segment)
	{
		return segments.remove(segment);
	}
	
	@Override
	public String toString()
	{
		String str = "";
		for (Segment s:segments)
		{
			str += s + " ";
		}
		
		return str;
	}
}
