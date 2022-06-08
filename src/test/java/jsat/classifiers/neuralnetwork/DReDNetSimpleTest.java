package jsat.classifiers.neuralnetwork;

import jsat.FixedProblems;
import jsat.TestTools;
import jsat.classifiers.ClassificationDataSet;
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
public class DReDNetSimpleTest {
    /*
     * RBF is a bit heuristic and works best with more data - so the training set size is enlarged
     */

    public DReDNetSimpleTest() {
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
     * Test of train method, of class DReDNetSimple.
     */
    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");
        ClassificationDataSet trainSet = FixedProblems.getInnerOuterCircle(2000, RandomUtil.getRandom());
        ClassificationDataSet testSet = FixedProblems.getInnerOuterCircle(200, RandomUtil.getRandom());

        DReDNetSimple net = new DReDNetSimple(500).clone();
        net.setEpochs(20);
        net.train(trainSet, true);

        net = net.clone();
        for (int i = 0; i < testSet.size(); i++)
            assertEquals(testSet.getDataPointCategory(i), net.classify(testSet.getDataPoint(i)).mostLikely());

    }

    /**
     * Test of train method, of class DReDNetSimple.
     */
    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        ClassificationDataSet trainSet = FixedProblems.getInnerOuterCircle(2000, RandomUtil.getRandom());
        ClassificationDataSet testSet = FixedProblems.getInnerOuterCircle(200, RandomUtil.getRandom());


        DReDNetSimple net = new DReDNetSimple(500).clone();
        net.setEpochs(20);
        //serialization check
        net = TestTools.deepCopy(net);
        net.train(trainSet);

        net = net.clone();
        for (int i = 0; i < testSet.size(); i++)
            assertEquals(testSet.getDataPointCategory(i), net.classify(testSet.getDataPoint(i)).mostLikely());
        //serialization check
        net = TestTools.deepCopy(net);
        for (int i = 0; i < testSet.size(); i++)
            assertEquals(testSet.getDataPointCategory(i), net.classify(testSet.getDataPoint(i)).mostLikely());
    }


}
