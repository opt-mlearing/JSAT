package jsat.linear.vectorcollection;

import jsat.linear.Vec;

import java.util.*;

/**
 * @param <N>
 * @author Edward Raff
 */
public interface IndexNode<N extends IndexNode> {

    /**
     * @return returns the parent node to this one, or {@code null} if this node is the root.
     */
    public N getParrent();


    /**
     * This method returns a lower bound on the minimum distance from a point in
     * or owned by this node to any point in or owner by the {@code other} node.
     * Because the value return is a lower bound, 0 would always be a valid
     * return value.
     *
     * @param other the other node of points to get the minimum distance to
     * @return a lower bound on the minimum distance.
     */
    public double minNodeDistance(N other);

    public double maxNodeDistance(N other);

    /**
     * @param other
     * @return an array where the first value is the minimum distance between nodes, and second value is the maximum
     */
    public default double[] minMaxDistance(N other) {
        return new double[]{minNodeDistance(other), maxNodeDistance(other)};
    }

    /**
     * This method returns a lower bound on the minimum distance from a point in
     * or owned by this node to the  {@code other} point.
     * Because the value return is a lower bound, 0 would always be a valid
     * return value.
     *
     * @param other
     * @return
     */
    public double minNodeDistance(int other);

    /**
     * Gets the distance from this node (or its centroid) to it's parent node (or centroid).
     *
     * @return
     */
    public default double getParentDistance() {
        N parent = getParrent();
        if (parent == null)
            return 0;//You have no parent
        else
            return parent.furthestDescendantDistance();//stupid loose default bound
    }

    /**
     * Returns an upper bound on the farthest distance from this node to any of the points it owns. <br>
     * <br>
     * In the Dual Tree papers, this is often given as ρ(N_i)
     *
     * @return an upper bound on the distance
     */
    public double furthestPointDistance();

    /**
     * Returns an upper bound on the farthest distance from this node to any of
     * the points it owns or its children own. <br>
     * <br> In the Dual Tree papers,
     * this is often given as λ(Ni)
     *
     * @return an upper bound on the distance
     */
    public double furthestDescendantDistance();


    /**
     * @return
     */
    public int numChildren();

    public IndexNode getChild(int indx);

    public Vec getVec(int indx);

    public int numPoints();

    public int getPoint(int indx);

    public default boolean hasChildren() {
        return numChildren() > 0;
    }

    default public boolean allPointsInLeaves() {
        return true;
    }

    default public Iterator<Integer> DescendantIterator() {
        Stack<IndexNode<N>> toProcess = new Stack<>();
        toProcess.add(this);

        return new Iterator<Integer>() {
            int curPointPos = 0;
            boolean primed = false;

            @Override
            public boolean hasNext() {
                do {
                    if (toProcess.isEmpty()) {
                        return false;
                    } else if (toProcess.peek().numPoints() >= curPointPos)//we have exaughsted this node, expand search
                    {
                        IndexNode tmp = toProcess.pop();
                        for (int i = 0; i < tmp.numChildren(); i++)
                            toProcess.add(tmp.getChild(i));
                        curPointPos = 0;
                    } else//we have points that have not been iterated on the stack
                        return (primed = true);
                }
                while (!toProcess.isEmpty());
                return false;
            }

            @Override
            public Integer next() {
                if (!primed)//call hasNext to get the structures in place and ready
                    if (!hasNext())
                        throw new NoSuchElementException();
                primed = false;
                return toProcess.peek().getPoint(curPointPos++);
            }
        };
    }

}

