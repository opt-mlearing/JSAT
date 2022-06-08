package jsat.datatransform;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import jsat.SimpleDataSet;
import jsat.distributions.Normal;
import jsat.utils.GridDataGenerator;
import jsat.utils.random.RandomUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class ImputerTest {

    public ImputerTest() {
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
     * Test of fit method, of class Imputer.
     */
    @Test
    public void testFit() {
        System.out.println("fit");
        GridDataGenerator gdg = new GridDataGenerator(new Normal(0.0, 0.5), RandomUtil.getRandom(), 1, 1, 1, 1);

        SimpleDataSet data = gdg.generateData(10000);
        //remove class label feature
        data.applyTransform(new RemoveAttributeTransform(data, new HashSet<Integer>(Arrays.asList(0)), Collections.EMPTY_SET));


        //true mean and median should be 0
        data.applyTransform(new InsertMissingValuesTransform(0.1));

        Imputer imputer = new Imputer(data, Imputer.NumericImputionMode.MEAN);
        for (int i = 0; i < data.getNumNumericalVars(); i++)
            assertEquals(0.0, imputer.numeric_imputs[i], 0.25);

        imputer = new Imputer(data, Imputer.NumericImputionMode.MEDIAN);
        for (int i = 0; i < data.getNumNumericalVars(); i++)
            assertEquals(0.0, imputer.numeric_imputs[i], 0.25);
        imputer = imputer.clone();
        for (int i = 0; i < data.getNumNumericalVars(); i++)
            assertEquals(0.0, imputer.numeric_imputs[i], 0.25);


        data.applyTransform(imputer);
        assertEquals(0, data.countMissingValues());


        //test categorical features 
        data = gdg.generateData(10000);
        //remove class label feature
        data.applyTransform(new RemoveAttributeTransform(data, new HashSet<Integer>(Arrays.asList(0)), Collections.EMPTY_SET));
        //breaking into 3 even sized bins, so the middle bin, indx 1, should be the mode
        data.applyTransform(new NumericalToHistogram(data, 3));
        data.applyTransform(new InsertMissingValuesTransform(0.1));
        imputer.fit(data);
        for (int i = 0; i < data.getNumCategoricalVars(); i++)
            assertEquals(1, imputer.cat_imputs[i]);

        imputer = imputer.clone();
        for (int i = 0; i < data.getNumCategoricalVars(); i++)
            assertEquals(1, imputer.cat_imputs[i]);


    }
}
