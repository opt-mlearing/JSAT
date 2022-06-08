package jsat.clustering.evaluation;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.linear.Vec;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class HomogeneityTest {

    public HomogeneityTest() {
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
     * Test of evaluate method, of class AdjustedRandIndex.
     */
    @Test
    public void testEvaluate_intArr_DataSet() {
        System.out.println("evaluate");
        ClassificationDataSet cds = new ClassificationDataSet(1, new CategoricalData[0], new CategoricalData(2));
        for (int i = 0; i < 2; i++)
            cds.addDataPoint(Vec.random(1), new int[0], 0);
        for (int i = 0; i < 2; i++)
            cds.addDataPoint(Vec.random(1), new int[0], 1);
        //class labels are now [0, 0, 1, 1]
        int[] d = new int[4];
        d[0] = d[1] = 1;
        d[2] = d[3] = 0;


        Homogeneity eval = new Homogeneity();

        double score = eval.naturalScore(eval.evaluate(d, cds));
        assertEquals(1.0, score, 0.005);

        d[1] = 2;
        d[3] = 3;

        score = eval.naturalScore(eval.evaluate(d, cds));
        assertEquals(1.0, score, 0.005);

        d[0] = d[2] = 0;
        d[1] = d[3] = 1;

        score = eval.naturalScore(eval.evaluate(d, cds));
        assertEquals(0.0, score, 0.005);

        d[0] = d[1] = d[2] = d[3] = 0;

        score = eval.naturalScore(eval.evaluate(d, cds));
        assertEquals(0.0, score, 0.005);
    }

}
