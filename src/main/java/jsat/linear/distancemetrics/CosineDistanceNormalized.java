package jsat.linear.distancemetrics;

import jsat.linear.Vec;

/**
 * This distance metric returns the same cosine distance as
 * {@link CosineDistance}. This implementation assumes that all vectors being
 * passed in for distance computations have already been L2 normalized. This
 * means the distance computation can be done more efficiently, but the results
 * will be incorrect if the inputs have not already been normalized. <br>
 * The word Normalized is postfixed to the name to avoid confusion, as many
 * might assume "Normalized-CosineDistance" would mean a cosine distance with
 * some form of additional normalization.
 *
 * @author Edward Raff
 */
public class CosineDistanceNormalized implements DistanceMetric {

    /*
     * NOTE: Math.min(val, 1) is used because numerical instability can cause
     * slightly larger values than 1 when the values are extremly close to
     * eachother. In this case, it would cause a negative value in the sqrt of
     * the cosineToDinstance calculation, resulting in a NaN. So the max is used
     * to avoid this.
     */
    private static final long serialVersionUID = -4041803247001806577L;

    @Override
    public double dist(Vec a, Vec b) {
        return CosineDistance.cosineToDistance(Math.min(a.dot(b), 1));
    }

    @Override
    public boolean isSymmetric() {
        return true;
    }

    @Override
    public boolean isSubadditive() {
        return true;
    }

    @Override
    public boolean isIndiscemible() {
        return true;
    }

    @Override
    public double metricBound() {
        return 1;
    }

    @Override
    public String toString() {
        return "Cosine Distance (Normalized)";
    }

    @Override
    public CosineDistanceNormalized clone() {
        return new CosineDistanceNormalized();
    }

}
