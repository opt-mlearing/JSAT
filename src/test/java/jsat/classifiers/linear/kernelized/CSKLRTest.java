package jsat.classifiers.linear.kernelized;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.distributions.kernels.RBFKernel;
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
public class CSKLRTest {

    public CSKLRTest() {
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

        for (CSKLR.UpdateMode mode : CSKLR.UpdateMode.values()) {
            CSKLR instance = new CSKLR(0.5, new RBFKernel(0.5), 10, mode);
            instance.setMode(mode);

            ClassificationDataSet train = FixedProblems.getInnerOuterCircle(200, RandomUtil.getRandom());
            ClassificationDataSet test = FixedProblems.getInnerOuterCircle(100, RandomUtil.getRandom());

            ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
            cme.evaluateTestSet(test);

            assertEquals(0, cme.getErrorRate(), 0.0);
        }

    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");

        for (CSKLR.UpdateMode mode : CSKLR.UpdateMode.values()) {
            CSKLR instance = new CSKLR(0.5, new RBFKernel(0.5), 10, mode);

            ClassificationDataSet train = FixedProblems.getInnerOuterCircle(200, RandomUtil.getRandom());
            ClassificationDataSet test = FixedProblems.getInnerOuterCircle(100, RandomUtil.getRandom());

            ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
            cme.evaluateTestSet(test);

            assertEquals(0, cme.getErrorRate(), 0.0);
        }

    }

    @Test
    public void testClone() {
        System.out.println("clone");

        CSKLR instance = new CSKLR(0.5, new RBFKernel(0.5), 10, CSKLR.UpdateMode.MARGIN);

        ClassificationDataSet t1 = FixedProblems.getInnerOuterCircle(500, RandomUtil.getRandom());
        ClassificationDataSet t2 = FixedProblems.getInnerOuterCircle(500, RandomUtil.getRandom(), 2.0, 10.0);

        instance = instance.clone();

        instance.train(t1);

        CSKLR result = instance.clone();
        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());

    }

}
