package jsat.math;

import java.util.Random;

import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.math.optimization.RosenbrockFunction;
import jsat.utils.random.RandomUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author edwardraff
 */
public class FunctionTest {

    public FunctionTest() {
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
     * Test of forwardDifference method, of class Function.
     */
    @Test
    public void testForwardDifference() {
        System.out.println("forwardDifference");
        RosenbrockFunction f = new RosenbrockFunction();
        FunctionVec trueDeriv = f.getDerivative();
        FunctionVec approxDeriv = Function.forwardDifference(f);
        Random rand = RandomUtil.getRandom();


        for (int d = 2; d < 10; d++)
            for (int iter = 0; iter < 100; iter++) {
                Vec x = new DenseVector(d);
                for (int i = 0; i < x.length(); i++)
                    x.set(i, rand.nextDouble() * 2 - 1);

                Vec trueD = trueDeriv.f(x);
                Vec approxD = approxDeriv.f(x);

                assertTrue(trueD.equals(approxD, 1e-1));
            }

    }

}
