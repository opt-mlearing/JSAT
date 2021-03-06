package jsat.linear.vectorcollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jsat.TestTools;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.linear.VecPaired;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.utils.DoubleList;
import jsat.utils.IntList;
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
public class DCITest {
    static List<VectorCollection<Vec>> collectionFactories;

    public DCITest() {
    }

    @BeforeClass
    public static void setUpClass() {
        collectionFactories = new ArrayList<>();
        collectionFactories.add(new DCI<>(25, 5));
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
    public void testSearch_Vec_double() {
        System.out.println("search");
        Random rand = new XORWOW(123);

        VectorArray<Vec> vecCol = new VectorArray<>(new EuclideanDistance());
        for (int i = 0; i < 2500; i++)
            vecCol.add(DenseVector.random(3, rand));

        for (VectorCollection<Vec> factory : collectionFactories) {
            VectorCollection<Vec> collection0 = factory.clone();
            collection0.build(vecCol, new EuclideanDistance());
            VectorCollection<Vec> collection1 = factory.clone();
            collection1 = TestTools.deepCopy(collection1);
            collection1.build(true, vecCol, new EuclideanDistance());

            collection0 = collection0.clone();
            collection1 = collection1.clone();
            collection1 = TestTools.deepCopy(collection1);


            for (int iters = 0; iters < 10; iters++)
                for (double range : new double[]{0.25, 0.5, 0.75, 2.0}) {
                    int randIndex = rand.nextInt(vecCol.size());

                    IntList nn_true = new IntList();
                    IntList nn_test = new IntList();

                    DoubleList nd_true = new DoubleList();
                    DoubleList nd_test = new DoubleList();


                    vecCol.search(vecCol.get(randIndex), range, nn_true, nd_true);
                    collection0.search(vecCol.get(randIndex), range, nn_test, nd_test);

                    int found = (int) nn_test.streamInts().filter(nn_true::contains).count();

                    //Since DCI is approximate, allow for missing half
                    assertEquals(nn_true.size(), found, nn_true.size() / 2.0);

                    collection1.search(vecCol.get(randIndex), range, nn_test, nd_test);

                    found = (int) nn_test.streamInts().filter(nn_true::contains).count();

                    //Since DCI is approximate, allow for missing half
                    assertEquals(nn_true.size(), found, nn_true.size() / 2.0);
                }
        }

    }

    @Test
    public void testSearch_Vec_int() {
        System.out.println("search");
        Random rand = new XORWOW(123);

        VectorArray<Vec> vecCol = new VectorArray<>(new EuclideanDistance());
        for (int i = 0; i < 2500; i++)
            vecCol.add(DenseVector.random(3, rand));

        for (VectorCollection<Vec> factory : collectionFactories) {
            VectorCollection<Vec> collection0 = factory.clone();
            collection0.build(vecCol, new EuclideanDistance());
            VectorCollection<Vec> collection1 = factory.clone();
            collection1.build(true, vecCol, new EuclideanDistance());

            collection0 = collection0.clone();
            collection1 = collection1.clone();


            for (int iters = 0; iters < 10; iters++)
                for (int neighbours : new int[]{1, 2, 4, 10, 20}) {
                    int randIndex = rand.nextInt(vecCol.size());

                    IntList nn_true = new IntList();
                    IntList nn_test = new IntList();

                    DoubleList nd_true = new DoubleList();
                    DoubleList nd_test = new DoubleList();


                    vecCol.search(vecCol.get(randIndex), neighbours, nn_true, nd_true);
                    collection0.search(vecCol.get(randIndex), neighbours, nn_test, nd_test);

                    int found = (int) nn_test.streamInts().filter(nn_true::contains).count();

                    //Since DCI is approximate, allow for missing half
                    assertEquals(neighbours, found, neighbours / 2.0);

                    collection1.search(vecCol.get(randIndex), neighbours, nn_test, nd_test);

                    found = (int) nn_test.streamInts().filter(nn_true::contains).count();

                    //Since DCI is approximate, allow for missing half
                    assertEquals(neighbours, found, neighbours / 2.0);

                }
        }

    }

    //    @Test
    public void testSearch_Vec_int_incramental() {
        System.out.println("search");
        Random rand = new XORWOW(123);

        VectorArray<Vec> vecCol = new VectorArray<Vec>(new EuclideanDistance());
        for (int i = 0; i < 1000; i++)
            vecCol.add(DenseVector.random(3, rand));


        IncrementalCollection<Vec> collection0 = new VPTree<Vec>(new EuclideanDistance());
        for (Vec v : vecCol)
            collection0.insert(v);

        for (int iters = 0; iters < 10; iters++)
            for (int neighbours : new int[]{1, 2, 5, 10, 20}) {
                int randIndex = rand.nextInt(vecCol.size());

                List<? extends VecPaired<Vec, Double>> foundTrue = vecCol.search(vecCol.get(randIndex), neighbours);
                List<? extends VecPaired<Vec, Double>> foundTest0 = collection0.search(vecCol.get(randIndex), neighbours);

                assertEquals(collection0.getClass().getName() + " failed", foundTrue.size(), foundTest0.size());
                for (int i = 0; i < foundTrue.size(); i++) {
                    assertTrue(collection0.getClass().getName() + " failed " + (i + 1) + "'th / " + neighbours + " " + foundTrue.get(i).pNormDist(2, foundTest0.get(i)),
                            foundTrue.get(i).equals(foundTest0.get(i), 1e-13));
                }
            }
    }
}
