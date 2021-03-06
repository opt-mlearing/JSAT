package jsat.classifiers.trees;

import jsat.FixedProblems;
import jsat.TestTools;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.datatransform.DataTransformProcess;
import jsat.datatransform.InsertMissingValuesTransform;
import jsat.datatransform.NumericalToHistogram;
import jsat.regression.RegressionDataSet;
import jsat.regression.RegressionModelEvaluation;
import jsat.utils.random.RandomUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class DecisionTreeTest {

    public DecisionTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTrainC_RegressionDataSet() {
        System.out.println("train");
        for (TreePruner.PruningMethod pruneMethod : TreePruner.PruningMethod.values())
            for (ImpurityScore.ImpurityMeasure gainMethod : ImpurityScore.ImpurityMeasure.values())
                for (boolean useCatFeatures : new boolean[]{true, false}) {
                    DecisionTree instance = new DecisionTree();
                    instance.setGainMethod(gainMethod);
                    instance.setTestProportion(0.3);
                    instance.setPruningMethod(pruneMethod);


                    RegressionDataSet train = FixedProblems.getLinearRegression(3000, RandomUtil.getRandom());
                    RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

                    RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
                    if (useCatFeatures)
                        rme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram(10)));
                    if (useCatFeatures)
                        rme.evaluateTestSet(train);
                    else
                        rme.evaluateTestSet(test);

                    assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 3);
                }
    }

    @Test
    public void testTrainC_RegressionDataSet_ExecutorService() {
        System.out.println("train");

        for (TreePruner.PruningMethod pruneMethod : TreePruner.PruningMethod.values())
            for (ImpurityScore.ImpurityMeasure gainMethod : ImpurityScore.ImpurityMeasure.values())
                for (boolean useCatFeatures : new boolean[]{true, false}) {
                    DecisionTree instance = new DecisionTree();
                    instance.setGainMethod(gainMethod);
                    instance.setTestProportion(0.3);
                    instance.setPruningMethod(pruneMethod);

                    RegressionDataSet train = FixedProblems.getLinearRegression(3000, RandomUtil.getRandom());
                    RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

                    RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train, true);
                    if (useCatFeatures)
                        rme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram(10)));
                    if (useCatFeatures)
                        rme.evaluateTestSet(train);
                    else
                        rme.evaluateTestSet(test);

                    assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 3);

                }
    }

    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");

        for (TreePruner.PruningMethod pruneMethod : TreePruner.PruningMethod.values())
            for (ImpurityScore.ImpurityMeasure gainMethod : ImpurityScore.ImpurityMeasure.values())
                for (boolean useCatFeatures : new boolean[]{true, false}) {
                    DecisionTree instance = new DecisionTree();
                    instance.setGainMethod(gainMethod);
                    instance.setTestProportion(0.3);
                    instance.setPruningMethod(pruneMethod);

                    int attempts = 3;
                    do {
                        ClassificationDataSet train = FixedProblems.getCircles(5000, 1.0, 10.0, 100.0);
                        ClassificationDataSet test = FixedProblems.getCircles(200, 1.0, 10.0, 100.0);

                        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
                        if (useCatFeatures)
                            cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram(50)));
                        cme.evaluateTestSet(test);

                        if (cme.getErrorRate() < 0.075)
                            break;

                    }
                    while (attempts-- > 0);
                    assertTrue(attempts > 0);

                }
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");

        for (TreePruner.PruningMethod pruneMethod : TreePruner.PruningMethod.values())
            for (ImpurityScore.ImpurityMeasure gainMethod : ImpurityScore.ImpurityMeasure.values())
                for (boolean useCatFeatures : new boolean[]{true, false}) {
                    DecisionTree instance = new DecisionTree();
                    instance.setGainMethod(gainMethod);
                    instance.setTestProportion(0.3);
                    instance.setPruningMethod(pruneMethod);

                    int attempts = 3;
                    do {
                        ClassificationDataSet train = FixedProblems.getCircles(5000, 1.0, 10.0, 100.0);
                        ClassificationDataSet test = FixedProblems.getCircles(200, 1.0, 10.0, 100.0);

                        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
                        if (useCatFeatures)
                            cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram(50)));
                        cme.evaluateTestSet(test);

                        if (cme.getErrorRate() < 0.075)
                            break;

                    }
                    while (attempts-- > 0);
                    assertTrue(attempts > 0);
                }
    }

    @Test
    public void testTrainC_ClassificationDataSet_missing() {
        System.out.println("trainC");

        for (TreePruner.PruningMethod pruneMethod : TreePruner.PruningMethod.values())
            for (ImpurityScore.ImpurityMeasure gainMethod : ImpurityScore.ImpurityMeasure.values())
                for (boolean useCatFeatures : new boolean[]{true, false}) {
                    DecisionTree instance = new DecisionTree();
                    instance.setGainMethod(gainMethod);
                    instance.setTestProportion(0.3);
                    instance.setPruningMethod(pruneMethod);

                    int attempts = 3;
                    do {
                        ClassificationDataSet train = FixedProblems.getCircles(5000, 1.0, 10.0, 100.0);
                        ClassificationDataSet test = FixedProblems.getCircles(200, 1.0, 10.0, 100.0);

                        train.applyTransform(new InsertMissingValuesTransform(0.01));

                        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
                        if (useCatFeatures)
                            cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram(50)));
                        cme.evaluateTestSet(test);

                        if (cme.getErrorRate() < 0.25)
                            break;

                        instance.train(train);
                        test.applyTransform(new InsertMissingValuesTransform(0.5));
                        for (int i = 0; i < test.size(); i++)
                            instance.classify(test.getDataPoint(i));

                    }
                    while (attempts-- > 0);
                    assertTrue(attempts > 0);
                }
    }

    @Test
    public void testTrain_RegressionDataSet_missing() {
        System.out.println("train");
        for (TreePruner.PruningMethod pruneMethod : TreePruner.PruningMethod.values())
            for (ImpurityScore.ImpurityMeasure gainMethod : ImpurityScore.ImpurityMeasure.values())
                for (boolean useCatFeatures : new boolean[]{true, false}) {
                    DecisionTree instance = new DecisionTree();
                    instance.setGainMethod(gainMethod);
                    instance.setTestProportion(0.3);
                    instance.setPruningMethod(pruneMethod);


                    RegressionDataSet train = FixedProblems.getLinearRegression(3000, RandomUtil.getRandom());
                    RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

                    train.applyTransform(new InsertMissingValuesTransform(0.01));

                    RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
                    if (useCatFeatures)
                        rme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram(10)));

                    if (useCatFeatures)
                        rme.evaluateTestSet(train);
                    else
                        rme.evaluateTestSet(test);

                    assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 3);

                    instance.train(train);
                    test.applyTransform(new InsertMissingValuesTransform(0.5));
                    for (int i = 0; i < test.size(); i++)
                        instance.regress(test.getDataPoint(i));
                }
    }


    @Test
    public void testClone() {
        System.out.println("clone");
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            DecisionTree instance = new DecisionTree();

            ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(1000, 3);
            ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(1000, 2);
            if (useCatFeatures) {
                t1.applyTransform(new NumericalToHistogram(t1));
                t2.applyTransform(new NumericalToHistogram(t2));
            }

            instance = instance.clone();
            instance = TestTools.deepCopy(instance);

            instance.train(t1);

            DecisionTree result = instance.clone();
            int errors = 0;
            for (int i = 0; i < t1.size(); i++)
                errors += Math.abs(t1.getDataPointCategory(i) - result.classify(t1.getDataPoint(i)).mostLikely());
            assertTrue(errors < 100);
            result.train(t2);

            errors = 0;
            for (int i = 0; i < t1.size(); i++)
                errors += Math.abs(t1.getDataPointCategory(i) - instance.classify(t1.getDataPoint(i)).mostLikely());
            assertTrue(errors < 100);

            errors = 0;
            for (int i = 0; i < t2.size(); i++)
                errors += Math.abs(t2.getDataPointCategory(i) - result.classify(t2.getDataPoint(i)).mostLikely());
            assertTrue(errors < 100);

        }
    }

}
