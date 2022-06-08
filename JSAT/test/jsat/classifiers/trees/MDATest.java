package jsat.classifiers.trees;

import java.util.Random;

import jsat.classifiers.CategoricalData;
import jsat.classifiers.ClassificationDataSet;
import jsat.datatransform.NumericalToHistogram;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.regression.RegressionDataSet;
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
public class MDATest {

    public MDATest() {
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

    public static ClassificationDataSet getHarderC(int datums, Random rand) {
        ClassificationDataSet cds = new ClassificationDataSet(20, new CategoricalData[0], new CategoricalData(2));
        for (int i = 0; i < datums; i++) {
            double x = (rand.nextDouble() - 0.5) * 10;
            double y = (rand.nextDouble() - 0.5) * 10;

            double score = 10 * x * y + x * x - y * y;

            Vec n = DenseVector.random(20);
            n.set(0, x);
            n.set(1, y);

            cds.addDataPoint(n, score + (rand.nextDouble() - 0.5) * 20 > 0 ? 0 : 1);

        }
        return cds;
    }

    public static RegressionDataSet getHarderR(int datums, Random rand) {
        RegressionDataSet rds = new RegressionDataSet(20, new CategoricalData[0]);
        for (int i = 0; i < datums; i++) {
            double x = (rand.nextDouble() - 0.5) * 10;
            double y = (rand.nextDouble() - 0.5) * 10;

            double score = 10 * x * y + x * x - y * y;

            Vec n = DenseVector.random(20);
            n.set(0, x);
            n.set(1, y);

            rds.addDataPoint(n, score + (rand.nextDouble() - 0.5) * 20);

        }
        return rds;
    }

    /**
     * Test of getImportanceStats method, of class MDA.
     */
    @Test
    public void testGetImportanceStats() {
        System.out.println("getImportanceStats");
        MDA instance = new MDA();


        //make the circles close to force tree to do lots of splits / make it harder
        ClassificationDataSet train = getHarderC(10000, RandomUtil.getRandom());
        int good_featres = 2;


        DecisionTree tree = new DecisionTree();
        tree.setPruningMethod(TreePruner.PruningMethod.NONE);
        tree.train(train);

        double[] importances = instance.getImportanceStats(tree, train);

        //make sure the first 2 features were infered as more important than the others!
        for (int i = good_featres; i < importances.length; i++) {
            for (int j = 0; j < good_featres; j++)
                assertTrue(importances[j] > importances[i]);
        }


        //categorical features, make space wider b/c we lose resolution 
        train = getHarderC(10000, RandomUtil.getRandom());


        train.applyTransform(new NumericalToHistogram(train, 7));
        tree = new DecisionTree();
        tree.setPruningMethod(TreePruner.PruningMethod.NONE);
        tree.train(train);

        importances = instance.getImportanceStats(tree, train);

        //make sure the first 2 features were infered as more important than the others!
        for (int i = good_featres; i < importances.length; i++) {
            for (int j = 0; j < good_featres; j++)
                assertTrue(importances[j] > importances[i]);
        }

    }

    @Test
    public void testGetImportanceStatsR() {
        System.out.println("getImportanceStatsR");
        MDA instance = new MDA();


        //make the circles close to force tree to do lots of splits / make it harder
        RegressionDataSet train = getHarderR(10000, RandomUtil.getRandom());
        int good_featres = 2;


        DecisionTree tree = new DecisionTree();
        tree.setPruningMethod(TreePruner.PruningMethod.NONE);
        tree.train(train);

        double[] importances = instance.getImportanceStats(tree, train);

        //make sure the first 2 features were infered as more important than the others!
        for (int i = good_featres; i < importances.length; i++) {
            for (int j = 0; j < good_featres; j++)
                assertTrue(importances[j] > importances[i]);
        }


        //categorical features, make space wider b/c we lose resolution 
        train = getHarderR(10000, RandomUtil.getRandom());


        train.applyTransform(new NumericalToHistogram(train, 7));
        tree = new DecisionTree();
        tree.setPruningMethod(TreePruner.PruningMethod.NONE);
        tree.train(train);

        importances = instance.getImportanceStats(tree, train);

        //make sure the first 2 features were infered as more important than the others!
        for (int i = good_featres; i < importances.length; i++) {
            for (int j = 0; j < good_featres; j++)
                assertTrue(importances[j] > importances[i]);
        }

    }
}
