package jsat.outlier;

import java.util.Arrays;

import jsat.DataSet;
import jsat.classifiers.DataPoint;
import jsat.distributions.multivariate.MultivariateDistribution;
import jsat.distributions.multivariate.NormalMR;
import jsat.utils.concurrent.ParallelUtils;

/**
 * This class provides an outlier detector based upon density estimation.
 *
 * @author Edward Raff
 */
public class DensityOutlier implements Outlier {
    private double outlierFraction;
    private MultivariateDistribution density;
    /**
     * Threshold for what counts as an outlier. Any value less than or equal to
     * this threshold will be considered an outlier.
     */
    private double threshold;

    public DensityOutlier() {
        this(0.05);
    }

    public DensityOutlier(double outlierFraction) {
        this(outlierFraction, new NormalMR());
    }

    public DensityOutlier(double outlierFraction, MultivariateDistribution density) {
        this.outlierFraction = outlierFraction;
        this.density = density;
    }

    public DensityOutlier(DensityOutlier toCopy) {
        this(toCopy.outlierFraction, toCopy.density.clone());
        this.threshold = toCopy.threshold;
    }

    public void setOutlierFraction(double outlierFraction) {
        this.outlierFraction = outlierFraction;
    }

    public double getOutlierFraction() {
        return outlierFraction;
    }

    public void setDensityDistribution(MultivariateDistribution density) {
        this.density = density;
    }

    public MultivariateDistribution getDensityDistribution() {
        return density;
    }


    @Override
    public void fit(DataSet d, boolean parallel) {
        density.setUsingData(d, parallel);
        double[] scores = new double[d.size()];
        ParallelUtils.run(parallel, scores.length, (start, end) ->
        {
            for (int i = start; i < end; i++)
                scores[i] = density.logPdf(d.getDataPoint(i).getNumericalValues());
        });
        Arrays.sort(scores);
        threshold = scores[(int) (scores.length * outlierFraction)];
    }

    @Override
    public double score(DataPoint x) {
        double logPDF = density.logPdf(x.getNumericalValues());
        return logPDF - threshold;
    }

    @Override
    protected DensityOutlier clone() throws CloneNotSupportedException {
        return new DensityOutlier(this);
    }

}
