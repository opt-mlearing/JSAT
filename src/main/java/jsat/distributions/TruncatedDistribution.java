package jsat.distributions;

import jsat.linear.Vec;

/**
 * This distribution truncates a given continuous distribution only be valid for
 * values in the range (min, max]. The {@link #pdf(double) pdf} for any value
 * outside that range will be 0.<br>
 * <br>
 * The {@link #pdf(double) }, {@link #cdf(double) }, and the {@link #invCdf(double)
 * } methods are implemented efficiently, with little overhead per call. All
 * other methods are approximated numerically, and incur more overhead.
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class TruncatedDistribution extends ContinuousDistribution {
    private ContinuousDistribution base;
    private double min;
    private double max;

    /**
     * The probability of a given value coming from the specified range of the
     * original distribution
     */
    private double probInOrigRange;
    /**
     * the PDF(min) of the base distribution
     */
    private double old_min_p;
    /**
     * The PDF(max) of the base distribution
     */
    private double old_max_p;

    public TruncatedDistribution(ContinuousDistribution base, double min, double max) {
        this.base = base;
        this.min = min;
        this.max = max;
        computeNeeded();
    }

    @Override
    public double pdf(double x) {
        if (x <= min || x > max)
            return 0;
        return base.pdf(x) / probInOrigRange;
    }

    @Override
    public double cdf(double x) {
        if (x <= min)
            return 0;
        else if (x >= max)
            return 1;
        else
            return (base.cdf(x) - old_min_p) / probInOrigRange;
    }

    @Override
    public double invCdf(double p) {
        double old_min_p = base.cdf(min);
        double old_max_p = base.cdf(max);

        //rescale p to the range of p values that are acceptable to the base distribution now
        double newP = (old_max_p - old_min_p) * p + old_min_p;


        return base.invCdf(newP);
    }

    private void computeNeeded() {
        old_min_p = base.cdf(min);
        old_max_p = base.cdf(max);
        probInOrigRange = old_max_p - old_min_p;
    }


    @Override
    public String getDistributionName() {
        return "(" + min + ", " + max + "] Truncated " + base.getDescriptiveName();
    }

    //TODO should min/max be set by this too?
    @Override
    public String[] getVariables() {
        return base.getVariables();
    }

    @Override
    public double[] getCurrentVariableValues() {
        return base.getCurrentVariableValues();
    }

    @Override
    public void setVariable(String var, double value) {
        base.setVariable(var, value);
        computeNeeded();
    }

    @Override
    public TruncatedDistribution clone() {
        return new TruncatedDistribution(base.clone(), min, max);
    }

    @Override
    public void setUsingData(Vec data) {
        base.setUsingData(data);
        computeNeeded();
    }

    @Override
    public double mode() {
        double baseMode = base.mode();
        if (baseMode <= max && baseMode > min)
            return baseMode;
        return super.mode();
    }

    @Override
    public double min() {
        return Math.max(Math.nextUp(min), base.min());
    }

    @Override
    public double max() {
        return Math.min(max, base.max());
    }


}
