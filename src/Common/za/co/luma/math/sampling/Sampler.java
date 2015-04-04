package za.co.luma.math.sampling;

import java.util.List;

/**
 * Used to sample a collection (or image).
 * 
 * @author Herman Tulleken
 * 
 * @param <T>
 *            The type of items in the collection to sample. In an image these are points, usually represented by
 *            Vectro2DDouble or Vector2DInt.
 */
public interface Sampler<T>
{
	/**
	 * This method samples the structure, and returns a list of results. The sampling is done in one step. Care must be
	 * taken when the sample size is large.
	 * 
	 * @return
	 */
	public List<T> sample();
}
