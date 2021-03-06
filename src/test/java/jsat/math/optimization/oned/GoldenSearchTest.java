package jsat.math.optimization.oned;

import jsat.math.Function1D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class GoldenSearchTest {
    private static final Function1D easyMin_at_0 = (double x) -> 1 + Math.pow(x, 2);

    public GoldenSearchTest() {
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
     * Test of findMin method, of class GoldenSearch.
     */
    @Test
    public void testFindMin() {
        System.out.println("findMin");

        for (double tol = 2; tol >= 1e-8; tol /= 2) {
            double result = GoldenSearch.findMin(-10, 10, easyMin_at_0, tol, 1000);
            assertEquals(0.0, result, tol * 1.5);
        }
    }

}
