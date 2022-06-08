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
public class LongListTest {

    public LongListTest() {
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
    public void testAdd_int_long() {
        System.out.println("clear");
        LongList list = new LongList();

        for (int sizes = 2; sizes < 100; sizes++) {
            for (int i = sizes - 1; i >= 0; i--)
                list.add(0, i);
            for (int i = 0; i < sizes; i++)
                assertEquals(i, list.getL(i));
        }
    }

}
