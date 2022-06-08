package jsat.datatransform.kernel;

import jsat.FixedProblems;
import jsat.classifiers.*;
import jsat.classifiers.svm.DCDs;
import jsat.datatransform.DataModelPipeline;
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
public class KernelPCATest {
    //Test uses Transform to solve a problem that is not linearly seprable in the original space
    public KernelPCATest() {
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

        for (Nystrom.SamplingMethod sampMethod : Nystrom.SamplingMethod.values()) {
            DataModelPipeline instance = new DataModelPipeline((Classifier) new DCDs(), new KernelPCA(new RBFKernel(0.5), 20, 100, sampMethod));

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

        for (Nystrom.SamplingMethod sampMethod : Nystrom.SamplingMethod.values()) {
            DataModelPipeline instance = new DataModelPipeline((Classifier) new DCDs(), new KernelPCA(new RBFKernel(0.5), 20, 100, sampMethod));

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

        DataModelPipeline instance = new DataModelPipeline((Classifier) new DCDs(), new KernelPCA(new RBFKernel(0.5), 20, 100, Nystrom.SamplingMethod.KMEANS));

        ClassificationDataSet t1 = FixedProblems.getCircles(500, 0.0, RandomUtil.getRandom(), 1.0, 4.0);
        ClassificationDataSet t2 = FixedProblems.getCircles(500, 0.0, RandomUtil.getRandom(), 2.0, 10.0);

        instance = instance.clone();

        instance.train(t1);


        DataModelPipeline result = instance.clone();

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());

    }

}
