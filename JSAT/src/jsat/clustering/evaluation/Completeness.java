package jsat.clustering.evaluation;

import java.util.List;

import jsat.DataSet;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;

/**
 * ﻿A clustering result satisfies completeness if all the data points that are members of a given class are elements of the same cluster.
 *
 * @author Edward Raff
 */
public class Completeness implements ClusterEvaluation {
    public Completeness() {
    }

    @Override
    public double evaluate(int[] designations, DataSet dataSet) {
        if (!(dataSet instanceof ClassificationDataSet))
            throw new RuntimeException("Completeness can only be calcuate for classification data sets");
        ClassificationDataSet cds = (ClassificationDataSet) dataSet;
        int clusters = 0;//how many clusters are there? 
        for (int clusterID : designations)
            clusters = Math.max(clusterID + 1, clusters);
        int C = cds.getPredicting().getNumOfCategories();
        int K = clusters;

        double[][] A = new double[C][K];
        //Rows of AK
        double[] class_sum = new double[C];
        double[] cluster_sum = new double[K];
        double n = 0.0;
        for (int i = 0; i < designations.length; i++) {
            int cluster = designations[i];
            if (cluster < 0)
                continue;//noisy point 
            int label = cds.getDataPointCategory(i);
            double weight = cds.getWeight(i);
            A[label][cluster] += weight;
            class_sum[label] += weight;
            cluster_sum[cluster] += weight;
            n += weight;
        }

        double h_kc = 0;
        double h_k = 0;

        for (int c = 0; c < C; c++) {
            for (int k = 0; k < K; k++) {
                //compute h_k, h_k needs only one loop to compute its values, hence c check
                if (cluster_sum[k] > 0 && c == 0)
                    h_k -= cluster_sum[k] / n * (Math.log(cluster_sum[k]) - Math.log(n));

                //h(k|c) calc
                if (A[c][k] == 0 || class_sum[c] == 0)
                    continue;
                h_kc -= A[c][k] / n * (Math.log(A[c][k]) - Math.log(class_sum[c]));
            }
        }

        if (h_k == 0)
            return 0;
        return h_kc / h_k;
    }

    @Override
    public double evaluate(List<List<DataPoint>> dataSets) {
        throw new UnsupportedOperationException("Completeness requires the true data set"
                + " labels, call evaluate(int[] designations, DataSet dataSet)"
                + " instead");
    }

    @Override
    public double naturalScore(double evaluate_score) {
        return 1 - evaluate_score;
    }

    @Override
    public Completeness clone() {
        return new Completeness();
    }

}
