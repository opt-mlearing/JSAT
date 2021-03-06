package jsat.classifiers;

import java.util.ArrayList;
import java.util.List;

import jsat.classifiers.calibration.BinaryScoreClassifier;
import jsat.parameters.Parameter.ParameterHolder;
import jsat.parameters.Parameterized;
import jsat.utils.concurrent.ParallelUtils;

/**
 * This classifier turns any classifier, specifically binary classifiers, into
 * multi-class classifiers. For a problem with <i>k</i> target classes, OneVsALl
 * will create <i>k</i> different classifiers. Each one is a reducing of one
 * class against all other classes. Then all <i>k</i> classifiers's results are
 * summed to produce a final classifier
 * <br><br>
 * If the base learner is an instance of {@link BinaryScoreClassifier}, then the
 * winning class label will be the associated classifier that produced the
 * highest score.
 *
 * @author Edward Raff
 */
public class OneVSAll implements Classifier, Parameterized {

    private static final long serialVersionUID = -326668337438092217L;
    private Classifier[] oneVsAlls;
    @ParameterHolder
    private Classifier baseClassifier;
    private CategoricalData predicting;
    private boolean concurrentTraining;
    private boolean useScoreIfAvailable = true;

    /**
     * Creates a new One VS All classifier.
     *
     * @param baseClassifier the base classifier to replicate
     * @see #setConcurrentTraining(boolean)
     */
    public OneVSAll(Classifier baseClassifier) {
        this(baseClassifier, true);
    }

    /**
     * Creates a new One VS All classifier.
     *
     * @param baseClassifier     the base classifier to replicate
     * @param concurrentTraining controls whether or not classifiers are trained
     *                           simultaneously or using sequentially using their
     *                           {@link Classifier#train(jsat.classifiers.ClassificationDataSet, java.util.concurrent.ExecutorService) } method.
     * @see #setConcurrentTraining(boolean)
     */
    public OneVSAll(Classifier baseClassifier, boolean concurrentTraining) {
        this.baseClassifier = baseClassifier;
        this.concurrentTraining = concurrentTraining;
    }

    /**
     * Controls what method of parallel training to use when
     * {@link #train(jsat.classifiers.ClassificationDataSet, java.util.concurrent.ExecutorService) }
     * is called. If set to true, each of the <i>k</i> classifiers will be trained in parallel, using
     * their serial algorithms. If set to false, the <i>k</i> classifiers will be trained sequentially,
     * calling the {@link Classifier#train(jsat.classifiers.ClassificationDataSet, java.util.concurrent.ExecutorService) }
     * for each classifier. <br>
     * <br>
     * This should be set to true for classifiers that do not support parallel training.<br>
     * Setting this to true also uses <i>k</i> times the memory, since each classifier is being created and trained at the same time.
     *
     * @param concurrentTraining whether or not to train the classifiers in parallel
     */
    public void setConcurrentTraining(boolean concurrentTraining) {
        this.concurrentTraining = concurrentTraining;
    }


    @Override
    public CategoricalResults classify(DataPoint data) {
        CategoricalResults cr = new CategoricalResults(predicting.getNumOfCategories());

        if (useScoreIfAvailable && oneVsAlls[0] instanceof BinaryScoreClassifier) {
            int maxIndx = 0;
            double maxScore = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < predicting.getNumOfCategories(); i++) {
                double score = -((BinaryScoreClassifier) oneVsAlls[i]).getScore(data);

                if (score > maxScore) {
                    maxIndx = i;
                    maxScore = score;
                }
            }

            cr.setProb(maxIndx, 1);
        } else {
            for (int i = 0; i < predicting.getNumOfCategories(); i++) {
                CategoricalResults oneVsAllCR = oneVsAlls[i].classify(data);
                double tmp = oneVsAllCR.getProb(0);
                if (tmp > 0)
                    cr.setProb(i, tmp);
            }

            cr.normalize();
        }

        return cr;
    }

    @Override
    public void train(ClassificationDataSet dataSet, boolean parallel) {
        oneVsAlls = new Classifier[dataSet.getClassSize()];

        predicting = dataSet.getPredicting();

        List<List<DataPoint>> categorized = new ArrayList<List<DataPoint>>();
        for (int i = 0; i < oneVsAlls.length; i++) {
            List<DataPoint> tmp = dataSet.getSamples(i);
            ArrayList<DataPoint> oneCat = new ArrayList<DataPoint>(tmp.size());
            oneCat.addAll(tmp);
            categorized.add(oneCat);
        }

        int numer = dataSet.getNumNumericalVars();
        CategoricalData[] categories = dataSet.getCategories();
        ParallelUtils.range(0, oneVsAlls.length, parallel && concurrentTraining).forEach(i ->
        {
            final ClassificationDataSet cds =
                    new ClassificationDataSet(numer, categories, new CategoricalData(2));
            for (DataPoint dp : categorized.get(i))//add the ones
                cds.addDataPoint(dp.getNumericalValues(), dp.getCategoricalValues(), 0);
            //Add all the 'others'
            for (int j = 0; j < categorized.size(); j++)
                if (j != i)
                    for (DataPoint dp : categorized.get(j))
                        cds.addDataPoint(dp.getNumericalValues(), dp.getCategoricalValues(), 1);

            oneVsAlls[i] = baseClassifier.clone();
            if (concurrentTraining)//we are training all models in parallel, so tell each model to do single-thread
                oneVsAlls[i].train(cds, false);
            else
                oneVsAlls[i].train(cds, parallel);

        });
    }

    @Override
    public OneVSAll clone() {
        OneVSAll clone = new OneVSAll(baseClassifier.clone(), concurrentTraining);
        if (this.predicting != null)
            clone.predicting = this.predicting.clone();
        if (this.oneVsAlls != null) {
            clone.oneVsAlls = new Classifier[this.oneVsAlls.length];
            for (int i = 0; i < oneVsAlls.length; i++)
                if (this.oneVsAlls[i] != null)
                    clone.oneVsAlls[i] = this.oneVsAlls[i].clone();
        }
        return clone;
    }

    @Override
    public boolean supportsWeightedData() {
        return baseClassifier.supportsWeightedData();
    }
}
