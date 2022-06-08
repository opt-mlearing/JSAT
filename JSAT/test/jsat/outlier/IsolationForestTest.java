package jsat.outlier;

import jsat.SimpleDataSet;
import jsat.distributions.Normal;
import jsat.utils.GridDataGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class IsolationForestTest {

    public IsolationForestTest() {
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
     * Test of fit method, of class LinearOCSVM.
     */
    @Test
    public void testFit() {
        System.out.println("fit");
        int N = 5000;
        SimpleDataSet trainData = new GridDataGenerator(new Normal(), 1, 1, 1).generateData(N);

        SimpleDataSet outlierData = new GridDataGenerator(new Normal(10, 1.0), 1, 1, 1).generateData(N);

        IsolationForest instance = new IsolationForest();

        instance.fit(trainData, false);

        double numOutliersInTrain = trainData.getDataPoints().stream().mapToDouble(instance::score).filter(x -> x < 0).count();
        assertEquals(0, numOutliersInTrain / trainData.size(), 0.05);//Better say something like 95% are inliers!

        double numOutliersInOutliers = outlierData.getDataPoints().stream().mapToDouble(instance::score).filter(x -> x < 0).count();
        assertEquals(1.0, numOutliersInOutliers / outlierData.size(), 0.1);//Better say 90% are outliers!
    }

}
