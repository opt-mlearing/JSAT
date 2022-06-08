package jsat.linear.vectorcollection;

/**
 * @author Edward Raff
 */
public class IndexTuple implements Comparable<IndexTuple> {
    public IndexNode a;
    public IndexNode b;
    double priority;

    public IndexTuple(IndexNode a, IndexNode b, double priority) {
        this.a = a;
        this.b = b;
        this.priority = priority;
    }


    @Override
    public int compareTo(IndexTuple o) {
        return Double.compare(this.priority, o.priority);
    }

}
