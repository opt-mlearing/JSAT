package jsat.utils;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author edwardraff
 */
public class IntListTest {

    public IntListTest() {
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
     * Test of clear method, of class IntList.
     */
    @Test
    public void testAdd_int_int() {
        System.out.println("clear");
        IntList list = new IntList();

        for (int sizes = 2; sizes < 100; sizes++) {
            for (int i = sizes - 1; i >= 0; i--)
                list.add(0, i);
            for (int i = 0; i < sizes; i++)
                assertEquals(i, list.getI(i));
        }
    }

}
