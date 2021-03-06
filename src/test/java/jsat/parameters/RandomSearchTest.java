package jsat.parameters;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.Classifier;
import jsat.distributions.Uniform;
import jsat.distributions.discrete.UniformDiscrete;
import jsat.linear.DenseVector;
import jsat.parameters.GridSearchTest.DumbModel;
import jsat.regression.RegressionDataSet;
import jsat.regression.Regressor;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class RandomSearchTest {
    ClassificationDataSet classData;
    RegressionDataSet regData;

    public RandomSearchTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        classData = new ClassificationDataSet(1, new CategoricalData[0], new CategoricalData(2));
        for (int i = 0; i < 100; i++)
            classData.addDataPoint(DenseVector.toDenseVec(1.0 * i), 0);
        for (int i = 0; i < 100; i++)
            classData.addDataPoint(DenseVector.toDenseVec(-1.0 * i), 1);

        regData = new RegressionDataSet(1, new CategoricalData[0]);
        for (int i = 0; i < 100; i++)
            regData.addDataPoint(DenseVector.toDenseVec(1.0 * i), 0);
        for (int i = 0; i < 100; i++)
            regData.addDataPoint(DenseVector.toDenseVec(-1.0 * i), 1);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testClassification() {
        System.out.println("testClassification");
        RandomSearch instance = new RandomSearch((Classifier) new DumbModel(), 5);

        instance.setTrials(5 * 5 * 5 * 5 * 5);

        instance.addParameter("Param1", new UniformDiscrete(0, 5));
        instance.addParameter("Param2", new Uniform(0.0, 5.0));
        instance.addParameter("Param3", new UniformDiscrete(0, 5));

        instance = instance.clone();
        instance.train(classData);
        instance = instance.clone();

        DumbModel model = (DumbModel) instance.getTrainedClassifier();
        assertEquals(1, model.param1);
        assertEquals(2, model.param2, 0.5);
        assertEquals(3, model.param3);
        assertFalse(model.wasWarmStarted);
    }

    @Test
    public void testClassificationAutoAdd() {
        System.out.println("testClassificationAutoAdd");
        RandomSearch instance = new RandomSearch((Classifier) new DumbModel(), 5);

        instance.setTrials(5 * 5 * 5 * 5 * 5);

        instance.autoAddParameters(classData);

        instance = instance.clone();
        instance.train(classData);
        instance = instance.clone();

        DumbModel model = (DumbModel) instance.getTrainedClassifier();
        assertEquals(1, model.param1);
        assertEquals(2, model.param2, 0.5);
        assertEquals(3, model.param3);
        assertFalse(model.wasWarmStarted);
    }

    @Test
    public void testClassificationEx() {
        System.out.println("testClassificationEx");
        RandomSearch instance = new RandomSearch((Classifier) new DumbModel(), 5);

        instance.setTrials(5 * 5 * 5 * 5 * 5);

        instance.addParameter("Param1", new UniformDiscrete(0, 5));
        instance.addParameter("Param2", new Uniform(0.0, 5.0));
        instance.addParameter("Param3", new UniformDiscrete(0, 5));

        instance = instance.clone();
        instance.train(classData, true);
        instance = instance.clone();

        DumbModel model = (DumbModel) instance.getTrainedClassifier();
        assertEquals(1, model.param1);
        assertEquals(2, model.param2, 0.5);
        assertEquals(3, model.param3);
        assertFalse(model.wasWarmStarted);
    }

    @Test
    public void testRegression() {
        System.out.println("testRegression");
        RandomSearch instance = new RandomSearch((Regressor) new DumbModel(), 5);
        instance.setTrials(5 * 5 * 5 * 5 * 5);

        instance.addParameter("Param1", new UniformDiscrete(0, 5));
        instance.addParameter("Param2", new Uniform(0.0, 5.0));
        instance.addParameter("Param3", new UniformDiscrete(0, 5));

        instance = instance.clone();
        instance.train(regData);
        instance = instance.clone();

        DumbModel model = (DumbModel) instance.getTrainedRegressor();
        assertEquals(1, model.param1);
        assertEquals(2, model.param2, 0.5);
        assertEquals(3, model.param3);
        assertFalse(model.wasWarmStarted);
    }

    @Test
    public void testRegressionEx() {
        System.out.println("testRegressionEx");
        RandomSearch instance = new RandomSearch((Regressor) new DumbModel(), 5);
        instance.setTrials(5 * 5 * 5 * 5 * 5);

        instance.addParameter("Param1", new UniformDiscrete(0, 5));
        instance.addParameter("Param2", new Uniform(0.0, 5.0));
        instance.addParameter("Param3", new UniformDiscrete(0, 5));

        instance = instance.clone();
        instance.train(regData, true);
        instance = instance.clone();

        DumbModel model = (DumbModel) instance.getTrainedRegressor();
        assertEquals(1, model.param1);
        assertEquals(2, model.param2, 0.5);
        assertEquals(3, model.param3);
        assertFalse(model.wasWarmStarted);
    }
}
