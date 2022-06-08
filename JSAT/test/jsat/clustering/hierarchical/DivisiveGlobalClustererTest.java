package jsat.clustering.hierarchical;

import java.util.*;

import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.clustering.evaluation.DaviesBouldinIndex;
import jsat.clustering.kmeans.NaiveKMeans;
import jsat.distributions.Uniform;
import jsat.linear.distancemetrics.DistanceMetric;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.utils.GridDataGenerator;
import jsat.utils.IntSet;
import jsat.utils.random.RandomUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.*;

/**
 * @author Edward Raff
 */
public class DivisiveGlobalClustererTest {
    static private DivisiveGlobalClusterer dgc;
    static private SimpleDataSet easyData;

    public DivisiveGlobalClustererTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        GridDataGenerator gdg = new GridDataGenerator(new Uniform(-0.05, 0.05), RandomUtil.getRandom(), 2, 2);
        easyData = gdg.generateData(60);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        DistanceMetric dm = new EuclideanDistance();
        dgc = new DivisiveGlobalClusterer(new NaiveKMeans(), new DaviesBouldinIndex(dm));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCluster_DataSet_int() {
        System.out.println("cluster(dataset, int)");
        List<List<DataPoint>> clusters = dgc.cluster(easyData, 4);
        assertEquals(4, clusters.size());
        Set<Integer> seenBefore = new IntSet();
        for (List<DataPoint> cluster : clusters) {
            int thisClass = cluster.get(0).getCategoricalValue(0);
            assertFalse(seenBefore.contains(thisClass));
            for (DataPoint dp : cluster)
                assertEquals(thisClass, dp.getCategoricalValue(0));
        }
    }

    @Test
    public void testCluster_DataSet() {
        System.out.println("cluster(dataset)");
        List<List<DataPoint>> clusters = dgc.cluster(easyData);
        assertEquals(4, clusters.size());
        Set<Integer> seenBefore = new IntSet();
        for (List<DataPoint> cluster : clusters) {
            int thisClass = cluster.get(0).getCategoricalValue(0);
            assertFalse(seenBefore.contains(thisClass));
            for (DataPoint dp : cluster)
                assertEquals(thisClass, dp.getCategoricalValue(0));
        }
    }

    @Test
    public void testCluster_DataSet_ExecutorService() {
        System.out.println("cluster(dataset, ExecutorService)");
        List<List<DataPoint>> clusters = dgc.cluster(easyData, true);
        assertEquals(4, clusters.size());
        Set<Integer> seenBefore = new IntSet();
        for (List<DataPoint> cluster : clusters) {
            int thisClass = cluster.get(0).getCategoricalValue(0);
            assertFalse(seenBefore.contains(thisClass));
            for (DataPoint dp : cluster)
                assertEquals(thisClass, dp.getCategoricalValue(0));
        }
    }

    @Test
    public void testCluster_DataSet_int_int() {
        System.out.println("cluster(dataset, int, int)");
        List<List<DataPoint>> clusters = dgc.cluster(easyData, 2, 20);
        assertEquals(4, clusters.size());
        Set<Integer> seenBefore = new IntSet();
        for (List<DataPoint> cluster : clusters) {
            int thisClass = cluster.get(0).getCategoricalValue(0);
            assertFalse(seenBefore.contains(thisClass));
            for (DataPoint dp : cluster)
                assertEquals(thisClass, dp.getCategoricalValue(0));
        }
    }

    @Test
    public void testCluster_DataSet_int_int_ExecutorService() {
        System.out.println("cluster(dataset, int, int, ExecutorService)");
        List<List<DataPoint>> clusters = dgc.cluster(easyData, 2, 20, true);
        assertEquals(4, clusters.size());
        Set<Integer> seenBefore = new IntSet();
        for (List<DataPoint> cluster : clusters) {
            int thisClass = cluster.get(0).getCategoricalValue(0);
            assertFalse(seenBefore.contains(thisClass));
            for (DataPoint dp : cluster)
                assertEquals(thisClass, dp.getCategoricalValue(0));
        }
    }

    @Test
    public void testCluster_DataSet_int_ExecutorService() {
        System.out.println("cluster(dataset, int, ExecutorService)");
        List<List<DataPoint>> clusters = dgc.cluster(easyData, 4, true);
        assertEquals(4, clusters.size());
        Set<Integer> seenBefore = new IntSet();
        for (List<DataPoint> cluster : clusters) {
            int thisClass = cluster.get(0).getCategoricalValue(0);
            assertFalse(seenBefore.contains(thisClass));
            for (DataPoint dp : cluster)
                assertEquals(thisClass, dp.getCategoricalValue(0));
        }
    }
}
