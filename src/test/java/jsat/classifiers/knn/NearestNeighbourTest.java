package jsat.classifiers.knn;

import java.util.Random;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.distributions.Normal;
import jsat.regression.RegressionDataSet;
import jsat.regression.RegressionModelEvaluation;
import jsat.utils.GridDataGenerator;
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
public class NearestNeighbourTest {
    static private ClassificationDataSet easyTrain;
    static private ClassificationDataSet easyTest;
    static private NearestNeighbour nn;
    static private NearestNeighbour nn2;

    public NearestNeighbourTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        GridDataGenerator gdg = new GridDataGenerator(new Normal(0, 0.05), new Random(12), 2);
        easyTrain = new ClassificationDataSet(gdg.generateData(80).getList(), 0);
        easyTest = new ClassificationDataSet(gdg.generateData(40).getList(), 0);
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        nn = new NearestNeighbour(1, false);
        nn2 = new NearestNeighbour(1, true);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTrainC_RegressionDataSet() {
        System.out.println("train");

        NearestNeighbour instance = new NearestNeighbour(7);

        RegressionDataSet train = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom());
        RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.5);

        //test weighted instances
        instance = new NearestNeighbour(7, true);

        rme = new RegressionModelEvaluation(instance, train);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.5);
    }

    @Test
    public void testTrainC_RegressionDataSet_ExecutorService() {
        System.out.println("train");

        NearestNeighbour instance = new NearestNeighbour(7);

        RegressionDataSet train = FixedProblems.getLinearRegression(1000, RandomUtil.getRandom());
        RegressionDataSet test = FixedProblems.getLinearRegression(100, RandomUtil.getRandom());

        RegressionModelEvaluation rme = new RegressionModelEvaluation(instance, train, true);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.5);

        //test weighted instances
        instance = new NearestNeighbour(7, true);

        rme = new RegressionModelEvaluation(instance, train, true);
        rme.evaluateTestSet(test);

        assertTrue(rme.getMeanError() <= test.getTargetValues().mean() * 0.5);
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        nn.train(easyTrain);
        nn2.train(easyTrain);
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), nn.classify(easyTest.getDataPoint(i)).mostLikely());
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), nn2.classify(easyTest.getDataPoint(i)).mostLikely());
    }

    @Test
    public void testClone() {
        System.out.println("clone");
        nn.train(easyTrain);
        nn2.train(easyTrain);
        Classifier clone = nn.clone();
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), clone.classify(easyTest.getDataPoint(i)).mostLikely());
        clone = nn2.clone();
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), clone.classify(easyTest.getDataPoint(i)).mostLikely());
    }

    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");
        nn.train(easyTrain, true);
        nn2.train(easyTrain, true);
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), nn.classify(easyTest.getDataPoint(i)).mostLikely());
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), nn2.classify(easyTest.getDataPoint(i)).mostLikely());
    }
}
