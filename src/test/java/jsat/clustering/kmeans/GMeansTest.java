package jsat.clustering.kmeans;

import java.util.List;
import java.util.Set;

import jsat.SimpleDataSet;
import jsat.classifiers.DataPoint;
import jsat.clustering.SeedSelectionMethods;
import jsat.distributions.Normal;
import jsat.distributions.TruncatedDistribution;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.utils.GridDataGenerator;
import jsat.utils.IntSet;
import jsat.utils.random.RandomUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class GMeansTest {
    static private SimpleDataSet easyData10;

    public GMeansTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        GridDataGenerator gdg = new GridDataGenerator(new TruncatedDistribution(new Normal(0, 0.01), -0.15, 0.15), RandomUtil.getRandom(), 2, 2);
        easyData10 = gdg.generateData(50);
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
    public void testCluster_4args_1_findK() {
        System.out.println("cluster findK");
        GMeans kMeans = new GMeans(new HamerlyKMeans(new EuclideanDistance(), SeedSelectionMethods.SeedSelection.FARTHEST_FIRST));
        List<List<DataPoint>> clusters = kMeans.cluster(easyData10, 1, 20, true);
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
    public void testCluster_3args_1_findK() {
        System.out.println("cluster findK");
        GMeans kMeans = new GMeans(new HamerlyKMeans(new EuclideanDistance(), SeedSelectionMethods.SeedSelection.FARTHEST_FIRST));
        List<List<DataPoint>> clusters = kMeans.cluster(easyData10, 1, 20);
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
