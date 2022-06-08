package jsat.classifiers.neuralnetwork;

import jsat.FixedProblems;
import jsat.classifiers.*;
import jsat.linear.distancemetrics.EuclideanDistance;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class LVQTest {

    public LVQTest() {
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
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");

        for (LVQ.LVQVersion method : LVQ.LVQVersion.values()) {
            LVQ instance = new LVQ(new EuclideanDistance(), 5);
            instance.setRepresentativesPerClass(20);
            instance.setLVQMethod(method);

            for (int trials = 0; trials < max_trials; trials++) {
                ClassificationDataSet train = FixedProblems.getCircles(1000, 1.0, 10.0, 100.0);
                ClassificationDataSet test = FixedProblems.getCircles(100, 1.0, 10.0, 100.0);

                ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
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


        for (LVQ.LVQVersion method : LVQ.LVQVersion.values()) {
            LVQ instance = new LVQ(new EuclideanDistance(), 5);
            instance.setRepresentativesPerClass(20);
            instance.setLVQMethod(method);
            for (int trials = 0; trials < max_trials; trials++) {
                ClassificationDataSet train = FixedProblems.getCircles(1000, 1.0, 10.0, 100.0);
                ClassificationDataSet test = FixedProblems.getCircles(100, 1.0, 10.0, 100.0);

                ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
                cme.evaluateTestSet(test);

                if (cme.getErrorRate() > 0.001 && trials == max_trials)//wrong too many times, something is broken
                    assertEquals(cme.getErrorRate(), 0.0, 0.001);
                else
                    break;//did good
            }
        }
    }

    @Test
    public void testClone() {
        System.out.println("clone");

        LVQ instance = new LVQ(new EuclideanDistance(), 5);

        ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(100, 3);
        ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(100, 6);

        instance = instance.clone();

        instance.train(t1);

        LVQ result = instance.clone();
        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());

    }

}
