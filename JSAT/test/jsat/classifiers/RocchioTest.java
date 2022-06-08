package jsat.classifiers;

import jsat.FixedProblems;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class RocchioTest {

    public RocchioTest() {
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
        Rocchio instance = new Rocchio();

        ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(10000, 3);
        ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(1000, 3);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.001);
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        Rocchio instance = new Rocchio();

        ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(10000, 3);
        ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(1000, 3);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.001);
    }

    @Test
    public void testClone() {
        System.out.println("clone");

        ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(10000, 3);
        ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(10000, 6);

        Rocchio instance = new Rocchio();

        instance = instance.clone();

        instance.train(t1);

        Rocchio result = instance.clone();
        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());
    }

}
