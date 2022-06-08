package jsat.datatransform.visualization;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jsat.SimpleDataSet;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.linear.DenseMatrix;
import jsat.linear.Matrix;
import jsat.linear.Vec;
import jsat.linear.VecPaired;
import jsat.linear.distancemetrics.ChebyshevDistance;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.linear.distancemetrics.ManhattanDistance;
import jsat.linear.vectorcollection.VectorArray;
import jsat.utils.SystemInfo;
import jsat.utils.random.RandomUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class LargeVizTest {

    public LargeVizTest() {
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
     * Test of transform method, of class LargeViz.
     */
    @Test
    public void testTransform_DataSet_ExecutorService() {
        System.out.println("transform");

        ExecutorService ex = Executors.newFixedThreadPool(SystemInfo.LogicalCores);

        Random rand = RandomUtil.getRandom();
        LargeViz instance = new LargeViz();


        //create a small data set, and apply a random projection to a higher dimension
        //should still get similar nns when projected back down
        final int K = 5;//num neighbors we want to see stay the same
        instance.setPerplexity(K * 3);

        Matrix orig_dim = new DenseMatrix(200, 2);
        for (int i = 0; i < orig_dim.rows(); i++) {
            int offset = i % 2 == 0 ? -5 : 5;
            for (int j = 0; j < orig_dim.cols(); j++) {
                orig_dim.set(i, j, rand.nextGaussian() + offset);
            }
        }


        Matrix s = Matrix.random(2, 10, rand);

        Matrix proj_data = orig_dim.multiply(s);

        SimpleDataSet proj = new SimpleDataSet(proj_data.cols(), new CategoricalData[0]);
        for (int i = 0; i < proj_data.rows(); i++)
            proj.add(new DataPoint(proj_data.getRow(i)));

        List<Set<Integer>> origNNs = new ArrayList<>();
        VectorArray<VecPaired<Vec, Integer>> proj_vc = new VectorArray<>(new EuclideanDistance());
        for (int i = 0; i < proj.size(); i++)
            proj_vc.add(new VecPaired<>(proj.getDataPoint(i).getNumericalValues(), i));

        for (int i = 0; i < proj.size(); i++) {
            Set<Integer> nns = new HashSet<>();
            for (VecPaired<VecPaired<Vec, Integer>, Double> neighbor : proj_vc.search(proj_vc.get(i), K))
                nns.add(neighbor.getVector().getPair());
            origNNs.add(nns);
        }

        SimpleDataSet transformed_0 = instance.transform(proj, true);
        SimpleDataSet transformed_1 = instance.transform(proj);


        for (SimpleDataSet transformed : new SimpleDataSet[]{transformed_0, transformed_1}) {
            double sameNN = 0;
            VectorArray<VecPaired<Vec, Integer>> trans_vc = new VectorArray<VecPaired<Vec, Integer>>(new EuclideanDistance());
            for (int i = 0; i < transformed.size(); i++)
                trans_vc.add(new VecPaired<Vec, Integer>(transformed.getDataPoint(i).getNumericalValues(), i));

            for (int i = 0; i < orig_dim.rows(); i++) {
                for (VecPaired<VecPaired<Vec, Integer>, Double> neighbor : trans_vc.search(trans_vc.get(i), K * 3))
                    if (origNNs.get(i).contains(neighbor.getVector().getPair()))
                        sameNN++;
            }

            double score = sameNN / (transformed.size() * K);
            assertTrue("was " + score, score >= 0.50);
        }

        ex.shutdown();
    }

    @Test
    public void testTransform_DiffDists() {
        System.out.println("transform");

        ExecutorService ex = Executors.newFixedThreadPool(SystemInfo.LogicalCores);

        Random rand = RandomUtil.getRandom();
        LargeViz instance = new LargeViz();


        //create a small data set, and apply a random projection to a higher dimension
        //should still get similar nns when projected back down
        final int K = 5;//num neighbors we want to see stay the same
        instance.setPerplexity(K * 3);
        instance.setDistanceMetricSource(new ChebyshevDistance());
        instance.setDistanceMetricEmbedding(new ManhattanDistance());

        Matrix orig_dim = new DenseMatrix(200, 2);
        for (int i = 0; i < orig_dim.rows(); i++) {
            int offset = i % 2 == 0 ? -5 : 5;
            for (int j = 0; j < orig_dim.cols(); j++) {
                orig_dim.set(i, j, rand.nextGaussian() + offset);
            }
        }


        Matrix s = Matrix.random(2, 10, rand);

        Matrix proj_data = orig_dim.multiply(s);

        SimpleDataSet proj = new SimpleDataSet(proj_data.cols(), new CategoricalData[0]);
        for (int i = 0; i < proj_data.rows(); i++)
            proj.add(new DataPoint(proj_data.getRow(i)));

        List<Set<Integer>> origNNs = new ArrayList<>();
        VectorArray<VecPaired<Vec, Integer>> proj_vc = new VectorArray<>(new EuclideanDistance());
        for (int i = 0; i < proj.size(); i++)
            proj_vc.add(new VecPaired<>(proj.getDataPoint(i).getNumericalValues(), i));

        for (int i = 0; i < proj.size(); i++) {
            Set<Integer> nns = new HashSet<>();
            for (VecPaired<VecPaired<Vec, Integer>, Double> neighbor : proj_vc.search(proj_vc.get(i), K))
                nns.add(neighbor.getVector().getPair());
            origNNs.add(nns);
        }

        SimpleDataSet transformed_0 = instance.transform(proj, true);
        SimpleDataSet transformed_1 = instance.transform(proj);


        for (SimpleDataSet transformed : new SimpleDataSet[]{transformed_0, transformed_1}) {
            double sameNN = 0;
            VectorArray<VecPaired<Vec, Integer>> trans_vc = new VectorArray<VecPaired<Vec, Integer>>(new EuclideanDistance());
            for (int i = 0; i < transformed.size(); i++)
                trans_vc.add(new VecPaired<Vec, Integer>(transformed.getDataPoint(i).getNumericalValues(), i));

            for (int i = 0; i < orig_dim.rows(); i++) {
                for (VecPaired<VecPaired<Vec, Integer>, Double> neighbor : trans_vc.search(trans_vc.get(i), K * 3))
                    if (origNNs.get(i).contains(neighbor.getVector().getPair()))
                        sameNN++;
            }

            double score = sameNN / (transformed.size() * K);
            assertTrue("was " + score, score >= 0.50);
        }

        ex.shutdown();
    }
}
