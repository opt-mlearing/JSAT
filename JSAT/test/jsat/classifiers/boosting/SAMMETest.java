package jsat.classifiers.boosting;

import jsat.FixedProblems;
import jsat.classifiers.*;
import jsat.classifiers.trees.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class SAMMETest {

    public SAMMETest() {
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

        SAMME instance = new SAMME(new DecisionTree(2, 2, TreePruner.PruningMethod.NONE, 0.1), 50);

        ClassificationDataSet train = FixedProblems.getCircles(1000, .1, 10.0, 100.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, .1, 10.0, 100.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.15);
    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");

        SAMME instance = new SAMME(new DecisionTree(2, 2, TreePruner.PruningMethod.NONE, 0.1), 50);

        ClassificationDataSet train = FixedProblems.getCircles(1000, .1, 10.0, 100.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, .1, 10.0, 100.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertTrue(cme.getErrorRate() <= 0.15);

    }

    @Test
    public void testClone() {
        System.out.println("clone");

        SAMME instance = new SAMME(new DecisionTree(10, 10, TreePruner.PruningMethod.NONE, 0.1), 50);

        ClassificationDataSet t1 = FixedProblems.getCircles(1000, 0.1, 10.0, 100.0);
        ClassificationDataSet t2 = FixedProblems.getCircles(1000, 0.1, 10.0);

        int errors;

        instance = instance.clone();

        instance.train(t1);

        SAMME result = instance.clone();

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
