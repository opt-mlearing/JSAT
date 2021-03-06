package jsat.distributions;

import java.io.Serializable;
import java.util.Random;

import jsat.linear.DenseVector;
import jsat.math.rootfinding.Zeroin;

/**
 * Base distribution class for distributions that have only one input.
 *
 * @author Edward Raff
 */
public abstract class Distribution implements Cloneable, Serializable {

    /**
     * Computes the value of the Cumulative Density Function (CDF) at the given
     * point. The CDF returns a value in the range [0, 1], indicating what
     * portion of values occur at or below that point.
     *
     * @param x the value to get the CDF of
     * @return the CDF(x)
     */
    abstract public double cdf(double x);

    /**
     * Computes the inverse Cumulative Density Function (CDF<sup>-1</sup>) at
     * the given point. It takes in a value in the range of [0, 1] and returns
     * the value x, such that CDF(x) = <tt>p</tt>
     *
     * @param p the probability value
     * @return the value such that the CDF would return <tt>p</tt>
     */
    public double invCdf(double p) {
        if (p < 0 || p > 1)
            throw new ArithmeticException("Value of p must be in the range [0,1], not " + p);
        double a = Double.isInfinite(min()) ? Double.MIN_VALUE : min();
        double b = Double.isInfinite(max()) ? Double.MAX_VALUE : max();

        //default case, lets just do a root finding on the CDF for the specific value of p
        return Zeroin.root(a, b, (x) -> cdf(x) - p);
    }

    /**
     * Computes the mean value of the distribution
     *
     * @return the mean value of the distribution
     */
    abstract public double mean();

    /**
     * Computes the median value of the distribution
     *
     * @return the median value of the distribution
     */
    public double median() {
        return invCdf(0.5);
    }

    /**
     * Computes the mode of the distribution. Not all distributions have a mode for all parameter values.
     * {@link Double#NaN NaN} may be returned if the mode is not defined for the current values of the
     * distribution.
     *
     * @return the mode of the distribution
     */
    abstract public double mode();

    /**
     * Computes the variance of the distribution. Not all distributions have a
     * finite variance for all parameter values. {@link Double#NaN NaN} may be
     * returned if the variance is not defined for the current values of the distribution.
     * {@link Double#POSITIVE_INFINITY Infinity} is a possible value to be returned
     * by some distributions.
     *
     * @return the variance of the distribution.
     */
    abstract public double variance();

    /**
     * Computes the skewness of the distribution. Not all distributions have a
     * finite skewness for all parameter values. {@link Double#NaN NaN} may be
     * returned if the skewness is not defined for the current values of the distribution.
     *
     * @return the skewness of the distribution.
     */
    abstract public double skewness();

    /**
     * Computes the standard deviation of the distribution. Not all distributions have a
     * finite standard deviation for all parameter values. {@link Double#NaN NaN} may be
     * returned if the variance is not defined for the current values of the distribution.
     * {@link Double#POSITIVE_INFINITY Infinity} is a possible value to be returned
     * by some distributions.
     *
     * @return the standard deviation of the distribution
     */
    public double standardDeviation() {
        return Math.sqrt(variance());
    }

    /**
     * The minimum value for which the {@link #pdf(double) } is meant to return
     * a value. Note that {@link Double#NEGATIVE_INFINITY} is a valid return
     * value.
     *
     * @return the minimum value for which the {@link #pdf(double) } is meant to
     * return a value.
     */
    abstract public double min();

    /**
     * The maximum value for which the {@link #pdf(double) } is meant to return
     * a value. Note that {@link Double#POSITIVE_INFINITY} is a valid return
     * value.
     *
     * @return the maximum value for which the {@link #pdf(double) } is meant to
     * return a value.
     */
    abstract public double max();

    /**
     * This method returns a double array containing the values of random samples from this distribution.
     *
     * @param numSamples the number of random samples to take
     * @param rand       the source of randomness
     * @return an array of the random sample values
     */
    public double[] sample(int numSamples, Random rand) {
        double[] samples = new double[numSamples];
        for (int i = 0; i < samples.length; i++)
            samples[i] = invCdf(rand.nextDouble());

        return samples;
    }

    /**
     * This method returns a double array containing the values of random samples from this distribution.
     *
     * @param numSamples the number of random samples to take
     * @param rand       the source of randomness
     * @return a vector of the random sample values
     */
    public DenseVector sampleVec(int numSamples, Random rand) {
        return DenseVector.toDenseVec(sample(numSamples, rand));
    }

    @Override
    abstract public Distribution clone();
}
