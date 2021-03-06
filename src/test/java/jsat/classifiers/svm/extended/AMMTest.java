package jsat.classifiers.svm.extended;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
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
public class AMMTest {

    public AMMTest() {
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

    /**
     * Test of getSubEpochs method, of class AMM.
     */
    @Test
    public void testSubEpochs() {
        System.out.println("getSubEpochs");
        AMM instance = new AMM();

        instance.setSubEpochs(10);
        assertEquals(10, instance.getSubEpochs());

        for (int i = -3; i < 1; i++)
            try {
                instance.setSubEpochs(i);
                fail("Invalid value should have thrown an error");
            } catch (Exception ex) {

            }
    }

    /**
     * Test of train method, of class AMM.
     */
    @Test
    public void testTrainC_ClassificationDataSet() {
        //Hard to come up witha  good test problem for AMM, since it works better on higher dim problems
        System.out.println("trainC");
        AMM instance = new AMM();

        ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(10000, 3, RandomUtil.getRandom());
        ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(1000, 3, RandomUtil.getRandom());

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.001);
    }

    /**
     * Test of clone method, of class AMM.
     */
    @Test
    public void testClone() {
        System.out.println("clone");

        ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(10000, 3, RandomUtil.getRandom());
        ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(10000, 6, RandomUtil.getRandom());

        AMM instance = new AMM();

        instance = instance.clone();

        instance.train(t1);

        AMM result = instance.clone();
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());
    }

}
