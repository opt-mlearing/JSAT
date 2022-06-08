package jsat.classifiers.trees;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPoint;
import jsat.datatransform.NumericalToHistogram;
import jsat.linear.ConcatenatedVec;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
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
public class ImportanceByUsesTest {

    public ImportanceByUsesTest() {
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
     * Test of getImportanceStats method, of class ImportanceByUses.
     */
    @Test
    public void testGetImportanceStats() {
        System.out.println("getImportanceStats");

        for (boolean weightByDepth : new boolean[]{true, false}) {
            ImportanceByUses instance = new ImportanceByUses(weightByDepth);

            int randomFeatures = 30;

            //make the circles close to force tree to do lots of splits / make it harder
            ClassificationDataSet train = FixedProblems.getCircles(10000, RandomUtil.getRandom(), 1.0, 1.35);
            int good_featres = train.getNumNumericalVars();
            ClassificationDataSet train_noise = new ClassificationDataSet(train.getNumNumericalVars() + randomFeatures, train.getCategories(), train.getPredicting());

            for (int i = 0; i < train.size(); i++) {
                DataPoint dp = train.getDataPoint(i);
                Vec n = dp.getNumericalValues();
                train_noise.addDataPoint(new ConcatenatedVec(n, DenseVector.random(randomFeatures)), train.getDataPointCategory(i));
            }


            DecisionTree tree = new DecisionTree();
            tree.setPruningMethod(TreePruner.PruningMethod.NONE);
            tree.train(train_noise);

            double[] importances = instance.getImportanceStats(tree, train_noise);

            //make sure the first 2 features were infered as more important than the others!
            for (int i = good_featres; i < importances.length; i++) {
                for (int j = 0; j < good_featres; j++)
                    assertTrue(importances[j] > importances[i]);
            }


            //categorical features, make space wider b/c we lose resolution 
            train = FixedProblems.getCircles(10000, RandomUtil.getRandom(), 1.0, 1.5);
//            train.applyTransformMutate(new PCA(train, 2, 0));
            good_featres = train.getNumNumericalVars();
            train_noise = new ClassificationDataSet(train.getNumNumericalVars() + randomFeatures, train.getCategories(), train.getPredicting());

            for (int i = 0; i < train.size(); i++) {
                DataPoint dp = train.getDataPoint(i);
                Vec n = dp.getNumericalValues();
                train_noise.addDataPoint(new ConcatenatedVec(n, DenseVector.random(randomFeatures)), train.getDataPointCategory(i));
            }

            train_noise.applyTransform(new NumericalToHistogram(train_noise));
            tree = new DecisionTree();
            tree.setPruningMethod(TreePruner.PruningMethod.NONE);
            tree.train(train_noise);

            importances = instance.getImportanceStats(tree, train_noise);

            //make sure the first 2 features were infered as more important than the others!
            for (int i = good_featres; i < importances.length; i++) {
                for (int j = 0; j < good_featres; j++)
                    if (importances[j] == 0)//sometimes it happens b/c we can seperate on just the first var when discretized
                        assertTrue(importances[j] >= importances[i]);
                    else assertTrue(importances[j] > importances[i]);
            }
        }
    }

}
