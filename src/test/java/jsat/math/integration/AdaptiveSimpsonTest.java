package jsat.math.integration;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class AdaptiveSimpsonTest {

    public AdaptiveSimpsonTest() {
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
     * Test of integrate method, of class AdaptiveSimpson.
     */
    @Test
    public void testIntegrate() {
        System.out.println("integrate");

        double tol = 1e-10;
        double expect, result;

        result = AdaptiveSimpson.integrate((x) -> Math.sin(x), tol, -1, Math.PI);
        expect = 1.5403023058681397174;
        assertEquals(expect, result, tol);


        result = AdaptiveSimpson.integrate((x) -> x * Math.exp(-x), tol, -1, 5);
        expect = -0.040427681994512802580;
        assertEquals(expect, result, tol);


        result = AdaptiveSimpson.integrate((x) -> Math.log(x), tol, 0.1, 50);
        expect = 145.93140878070670750;
        assertEquals(expect, result, tol);
    }

}
