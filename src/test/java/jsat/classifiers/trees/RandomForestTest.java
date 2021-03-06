package jsat.classifiers.trees;

import jsat.FixedProblems;
import jsat.TestTools;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.datatransform.*;
import jsat.linear.DenseVector;
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
public class RandomForestTest {

    static DenseVector coefs = new DenseVector(new double[]{0.1, 0.9, -0.2, 0.4, -0.5});

    public RandomForestTest() {
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

    static int max_trials = 3;

    @Test
    public void testTrainC_RegressionDataSet() {
        System.out.println("train");
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            RandomForest instance = new RandomForest();

            RegressionDataSet train = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom(), coefs);
            RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom(), coefs);

            RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
            if (useCatFeatures)
                rme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
            rme.evaluateTestSet(test);

            assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 2.5);
        }
    }

    @Test
    public void testTrainC_RegressionDataSetMiingValue() {
        System.out.println("train");
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            RandomForest instance = new RandomForest();

            RegressionDataSet train = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom(), coefs);
            RegressionDataSet test = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom(), coefs);

            train.applyTransform(new InsertMissingValuesTransform(0.1));
            test.applyTransform(new InsertMissingValuesTransform(0.01));

            RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
            if (useCatFeatures)
                rme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram(10)));
            rme.evaluateTestSet(test);

            assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 3.5);
        }
    }

    @Test
    public void testTrainC_RegressionDataSet_ExecutorService() {
        System.out.println("train");

        for (boolean useCatFeatures : new boolean[]{true, false}) {
            RandomForest instance = new RandomForest();
            RegressionDataSet train = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom(), coefs);
            RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom(), coefs);

            RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train, true);
            if (useCatFeatures)
                rme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
            rme.evaluateTestSet(test);

            assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 2.5);

        }
    }

    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");

        for (boolean useCatFeatures : new boolean[]{true, false}) {
            RandomForest instance = new RandomForest();
            for (int trials = 0; trials < max_trials; trials++) {
                ClassificationDataSet train = FixedProblems.getCircles(1000, 1.0, 10.0, 100.0);
                //RF may not get boundry perfect, so use noiseless for testing
                ClassificationDataSet test = FixedProblems.getCircles(100, 0.0, RandomUtil.getRandom(), 1.0, 10.0, 100.0);

                ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
                if (useCatFeatures)
                    cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
                cme.evaluateTestSet(test);

                if (cme.getErrorRate() > 0.001 && trials == max_trials)//wrong too many times, something is broken
                    assertEquals(cme.getErrorRate(), 0.0, 0.001);
                else
                    break;//did good
            }
        }
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            RandomForest instance = new RandomForest();

            for (int trials = 0; trials < max_trials; trials++) {
                ClassificationDataSet train = FixedProblems.getCircles(1000, 1.0, 10.0, 100.0);
                //RF may not get boundry perfect, so use noiseless for testing
                ClassificationDataSet test = FixedProblems.getCircles(100, 0.0, RandomUtil.getRandom(), 1.0, 10.0, 100.0);

                ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
                if (useCatFeatures)
                    cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
                cme.evaluateTestSet(test);

                if (cme.getErrorRate() > 0.001 && trials == max_trials)//wrong too many times, something is broken
                    assertEquals(cme.getErrorRate(), 0.0, 0.001);
                else
                    break;//did good
            }
        }
    }

    @Test
    public void testTrainC_ClassificationDataSetMissingFeat() {
        System.out.println("trainC");
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            RandomForest instance = new RandomForest();

            for (int trials = 0; trials < max_trials; trials++) {
                ClassificationDataSet train = FixedProblems.getCircles(1000, 1.0, 10.0, 100.0);
                //RF may not get boundry perfect, so use noiseless for testing
                ClassificationDataSet test = FixedProblems.getCircles(1000, 0.0, RandomUtil.getRandom(), 1.0, 10.0, 100.0);

                train.applyTransform(new InsertMissingValuesTransform(0.1));
                test.applyTransform(new InsertMissingValuesTransform(0.01));

                ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
                if (useCatFeatures)
                    cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
                cme.evaluateTestSet(test);

                double target = 0.1;
                if (useCatFeatures)//hard to get right with only 2 features like this
                    target = 0.17;
                if (cme.getErrorRate() > 0.001 && trials == max_trials)//wrong too many times, something is broken
                    assertEquals(cme.getErrorRate(), 0.0, target);
                else
                    break;//did good
            }
        }
    }


    @Test
    public void testClone() {
        System.out.println("clone");
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            RandomForest instance = new RandomForest();

            ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(1000, 2);
            ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(1000, 3);
            if (useCatFeatures) {
                t1.applyTransform(new NumericalToHistogram(t1));
                t2.applyTransform(new NumericalToHistogram(t2));
            }

            instance = instance.clone();
            instance = TestTools.deepCopy(instance);

            instance.train(t1);

            double errors = 0;
            RandomForest result = instance.clone();
            errors = 0;
            for (int i = 0; i < t1.size(); i++)
                if (t1.getDataPointCategory(i) != result.classify(t1.getDataPoint(i)).mostLikely())
                    errors++;
            assertEquals(0.0, errors / t1.size(), 0.02);
            result = TestTools.deepCopy(instance);
            errors = 0;
            for (int i = 0; i < t1.size(); i++)
                if (t1.getDataPointCategory(i) != result.classify(t1.getDataPoint(i)).mostLikely())
                    errors++;
            assertEquals(0.0, errors / t1.size(), 0.02);
            result.train(t2);

            errors = 0;
            for (int i = 0; i < t1.size(); i++)
                if (t1.getDataPointCategory(i) != instance.classify(t1.getDataPoint(i)).mostLikely())
                    errors++;
            assertEquals(0.0, errors / t1.size(), 0.02);


            errors = 0;
            for (int i = 0; i < t2.size(); i++)
                if (t2.getDataPointCategory(i) != result.classify(t2.getDataPoint(i)).mostLikely())
                    errors++;
            assertEquals(0.0, errors / t2.size(), 0.02);
        }
    }

}
