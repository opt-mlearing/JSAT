package jsat.linear.vectorcollection;

import jsat.linear.Vec;

/**
 * This interface is for Vector Collections that support incremental
 * construction. If all data is available at the onset, it is recommended to use
 * the appropriate constructor / bulk insertion as they may be more compute
 * efficient or produce better indexes. The incremental insertion of points is
 * not guaranteed to result in a collection that is equally as performant in
 * either construction or querying. However, it does allow for additions to the
 * collection without needing to re-build the entire collection. Efficiency and
 * performance of incremental additions will depend on the base implementation.
 *
 * @param <V> The type of vectors stored in this collection
 * @author Edward Raff
 */
public interface IncrementalCollection<V extends Vec> extends VectorCollection<V> {
    /**
     * Incrementally adds the given datapoint into the collection
     *
     * @param x the vector to add to the collection
     */
    public void insert(V x);

    @Override
    public IncrementalCollection<V> clone();
}
