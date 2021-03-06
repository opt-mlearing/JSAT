package jsat.linear.distancemetrics;

import java.io.Serializable;
import java.util.List;

import jsat.linear.Vec;

/**
 * A distance metric defines the distance between two points in a metric space.
 * There are three necessary properties for a metric to be valid,
 * {@link #isSymmetric() symmetry}, {@link #isIndiscemible() indisceribility},
 * and the {@link #isSubadditive() triangle inequality } . A metric that does
 * not meet all, (or none) or these properties is called a pseudo-metric. Many
 * learning algorithms rely on these properties to accelerate computations,
 * though may not need all the properties to hold.
 * <br><br>
 * A metric may support the use of a list of pre-computed information to
 * accelerate distance computations between points, which can be checked using
 * the {@link #supportsAcceleration() } method. The associated methods are
 * defined such that the cache calls can be used in a seamless way that will
 * automatically invoke the caching behavior when supported. Simply initiate
 * with <br>
 * {@code List<Double> distCache = dm.getAccelerationCache(vecList);} <br>
 * to initiate the cache, if not supported - null will be returned, which is
 * allowed when calling<br>
 * {@code double dist = dm.dist(indx1, indx2, vecList, distCache);}<br>
 * Null is used as a special case for {@code distCache}, at which point the
 * implementation will call the standard
 * {@link #dist(jsat.linear.Vec, jsat.linear.Vec) } using the list and indices.
 * The other cache accelerated methods behave in the same way, including
 * {@link #getQueryInfo(jsat.linear.Vec) }<br>
 * Using this set up, no branching or special case code is necessary to
 * automatically use the acceleration capabilities of supported distance metrics.
 *
 * @author Edward Raff
 */
public interface DistanceMetric extends Cloneable, Serializable {
    /**
     * Computes the distance between 2 vectors.
     * The smaller the value, the closer, and there for,
     * more similar, the vectors are. 0 indicates the vectors are the same.
     *
     * @param a the first vector
     * @param b the second vector
     * @return the distance between them
     */
    public double dist(Vec a, Vec b);

    /**
     * Returns true if this distance metric obeys the rule that, for any x, y, and z &isin; S <br>
     * d(x, y) = d(y, x)
     *
     * @return true if this distance metric is symmetric, false if it is not
     */
    public boolean isSymmetric();

    /**
     * Returns true if this distance metric obeys the rule that, for any x, y, and z &isin; S <br>
     * d(x, z) &le; d(x, y) + d(y, z)
     *
     * @return true if this distance metric supports the triangle inequality, false if it does not.
     */
    public boolean isSubadditive();

    /**
     * Returns true if this distance metric obeys the rule that, for any x and y  &isin; S <br>
     * d(x, y) = 0 if and only if x = y
     *
     * @return true if this distance metric is indicemible, false otherwise.
     */
    public boolean isIndiscemible();

    /**
     * Returns true if this distance method obeys all the rules required to be a valid metric.
     *
     * @return true if this distance method obeys all the rules required to be a valid metric.
     */
    default public boolean isValidMetric() {
        return isSymmetric() && isSubadditive() && isIndiscemible();
    }

    /**
     * All metrics must return values greater than or equal to 0.
     * The upper bound on the value returned is different for
     * different metrics. This method returns the theoretical
     * maximal value that could be returned by this distance
     * metric. That means {@link Double#POSITIVE_INFINITY }
     * is a valid return value.
     *
     * @return the maximal distance for any two points in that could exist by this distance metric.
     */
    public double metricBound();

    /**
     * Indicates if this distance metric supports building an acceleration cache
     * using the {@link #getAccelerationCache(java.util.List) } and associated
     * distance methods. By default this method will return {@code false}. If
     * {@code true}, then a cache can be obtained from this distance metric and
     * used in conjunction with {@link #dist(int, jsat.linear.Vec,
     * java.util.List, java.util.List) } and {@link #dist(int, int,
     * java.util.List, java.util.List) } to perform distance computations.
     *
     * @return {@code true} if cache acceleration is supported for this metric,
     * {@code false} otherwise.
     */
    default public boolean supportsAcceleration() {
        return false;
    }

    /**
     * Returns a cache of double values associated with the given list of
     * vectors in the given order. This can be used by the distance metric to
     * increase runtime at the cost of memory. This is an optional method.
     * <br> If this metric does not support acceleration, {@code null} will be
     * returned.
     *
     * @param vecs the list of vectors to build an acceleration cache for
     * @return the list of double for the cache
     */
    default public List<Double> getAccelerationCache(List<? extends Vec> vecs) {
        return getAccelerationCache(vecs, false);
    }

    /**
     * Returns a cache of double values associated with the given list of
     * vectors in the given order. This can be used by the distance metric to
     * increase runtime at the cost of memory. This is an optional method.
     * <br> If this metric does not support acceleration, {@code null} will be
     * returned.
     *
     * @param vecs     the list of vectors to build an acceleration cache for
     * @param parallel {@code true} if multiple threads should be used to
     *                 perform clustering. {@code false} if it should be done in a single
     *                 threaded manner.
     * @return the list of double for the cache
     */
    default public List<Double> getAccelerationCache(List<? extends Vec> vecs, boolean parallel) {
        return null;
    }

    /**
     * Computes the distance between 2 vectors in the original list of vectors.
     * <br> If the cache input is {@code null}, then
     * {@link #dist(jsat.linear.Vec, jsat.linear.Vec) } will be called directly.
     *
     * @param a     the index of the first vector
     * @param b     the index of the second vector
     * @param vecs  the list of vectors used to build the cache
     * @param cache the cache associated with the given list of vectors
     * @return the distance between the two vectors
     */
    default public double dist(int a, int b, List<? extends Vec> vecs, List<Double> cache) {
        return dist(vecs.get(a), vecs.get(b));
    }

    /**
     * Computes the distance between 2 vectors from two different lists of
     * vectors.<br> If the cache input is {@code null}, then
     * {@link #dist(jsat.linear.Vec, jsat.linear.Vec)} will be called directly.
     *
     * @param a       the index of the first vector from the first list
     * @param b       the index of the second vector from the second list
     * @param vecs_a  the first list of vectors used to build the cache
     * @param cache_a the cache associated with the first list of vectors
     * @param vecs_b  the second list of vectors used to build the cache
     * @param cache_b the cache associated with the second list of vectors
     * @return the distance between the two vectors
     */
    default public double dist(int a, int b, List<? extends Vec> vecs_a, List<Double> cache_a, List<? extends Vec> vecs_b, List<Double> cache_b) {
        Vec b_vec = vecs_b.get(b);
        List<Double> b_qi = null;
        if (cache_b != null) {
            int factor = cache_b.size() / vecs_b.size();
            b_qi = cache_b.subList(b * factor, (b + 1) * factor);
        }
        return dist(a, b_vec, b_qi, vecs_a, cache_a);
    }

    /**
     * Computes the distance between one vector in the original list of vectors
     * with that of another vector not from the original list.
     * <br> If the cache input is {@code null}, then
     * {@link #dist(jsat.linear.Vec, jsat.linear.Vec) } will be called directly.
     *
     * @param a     the index of the vector in the cache
     * @param b     the other vector
     * @param vecs  the list of vectors used to build the cache
     * @param cache the cache associated with the given list of vectors
     * @return the distance between the two vectors
     */
    default public double dist(int a, Vec b, List<? extends Vec> vecs, List<Double> cache) {
        return dist(a, b, getQueryInfo(b), vecs, cache);
    }

    /**
     * Pre computes query information that would have be generated if the query
     * was a member of the original list of vectors when calling
     * {@link #getAccelerationCache(java.util.List) } . This can then be used if
     * a large number of distance computations are going to be done against
     * points in the original set for a point that is outside the original space.
     * <br><br>
     * If this metric does not support acceleration, {@code null} will be
     * returned.
     *
     * @param q the query point to generate cache information for
     * @return the cache information for the query point
     */
    default public List<Double> getQueryInfo(Vec q) {
        return null;
    }

    /**
     * Computes the distance between one vector in the original list of vectors
     * with that of another vector not from the original list, but had
     * information generated by {@link #getQueryInfo(jsat.linear.Vec) }.
     * <br> If the cache input is {@code null}, then
     * {@link #dist(jsat.linear.Vec, jsat.linear.Vec) } will be called directly.
     *
     * @param a     the index of the vector in the cache
     * @param b     the other vector
     * @param qi    the query information about b
     * @param vecs  the list of vectors used to build the cache
     * @param cache the cache associated with the given list of vectors
     * @return the distance between the two vectors
     */
    default public double dist(int a, Vec b, List<Double> qi, List<? extends Vec> vecs, List<Double> cache) {
        return dist(vecs.get(a), b);
    }

    /**
     * Returns a descriptive name of the Distance Metric in use
     *
     * @return the name of this metric
     */
    @Override
    public String toString();

    public DistanceMetric clone();
}
