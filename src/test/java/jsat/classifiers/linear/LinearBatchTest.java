package jsat.classifiers.linear;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPointPair;
import jsat.datatransform.LinearTransform;
import jsat.lossfunctions.*;
import jsat.math.OnLineStatistics;
import jsat.math.optimization.LBFGS;
import jsat.math.optimization.WolfeNWLineSearch;
import jsat.regression.RegressionDataSet;
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
public class LinearBatchTest {
    public LinearBatchTest() {
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
    public void testClassifyBinary() {
        System.out.println("binary classifiation");

        for (boolean useBias : new boolean[]{false, true}) {
            LinearBatch linearBatch = new LinearBatch(new HingeLoss(), 1e-4);

            ClassificationDataSet train = FixedProblems.get2ClassLinear(500, RandomUtil.getRandom());

            linearBatch.setUseBiasTerm(useBias);
            linearBatch.train(train);

            ClassificationDataSet test = FixedProblems.get2ClassLinear(200, RandomUtil.getRandom());

            for (DataPointPair<Integer> dpp : test.getAsDPPList())
                assertEquals(dpp.getPair().longValue(), linearBatch.classify(dpp.getDataPoint()).mostLikely());
        }
    }

    @Test()
    public void testTrainWarmCFast() {
        ClassificationDataSet train = FixedProblems.get2ClassLinear(10000, RandomUtil.getRandom());

        LinearSGD warmModel = new LinearSGD(new SoftmaxLoss(), 1e-4, 0);
        warmModel.setEpochs(20);
        warmModel.train(train);


        long start, end;


        LinearBatch notWarm = new LinearBatch(new SoftmaxLoss(), 1e-4);

        start = System.currentTimeMillis();
        notWarm.train(train);
        end = System.currentTimeMillis();
        long normTime = (end - start);


        LinearBatch warm = new LinearBatch(new SoftmaxLoss(), 1e-4);

        start = System.currentTimeMillis();
        warm.train(train, warmModel);
        end = System.currentTimeMillis();
        long warmTime = (end - start);

        assertTrue("Warm start wasn't faster? " + warmTime + " vs " + normTime, warmTime < normTime * 0.95);
    }

    @Test
    public void testClassifyBinaryMT() {
        System.out.println("binary classifiation MT");

        for (boolean useBias : new boolean[]{false, true}) {
            LinearBatch linearBatch = new LinearBatch(new LogisticLoss(), 1e-4);

            ClassificationDataSet train = FixedProblems.get2ClassLinear(500, RandomUtil.getRandom());

            linearBatch.setUseBiasTerm(useBias);
            linearBatch.train(train, true);

            ClassificationDataSet test = FixedProblems.get2ClassLinear(200, RandomUtil.getRandom());

            for (DataPointPair<Integer> dpp : test.getAsDPPList())
                assertEquals(dpp.getPair().longValue(), linearBatch.classify(dpp.getDataPoint()).mostLikely());
        }
    }

    @Test
    public void testClassifyMulti() {
        System.out.println("multi class classification");
        for (boolean useBias : new boolean[]{false, true}) {
            LinearBatch linearBatch = new LinearBatch(new HingeLoss(), 1e-4);

            ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(500, 6, RandomUtil.getRandom());

            linearBatch.setUseBiasTerm(useBias);
            linearBatch.train(train);

            ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(200, 6, RandomUtil.getRandom());

            for (DataPointPair<Integer> dpp : test.getAsDPPList())
                assertEquals(dpp.getPair().longValue(), linearBatch.classify(dpp.getDataPoint()).mostLikely());
        }
    }

    @Test()
    public void testTrainWarmCMultieFast() {
        System.out.println("testTrainWarmCMultieFast");
        ClassificationDataSet train = FixedProblems.getHalfCircles(1000, RandomUtil.getRandom(), 0.1, 1.0, 5.0);

        LinearBatch warmModel = new LinearBatch(new HingeLoss(), 1e-2);
        warmModel.train(train);

        LinearBatch notWarm = new LinearBatch(new SoftmaxLoss(), 1e-2);
        notWarm.train(train);


        LinearBatch warm = new LinearBatch(new SoftmaxLoss(), 1e-2);
        warm.train(train, warmModel);

        int origErrors = 0;
        for (int i = 0; i < train.size(); i++)
            if (notWarm.classify(train.getDataPoint(i)).mostLikely() != train.getDataPointCategory(i)) origErrors++;
        int warmErrors = 0;
        for (int i = 0; i < train.size(); i++)
            if (warm.classify(train.getDataPoint(i)).mostLikely() != train.getDataPointCategory(i)) warmErrors++;

        assertTrue("Warm was less acurate? " + warmErrors + " vs " + origErrors, warmErrors <= origErrors * 1.15);
    }

    @Test
    public void testClassifyMultiMT() {
        System.out.println("multi class classification MT");

        for (boolean useBias : new boolean[]{false, true}) {
            LinearBatch linearBatch = new LinearBatch(new SoftmaxLoss(), 1e-4);

            ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(500, 6, RandomUtil.getRandom());

            linearBatch.setUseBiasTerm(useBias);
            linearBatch.train(train, true);

            ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(200, 6, RandomUtil.getRandom());

            for (DataPointPair<Integer> dpp : test.getAsDPPList())
                assertEquals(dpp.getPair().longValue(), linearBatch.classify(dpp.getDataPoint()).mostLikely());
        }
    }

    @Test
    public void testRegression() {
        System.out.println("regression");


        for (boolean useBias : new boolean[]{false, true}) {
            LinearBatch linearBatch = new LinearBatch(new SquaredLoss(), 1e-4);
            RegressionDataSet train = FixedProblems.getLinearRegression(500, RandomUtil.getRandom());

            linearBatch.setUseBiasTerm(useBias);

            linearBatch.train(train);

            RegressionDataSet test = FixedProblems.getLinearRegression(200, RandomUtil.getRandom());

            for (DataPointPair<Double> dpp : test.getAsDPPList()) {
                double truth = dpp.getPair();
                double pred = linearBatch.regress(dpp.getDataPoint());
                double relErr = (truth - pred) / truth;
                assertEquals(0, relErr, 0.1);
            }
        }
    }

    @Test
    public void testRegressionMT() {
        System.out.println("regression MT");
        for (boolean useBias : new boolean[]{false, true}) {
            LinearBatch linearBatch = new LinearBatch(new SquaredLoss(), 1e-4);
            RegressionDataSet train = FixedProblems.getLinearRegression(500, RandomUtil.getRandom());

            linearBatch.setOptimizer(new LBFGS(10, 10000, new WolfeNWLineSearch()));
            linearBatch.setTolerance(1e-4);
            linearBatch.setUseBiasTerm(useBias);
            linearBatch.train(train, true);

            RegressionDataSet test = FixedProblems.getLinearRegression(200, RandomUtil.getRandom());

            OnLineStatistics avgRelErr = new OnLineStatistics();
            for (DataPointPair<Double> dpp : test.getAsDPPList()) {
                double truth = dpp.getPair();
                double pred = linearBatch.regress(dpp.getDataPoint());
                double relErr = (truth - pred) / truth;
                avgRelErr.add(relErr);
            }
            assertEquals(0, avgRelErr.getMean(), 0.05);
        }
    }

    @Test()
    public void testTrainWarmRFast() {
        RegressionDataSet train = FixedProblems.getLinearRegression(100000, RandomUtil.getRandom());
        train.applyTransform(new LinearTransform(train));//make this range better for convergence check

        LinearBatch warmModel = new LinearBatch(new SquaredLoss(), 1e-4);
        warmModel.train(train);


        long start, end;


        LinearBatch notWarm = new LinearBatch(new SquaredLoss(), 1e-4);

        start = System.currentTimeMillis();
        notWarm.train(train);
        end = System.currentTimeMillis();
        long normTime = (end - start);


        LinearBatch warm = new LinearBatch(new SquaredLoss(), 1e-4);

        start = System.currentTimeMillis();
        warm.train(train, warmModel);
        end = System.currentTimeMillis();
        long warmTime = (end - start);

        assertTrue("Warm start slower? " + warmTime + " vs " + normTime, warmTime <= normTime * 1.05);
        assertTrue(warm.getRawWeight(0).equals(notWarm.getRawWeight(0), 1e-2));
    }
}
