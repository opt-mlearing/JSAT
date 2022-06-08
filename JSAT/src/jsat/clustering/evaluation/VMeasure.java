package jsat.clustering.evaluation;

import java.util.List;

import jsat.DataSet;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;

/**
 * ﻿V-Measure is a general purpose cluster evaluation technique, which is the
 * harmonic mean of {@link Homogeneity} and {@link Completeness}. Normally, a
 * value of 1.0 would be perfect clustering, and 0 would be the worst possible
 * score.
 *
 * @author Edward Raff
 */
public class VMeasure implements ClusterEvaluation {
    private double beta;

    /**
     * Creates a V-Means which is the weighted harmonic mean between
     * {@link Homogeneity} and {@link Completeness}. ﻿if β is greater than 1
     * completeness is weighted more strongly in the calculation, if β is less
     * than 1, homogeneity is weighted more strongly
     *
     * @param beta the weight preference to apply to completeness (greater than
     *             one) or homogeneity ( less than 1 but &gt; 0).
     */
    public VMeasure(double beta) {
        if (beta < 0)
            throw new IllegalArgumentException("Beta must be positive, not " + beta);
        this.beta = beta;
    }

    /**
     * Creates the standard V-Measure which is the harmonic mean between
     * {@link Homogeneity} and {@link Completeness}
     */
    public VMeasure() {
        this(1.0);
    }


    @Override
    public double evaluate(int[] designations, DataSet dataSet) {
        if (!(dataSet instanceof ClassificationDataSet))
            throw new RuntimeException("VMeasure can only be calcuate for classification data sets");

        Homogeneity homo = new Homogeneity();
        Completeness comp = new Completeness();

        double h = homo.naturalScore(homo.evaluate(designations, dataSet));
        double c = comp.naturalScore(comp.evaluate(designations, dataSet));

        double v;
        if ((beta * h) + c == 0.0)
            v = 0;
        else
            v = (1 + beta) * h * c / ((beta * h) + c);
        return 1 - v;
    }

    @Override
    public double evaluate(List<List<DataPoint>> dataSets) {
        throw new UnsupportedOperationException("VMeasure requires the true data set"
                + " labels, call evaluate(int[] designations, DataSet dataSet)"
                + " instead");
    }

    @Override
    public double naturalScore(double evaluate_score) {
        return 1 - evaluate_score;
    }

    @Override
    public VMeasure clone() {
        return new VMeasure(this.beta);
    }

}
