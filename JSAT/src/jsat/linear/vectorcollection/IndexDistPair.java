package jsat.linear.vectorcollection;

/**
 * This class exists as a helper method for use with nearest neighbor
 * implementations. It stores an integer to represent the index of a vector, and
 * a double to store the distance of the index to a given query.
 *
 * @author Edward Raff
 */
public class IndexDistPair implements Comparable<IndexDistPair> {
    /**
     * the index of a vector
     */
    protected int indx;

    /**
     * the distance of this index to a query vector
     */
    protected double dist;

    public IndexDistPair(int indx, double dist) {
        this.indx = indx;
        this.dist = dist;
    }

    public int getIndex() {
        return indx;
    }

    public void setIndex(int indx) {
        this.indx = indx;
    }

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    @Override
    public int compareTo(IndexDistPair o) {
        return Double.compare(this.dist, o.dist);
    }
}
