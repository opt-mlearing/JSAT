package jsat.distributions.discrete;

import java.util.Arrays;

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
public class PoissonTest {
    static int[] testVals = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    public PoissonTest() {
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


    @Test
    public void testLogPmf() {
        System.out.println("logPmf");
        Poisson instance = new Poisson(7);

        double[] expected_7 = new double[]
                {
                        -7.00000000000000, -5.05408985094469, -3.80132688244932, -2.95402902206212, -2.39441323412669, -2.05794099750548, -1.90379031767822, -1.90379031767822, -2.03732171030274, -2.28863613858365, -2.64531108252238
                };

        for (int i = 0; i < expected_7.length; i++) {
            assertEquals(expected_7[i], instance.logPmf(testVals[i]), 1e-4);
        }

    }

    @Test
    public void testPmf() {
        System.out.println("pmf");
        Poisson instance = new Poisson(7);

        double[] expected_7 = new double[]
                {
                        0.000911881965554516, 0.00638317375888161, 0.0223411081560856, 0.0521292523641998, 0.0912261916373497, 0.127716668292290, 0.149002779674338, 0.149002779674338, 0.130377432215046, 0.101404669500591, 0.0709832686504137
                };

        for (int i = 0; i < expected_7.length; i++) {
            assertEquals(expected_7[i], instance.pmf(testVals[i]), 1e-4);
        }
    }

    @Test
    public void testCdf() {
        System.out.println("cdf");
        Poisson instance = new Poisson(7);

        double[] expected_7 = new double[]
                {
                        0.000911881965554516, 0.00729505572443613, 0.0296361638805218, 0.0817654162447216, 0.172991607882071, 0.300708276174361, 0.449711055848699, 0.598713835523037, 0.729091267738082, 0.830495937238673, 0.901479205889087
                };

        for (int i = 0; i < expected_7.length; i++) {
            assertEquals(expected_7[i], instance.cdf(testVals[i]), 1e-4);

            //its hard to get the right value for the probabilities right on the line, so lets nudge them a little to make sure we map to the right spot
            double val;
            if (i == 0)
                val = instance.invCdf(expected_7[i] * .99);
            else
                val = instance.invCdf(expected_7[i - 1] + (expected_7[i] - expected_7[i - 1]) * 0.95);

            double expected = testVals[i] >= instance.max() ? instance.max() : testVals[i];
            assertEquals(expected, val, 1e-3);
        }
    }

    @Test
    public void testSummaryStats() {
        System.out.println("stats");
        Poisson instance = new Poisson(7);
        //mean, median, variance, standard dev, skew
        double[] expected_7_5 = {7.00000000000000, 7.00000000000000, 7.00000000000000, 2.64575131106459, 0.377964473009227};


        assertEquals(expected_7_5[0], instance.mean(), 1e-4);
        assertEquals(expected_7_5[1], instance.median(), 1e-4);
        assertEquals(expected_7_5[2], instance.variance(), 1e-4);
        assertEquals(expected_7_5[3], instance.standardDeviation(), 1e-4);
        assertEquals(expected_7_5[4], instance.skewness(), 1e-4);

    }


    @Test
    public void testSample() {
        System.out.println("sample");

        //Poisson can get wonked on variance, so larger range used there
        /*
         * "however in practice, the observed variance is usually larger than
         * the theoretical variance and in the case of Poisson, larger than its
         * mean. This is known as overdispersion, an important concept that
         * occurs with discrete data"
         */

//        for(int d = 1; d  < 100000000; d*=2)
//        {
//            long start = System.currentTimeMillis();
//            Vec samples = new Poisson(d).sampleVec(100000, RandomUtil.getRandom());
//            long end = System.currentTimeMillis();
//            System.out.println(d + ": " + (end-start));
//        }

        for (Poisson instance : Arrays.asList(new Poisson(2), new Poisson(20), new Poisson(40), new Poisson(200))) {
            System.out.println(instance.mean());
            Vec samples = instance.sampleVec(10000, RandomUtil.getRandom());

            assertEquals(instance.mean(), samples.mean(), instance.getLambda() * 2e-1);
            assertEquals(instance.median(), samples.median(), instance.getLambda() * 2e-1);
            assertEquals(0.0, (instance.standardDeviation() - samples.standardDeviation()) / instance.standardDeviation(), 0.1);
            assertEquals(instance.skewness(), samples.skewness(), instance.getLambda() * 2e-1);
        }

    }
}
