package jsat.classifiers.boosting;

import jsat.FixedProblems;
import jsat.classifiers.*;
import jsat.classifiers.trees.*;
import jsat.datatransform.LinearTransform;
import jsat.regression.RegressionDataSet;
import jsat.regression.RegressionModelEvaluation;
import jsat.regression.Regressor;
import jsat.utils.random.RandomUtil;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class BaggingTest {

    public BaggingTest() {
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

        Bagging instance = new Bagging((Regressor) new DecisionTree());

        RegressionDataSet train = FixedProblems.getLinearRegression(5000, RandomUtil.getRandom());
        RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.5);

    }

    @Test
    public void testTrainC_RegressionDataSet_ExecutorService() {
        System.out.println("train");

        Bagging instance = new Bagging((Regressor) new DecisionTree());

        RegressionDataSet train = FixedProblems.getLinearRegression(5000, RandomUtil.getRandom());
        RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train, true);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.5);
    }


    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");

        Bagging instance = new Bagging((Classifier) new DecisionTree());

        ClassificationDataSet train = FixedProblems.getCircles(5000, .1, 10.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, .1, 10.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
        cme.evaluateTestSet(test);


        assertTrue(cme.getErrorRate() <= 0.05);

    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");

        Bagging instance = new Bagging((Classifier) new DecisionTree());

        ClassificationDataSet train = FixedProblems.getCircles(5000, .1, 10.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, .1, 10.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.05);

    }

    @Test
    public void testClone() {
        System.out.println("clone");

        Bagging instance = new Bagging((Classifier) new DecisionTree());

        ClassificationDataSet t1 = FixedProblems.getCircles(1000, 0.1, 10.0);
        ClassificationDataSet t2 = FixedProblems.getCircles(1000, 0.1, 10.0);

        t2.applyTransform(new LinearTransform(t2));

        int errors;

        instance = instance.clone();

        instance.train(t1);

        Bagging result = instance.clone();

        errors = 0;
        for (int i = 0; i < t1.size(); i++)
            errors += Math.abs(t1.getDataPointCategory(i) - result.classify(t1.getDataPoint(i)).mostLikely());
        assertTrue(errors < 100);
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            errors += Math.abs(t1.getDataPointCategory(i) - instance.classify(t1.getDataPoint(i)).mostLikely());
        assertTrue(errors < 100);

        for (int i = 0; i < t2.size(); i++)
            errors += Math.abs(t2.getDataPointCategory(i) - result.classify(t2.getDataPoint(i)).mostLikely());
        assertTrue(errors < 100);
    }

}
