package jsat.clustering.hierarchical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import jsat.DataSet;
import jsat.classifiers.DataPoint;

import static jsat.clustering.ClustererBase.createClusterListFromAssignmentArray;

import jsat.clustering.KClusterer;
import jsat.clustering.dissimilarity.LanceWilliamsDissimilarity;
import jsat.clustering.dissimilarity.WardsDissimilarity;
import jsat.linear.Vec;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.math.OnLineStatistics;
import jsat.utils.IndexTable;
import jsat.utils.IntDoubleMap;
import jsat.utils.IntDoubleMapArray;
import jsat.utils.IntList;
import jsat.utils.ListUtils;
import jsat.utils.SystemInfo;
import jsat.utils.concurrent.AtomicDouble;
import jsat.utils.concurrent.ParallelUtils;

/**
 * This class implements Hierarchical Agglomerative Clustering via the Nearest
 * Neighbor Chain approach. This runs in O(n<sup>2</sup>) time for any
 * {@link LanceWilliamsDissimilarity Lance Williams} dissimilarity and uses O(n)
 * memory. <br>
 * This implementation also supports multi-threaded execution.
 * <p>
 * see:
 * <ul>
 * <li>Müllner, D. (2011). Modern hierarchical, agglomerative clustering
 * algorithms. arXiv Preprint arXiv:1109.2378. Retrieved from
 * <a href="http://arxiv.org/abs/1109.2378">here</a></li>
 * <li>Murtagh, F., & Contreras, P. (2011). Methods of Hierarchical Clustering.
 * In Data Mining and Knowledge Discovery. Wiley-Interscience.</li>
 * </ul>
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class NNChainHAC implements KClusterer {
    private LanceWilliamsDissimilarity distMeasure;
    private DistanceMetric dm;

    /**
     * Stores the merge list, each merge is in a pair of 2 values. The final
     * merge list should contain the last merged pairs at the front of the array
     * (index  0, 1), and the first merges at the end of the array. The left
     * value in each pair is the index of the data point that the clusters were
     * merged under, while the right value is the index that was merged in and
     * treated as no longer its own cluster.
     */
    private int[] merges;

    /**
     * Creates a new NNChainHAC using the {@link WardsDissimilarity Ward} method.
     */
    public NNChainHAC() {
        this(new WardsDissimilarity());
    }

    /**
     * Creates a new NNChainHAC
     *
     * @param distMeasure the dissimilarity measure to use
     */
    public NNChainHAC(LanceWilliamsDissimilarity distMeasure) {
        this(distMeasure, new EuclideanDistance());
    }

    /**
     * Creates a new NNChain using the given dissimilarity measure and distance
     * metric. The correctness guarantees may not hold for distances other than
     * the {@link EuclideanDistance Euclidean} distance, which is the norm for
     * Hierarchical Cluster.
     *
     * @param distMeasure the dissimilarity measure to use
     * @param distance    the distance metric to use
     */
    public NNChainHAC(LanceWilliamsDissimilarity distMeasure, DistanceMetric distance) {
        this.distMeasure = distMeasure;
        this.dm = distance;
    }

    /**
     * Copy constructor
     *
     * @param toCopy the object to copy
     */
    protected NNChainHAC(NNChainHAC toCopy) {
        this.distMeasure = toCopy.distMeasure.clone();
        this.dm = toCopy.dm.clone();
        if (toCopy.merges != null) this.merges = Arrays.copyOf(toCopy.merges, toCopy.merges.length);
    }

    @Override
    public NNChainHAC clone() {
        return new NNChainHAC(this);
    }

    @Override
    public int[] cluster(DataSet dataSet, boolean parallel, int[] designations) {
        return cluster(dataSet, 2, (int) Math.sqrt(dataSet.size()), parallel, designations);
    }

    private double getDist(int a, int j, int[] size, List<Vec> vecs, List<Double> cache, List<Map<Integer, Double>> d_xk) {
        if (size[j] == 1 && size[a] == 1) return dm.dist(a, j, vecs, cache);
        else {
            //a is the one we are using over and over, its more likely to have the valu - check it first
            if (d_xk.get(a) != null) {
                Double tmp = d_xk.get(a).get(j);
                if (tmp != null) return tmp;
                else//wasn't found searching d_xk
                    return d_xk.get(j).get(a);//has to be found now
            } else return d_xk.get(j).get(a);//has to be found now
        }
    }

    /**
     * Returns the assignment array for that would have been computed for the
     * previous data set with the desired number of clusters.
     *
     * @param designations the array to store the assignments in
     * @param clusters     the number of clusters desired
     * @return the original array passed in, or <tt>null</tt> if no data set has been clustered.
     * @see #hasStoredClustering()
     */
    public int[] getClusterDesignations(int[] designations, int clusters) {
        if (merges == null) return null;
        return PriorityHAC.assignClusterDesignations(designations, clusters, merges);
    }

    /**
     * Returns the assignment array for that would have been computed for the
     * previous data set with the desired number of clusters.
     *
     * @param clusters the number of clusters desired
     * @param data
     * @return the list of data points in each cluster, or <tt>null</tt> if no
     * data set has been clustered.
     * @see #hasStoredClustering()
     */
    public List<List<DataPoint>> getClusterDesignations(int clusters, DataSet data) {
        if (merges == null || (merges.length + 2) / 2 != data.size()) return null;
        int[] assignments = new int[data.size()];
        assignments = getClusterDesignations(assignments, clusters);
        return createClusterListFromAssignmentArray(assignments, data);
    }

    @Override
    public int[] cluster(DataSet dataSet, int clusters, boolean parallel, int[] designations) {
        return cluster(dataSet, clusters, clusters, parallel, designations);
    }

    @Override
    public int[] cluster(DataSet dataSet, int lowK, int highK, boolean parallel, int[] designations) {
        if (designations == null) designations = new int[dataSet.size()];

        final int N = dataSet.size();

        merges = new int[N * 2 - 2];
        /**
         * Keeps track of which index was removed from the list due to being
         * merged at the given point
         */
        IntList merge_removed = new IntList(N);
        /**
         * Keeps track of which index was kept and was in the merge at the given
         * point
         */
        IntList merge_kept = new IntList(N);
        /**
         * The number of items in the cluster denoted at the given index
         */
        final int[] size = new int[N];
        Arrays.fill(size, 1);


        /**
         * Stores the distance between the two clusters that were merged at each step. 
         */
        double[] mergedDistance = new double[N - 1];
        int L_pos = 0;

        final IntList S = new IntList(N);
        ListUtils.addRange(S, 0, N, 1);


        final List<Map<Integer, Double>> dist_map = new ArrayList<>(N);
        for (int i = 0; i < N; i++)
            dist_map.add(null);

        final List<Vec> vecs = dataSet.getDataVectors();
        final List<Double> cache = dm.getAccelerationCache(vecs, parallel);

        int[] chain = new int[N];
        int chainPos = 0;

        while (S.size() > 1) {
            int a, b;
            if (chainPos <= 3) {
                // 7: a←(any element of S) >  E.g. S[0]
                a = S.getI(0);
                // 8: chain ←[a]
                chainPos = 0;
                chain[chainPos++] = a;
                // 9: b←(any element of S \ {a})   > E.g. S[1]
                b = S.getI(1);
            } else {
                // 11: a←chain[−4]  > 4th to last element
                a = chain[chainPos - 4];
                // 12: b←chain[−3]  > 3rd to last element
                b = chain[chainPos - 3];
                // 13: Remove chain[−1], chain[−2] and chain[−3]  >  Cut the tail (x, y,x)
                chainPos -= 3;
            }

            double dist_ab;
            do//15:
            {
                // 16: c ← argmin_{ x!=a} d[x, a] with preference for b

                AtomicInteger c = new AtomicInteger(b);
                AtomicDouble minDist = new AtomicDouble(getDist(a, c.get(), size, vecs, cache, dist_map));

                final int a_ = a;
                final int c_ = c.get();
                boolean doPara = parallel && S.size() > SystemInfo.LogicalCores * 2 && S.size() >= 100;

                ParallelUtils.run(doPara, S.size(), (start, end) -> {
                    double local_minDist = Double.POSITIVE_INFINITY;
                    int local_c = S.get(start);


                    for (int i = start; i < end; i++) {
                        int j = S.getI(i);
                        if (j == a_ || j == c_) continue;//we already have these guys! just not removed from S yet
                        double dist = getDist(a_, j, size, vecs, cache, dist_map);

                        if (dist < local_minDist) {
                            local_minDist = dist;
                            local_c = j;
                        }
                    }

                    synchronized (minDist) {
                        if (local_minDist < minDist.get()) {
                            minDist.set(local_minDist);
                            c.set(local_c);
                        }
                    }
                });

                dist_ab = minDist.get();

                //17: a, b ← c, a
                b = a;
                a = c.get();
                //18: Append a to chain
                chain[chainPos++] = a;

            } while (chainPos < 3 || a != chain[chainPos - 3]); //19: until length(chain) ≥ 3 and a = chain[−3]  > a, b are reciprocal

            final int n = Math.min(a, b);
            final int removed = Math.max(a, b);

            // 20: Append (a, b, d[a, b]) to L  >  nearest neighbors.
            merge_removed.add(removed);
            merge_kept.add(n);

            mergedDistance[L_pos] = dist_ab;

            L_pos++;
            // 21: Remove a, b from S
            S.removeAll(Arrays.asList(a, b));
//            System.out.println("Removed " + a + " " + b + " S=" + S + " chain=" + IntList.view(chain, chainPos));
            // 22: n←(new node label)

            for (int i = Math.max(0, chainPos - 5); i < chainPos; i++)//bug in paper? we end with [a, b, a] in the chain, but one of them is a bad index now
                if (chain[i] == removed) chain[i] = n;
            // 23: size[n]←size[a]+size[b]
            final int size_a = size[a], size_b = size[b];
            //set defered till later to make sure we don't muck anything needed in computatoin

            // 24: Update d with the information d[n,x], for all x ∈ S.

            boolean singleThread = !parallel || S.size() <= SystemInfo.LogicalCores * 10;
            final Map<Integer, Double> map_n; // = S.isEmpty() ? null : new IntDoubleMap(S.size());
            if (S.isEmpty()) map_n = null;
            else if (S.size() * 100 >= N || !singleThread)// Wastefull, but faster and acceptable
                map_n = new IntDoubleMapArray(N);
            else {
                map_n = new IntDoubleMap(S.size());
                //pre fill to guarantee thread safe alteration of values when done in parallel && using IntDoubleMap implementation
                for (int x : S)
                    map_n.put(x, -0.0);
            }


            final int a_ = a;
            final int b_ = b;
            final double dist_ab_ = dist_ab;
            ParallelUtils.streamP(S.streamInts(), !singleThread).forEach(x -> {
                double d_ax = getDist(a_, x, size, vecs, cache, dist_map);
                double d_bx = getDist(b_, x, size, vecs, cache, dist_map);
                double d_xn = distMeasure.dissimilarity(size_a, size_b, size[x], dist_ab_, d_ax, d_bx);

                Map<Integer, Double> dist_map_x = dist_map.get(x);
                if (dist_map_x == null) {
                    //                    dist_map[x] = new IntDoubleMap(1);
                    //                    dist_map[x].put(n, d_xn);
                } else //if(dist_map[x] != null)
                {
                    dist_map_x.remove(b_);
                    dist_map_x.put(n, d_xn);
                    if (dist_map_x.size() * 50 < N && !(dist_map_x instanceof IntDoubleMap))//we are using such a small percentage, put it into a sparser map
                        dist_map.set(x, new IntDoubleMap(dist_map_x));//distMap is an Array list already filled with entries, so this is thread safe set
                }

                map_n.put(x, d_xn);
            });

            dist_map.set(removed, null);//no longer in use no mater what
            dist_map.set(n, map_n);

            // 25: S ← S ∪ {n}
            size[n] = size_a + size_b;
            S.add(n);
        }

        fixMergeOrderAndAssign(mergedDistance, merge_kept, merge_removed, lowK, N, highK, designations);

        return designations;
    }

    /**
     * After clustering, we need to fix up the merge order - since the NNchain
     * only gets the merges correct, not their ordering. This also figures out
     * what number of clusters to use
     *
     * @param mergedDistance
     * @param merge_kept
     * @param merge_removed
     * @param lowK
     * @param N
     * @param highK
     * @param designations
     */
    private void fixMergeOrderAndAssign(double[] mergedDistance, IntList merge_kept, IntList merge_removed, int lowK, final int N, int highK, int[] designations) {
        //Now that we are done clustering, we need to re-order the merges so that the smallest distances are mergered first
        IndexTable it = new IndexTable(mergedDistance);
        it.apply(merge_kept);
        it.apply(merge_removed);
        it.apply(mergedDistance);
        for (int i = 0; i < it.length(); i++) {
            merges[merges.length - i * 2 - 1] = merge_removed.get(i);
            merges[merges.length - i * 2 - 2] = merge_kept.get(i);
        }

        //Now lets figure out a guess at the cluster size
        /*
         * Keep track of the average dist when merging, mark when it becomes abnormaly large as a guess at K
         */
        OnLineStatistics distChange = new OnLineStatistics();
        double maxStndDevs = Double.MIN_VALUE;

        /**
         * How many clusters to return
         */
        int clusterSize = lowK;

        for (int i = 0; i < mergedDistance.length; i++) {

            //Keep track of the changes in cluster size, and mark if this one was abnormall large
            distChange.add(mergedDistance[i]);

            int curK = N - i;
            if (curK >= lowK && curK <= highK)//In the cluster window?
            {
                double stndDevs = (mergedDistance[i] - distChange.getMean()) / distChange.getStandardDeviation();
                if (stndDevs > maxStndDevs) {
                    maxStndDevs = stndDevs;
                    clusterSize = curK;
                }
            }
        }

        PriorityHAC.assignClusterDesignations(designations, clusterSize, merges);
    }
}
