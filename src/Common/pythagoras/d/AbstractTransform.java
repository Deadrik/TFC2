//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Implements some code shared by the various {@link Transform} implementations.
 */
public abstract class AbstractTransform implements Transform
{
    @Override // from Transform
    public Vector scale () {
        return new Vector(scaleX(), scaleY());
    }

    @Override // from Transform
    public Vector translation () {
        return new Vector(tx(), ty());
    }

    @Override // from Transform
    public Transform setUniformScale (double scale) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setScale (double scaleX, double scaleY) {
        setScaleX(scaleX);
        setScaleY(scaleY);
        return this;
    }

    @Override // from Transform
    public Transform setScaleX (double scaleX) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setScaleY (double scaleY) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setRotation (double angle) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTranslation (double tx, double ty) {
        setTx(tx);
        setTy(ty);
        return this;
    }

    @Override // from Transform
    public Transform uniformScale (double scale) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform scale (double scaleX, double scaleY) {
        scaleX(scaleX);
        scaleY(scaleY);
        return this;
    }

    @Override // from Transform
    public Transform scaleX (double scaleX) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform scaleY (double scaleY) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform rotate (double angle) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform translate (double tx, double ty) {
        translateX(tx);
        translateY(ty);
        return this;
    }

    @Override // from Transform
    public Transform translateX (double tx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform translateY (double ty) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTx (double tx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTy (double ty) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTransform (double m00, double m01, double m10, double m11, double tx, double ty) {
        throw new UnsupportedOperationException();
    }

    @Deprecated @Override // from Transform
    public Transform clone () {
        return copy();
    }

    @Override // from Transform
    public abstract Transform copy ();
}
