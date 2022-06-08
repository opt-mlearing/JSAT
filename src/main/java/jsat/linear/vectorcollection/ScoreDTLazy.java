package jsat.linear.vectorcollection;

/**
 * @author Edward Raff
 */
public interface ScoreDTLazy extends ScoreDT {

    @Override
    public double score(IndexNode ref, IndexNode query, double origScore);

    @Override
    public default double score(IndexNode ref, IndexNode query) {
        return score(ref, query, -1);
    }

}
