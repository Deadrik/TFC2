//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import pythagoras.util.Platform;

/**
 * Provides most of the implementation of {@link IDimension}, obtaining only width and height from
 * the derived class.
 */
public abstract class AbstractDimension implements IDimension
{
    @Override // from interface IDimension
    public Dimension clone () {
        return new Dimension(this);
    }

    @Override
    public int hashCode () {
        return Platform.hashCode(width()) ^ Platform.hashCode(height());
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractDimension) {
            AbstractDimension d = (AbstractDimension)obj;
            return (d.width() == width() && d.height() == height());
        }
        return false;
    }

    @Override
    public String toString () {
        return Dimensions.dimenToString(width(), height());
    }
}
