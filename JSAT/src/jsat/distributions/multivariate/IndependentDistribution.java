package jsat.distributions.multivariate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import jsat.distributions.ContinuousDistribution;
import jsat.distributions.Distribution;
import jsat.distributions.discrete.DiscreteDistribution;
import jsat.linear.DenseVector;
import jsat.linear.Vec;

/**
 * @author Edward Raff
 */
public class IndependentDistribution implements MultivariateDistribution {
    protected List<Distribution> distributions;

    public IndependentDistribution(List<Distribution> distributions) {
        this.distributions = distributions;
    }

    public IndependentDistribution(IndependentDistribution toCopy) {
        this.distributions = toCopy.distributions.stream()
                .map(Distribution::clone)
                .collect(Collectors.toList());
    }

    @Override
    public double logPdf(Vec x) {
        if (x.length() != distributions.size())
            throw new ArithmeticException("Expected input of size " + distributions.size() + " not " + x.length());
        double logPDF = 0;
        for (int i = 0; i < x.length(); i++) {
            Distribution dist = distributions.get(i);
            if (dist instanceof DiscreteDistribution)
                logPDF += ((DiscreteDistribution) dist).logPmf((int) Math.round(x.get(i)));
            else
                logPDF += ((ContinuousDistribution) dist).logPdf(x.get(i));
        }

        return logPDF;
    }

    @Override
    public <V extends Vec> boolean setUsingData(List<V> dataSet, boolean parallel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public MultivariateDistribution clone() {
        return new IndependentDistribution(this);
    }

    @Override
    public List<Vec> sample(int count, Random rand) {
        List<Vec> sample = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            sample.add(new DenseVector(distributions.size()));
        }

        for (int j = 0; j < distributions.size(); j++) {
            Distribution d = distributions.get(j);
            double[] vals = d.sample(count, rand);
            for (int i = 0; i < sample.size(); i++)
                sample.get(i).set(j, vals[i]);
        }

        return sample;
    }

}
