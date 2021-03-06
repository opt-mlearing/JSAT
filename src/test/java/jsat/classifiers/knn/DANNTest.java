package jsat.classifiers.knn;

import java.util.Random;

import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.distributions.Normal;
import jsat.utils.GridDataGenerator;
import org.junit.After;
import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Edward Raff
 */
public class DANNTest {
    static private ClassificationDataSet easyTrain;
    static private ClassificationDataSet easyTest;
    static private DANN dann;

    public DANNTest() {
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
        dann = new DANN(20, 1);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        dann.train(easyTrain);
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), dann.classify(easyTest.getDataPoint(i)).mostLikely());
    }

    @Test
    public void testClone() {
        System.out.println("clone");
        dann.train(easyTrain);
        Classifier clone = dann.clone();
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), clone.classify(easyTest.getDataPoint(i)).mostLikely());
    }

    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");
        dann.train(easyTrain, true);
        for (int i = 0; i < easyTest.size(); i++)
            assertEquals(easyTest.getDataPointCategory(i), dann.classify(easyTest.getDataPoint(i)).mostLikely());
    }
}
