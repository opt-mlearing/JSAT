package jsat.clustering.dissimilarity;

import jsat.linear.distancemetrics.DistanceMetric;

/**
 * Median link dissimilarity, also called WPGMC. When two points are merged
 * under the Median dissimilarity, the weighting to all points in every
 * clustering is distributed evenly.
 *
 * @author Edward Raff
 */
public class MedianDissimilarity extends LanceWilliamsDissimilarity {
    public MedianDissimilarity(DistanceMetric dm) {
        super(dm);
    }

    public MedianDissimilarity(MedianDissimilarity toCopy) {
        super(toCopy);
    }

    @Override
    protected double aConst(boolean iFlag, int ni, int nj, int nk) {
        return 0.5;
    }

    @Override
    protected double bConst(int ni, int nj, int nk) {
        return -0.25;
    }

    @Override
    protected double cConst(int ni, int nj, int nk) {
        return 0;
    }

    @Override
    public MedianDissimilarity clone() {
        return new MedianDissimilarity(this);
    }

}
