package jsat.linear.vectorcollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.linear.VecPaired;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.utils.SystemInfo;
import jsat.utils.random.XORWOW;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class RTreeTest {
    static List<VectorCollection<Vec>> collectionFactories;

    public RTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        collectionFactories = new ArrayList<>();
        collectionFactories.add(new RTree<>());
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

    //    @Test
    public void testSearch_Vec_double() {
        System.out.println("search");
        Random rand = new XORWOW(123);

        VectorArray<Vec> vecCol = new VectorArray<>(new EuclideanDistance());
        for (int i = 0; i < 250; i++)
            vecCol.add(DenseVector.random(3, rand));

        for (VectorCollection<Vec> factory : collectionFactories) {
            ExecutorService ex = Executors.newFixedThreadPool(SystemInfo.LogicalCores);

            VectorCollection<Vec> collection0 = factory.clone();
            collection0.build(vecCol, new EuclideanDistance());
            VectorCollection<Vec> collection1 = factory.clone();
            collection1.build(true, vecCol, new EuclideanDistance());

            collection0 = collection0.clone();
            collection1 = collection1.clone();

            ex.shutdownNow();

            for (int iters = 0; iters < 10; iters++)
                for (double range : new double[]{0.25, 0.5, 0.75, 2.0}) {
                    int randIndex = rand.nextInt(vecCol.size());

                    List<? extends VecPaired<Vec, Double>> foundTrue = vecCol.search(vecCol.get(randIndex), range);
                    List<? extends VecPaired<Vec, Double>> foundTest0 = collection0.search(vecCol.get(randIndex), range);
                    List<? extends VecPaired<Vec, Double>> foundTest1 = collection1.search(vecCol.get(randIndex), range);

                    VectorArray<VecPaired<Vec, Double>> testSearch0 = new VectorArray<>(new EuclideanDistance(), foundTest0);
                    assertEquals(factory.getClass().getName() + " failed", foundTrue.size(), foundTest0.size());
                    for (Vec v : foundTrue) {
                        List<? extends VecPaired<VecPaired<Vec, Double>, Double>> nn = testSearch0.search(v, 1);
                        assertTrue(factory.getClass().getName() + " failed", nn.get(0).equals(v, 1e-13));
                    }


                    VectorArray<VecPaired<Vec, Double>> testSearch1 = new VectorArray<>(new EuclideanDistance(), foundTest1);
                    assertEquals(factory.getClass().getName() + " failed", foundTrue.size(), foundTest1.size());
                    for (Vec v : foundTrue) {
                        List<? extends VecPaired<VecPaired<Vec, Double>, Double>> nn = testSearch1.search(v, 1);
                        assertTrue(factory.getClass().getName() + " failed", nn.get(0).equals(v, 1e-13));
                    }


                }
        }

    }

    @Test
    public void testSearch_Vec_int() {
        System.out.println("search");
        Random rand = new XORWOW(123);

        VectorArray<Vec> vecCol = new VectorArray<>(new EuclideanDistance());
        for (int i = 0; i < 250; i++)
            vecCol.add(DenseVector.random(3, rand));

        for (VectorCollection<Vec> factory : collectionFactories) {
            ExecutorService ex = Executors.newFixedThreadPool(SystemInfo.LogicalCores);

            VectorCollection<Vec> collection0 = factory.clone();
            collection0.build(vecCol, new EuclideanDistance());
            VectorCollection<Vec> collection1 = factory.clone();
            collection1.build(true, vecCol, new EuclideanDistance());

            collection0 = collection0.clone();
            collection1 = collection1.clone();

            ex.shutdownNow();

            for (int iters = 0; iters < 10; iters++)
                for (int neighbours : new int[]{1, 2, 4, 10, 20}) {
                    int randIndex = rand.nextInt(vecCol.size());

                    List<? extends VecPaired<Vec, Double>> foundTrue = vecCol.search(vecCol.get(randIndex), neighbours);
                    List<? extends VecPaired<Vec, Double>> foundTest0 = collection0.search(vecCol.get(randIndex), neighbours);
                    List<? extends VecPaired<Vec, Double>> foundTest1 = collection1.search(vecCol.get(randIndex), neighbours);

                    VectorArray<VecPaired<Vec, Double>> testSearch0 = new VectorArray<>(new EuclideanDistance(), foundTest0);
                    assertEquals(factory.getClass().getName() + " failed", foundTrue.size(), foundTest0.size());
                    for (Vec v : foundTrue) {
                        List<? extends VecPaired<VecPaired<Vec, Double>, Double>> nn = testSearch0.search(v, 1);
                        assertTrue(factory.getClass().getName() + " failed", nn.get(0).equals(v, 1e-13));
                    }

                    VectorArray<VecPaired<Vec, Double>> testSearch1 = new VectorArray<>(new EuclideanDistance(), foundTest1);
                    assertEquals(factory.getClass().getName() + " failed " + neighbours, foundTrue.size(), foundTest1.size());
                    for (Vec v : foundTrue) {
                        List<? extends VecPaired<VecPaired<Vec, Double>, Double>> nn = testSearch1.search(v, 1);
                        assertTrue(factory.getClass().getName() + " failed " + neighbours, nn.get(0).equals(v, 1e-13));
                    }


                }
        }

    }
}
