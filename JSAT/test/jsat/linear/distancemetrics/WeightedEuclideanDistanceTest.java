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
public class WeightedEuclideanDistanceTest {
    static private ExecutorService ex;
    static private Vec weights;
    static private Vec zero;
    static private Vec ones;
    static private Vec half;
    static private Vec inc;

    static private List<Vec> vecs;

    static private double[][] expected;

    public WeightedEuclideanDistanceTest() {
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
        //weighting
        weights = DenseVector.toDenseVec(1, 0.5, 1, 2, 1);
        for (Vec v : vecs) {
            v.set(1, v.get(1) * 2);
            v.set(3, v.get(3) / 2);
        }
        expected = new double[][]
                {
                        {0.0, 2.34520787991, 1.17260393996, 5.14781507049,},
                        {2.34520787991, 0.0, 1.17260393996, 3.60555127546,},
                        {1.17260393996, 1.17260393996, 0.0, 4.28660704987,},
                        {5.14781507049, 3.60555127546, 4.28660704987, 0.0,},
                };
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDist_Vec_Vec() {
        System.out.println("dist");

        WeightedEuclideanDistance dist = new WeightedEuclideanDistance(weights);

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
                WeightedEuclideanDistance d = dist.clone();
                assertEquals(expected[i][j], d.dist(vecs.get(i), vecs.get(j)), 1e-8);
                assertEquals(expected[i][j], d.dist(i, j, vecs, cache), 1e-8);
                assertEquals(expected[i][j], d.dist(i, vecs.get(j), vecs, cache), 1e-8);
                assertEquals(expected[i][j], d.dist(i, vecs.get(j), dist.getQueryInfo(vecs.get(j)), vecs, cache), 1e-8);
            }
    }

    @Test
    public void testMetricProperties() {
        System.out.println("isSymmetric");
        WeightedEuclideanDistance instance = new WeightedEuclideanDistance(weights);
        assertTrue(instance.isSymmetric());
        assertTrue(instance.isSubadditive());
        assertTrue(instance.isIndiscemible());
    }

    @Test
    public void testMetricBound() {
        System.out.println("metricBound");
        WeightedEuclideanDistance instance = new WeightedEuclideanDistance(weights);
        assertTrue(instance.metricBound() > 0);
        assertTrue(Double.isInfinite(instance.metricBound()));
    }


}
