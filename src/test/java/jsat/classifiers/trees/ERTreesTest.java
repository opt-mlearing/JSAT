package jsat.classifiers.trees;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.datatransform.DataTransformProcess;
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
public class ERTreesTest {

    public ERTreesTest() {
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
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");

        for (int i = 0; i < 3; i++) {
            boolean useCatFeatures = i < 2;
            ERTrees instance = new ERTrees();
            instance.setBinaryCategoricalSplitting(i == 1);

            ClassificationDataSet train = FixedProblems.getCircles(10000, RandomUtil.getRandom(), 1.0, 10.0, 100.0);
            ClassificationDataSet test = FixedProblems.getCircles(1000, RandomUtil.getRandom(), 1.0, 10.0, 100.0);

            ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
            if (useCatFeatures)
                cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
            cme.evaluateTestSet(test);

            assertTrue(cme.getErrorRate() <= 0.001);
        }
    }

    @Test
    public void testTrainC_RegressionDataSet() {
        System.out.println("train");
        for (int i = 0; i < 3; i++) {
            boolean useCatFeatures = i < 2;
            ERTrees instance = new ERTrees();
            instance.setBinaryCategoricalSplitting(i == 1);


            RegressionDataSet train = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom());
            RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

            RegressionModelEvaluation cme = new RegressionModelEvaluation(instance, train);
            if (useCatFeatures)
                cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
            cme.evaluateTestSet(test);


            assertTrue(cme.getMeanError() <= test.getTargetValues().mean() * 2.5);
        }
    }

    @Test
    public void testTrainC_RegressionDataSet_ExecutorService() {
        System.out.println("train");

        for (int i = 0; i < 3; i++) {
            boolean useCatFeatures = i < 2;
            ERTrees instance = new ERTrees();
            instance.setBinaryCategoricalSplitting(i == 1);

            RegressionDataSet train = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom());
            RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

            RegressionModelEvaluation cme = new RegressionModelEvaluation(instance, train, true);
            if (useCatFeatures)
                cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
            cme.evaluateTestSet(test);

            assertTrue(cme.getMeanError() <= test.getTargetValues().mean() * 2.5);
        }
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        for (int i = 0; i < 3; i++) {
            boolean useCatFeatures = i < 2;
            ERTrees instance = new ERTrees();
            instance.setBinaryCategoricalSplitting(i == 1);


            ClassificationDataSet train = FixedProblems.getCircles(10000, RandomUtil.getRandom(), 1.0, 10.0, 100.0);
            ClassificationDataSet test = FixedProblems.getCircles(1000, RandomUtil.getRandom(), 1.0, 10.0, 100.0);

            ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
            if (useCatFeatures)
                cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
            cme.evaluateTestSet(test);

            assertTrue(cme.getErrorRate() <= 0.001);
        }
    }


    @Test
    public void testClone() {
        System.out.println("clone");
        for (int k = 0; k < 3; k++) {
            boolean useCatFeatures = k < 2;
            ERTrees instance = new ERTrees();
            instance.setBinaryCategoricalSplitting(k == 1);

            ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(1000, 3, RandomUtil.getRandom());
            ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(1000, 6, RandomUtil.getRandom());
            if (useCatFeatures) {
                t1.applyTransform(new NumericalToHistogram(t1));
                t2.applyTransform(new NumericalToHistogram(t2));
            }

            instance = instance.clone();

            instance.train(t1);

            ERTrees result = instance.clone();
            for (int i = 0; i < t1.size(); i++)
                assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
            result.train(t2);

            for (int i = 0; i < t1.size(); i++)
                assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

            for (int i = 0; i < t2.size(); i++)
                assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());
        }
    }

}
