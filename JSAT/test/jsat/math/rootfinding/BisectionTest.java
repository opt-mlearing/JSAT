package jsat.math.rootfinding;

import jsat.math.Function1D;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static java.lang.Math.*;

/**
 * @author Edward Raff
 */
public class BisectionTest {
    /**
     * Root at 0
     */
    Function1D sinF = (double x) -> sin(x);

    /**
     * Root at approx -4.87906
     */
    Function1D polyF = (double x) -> pow(x, 3) + 5 * pow(x, 2) + x + 2;

    public BisectionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    /**
     * Test of root method, of class Bisection.
     */
    @Test
    public void testRoot_4args() {
        System.out.println("root");
        double eps = 1e-15;
        double result = Bisection.root(-PI / 2, PI / 2, sinF);
        assertEquals(0, result, eps);

        result = Bisection.root(-6, 6, polyF);
        assertEquals(-4.8790576334840479813, result, eps);

        result = Bisection.root(-6, 6, polyF);
        assertEquals(-4.8790576334840479813, result, eps);


        result = Bisection.root(-PI / 2, PI / 2, (x) -> sin(x + 1));
        assertEquals(-1.0, result, eps);
    }

    /**
     * Test of root method, of class Bisection.
     */
    @Test
    public void testRoot_5args() {
        System.out.println("root");
        double eps = 1e-15;
        double result = Bisection.root(eps, -PI / 2, PI / 2, sinF);
        assertEquals(0, result, eps);

        result = Bisection.root(eps, -6, 6, polyF);
        assertEquals(-4.8790576334840479813, result, eps);

        result = Bisection.root(eps, -6, 6, polyF);
        assertEquals(-4.8790576334840479813, result, eps);


        result = Bisection.root(eps, -PI / 2, PI / 2, (x) -> sin(x + 1));
        assertEquals(-1.0, result, eps);

    }

    @Test
    public void testRoot_6args() {
        System.out.println("root");
        double eps = 1e-15;
        double result = Bisection.root(eps, -PI / 2, PI / 2, sinF);
        assertEquals(0, result, eps);

        result = Bisection.root(eps, -PI / 2, PI / 2, (x) -> sin(x + 1));
        assertEquals(-1.0, result, eps);

        result = Bisection.root(eps, -PI / 2, PI / 2, (x) -> sin(x + 3));
        assertEquals(PI - 3.0, result, eps);

        result = Bisection.root(eps, -6.0, 6.0, polyF);
        assertEquals(-4.8790576334840479813, result, eps);
    }

    /**
     * Test of root method, of class Bisection.
     */
    @Test
    public void testRoot_7args() {
        System.out.println("root");
        double eps = 1e-13;
        int maxIterations = 1000;
        double result = Bisection.root(eps, maxIterations, -PI / 2, PI / 2, sinF);
        assertEquals(0, result, eps);

        result = Bisection.root(eps, maxIterations, -PI / 2, PI / 2, (x) -> sin(x + 1));
        assertEquals(-1.0, result, eps);

        result = Bisection.root(eps, maxIterations, -PI / 2, PI / 2, (x) -> sin(x + 3));
        assertEquals(PI - 3.0, result, eps);

        result = Bisection.root(eps, maxIterations, -6, 6, polyF);
        assertEquals(-4.8790576334840479813, result, eps);
    }
}
