package jsat.distributions;

import java.util.Arrays;
import java.util.Random;

import jsat.linear.Vec;
import jsat.utils.random.RandomUtil;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class StudentTTest {


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSample() {
        System.out.println("hashCode");
        StudentT d1 = new StudentT(1.9);
        StudentT d2 = new StudentT(2.1);

        Random rand = RandomUtil.getRandom();
        for (StudentT d : Arrays.asList(d1, d2)) {

            Vec sample = d.sampleVec(100000, rand);
            assertEquals(d.mean(), sample.mean(), 1e-1);
        }
    }

    @Test
    public void testEquals() {
        System.out.println("equals");
        ContinuousDistribution d1 = new StudentT(0.5, 0.5, 0.5);
        ContinuousDistribution d2 = new StudentT(0.6, 0.5, 0.5);
        ContinuousDistribution d3 = new StudentT(0.5, 0.6, 0.5);
        ContinuousDistribution d4 = new StudentT(0.5, 0.5, 0.5);
        ContinuousDistribution d5 = new StudentT(0.5, 0.5, 0.6);
        Integer i = new Integer(1);
        assertFalse(d1.equals(d2));
        assertFalse(d1.equals(d3));
        assertFalse(d1.equals(d5));
        assertFalse(d2.equals(d3));
        assertFalse(d1.equals(i));
        assertFalse(d1.equals(null));
        assertEquals(d1, d1);
        assertEquals(d1, d4);
        assertEquals(d1, d1.clone());
    }

    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        ContinuousDistribution d1 = new StudentT(0.5, 0.5, 0.5);
        ContinuousDistribution d2 = new StudentT(0.6, 0.5, 0.5);
        ContinuousDistribution d4 = new StudentT(0.5, 0.5, 0.5);
        ContinuousDistribution d5 = new StudentT(0.5, 0.5, 0.6);
        assertEquals(d1.hashCode(), d4.hashCode());
        assertFalse(d1.hashCode() == d2.hashCode());
        assertFalse(d1.hashCode() == d5.hashCode());
    }

}
