package jsat.math.optimization.oned;

import jsat.math.Function1D;

/**
 * The class provides an implementation of the Golden Search method of function
 * minimization.
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class GoldenSearch {
    private static final double goldenRatio = (Math.sqrt(5) - 1) / 2;

    /**
     * Attempts to numerically find the value {@code x} that minimizes the one
     * dimensional function {@code f(x)} in the range {@code [min, max]}.
     *
     * @param min      the minimum of the search range
     * @param max      the maximum of the search range
     * @param f        the one dimensional function to minimize
     * @param eps      the desired accuracy of the returned value
     * @param maxSteps the maximum number of search steps to take
     * @return the value {@code x} that appears to minimize {@code f(x)}
     */
    public static double findMin(double min, double max, Function1D f, double eps, int maxSteps) {
        double a = min, b = max;
        double fa = f.f(a), fb = f.f(b);

        double c = b - goldenRatio * (b - a);
        double d = a + goldenRatio * (b - a);
        double fc = f.f(c);
        double fd = f.f(d);

        while (Math.abs(c - d) > eps && maxSteps-- > 0) {
            if (fc < fd) {
                // (b, f(b)) ← (d, f(d))
                b = d;
                fb = fd;
                //(d, f(d)) ← (c, f(c)) 
                d = c;
                fd = fc;
                // update c = b + φ (a - b) and f(c)
                c = b - goldenRatio * (b - a);
                fc = f.f(c);
            } else {
                //(a, f(a)) ← (c, f(c))
                a = c;
                fa = fc;
                //(c, f(c)) ← (d, f(d)) 
                c = d;
                fc = fd;
                // update d = a + φ (b - a) and f(d)
                d = a + goldenRatio * (b - a);
                fd = f.f(d);
            }
        }

        return (a + b) / 2;
    }
}
