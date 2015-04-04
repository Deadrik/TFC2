//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

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
    public Transform setUniformScale (float scale) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setScale (float scaleX, float scaleY) {
        setScaleX(scaleX);
        setScaleY(scaleY);
        return this;
    }

    @Override // from Transform
    public Transform setScaleX (float scaleX) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setScaleY (float scaleY) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setRotation (float angle) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTranslation (float tx, float ty) {
        setTx(tx);
        setTy(ty);
        return this;
    }

    @Override // from Transform
    public Transform uniformScale (float scale) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform scale (float scaleX, float scaleY) {
        scaleX(scaleX);
        scaleY(scaleY);
        return this;
    }

    @Override // from Transform
    public Transform scaleX (float scaleX) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform scaleY (float scaleY) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform rotate (float angle) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform translate (float tx, float ty) {
        translateX(tx);
        translateY(ty);
        return this;
    }

    @Override // from Transform
    public Transform translateX (float tx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform translateY (float ty) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform shear (float sx, float sy) {
        shearX(sx);
        shearY(sy);
        return this;
    }

    @Override // from Transform
    public Transform shearX (float sx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform shearY (float sy) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTx (float tx) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTy (float ty) {
        throw new UnsupportedOperationException();
    }

    @Override // from Transform
    public Transform setTransform (float m00, float m01, float m10, float m11, float tx, float ty) {
        throw new UnsupportedOperationException();
    }

    @Deprecated @Override // from Transform
    public Transform clone () {
        return copy();
    }

    @Override // from Transform
    public abstract Transform copy ();
}
