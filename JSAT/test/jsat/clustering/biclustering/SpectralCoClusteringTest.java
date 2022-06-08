/*
 * This code was contributed under the public domain.
 */
package jsat.clustering.biclustering;

import java.util.ArrayList;
import java.util.List;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.clustering.HDBSCAN;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.utils.IntList;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * @author edwardraff
 */
public class SpectralCoClusteringTest {

    public SpectralCoClusteringTest() {
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
     * Test of cluster method, of class SpectralCoClustering.
     */
    @Test
    public void testCluster_4args() {
        System.out.println("cluster");

        IntList labels = new IntList();
        List<Vec> data = new ArrayList<>();
        int true_k = 4;
        int features_per = 3;
        int n_c = 5;
        int noisy_features = 0;
        int d = features_per * true_k + noisy_features;

        List<List<Integer>> true_row_assingments = new ArrayList<>();
        List<List<Integer>> true_col_assingments = new ArrayList<>();

        for (int y = 0; y < true_k; y++) {
            IntList row_asign = new IntList();
            IntList col_asign = new IntList();
            for (int j = y * features_per; j < (y + 1) * features_per; j++)
                col_asign.add(j);


            for (int i = 0; i < n_c; i++) {
                row_asign.add(data.size());
                labels.add(y);
//                DenseVector x = new DenseVector(d);
                Vec x = DenseVector.random(d).multiply(0.01);
                for (int j = y * features_per; j < (y + 1) * features_per; j++)
                    x.increment(j, 1.0);
                data.add(x);
            }

            true_row_assingments.add(row_asign);
            true_col_assingments.add(col_asign);
        }

        ClassificationDataSet dataSet = new ClassificationDataSet(d, new CategoricalData[0], new CategoricalData(true_k));
        for (int i = 0; i < labels.size(); i++)
            dataSet.addDataPoint(data.get(i), labels.get(i));


//        boolean parallel = false;
        for (boolean parallel : new boolean[]{false, true})
            for (SpectralCoClustering.InputNormalization in : SpectralCoClustering.InputNormalization.values()) {
                System.out.println(in + " " + parallel);
                SpectralCoClustering instance = new SpectralCoClustering(in);
                List<List<Integer>> row_assignments = new ArrayList<>();
                List<List<Integer>> col_assignments = new ArrayList<>();
                instance.bicluster(dataSet, true_k, parallel, row_assignments, col_assignments);

                assertEquals(true_k, row_assignments.size());
                assertEquals(true_k, col_assignments.size());
                double score = ConsensusScore.score(parallel,
                        true_row_assingments, true_col_assingments,
                        row_assignments, col_assignments);


                //        for(int c = 0; c < true_k; c++)
                //        {
                //            System.out.println(c);
                //            System.out.println(row_assignments.get(c));
                //            System.out.println(col_assignments.get(c));
                //            System.out.println("\n\n");
                //        }
                //
                //        System.out.println("Score: " + score);

                //Should be able to get a perfect score
                assertEquals(1.0, score, 0.0);
            }

    }

    @Test
    public void testCluster_UnkK() {
        System.out.println("cluster");

        IntList labels = new IntList();
        List<Vec> data = new ArrayList<>();
        int true_k = 4;
        int features_per = 3;
        int n_c = 5;
        int noisy_features = 0;
        int d = features_per * true_k + noisy_features;

        List<List<Integer>> true_row_assingments = new ArrayList<>();
        List<List<Integer>> true_col_assingments = new ArrayList<>();

        for (int y = 0; y < true_k; y++) {
            IntList row_asign = new IntList();
            IntList col_asign = new IntList();
            for (int j = y * features_per; j < (y + 1) * features_per; j++)
                col_asign.add(j);


            for (int i = 0; i < n_c; i++) {
                row_asign.add(data.size());
                labels.add(y);
//                DenseVector x = new DenseVector(d);
                Vec x = DenseVector.random(d).multiply(0.01);
                for (int j = y * features_per; j < (y + 1) * features_per; j++)
                    x.increment(j, 1.0);
                data.add(x);
            }

            true_row_assingments.add(row_asign);
            true_col_assingments.add(col_asign);
        }

        ClassificationDataSet dataSet = new ClassificationDataSet(d, new CategoricalData[0], new CategoricalData(true_k));
        for (int i = 0; i < labels.size(); i++)
            dataSet.addDataPoint(data.get(i), labels.get(i));


        for (boolean parallel : new boolean[]{false, true})
            for (SpectralCoClustering.InputNormalization in : SpectralCoClustering.InputNormalization.values()) {
                System.out.println(in + " " + parallel);
                SpectralCoClustering instance = new SpectralCoClustering(in);

                instance.setBaseClusterAlgo(new HDBSCAN(5));

                List<List<Integer>> row_assignments = new ArrayList<>();
                List<List<Integer>> col_assignments = new ArrayList<>();
                instance.bicluster(dataSet, parallel, row_assignments, col_assignments);

                assertEquals(true_k, row_assignments.size());
                assertEquals(true_k, col_assignments.size());
                double score = ConsensusScore.score(parallel,
                        true_row_assingments, true_col_assingments,
                        row_assignments, col_assignments);

                //        for(int c = 0; c < row_assignments.size(); c++)
                //        {
                //            System.out.println(c);
                //            System.out.println(row_assignments.get(c));
                //            System.out.println(col_assignments.get(c));
                //            System.out.println("\n\n");
                //        }
                //
                //        System.out.println("Score: " + score);

                //Should be able to do pretty well
                assertEquals(1.0, score, 0.1);
            }

    }


}
