package jsat.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jsat.DataSet;
import jsat.classifiers.DataPoint;
import jsat.distributions.Distribution;
import jsat.distributions.LogUniform;
import jsat.distributions.kernels.KernelTrick;
import jsat.distributions.kernels.RBFKernel;
import jsat.linear.CholeskyDecomposition;
import jsat.linear.DenseMatrix;
import jsat.linear.Matrix;
import jsat.linear.Vec;
import jsat.parameters.Parameter.ParameterHolder;
import jsat.parameters.Parameterized;
import jsat.utils.concurrent.ParallelUtils;

/**
 * A kernelized implementation of Ridge Regression. Ridge
 * Regression is equivalent to {@link MultipleLinearRegression} with an added
 * L<sub>2</sub> penalty for the weight vector. <br><br>
 * This algorithm is very expensive to compute O(n<sup>3</sup>), where n is the
 * number of training points.
 *
 * @author Edward Raff
 */
public class KernelRidgeRegression implements Regressor, Parameterized {

    private static final long serialVersionUID = 6275333785663250072L;
    private double lambda;
    @ParameterHolder
    private KernelTrick k;
    private List<Vec> vecs;
    private double[] alphas;

    /**
     * Creates a new Kernel Ridge Regression learner that uses an RBF kernel
     */
    public KernelRidgeRegression() {
        this(1e-6, new RBFKernel());
    }

    /**
     * Creates a new Kernel Ridge Regression learner
     *
     * @param lambda the regularization parameter
     * @param kernel the kernel to use
     * @see #setLambda(double)
     */
    public KernelRidgeRegression(double lambda, KernelTrick kernel) {
        setLambda(lambda);
        setKernel(kernel);
    }

    /**
     * Copy Constructor
     *
     * @param toCopy the object to copy
     */
    protected KernelRidgeRegression(KernelRidgeRegression toCopy) {
        this(toCopy.lambda, toCopy.getKernel().clone());
        if (toCopy.alphas != null)
            this.alphas = Arrays.copyOf(toCopy.alphas, toCopy.alphas.length);
        if (toCopy.vecs != null)
            this.vecs = new ArrayList<>(toCopy.vecs);
    }

    /**
     * Guesses the distribution to use for the &lambda; parameter
     *
     * @param d the dataset to get the guess for
     * @return the guess for the &lambda; parameter
     */
    public static Distribution guessLambda(DataSet d) {
        return new LogUniform(1e-7, 1e-2);
    }

    /**
     * Sets the regularization parameter used. The value of lambda depends on
     * the data set and kernel used, with easier problems using smaller lambdas.
     *
     * @param lambda the positive regularization constant in (0, Inf)
     */
    public void setLambda(double lambda) {
        if (Double.isNaN(lambda) || Double.isInfinite(lambda) || lambda <= 0)
            throw new IllegalArgumentException("lambda must be a positive constant, not " + lambda);
        this.lambda = lambda;
    }

    /**
     * Returns the regularization constant in use
     *
     * @return the regularization constant in use
     */
    public double getLambda() {
        return lambda;
    }

    /**
     * Sets the kernel trick to use
     *
     * @param k the kernel to use
     */
    public void setKernel(KernelTrick k) {
        this.k = k;
    }

    /**
     * Returns the kernel in use
     *
     * @return the kernel in use
     */
    public KernelTrick getKernel() {
        return k;
    }

    @Override
    public double regress(DataPoint data) {
        Vec x = data.getNumericalValues();
        double score = 0;
        for (int i = 0; i < alphas.length; i++)
            score += alphas[i] * k.eval(vecs.get(i), x);
        return score;
    }

    @Override
    public void train(RegressionDataSet dataSet, boolean parallel) {
        final int N = dataSet.size();
        vecs = new ArrayList<>(N);
        //alphas initalized later
        Vec Y = dataSet.getTargetValues();
        for (int i = 0; i < N; i++)
            vecs.add(dataSet.getDataPoint(i).getNumericalValues());

        final Matrix K = new DenseMatrix(N, N);
        ParallelUtils.run(parallel, N, (i) ->
        {
            K.set(i, i, k.eval(vecs.get(i), vecs.get(i)) + lambda);//diagonal values
            for (int j = i + 1; j < N; j++) {
                double K_ij = k.eval(vecs.get(i), vecs.get(j));
                K.set(i, j, K_ij);
                K.set(j, i, K_ij);
            }
        });

        CholeskyDecomposition cd;
        if (parallel)
            cd = new CholeskyDecomposition(K, ParallelUtils.CACHED_THREAD_POOL);
        else
            cd = new CholeskyDecomposition(K);
        Vec alphaTmp = cd.solve(Y);
        alphas = alphaTmp.arrayCopy();
    }

    @Override
    public boolean supportsWeightedData() {
        return false;
    }

    @Override
    public KernelRidgeRegression clone() {
        return new KernelRidgeRegression(this);
    }
}
