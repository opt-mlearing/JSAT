package jsat.distributions.multivariate;

import java.util.List;

import jsat.linear.DenseMatrix;
import jsat.linear.DenseVector;
import jsat.linear.Matrix;
import jsat.linear.MatrixStatistics;
import jsat.linear.Vec;

/**
 * This class implements the Multivariate Normal Distribution, but augments it
 * so that {@link #setUsingData(jsat.DataSet, boolean) fitting} the distribution
 * uses a robust estimate of the distribution parameters. This comes at
 * increased cost that is cubic with respect to the number of variables.
 *
 * @author Edward Raff
 */
public class NormalMR extends NormalM {

    @Override
    public <V extends Vec> boolean setUsingData(List<V> dataSet, boolean parallel) {
        try {
            Vec mean = new DenseVector(dataSet.get(0).length());
            Matrix cov = new DenseMatrix(mean.length(), mean.length());
            MatrixStatistics.FastMCD(mean, cov, dataSet, parallel);

            setMeanCovariance(mean, cov);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
