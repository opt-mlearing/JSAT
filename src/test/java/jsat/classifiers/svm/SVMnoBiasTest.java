package jsat.classifiers.svm;

import java.util.Random;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.distributions.kernels.RBFKernel;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import jsat.utils.random.RandomUtil;

/**
 * @author Edward Raff
 */
@Slf4j
public class SVMnoBiasTest {

    public SVMnoBiasTest() {
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
    public void testTrainC_ClassificationDataSet_1_ExecutorService() {
        ClassificationDataSet trainData = FixedProblems.getInnerOuterCircle(900, new Random(1234L));
        ClassificationDataSet testData = FixedProblems.getInnerOuterCircle(100, new Random(4321L));
        for (SupportVectorLearner.CacheMode cacheMode : SupportVectorLearner.CacheMode.values()) {
            SVMnoBias classifier = new SVMnoBias(new RBFKernel(0.5));
            classifier.setCacheMode(cacheMode);
            classifier.setC(10);
            classifier.train(trainData, true);
            for (int i = 0; i < testData.size(); i++) {
                Assert.assertEquals(testData.getDataPointCategory(i),
                        classifier.classify(testData.getDataPoint(i)).mostLikely());
            }
        }
    }

    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        log.info("trainC");
        ClassificationDataSet trainSet = FixedProblems.getInnerOuterCircle(150, new Random(2));
        ClassificationDataSet testSet = FixedProblems.getInnerOuterCircle(50, new Random(3));
        for (SupportVectorLearner.CacheMode cacheMode : SupportVectorLearner.CacheMode.values()) {
            SVMnoBias classifier = new SVMnoBias(new RBFKernel(0.5));
            classifier.setCacheMode(cacheMode);
            classifier.setC(10);
            classifier.train(trainSet, true);
            for (int i = 0; i < testSet.size(); i++) {
                Assert.assertEquals(testSet.getDataPointCategory(i),
                        classifier.classify(testSet.getDataPoint(i)).mostLikely());
            }
        }
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        log.info("trainC");
        ClassificationDataSet trainSet = FixedProblems.getInnerOuterCircle(150, new Random(2));
        ClassificationDataSet testSet = FixedProblems.getInnerOuterCircle(50, new Random(3));
        for (SupportVectorLearner.CacheMode cacheMode : SupportVectorLearner.CacheMode.values()) {
            SVMnoBias classifier = new SVMnoBias(new RBFKernel(0.5));
            classifier.setCacheMode(cacheMode);
            classifier.setC(10);
            classifier.train(trainSet);
            for (int i = 0; i < testSet.size(); i++) {
                Assert.assertEquals(testSet.getDataPointCategory(i),
                        classifier.classify(testSet.getDataPoint(i)).mostLikely());
            }
            //test warm start off corrupted solution
            double[] a = classifier.alphas;
            Random rand = RandomUtil.getRandom();
            for (int i = 0; i < a.length; i++) {
                a[i] = Math.min(Math.max(a[i] + rand.nextDouble() * 2 - 1, 0), 10);
            }
            SVMnoBias classifier2 = new SVMnoBias(new RBFKernel(0.5));
            classifier2.setCacheMode(cacheMode);
            classifier2.setC(10);
            classifier2.train(trainSet, a);
            for (int i = 0; i < testSet.size(); i++) {
                Assert.assertEquals(testSet.getDataPointCategory(i),
                        classifier2.classify(testSet.getDataPoint(i)).mostLikely());
            }
        }

    }

}
