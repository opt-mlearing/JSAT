package jsat.linear.distancemetrics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.utils.SystemInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class ChebyshevDistanceTest {
    static private ExecutorService ex;
    static private Vec zero;
    static private Vec ones;
    static private Vec half;
    static private Vec inc;

    static private List<Vec> vecs;

    static private double[][] expected;

    public ChebyshevDistanceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        ex = Executors.newFixedThreadPool(SystemInfo.LogicalCores);
    }

    @AfterClass
    public static void tearDownClass() {
        ex.shutdown();
    }

    @Before
    public void setUp() {
        zero = new DenseVector(5);

        ones = new DenseVector(5);
        ones.mutableAdd(1.0);

        half = new DenseVector(5);
        half.mutableAdd(0.5);

        inc = new DenseVector(5);
        for (int i = 0; i < inc.length(); i++)
            inc.set(i, i);

        vecs = Arrays.asList(zero, ones, half, inc);
        expected = new double[][]
                {
                        {0, 1, 0.5, 4,},
                        {1, 0, 0.5, 3,},
                        {0.5, 0.5, 0.0, 3.5,},
                        {4, 3, 3.5, 0,}
                };
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDist_Vec_Vec() {
        System.out.println("dist");

        ChebyshevDistance dist = new ChebyshevDistance();

        List<Double> cache = dist.getAccelerationCache(vecs);
        List<Double> cache2 = dist.getAccelerationCache(vecs, true);
        if (cache != null) {
            assertEquals(cache.size(), cache2.size());
            for (int i = 0; i < cache.size(); i++)
                assertEquals(cache.get(i), cache2.get(i), 0.0);
            assertTrue(dist.supportsAcceleration());
        } else {
            assertNull(cache2);
            assertFalse(dist.supportsAcceleration());
        }

        try {
            dist.dist(half, new DenseVector(half.length() + 1));
            fail("Distance between vecs should have erred");
        } catch (Exception ex) {

        }

        for (int i = 0; i < vecs.size(); i++)
            for (int j = 0; j < vecs.size(); j++) {
                ChebyshevDistance d = dist.clone();
                assertEquals(expected[i][j], d.dist(vecs.get(i), vecs.get(j)), 1e-12);
                assertEquals(expected[i][j], d.dist(i, j, vecs, cache), 1e-12);
                assertEquals(expected[i][j], d.dist(i, vecs.get(j), vecs, cache), 1e-12);
                assertEquals(expected[i][j], d.dist(i, vecs.get(j), dist.getQueryInfo(vecs.get(j)), vecs, cache), 1e-12);
            }
    }

    @Test
    public void testMetricProperties() {
        System.out.println("isSymmetric");
        ChebyshevDistance instance = new ChebyshevDistance();
        assertTrue(instance.isSymmetric());
        assertTrue(instance.isSubadditive());
        assertTrue(instance.isIndiscemible());
    }

    @Test
    public void testMetricBound() {
        System.out.println("metricBound");
        ChebyshevDistance instance = new ChebyshevDistance();
        assertTrue(instance.metricBound() > 0);
        assertTrue(Double.isInfinite(instance.metricBound()));
    }


}
