package jsat.classifiers.bayesian;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.datatransform.DataTransformProcess;
import jsat.datatransform.NumericalToHistogram;
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
public class MultinomialNaiveBayesTest {

    public MultinomialNaiveBayesTest() {
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

        for (boolean useCatFeatures : new boolean[]{true, false}) {
            MultinomialNaiveBayes instance = new MultinomialNaiveBayes();

            ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(10000, 3, RandomUtil.getRandom());
            ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(1000, 3, RandomUtil.getRandom());

            ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
            if (useCatFeatures)
                cme.setDataTransformProcess(new DataTransformProcess(new NumericalToHistogram()));
            cme.evaluateTestSet(test);

            assertTrue(cme.getErrorRate() <= 0.001);

        }
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            MultinomialNaiveBayes instance = new MultinomialNaiveBayes();


            ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(10000, 3, RandomUtil.getRandom());
            ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(1000, 3, RandomUtil.getRandom());

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
        for (boolean useCatFeatures : new boolean[]{true, false}) {
            MultinomialNaiveBayes instance = new MultinomialNaiveBayes();

            ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(1000, 3, RandomUtil.getRandom());
            ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(1000, 6, RandomUtil.getRandom());
            if (useCatFeatures) {
                t1.applyTransform(new NumericalToHistogram(t1));
                t2.applyTransform(new NumericalToHistogram(t2));
            }

            instance = instance.clone();

            instance.train(t1);

            MultinomialNaiveBayes result = instance.clone();
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
