package jsat.classifiers;

import jsat.FixedProblems;
import jsat.classifiers.svm.DCDs;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class OneVSAllTest {

    public OneVSAllTest() {
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
        for (boolean conc : new boolean[]{true, false}) {
            OneVSAll instance = new OneVSAll(new DCDs(), conc);

            ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(1000, 7);
            ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(100, 7);

            ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
            cme.evaluateTestSet(test);

            assertTrue(cme.getErrorRate() <= 0.001);
        }
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        for (boolean conc : new boolean[]{true, false}) {
            OneVSAll instance = new OneVSAll(new DCDs(), conc);

            ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(1000, 7);
            ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(100, 7);

            ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
            cme.evaluateTestSet(test);

            assertTrue(cme.getErrorRate() <= 0.001);
        }
    }

    @Test
    public void testClone() {
        System.out.println("clone");

        ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(1000, 7);
        ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(1000, 9);

        OneVSAll instance = new OneVSAll(new DCDs());

        instance = instance.clone();

        instance.train(t1);

        OneVSAll result = instance.clone();
        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());
    }

}
