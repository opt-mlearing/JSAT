package jsat.math.optimization.stochastic;

import java.util.Random;

import jsat.linear.DenseVector;
import jsat.linear.SubVector;
import jsat.linear.Vec;
import jsat.math.FunctionVec;
import jsat.math.optimization.RosenbrockFunction;
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
public class AdaDeltaTest {

    public AdaDeltaTest() {
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
    public void testUpdate_3args() {
        System.out.println("update");
        Random rand = RandomUtil.getRandom();
        Vec x0 = new DenseVector(10);
        for (int i = 0; i < x0.length(); i++)
            x0.set(i, rand.nextDouble());

        RosenbrockFunction f = new RosenbrockFunction();
        FunctionVec fp = f.getDerivative();
        double eta = 0.01;
        AdaDelta instance = new AdaDelta();
        instance.setup(x0.length());

        for (int i = 0; i < 100000; i++) {
            instance.update(x0, fp.f(x0).normalized(), eta);
            instance = instance.clone();
        }
        assertEquals(0.0, f.f(x0), 1e-1);
    }

    @Test
    public void testUpdate_5args() {
        System.out.println("update");
        Random rand = RandomUtil.getRandom();
        Vec xWithBias = new DenseVector(21);
        for (int i = 0; i < xWithBias.length(); i++)
            xWithBias.set(i, rand.nextDouble());

        Vec x0 = new SubVector(0, 20, xWithBias);

        RosenbrockFunction f = new RosenbrockFunction();
        FunctionVec fp = f.getDerivative();
        double eta = 0.01;


        AdaDelta instance = new AdaDelta();
        instance.setup(x0.length());

        for (int i = 0; i < 100000; i++) {
            double bias = xWithBias.get(20);
            Vec gradWithBias = fp.f(xWithBias);
            gradWithBias.normalize();
            double biasGrad = gradWithBias.get(20);
            Vec grad = new SubVector(0, 20, gradWithBias);
            double biasDelta = instance.update(x0, grad, eta, bias, biasGrad);
            xWithBias.set(20, bias - biasDelta);

            instance = instance.clone();
        }
        assertEquals(0.0, f.f(xWithBias), 1e-1);

    }

}
