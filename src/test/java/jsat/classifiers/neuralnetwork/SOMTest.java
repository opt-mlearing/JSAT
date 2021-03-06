package jsat.classifiers.neuralnetwork;

import jsat.FixedProblems;
import jsat.classifiers.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class SOMTest {

    public SOMTest() {
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

        SOM instance = new SOM(5, 5);
        instance.setMaxIterations(200);

        ClassificationDataSet train = FixedProblems.getCircles(1000, 1.0, 10.0, 100.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, 1.0, 10.0, 100.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train, true);
        cme.evaluateTestSet(test);

        assertEquals(0, cme.getErrorRate(), 0.1);

    }

    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");


        SOM instance = new SOM(5, 5);
        instance.setMaxIterations(50);

        ClassificationDataSet train = FixedProblems.getCircles(1000, 1.0, 10.0, 100.0);
        ClassificationDataSet test = FixedProblems.getCircles(100, 1.0, 10.0, 100.0);

        ClassificationModelEvaluation cme = new ClassificationModelEvaluation(instance, train);
        cme.evaluateTestSet(test);

        assertEquals(0, cme.getErrorRate(), 0.1);

    }

    @Test
    public void testClone() {
        System.out.println("clone");

        SOM instance = new SOM(5, 5);
        instance.setMaxIterations(50);

        ClassificationDataSet t1 = FixedProblems.getSimpleKClassLinear(5000, 3);
        ClassificationDataSet t2 = FixedProblems.getSimpleKClassLinear(5000, 6);

        instance = instance.clone();

        instance.train(t1);

        SOM result = instance.clone();
        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), result.classify(t1.getDataPoint(i)).mostLikely());
        result.train(t2);

        for (int i = 0; i < t1.size(); i++)
            assertEquals(t1.getDataPointCategory(i), instance.classify(t1.getDataPoint(i)).mostLikely());

        for (int i = 0; i < t2.size(); i++)
            assertEquals(t2.getDataPointCategory(i), result.classify(t2.getDataPoint(i)).mostLikely());

    }

}
