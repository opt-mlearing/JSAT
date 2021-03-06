package jsat.regression;

import jsat.FixedProblems;
import jsat.datatransform.LinearTransform;
import jsat.distributions.kernels.LinearKernel;
import jsat.linear.DenseVector;
import jsat.utils.random.RandomUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class RANSACTest {

    public RANSACTest() {
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

        RANSAC instance = new RANSAC(new KernelRLS(new LinearKernel(1), 1e-1), 10, 20, 40, 5);

        RegressionDataSet train = FixedProblems.getLinearRegression(500, RandomUtil.getRandom());
        for (int i = 0; i < 20; i++)
            train.addDataPoint(DenseVector.random(train.getNumNumericalVars()), train.getTargetValues().mean());
        RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.25);

    }

    @Test
    public void testTrainC_RegressionDataSet_ExecutorService() {
        System.out.println("train");

        RANSAC instance = new RANSAC(new KernelRLS(new LinearKernel(1), 1e-1), 10, 20, 40, 5);

        RegressionDataSet train = FixedProblems.getLinearRegression(500, RandomUtil.getRandom());
        for (int i = 0; i < 20; i++)
            train.addDataPoint(DenseVector.random(train.getNumNumericalVars()), train.getTargetValues().mean());
        RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train, true);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.25);
    }

    @Test
    public void testClone() {
        System.out.println("clone");

        RANSAC instance = new RANSAC(new KernelRLS(new LinearKernel(1), 1e-1), 10, 20, 40, 5);

        RegressionDataSet t1 = FixedProblems.getLinearRegression(500, RandomUtil.getRandom());
        for (int i = 0; i < 20; i++)
            t1.addDataPoint(DenseVector.random(t1.getNumNumericalVars()), t1.getTargetValues().mean());
        RegressionDataSet t2 = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());
        t2.applyTransform(new LinearTransform(t2, 1, 10));

        instance = instance.clone();

        instance.train(t1);

        RANSAC result = instance.clone();
        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getTargetValue(i), result.regress(t1.getDataPoint(i)), t1.getTargetValues().mean());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getTargetValue(i), instance.regress(t1.getDataPoint(i)), t1.getTargetValues().mean());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getTargetValue(i), result.regress(t2.getDataPoint(i)), t2.getTargetValues().mean() * 0.5);

    }

}
