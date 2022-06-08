package jsat.clustering;

import jsat.classifiers.DataPoint;
import jsat.distributions.Uniform;
import jsat.utils.GridDataGenerator;
import jsat.SimpleDataSet;

import java.util.List;

import static jsat.TestTools.checkClusteringByCat;

import jsat.clustering.SeedSelectionMethods.SeedSelection;
import jsat.linear.distancemetrics.EuclideanDistance;
import jsat.utils.random.RandomUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Edward Raff
 */
public class PAMTest {
    //Like KMeans the cluster number detection isnt stable enough yet that we can test that it getst he right result. 
    static private PAM pam;
    static private SimpleDataSet easyData10;

    public PAMTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        pam = new PAM(new EuclideanDistance(), RandomUtil.getRandom(), SeedSelection.FARTHEST_FIRST);
        pam.setMaxIterations(1000);
        GridDataGenerator gdg = new GridDataGenerator(new Uniform(-0.05, 0.05), RandomUtil.getRandom(), 2, 5);
        easyData10 = gdg.generateData(100);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    /**
     * Test of cluster method, of class PAM.
     */
    @Test
    public void testCluster_3args_1() {
        System.out.println("cluster(dataSet, int, ExecutorService)");
        boolean good = false;
        int count = 0;
        do {
            List<List<DataPoint>> clusters = pam.cluster(easyData10, 10, true);
            assertEquals(10, clusters.size());
            good = checkClusteringByCat(clusters);
        }
        while (!good && count++ < 3);
        assertTrue(good);
    }

    /**
     * Test of cluster method, of class PAM.
     */
    @Test
    public void testCluster_DataSet_int() {
        System.out.println("cluster(dataset, int)");
        boolean good = false;
        int count = 0;
        do {
            List<List<DataPoint>> clusters = pam.cluster(easyData10, 10);
            assertEquals(10, clusters.size());
            good = checkClusteringByCat(clusters);
        }
        while (!good && count++ < 3);
        assertTrue(good);
    }

}
