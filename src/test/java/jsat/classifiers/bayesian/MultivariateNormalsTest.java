package jsat.classifiers.bayesian;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.utils.SystemInfo;
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
public class MultivariateNormalsTest {

    public MultivariateNormalsTest() {
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
        MultivariateNormals instance = new MultivariateNormals();

        ClassificationDataSet train = FixedProblems.getCircles(1000, 3, 0.1, 1.0, 10.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, 3, 0.1, 1.0, 10.0);


        ExecutorService ex = Executors.newFixedThreadPool(SystemInfo.LogicalCores);
        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.001);
        ex.shutdownNow();

    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        MultivariateNormals instance = new MultivariateNormals();

        ClassificationDataSet train = FixedProblems.getCircles(1000, 3, 0.1, 1.0, 10.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, 3, 0.1, 1.0, 10.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.001);
    }

    @Test
    public void testClone() {
        System.out.println("clone");

        ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(1000, 3, RandomUtil.getRandom());
        ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(1000, 6, RandomUtil.getRandom());

        MultivariateNormals instance = new MultivariateNormals();

        instance = instance.clone();

        instance.train(t1);

        MultivariateNormals result = instance.clone();
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());
    }

}
