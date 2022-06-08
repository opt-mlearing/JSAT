package jsat.datatransform;

import java.util.Random;

import jsat.DataSet;
import jsat.classifiers.DataPoint;
import jsat.linear.Vec;
import jsat.utils.random.RandomUtil;

/**
 * This transform mostly exists for testing code. It alters a dataset by setting
 * features to missing with a fixed probability.
 *
 * @author edwardraff
 */
public class InsertMissingValuesTransform implements InPlaceTransform {
    private double prob;
    private Random rand;

    /**
     * @param prob the probability of setting each feature to missing
     */
    public InsertMissingValuesTransform(double prob) {
        this(prob, RandomUtil.getRandom());
    }

    /**
     * @param prob the probability of setting each feature to missing
     * @param rand the source of randomness
     */
    public InsertMissingValuesTransform(double prob, Random rand) {
        this.prob = Math.min(1, Math.max(0, prob));
        this.rand = rand;
    }

    @Override
    public void fit(DataSet data) {
        //no-op, nothing to do
    }

    @Override
    public void mutableTransform(DataPoint dp) {
        Vec v = dp.getNumericalValues();
        for (int i = 0; i < v.length(); i++)
            if (rand.nextDouble() < prob)
                v.set(i, Double.NaN);
        int[] cats = dp.getCategoricalValues();
        for (int i = 0; i < cats.length; i++)
            if (rand.nextDouble() < prob)
                cats[i] = -1;
    }

    @Override
    public boolean mutatesNominal() {
        return true;
    }

    @Override
    public DataPoint transform(DataPoint dp) {
        DataPoint ndp = dp.clone();
        mutableTransform(ndp);
        return ndp;
    }

    @Override
    public InsertMissingValuesTransform clone() {
        return new InsertMissingValuesTransform(prob, rand);
    }

}
