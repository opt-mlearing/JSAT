package jsat.linear.vectorcollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.stream.Collectors;

import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.utils.BoundedSortedList;
import jsat.utils.ProbailityMatch;

import static jsat.linear.VecPaired.*;

import jsat.utils.IndexTable;
import jsat.utils.IntList;

/**
 * @param <V>
 * @author Edward Raff
 */
public class RTree<V extends Vec> implements IncrementalCollection<V> {

    private static final long serialVersionUID = -7067110612346062800L;

    @Override
    public void search(Vec query, double range, List<Integer> neighbors, List<Double> distances) {
        Rectangle searchSpace = new Rectangle(dim, range, query);

        neighbors.clear();
        distances.clear();
        search(searchSpace, root, neighbors, distances);

        Iterator<Integer> nIter = neighbors.iterator();
        ListIterator<Double> dIter = distances.listIterator();
        assert neighbors.size() == distances.size();

        while (nIter.hasNext()) {
            int indx = nIter.next();
            double dist = dIter.next();

            if ((dist = dm.dist(query, extractTrueVec(get(indx)))) <= range)
                dIter.set(dist);
            else//false match, remove it
            {
                nIter.remove();
                dIter.remove();
            }
        }
        IndexTable it = new IndexTable(distances);
        it.apply(distances);
        it.apply(neighbors);
    }

    @Override
    public void search(Vec query, int numNeighbors, List<Integer> neighbors, List<Double> distances) {
        /**
         * Match up nodes with the minDist from the query to that node
         */
        Stack<ProbailityMatch<RNode<V>>> stack = new Stack<>();

        BoundedSortedList<ProbailityMatch<Integer>> curBest = new BoundedSortedList<>(numNeighbors);
        curBest.add(new ProbailityMatch<>(Double.MAX_VALUE, -1));//add a fake just to make life easy coding

        stack.push(new ProbailityMatch<>(minDist(query, root.bound), root));

        /**
         * Active Branch list
         */
        List<ProbailityMatch<RNode<V>>> ABL = new ArrayList<>();

        while (!stack.isEmpty()) {
            ProbailityMatch<RNode<V>> poped = stack.pop();
            RNode<V> N = poped.getMatch();
            double minDistN = poped.getProbability();
            if (minDistN <= curBest.last().getProbability()) {
                if (N.isLeaf()) {
                    for (int indx : N.points) {
                        double dist = dm.dist(query, extractTrueVec(get(indx)));
                        curBest.add(new ProbailityMatch<>(dist, indx));
                    }
                } else {
                    for (int i = 0; i < N.size(); i++) {
                        double i_min = minDist(query, N.getChild(i).bound);
                        if (i_min <= curBest.last().getProbability())
                            ABL.add(new ProbailityMatch<>(i_min, N.getChild(i)));
                    }
                    Collections.sort(ABL, Collections.reverseOrder());
                    stack.addAll(ABL);
                    ABL.clear();
                }
            }
        }


        //Now prepare to return 
        neighbors.clear();
        distances.clear();
        for (int i = 0; i < curBest.size(); i++) {
            ProbailityMatch<Integer> pm = curBest.get(i);
            neighbors.add(pm.getMatch());
            distances.add(pm.getProbability());
        }
    }

    /**
     * Returns the list of all points contained in the given rectangle
     *
     * @param query     the rectangle to find all points that would be contained by it
     * @param node      the current node to search
     * @param neighbors the place to store the nodes
     */
    private void search(Rectangle query, RNode<V> node, List<Integer> neighbors, List<Double> distances) {
        if (!node.isLeaf()) {
            for (int i = 0; i < node.size(); i++)
                if (node.getChild(i).bound.intersects(query))
                    search(query, node.getChild(i), neighbors, distances);
        } else
            for (int i = 0; i < node.size(); i++)
                if (query.contains(get(node.points.get(i)))) {
                    neighbors.add(node.points.get(i));
                    distances.add(Double.NaN);
                }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public RTree<V> clone() {
        return new RTree<>(this);
    }

    private RNode cloneChangeContext(RNode toClone) {
        if (toClone != null)
            if (toClone instanceof jsat.linear.vectorcollection.RTree.RNode)
                return new RNode((RNode) toClone);
        return null;
    }

    @Override
    public List<Double> getAccelerationCache() {
        return null;
    }

    private class RNode<V extends Vec> implements Comparable<RNode<V>>, Cloneable {
        List<RNode<V>> children;
        RNode<V> parent;
        IntList points;
        Rectangle bound;

        /**
         * Creating a new leaf node
         *
         * @param points
         */
        public RNode(List<Integer> points) {
            this.points = new IntList(points);
            children = new ArrayList<>();
            bound = Rectangle.contains(points.stream().map(i -> get(i)).collect(Collectors.toList()));
        }

        public RNode(RNode<V> toCopy) {
            this();
            for (RNode<V> child : toCopy.children) {
                RNode<V> cloneChild = cloneChangeContext(child);
                cloneChild.parent = this;
                this.children.add(cloneChild);
            }
            if (toCopy.points != null)
                for (int v : toCopy.points)
                    this.points.add(v);
            if (toCopy.bound != null)
                this.bound = toCopy.bound.clone();
        }

        public RNode() {
            points = new IntList();
            children = new ArrayList<>();
            bound = null;
        }

        RNode<V> getChild(int n) {
            return children.get(n);
        }

        Rectangle nthBound(int n) {
            if (isLeaf())
                return new Rectangle(get(points.get(n)));
            else
                return children.get(n).bound;
        }

        @SuppressWarnings("unused")
        boolean isFull() {
            return points.size() >= M;
        }

        /**
         * @param indx point to add
         * @return true if this node needs to be split
         */
        boolean add(int indx) {
            points.add(indx);
            if (bound == null)
                bound = new Rectangle(get(indx));
            else
                bound.adjustToContain(get(indx));
            return size() > M;
        }

        /**
         * @param node
         * @return true if this node needs to be split
         */
        boolean add(RNode<V> node) {
            node.parent = this;
            children.add(node);
            if (bound == null)
                bound = new Rectangle(node.bound);
            else
                bound.adjustToContain(node.bound);
            return size() > M;
        }

        boolean isLeaf() {
            return children.isEmpty();
        }

        @Override
        public int compareTo(RNode<V> o) {
            return Double.compare(this.bound.area(), o.bound.area());
        }

        /**
         * If this node is a leaf, it returns the number of vectors
         * contained by it. Otherwise, it returns the number of
         * children nodes this node contains
         *
         * @return the number of elements contained by this noe
         */
        private int size() {
            if (isLeaf())
                return points.size();
            else
                return children.size();
        }

        @Override
        protected RNode<V> clone() {
            return new RNode<>(this);
        }
    }

    static private class Rectangle implements Cloneable {
        /**
         * The maximum values for the rectangle
         */
        private Vec uB;
        /**
         * The minimum values for the rectangle
         */
        private Vec lB;

        public Rectangle(Vec upperBound, Vec lowerBound) {
            this.uB = upperBound;
            this.lB = lowerBound;
        }

        public Rectangle(int dimensions, double distance, Vec center) {
            uB = new DenseVector(dimensions);
            lB = new DenseVector(dimensions);

            for (int i = 0; i < dimensions; i++) {
                uB.set(i, center.get(i) + distance);
                lB.set(i, center.get(i) - distance);
            }
        }

        @SuppressWarnings("unused")
        public Rectangle(int dimensions) {
            uB = new DenseVector(dimensions);
            lB = new DenseVector(dimensions);
        }

        public Rectangle(Vec point) {
            uB = point.clone();
            lB = point.clone();
        }

        @SuppressWarnings("unused")
        public Rectangle(Vec... points) {
            this(Arrays.asList(points));
        }

        /**
         * Creates a rectangle that covers all the given points tightly
         *
         * @param points
         */
        public Rectangle(List<Vec> points) {
            uB = new DenseVector(points.get(0).length());
            lB = new DenseVector(uB.length());

            for (int i = 0; i < uB.length(); i++) {
                double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
                for (int j = 0; j < points.size(); j++) {
                    max = Math.max(max, points.get(j).get(i));
                    min = Math.min(min, points.get(j).get(i));
                }

                uB.set(i, max);
                lB.set(i, min);
            }
        }

        /**
         * Creates a new rectangle that contains all the given rectangles
         *
         * @param recs
         */
        public Rectangle(Rectangle... recs) {
            uB = new DenseVector(recs[0].uB.length());
            lB = new DenseVector(uB.length());

            for (int i = 0; i < uB.length(); i++) {
                double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
                for (int j = 0; j < recs.length; j++) {
                    max = Math.max(max, recs[j].uB.get(i));
                    min = Math.min(min, recs[j].lB.get(i));
                }

                uB.set(i, max);
                lB.set(i, min);
            }
        }

        double increasedArea(Vec v) {
            double newArea = 1;
            double curArea = 1;
            for (int i = 0; i < uB.length(); i++) {
                double curAreaTerm = uB.get(i) - lB.get(i);
                double vi = v.get(i);
                if (vi < lB.get(i))
                    newArea *= uB.get(i) - vi;
                else if (vi > uB.get(i))
                    newArea *= vi - lB.get(i);
                else
                    newArea *= curAreaTerm;
                curArea *= curAreaTerm;
            }
            return newArea - curArea;
        }

        double increasedArea(Rectangle r) {
            double newArea = 1;
            double curArea = 1;
            for (int i = 0; i < uB.length(); i++) {
                double curAreaTerm = uB.get(i) - lB.get(i);
                curArea *= curAreaTerm;

                double newUBi = Math.max(uB.get(i), r.uB.get(i));
                double newLBi = Math.min(lB.get(i), r.lB.get(i));

                newArea *= (newUBi - newLBi);

            }
            return newArea - curArea;
        }

        double area() {
            double area = 1;
            for (int i = 0; i < uB.length(); i++)
                area *= uB.get(i) - lB.get(i);
            return area;
        }

        boolean intersects(Rectangle rect) {
            for (int i = 0; i < uB.length(); i++) {
                if (this.uB.get(i) < rect.lB.get(i) || this.lB.get(i) > rect.uB.get(i))
                    return false;
            }

            return true;
        }

        boolean contains(Vec point) {
            for (int i = 0; i < uB.length(); i++)
                if (this.uB.get(i) < point.get(i) || this.lB.get(i) > point.get(i))
                    return false;
            return true;
        }

        void adjustToContain(Vec point) {
            for (int i = 0; i < uB.length(); i++) {
                double vi = point.get(i);
                if (vi > uB.get(i))
                    uB.set(i, vi);
                else if (vi < lB.get(i))
                    lB.set(i, vi);
            }
        }

        void adjustToContain(Rectangle r) {
            adjustToContain(r.uB);
            adjustToContain(r.lB);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(lB.get(0)).append(":").append(uB.get(0));
            for (int i = 1; i < uB.length(); i++)
                sb.append(",").append(lB.get(i)).append(":").append(uB.get(i));
            sb.append("]");
            return sb.toString();
        }

        @Override
        protected Rectangle clone() {
            return new Rectangle(uB.clone(), lB.clone());
        }

        static <V extends Vec> Rectangle contains(List<V> points) {
            DenseVector uB = new DenseVector(points.get(0).length());
            DenseVector lB = new DenseVector(uB.length());

            for (int i = 0; i < uB.length(); i++) {
                double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
                for (int j = 0; j < points.size(); j++) {
                    max = Math.max(max, points.get(j).get(i));
                    min = Math.min(min, points.get(j).get(i));
                }

                uB.set(i, max);
                lB.set(i, min);
            }
            return new Rectangle(uB, lB);
        }
    }

    private int size;
    private RNode root;

    /**
     * Maximum number of entries per node
     */
    private int M;
    /**
     * Minimum number of entries per node
     */
    private int m;
    /**
     * The dimension of vectors stored
     */
    private int dim;

    /**
     * Scratch space for distance calculations
     */
    private DenseVector dcScratch;

    private DistanceMetric dm;

    private List<V> allVecs;

    public RTree() {
        this(new EuclideanDistance());
    }

    public RTree(DistanceMetric dm) {
        this(dm, 5);
    }

    public RTree(DistanceMetric dm, int max) {
        this(dm, max, (int) (max * 0.4));
    }

    public RTree(DistanceMetric dm, int max, int min) {
        this.root = new RNode();
        if (max < 2)
            throw new RuntimeException("The maximum number of elements per node must be at least 2");
        else if (min > max / 2 || min < 1)
            throw new RuntimeException("Invalid minumum, min must be in the range[1, " + max / 2 + "]");
        this.M = max;
        this.m = min;
        setDistanceMetric(dm);
        this.allVecs = new ArrayList<>();
    }

    /**
     * Copy constructor
     *
     * @param toCopy
     */
    public RTree(RTree<V> toCopy) {
        this(toCopy.dm.clone(), toCopy.M, toCopy.m);
        this.size = toCopy.size;
        this.dim = toCopy.dim;
        if (toCopy.root != null)
            this.root = cloneChangeContext(toCopy.root);
        for (V v : toCopy.allVecs)
            this.allVecs.add(v);
        if (toCopy.dcScratch != null)
            this.dcScratch = toCopy.dcScratch.clone();
    }

    @Override
    public void build(boolean parallel, List<V> collection, DistanceMetric dm) {
        setDistanceMetric(dm);
        for (V v : collection)
            insert(v);
    }

    @Override
    public void setDistanceMetric(DistanceMetric dm) {
        this.dm = dm;
    }

    @Override
    public DistanceMetric getDistanceMetric() {
        return dm;
    }


    private RNode<V> chooseLeaf(Vec v) {
        /*
         * CL1 [Intialize ] Set N to be the root node
         */
        RNode<V> N = root;

        /*
         * CL2  [Leaf check ] If N 1s a leaf, return N.
         */
        while (!N.isLeaf()) {
            /*
             * CL3 [Choose subtree ] If N 1s not a leaf,
             * (1) let F be the entry in N whose rectangle
             * FI needs least enlargement to
             * include EI.
             *
             * (2)
             * Resolve ties by choosmg
             * the entry with the rectangle of smallest
             * area
             */
            double leastEnlargment = N.children.get(0).bound.increasedArea(v);
            int ind = 0;
            for (int i = 1; i < N.children.size(); i++) {
                //Part (1) of CL3
                double nb = N.children.get(i).bound.increasedArea(v);
                if (nb < leastEnlargment)//Found a better one
                {
                    leastEnlargment = nb;
                    ind = i;
                } else if (nb == leastEnlargment)//Most likely when 2 or more rectangles intersect this new point
                {//Part (2) of CL3 
                    //Only pic the new one if it has a smaller area
                    if (N.children.get(i).bound.area() < N.children.get(ind).bound.area()) {
                        leastEnlargment = nb;
                        ind = i;
                    }
                }
            }

            /*
             * CL4 [Descend until a leaf 1s reached.] Set
             * N to be the cMd node pomted to by
             * Fp and repeat from CL2
             */
            N = N.children.get(ind);
        }

        return N;
    }

    private RNode<V> splitNode(RNode<V> toSplit) {
        //Quadratic Split
        /*
         * [Pick first entry for each group ]
         * Apply Algorithm PickSeeds to choose
         * two entries to be the first elements
         * of the groups Assign each to a
         * group
         */
        double d = Double.MIN_VALUE;
        int e1 = 0, e2 = 0;
        //PickSeeds
        /**
         * PSl [Calculate inefficiency of grouping
         * entries together] For each pair of
         * entries El and E2, compose a rectangle
         * J including El I and E2 I Calculate
         * d = area(J) - area(El I) - area(E2 I)
         */
        for (int i = 0; i < toSplit.size(); i++)
            for (int j = 0; j < toSplit.size(); j++) {
                if (j == i)
                    continue;
                Rectangle E1Bound = toSplit.nthBound(i);
                Rectangle E2Bound = toSplit.nthBound(j);
                Rectangle J = new Rectangle(E1Bound, E2Bound);
                double dCandidate = J.area() - E1Bound.area() - E2Bound.area();
                if (dCandidate > d)//PS2 [Choose the most wasteful pm ] Choose the pair with the largest d
                {
                    e1 = i;
                    e2 = j;
                    d = dCandidate;
                }

            }
        {//Make sure that e1 < e2, makes removing easier 
            int maxE = Math.max(e1, e2);
            e1 = Math.min(e1, e2);
            e2 = maxE;
        }

        if (toSplit.isLeaf()) {
            IntList group1 = new IntList(m + 1);
            IntList group2 = new IntList(m + 1);

            IntList toAsign = toSplit.points;//toSplit.points will get overwritten

            group2.add(toAsign.remove(e2));
            group1.add(toAsign.remove(e1));

            Rectangle rec2 = new Rectangle(get(group2.get(0)));
            Rectangle rec1 = new Rectangle(get(group1.get(0)));

            while (!toAsign.isEmpty()) {
                /*
                 * QS2 [Check If done ] If all entries have been assigned, stop. If one group has
                 * so few entries that all the rest must be assigned to it in order for it to
                 * have the muumum number m, assign them and stop
                 */

                if (group1.size() >= m && group2.size() < m && toAsign.size() - group2.size() == 0) {
                    group2.addAll(toAsign);
                    toAsign.clear();
                    continue;
                } else if (group2.size() >= m && group1.size() < m && toAsign.size() - group1.size() == 0) {
                    group1.addAll(toAsign);
                    toAsign.clear();
                    continue;
                }


                /*
                 * QS3 [Select entry to assign ] Invoke Algorithm PickNext to choose the next
                 * entry to assign. Add it to the group whose covering rectangle will have to
                 * be enlarged least to accommodate it. Resolve ties by adding the entry to
                 * the group mth smaller area, then to the one with fewer entries, then to
                 * either Repeat from QS2
                 */

                //PICK NEXT 
                /*
                 * [Determme cost of puttmg each entry m each group ] For each entry
                 * E not yet m a group, calculate d1 = the area increase required in the
                 * covering rectangle of Group 1 to include EI. Calculate d2 similarly
                 * for Group 2
                 */
                double minEnlargment = Double.MAX_VALUE;
                int index = -1;//the index we are picking next
                boolean toG1 = false;//whether it should be placed into group 1 or group 2
                for (int i = 0; i < toAsign.size(); i++) {
                    double enlarg1 = rec1.increasedArea(get(toAsign.get(i)));
                    double enlarg2 = rec2.increasedArea(get(toAsign.get(i)));
                    boolean thisToG1 = enlarg1 < enlarg2;
                    double enlarg = Math.min(enlarg1, enlarg2);

                    if (enlarg < minEnlargment) {
                        minEnlargment = enlarg;
                        index = i;
                        toG1 = thisToG1;
                    }
                }
                //Place it
                (toG1 ? group1 : group2).add(toAsign.remove(index));
            }

            toSplit.points = group1;
            toSplit.bound = Rectangle.contains(toSplit.points.stream().map(i -> get(i)).collect(Collectors.toList()));
            return new RNode<>(group2);
        } else//TODO handles rectangles... very similar,
        {
            List<RNode<V>> toAsign = toSplit.children;

            toSplit.children = new ArrayList<>();
            toSplit.bound = null;

            RNode<V> group1 = toSplit;
            RNode<V> group2 = new RNode<>();


            group2.add(toAsign.remove(e2));
            group1.add(toAsign.remove(e1));

            Rectangle rec2 = group2.bound;
            Rectangle rec1 = group1.bound;

            while (!toAsign.isEmpty()) {
                /*
                 * If one group has
                 * so few entries that all the rest must
                 * be assigned to it m order for it to
                 * have the muumum number m,
                 */

                if (group1.size() >= m && group2.size() < m && toAsign.size() - group2.size() == 0) {
                    for (RNode<V> node : toAsign)
                        group2.add(node);
                    toAsign.clear();
                    continue;
                } else if (group2.size() >= m && group1.size() < m && toAsign.size() - group1.size() == 0) {
                    for (RNode<V> node : toAsign)
                        group1.add(node);
                    toAsign.clear();
                    continue;
                }

                //PICK NEXT find point with the least change in area
                double minEnlargment = Double.MAX_VALUE;
                int index = -1;//the index we are picking next
                boolean toG1 = false;//whether it should be placed into group 1 or group 2
                for (int i = 0; i < toAsign.size(); i++) {
                    double enlarg1 = rec1.increasedArea(toAsign.get(i).bound);
                    double enlarg2 = rec2.increasedArea(toAsign.get(i).bound);
                    boolean thisToG1 = enlarg1 < enlarg2;
                    double enlarg = Math.min(enlarg1, enlarg2);

                    if (enlarg < minEnlargment) {
                        minEnlargment = enlarg;
                        index = i;
                        toG1 = thisToG1;
                    }
                }
                //Place it
                (toG1 ? group1 : group2).add(toAsign.remove(index));
            }

            return group2;
        }
    }

    private void AdjustTree(RNode<V> L, RNode<V> LL) {
        /*
         * AT1 [Imtlahze.] Set N=L If L was split
         * previously, set NN to be the resultmg
         * second node
         */
        RNode<V> N = L, NN = LL;

        while (N != root)//AT2 [Check If done ] If N 1s the root, stop
        {
            /*
             * AT3 [Adjust covermg rectangle m parent
             * entry ] Let P be the parent node of
             * N, and let EN be N???s entry in P
             * Adjust EN I so that it tightly encloses
             * all entry rectangles in N.
             */
            RNode<V> P = N.parent;
            P.bound.adjustToContain(N.bound);//P alread contains us, so we dont add ourselves again!
            if (NN != null) {
                /*
                 * AT4 [Propagate node split upward] If N has a partner NN resultmg from an
                 * earher spht, create a new entry Em with ENNp pointmg to NN and Em I
                 * enclosing all rectangles in NN. Add Em to P If there is room. Otherwise,
                 * invoke SplitNode to produce P and PP containing Em and all P???s old
                 * entries
                 */
                if (P.add(NN))
                    NN = splitNode(P); //Asignment is part of step AT5 below
                else
                    NN = null;
            }
            /*
             * AT5 [Move up to next level.] Set N=P and
             * set NN=PP If a spht occurred,
             * Repeat from AT2.
             */
            N = P;
        }

        //Step I4 [Grow tree taller]
        if (NN != null)//That means we caues the root to split! Need a new root!
        {
            root = new RNode<>();
            root.add(N);
            root.add(NN);
        }

    }

    @Override
    public V get(int indx) {
        return allVecs.get(indx);
    }

    @Override
    public synchronized void insert(V v) {
        int indx = allVecs.size();
        if (indx == 0) {
            this.dim = v.length();
            this.dcScratch = new DenseVector(dim);
        }
        allVecs.add(v);
        /*
         * I1 [Find position for new record ]
         * Invoke ChooseLeaf to select a leaf
         * node L in which to place E
         */
        RNode<V> L = chooseLeaf(v), LL = null;

        /*
         * I2 [Add record to leaf node ] If L has
         * room for another entry, mstaI.l E
         * Othemse mvoke SplitNode to obtam
         * L and U contammg E and all the
         * old entrees of L
         */
        if (L.add(indx))//true if we need to split
            LL = splitNode(L);
        /*
         * I3 [Propagate changes upward] Invoke
         * AdjustTree on L, also passmg U If a
         * spht was performed
         */
        AdjustTree(L, LL);
        //step I4 handeled in AdjustTree

        size++;
    }

    /**
     * The minium distance from a query point to the given rectangle
     *
     * @param p the query point
     * @param r the rectangle compute the distance to
     * @return the minimum distance from the point to the rectangle
     */
    private double minDist(Vec p, Rectangle r) {
        if (r.contains(p))
            return 0;
        //Set up sctach vector
        for (int i = 0; i < dim; i++) {
            double pi = p.get(i);
            if (pi < r.lB.get(i))
                dcScratch.set(i, r.lB.get(i));
            else if (pi > r.uB.get(i))
                dcScratch.set(i, r.uB.get(i));
            else
                dcScratch.set(i, pi);
        }

        return dm.dist(p, dcScratch);
    }

    /**
     * The minimum of the maximum possible distance from the query to the rectangle
     *
     * @param p the query point
     * @param r the rectangle compute the distance to
     * @return the minimum of the maximum distance from the point to the rectangle
     */
    @SuppressWarnings("unused")
    private double minMaxDist(Vec p, Rectangle r) {
        if (r.contains(p))
            return 0;
        /*
         * MinMaxDist is usualy describe with the minimum over another loop, explicisty as the euclidant distance
         * Instead, we prepare a single vector for each loop (k), and set its values accordinly (with index k being an exception in the value set)
         * We then compute the distance metric and select the min for each k
         */

        double minDist = Double.MAX_VALUE;
        for (int k = 0; k < dim; k++) {
            //setUp vector
            for (int j = 0; j < dim; j++) {
                double pj = p.get(j);
                double sj = r.lB.get(j);
                double tj = r.uB.get(j);
                if (j == k)//rm_k
                    if (pj <= (sj + tj) * 0.5)
                        dcScratch.set(j, sj);
                    else
                        dcScratch.set(j, tj);
                else {
                    if (pj >= (sj + tj) * 0.5)
                        dcScratch.set(j, sj);
                    else
                        dcScratch.set(j, tj);
                }
            }
            //Now just compute distance
            double dist = dm.dist(p, dcScratch);
            minDist = Math.min(dist, minDist);
        }

        return minDist;
    }

    /**
     * The maximal distance possible between the query point and the edge of the given rectangle farthest from the point.
     *
     * @param p the query point
     * @param r the rectangle compute the distance to
     * @return the maximum distance from the point to the rectangle
     */
    @SuppressWarnings("unused")
    private double maxDist(Vec p, Rectangle r) {
        if (r.contains(p))
            return 0;
        //set up vector
        for (int i = 0; i < dim; i++) {
            double pi = p.get(i);
            double si = r.lB.get(i);
            double ti = r.uB.get(i);

            if (pi < si)
                dcScratch.set(i, ti);
            else if (pi > ti)
                dcScratch.set(i, si);
            else
                dcScratch.set(i, pi);
        }

        return dm.dist(p, dcScratch);
    }

}
