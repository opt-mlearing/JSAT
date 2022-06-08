package jsat.outlier;

import java.io.Serializable;

import jsat.DataSet;
import jsat.classifiers.DataPoint;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public interface Outlier extends Serializable {
    default public void fit(DataSet d) {
        fit(d, false);
    }

    public void fit(DataSet d, boolean parallel);

    /**
     * Returns an unbounded anomaly/outlier score. Negative values indicate the
     * input is likely to be an outlier, and positive values that the input is
     * likely to be an inlier.
     *
     * @param x
     * @return
     */
    public double score(DataPoint x);

    default public boolean isOutlier(DataPoint x) {
        return score(x) < 0;
    }
}
