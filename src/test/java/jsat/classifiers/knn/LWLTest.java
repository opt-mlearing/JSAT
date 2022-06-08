package jsat.classifiers.knn;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.ClassificationModelEvaluation;
import jsat.classifiers.bayesian.NaiveBayesUpdateable;
import jsat.classifiers.svm.DCDs;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.regression.*;
import jsat.utils.random.RandomUtil;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class LWLTest {

    public LWLTest() {
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

        LWL instance = new LWL((Regressor) new DCDs(), 30, new EuclideanDistance());

        RegressionDataSet train = FixedProblems.getLinearRegression(5000, RandomUtil.getRandom());
        RegressionDataSet test = FixedProblems.getLinearRegression(200, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.3);

    }

    @Test
    public void testTrainC_RegressionDataSet_ExecutorService() {
        System.out.println("train");

        LWL instance = new LWL((Regressor) new DCDs(), 15, new EuclideanDistance());

        RegressionDataSet train = FixedProblems.getLinearRegression(5000, RandomUtil.getRandom());
        RegressionDataSet test = FixedProblems.getLinearRegression(200, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train, true);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.3);

    }

    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");

        LWL instance = new LWL(new NaiveBayesUpdateable(), 15, new EuclideanDistance());

        ClassificationDataSet train = FixedProblems.getCircles(5000, 1.0, 10.0, 100.0);
        ClassificationDataSet test = FixedProblems.getCircles(200, 1.0, 10.0, 100.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.001);
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");

        LWL instance = new LWL(new NaiveBayesUpdateable(), 15, new EuclideanDistance());

        ClassificationDataSet train = FixedProblems.getCircles(5000, 1.0, 10.0, 100.0);
        ClassificationDataSet test = FixedProblems.getCircles(200, 1.0, 10.0, 100.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.001);

    }

    @Test
    public void testClone() {
        System.out.println("clone");

        LWL instance = new LWL(new NaiveBayesUpdateable(), 15, new EuclideanDistance());

        ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(100, 3);
        ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(100, 6);

        instance = instance.clone();

        instance.train(t1);

        LWL result = instance.clone();
        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());

    }

}
