package jsat.linear.vectorcollection;

/**
 * @author Edward Raff
 */
public interface ScoreDT {
    /**
     * @param query
     * @param ref
     * @return {@link Double#POSITIVE_INFINITY} if the node should be pruned.
     */
    public double score(IndexNode ref, IndexNode query);

    /**
     * This method re-scores a given reference query node pair. By default this
     * simply returns the original score that was given and does no computation.
     * If the given original score does not look valid (is less than zero), the
     * score will be re-computed. Some algorithms may choose to implement this
     * method when pruning is best done after initial depth-first traversals
     * have already been completed of other branches.
     *
     * @param ref
     * @param query
     * @param origScore
     * @return
     */
    default double score(IndexNode ref, IndexNode query, double origScore) {
        if (origScore < 0)
            return score(ref, query);
        else
            return origScore;
    }

}
