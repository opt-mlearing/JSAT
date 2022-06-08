package jsat.classifiers.imbalance;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPointPair;
import jsat.classifiers.linear.LogisticRegressionDCD;
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
public class SMOTETest {
    public SMOTETest() {
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
        ClassificationDataSet train = FixedProblems.get2ClassLinear(200, 20, RandomUtil.getRandom());

        SMOTE smote = new SMOTE(new LogisticRegressionDCD());
        smote.train(train, true);

        ClassificationDataSet test = FixedProblems.get2ClassLinear(200, 200, RandomUtil.getRandom());

        for (DataPointPair<Integer> dpp : test.getAsDPPList())
            assertEquals(dpp.getPair().longValue(), smote.classify(dpp.getDataPoint()).mostLikely());

        smote = smote.clone();

        for (DataPointPair<Integer> dpp : test.getAsDPPList())
            assertEquals(dpp.getPair().longValue(), smote.classify(dpp.getDataPoint()).mostLikely());
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        ClassificationDataSet train = FixedProblems.get2ClassLinear(200, 20, RandomUtil.getRandom());

        SMOTE smote = new SMOTE(new LogisticRegressionDCD());
        smote.train(train);

        ClassificationDataSet test = FixedProblems.get2ClassLinear(200, 200, RandomUtil.getRandom());

        for (DataPointPair<Integer> dpp : test.getAsDPPList())
            assertEquals(dpp.getPair().longValue(), smote.classify(dpp.getDataPoint()).mostLikely());

        smote = smote.clone();

        for (DataPointPair<Integer> dpp : test.getAsDPPList())
            assertEquals(dpp.getPair().longValue(), smote.classify(dpp.getDataPoint()).mostLikely());
    }
}
