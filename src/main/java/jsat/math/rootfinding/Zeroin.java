package jsat.math.rootfinding;

import static java.lang.Math.*;

import jsat.math.Function1D;

/**
 * This class provides an implementation of the popular ZeroIn root finder.
 *
 * @author Edward Raff
 */
public class Zeroin implements RootFinder {

    private static final long serialVersionUID = -8359510619103768778L;

    /**
     * Performs root finding on the function {@code f}.
     *
     * @param a the left bound on the root (i.e., f(a) &lt; 0)
     * @param b the right bound on the root (i.e., f(b) &gt; 0)
     * @param f the function to find the root of
     * @return the value of variable {@code pos} that produces a zero value
     * output
     */
    public static double root(double a, double b, Function1D f) {
        return root(1e-15, 1000, a, b, f);
    }

    /**
     * Performs root finding on the function {@code f}.
     *
     * @param eps the desired accuracy of the result
     * @param a   the left bound on the root (i.e., f(a) &lt; 0)
     * @param b   the right bound on the root (i.e., f(b) &gt; 0)
     * @param f   the function to find the root of
     * @return the value of variable {@code pos} that produces a zero value
     * output
     */
    public static double root(double eps, double a, double b, Function1D f) {
        return root(eps, 1000, a, b, f);
    }

    /**
     * Performs root finding on the function {@code f}.
     *
     * @param eps           the desired accuracy of the result
     * @param maxIterations the maximum number of iterations to perform
     * @param a             the left bound on the root (i.e., f(a) &lt; 0)
     * @param b             the right bound on the root (i.e., f(b) &gt; 0)
     * @param f             the function to find the root of
     * @return the value of variable {@code pos} that produces a zero value
     * output
     */
    public static double root(double eps, int maxIterations, double a, double b, Function1D f) {
        /*
         * Code has few comments, taken fro algorithum descriptoin http://en.wikipedia.org/wiki/Brent%27s_method#Algorithm ,
         * which is from Brent's book (according to comments, I would like to get the book either way)
         *
         */

        double fa = f.f(a);
        double fb = f.f(b);

        if (abs(fa - 0) < 2 * eps)
            return a;
        if (abs(fb - 0) < 2 * eps)
            return b;

        if (fa * fb >= 0)
            throw new ArithmeticException("The given search interval does not appear to contain the root ");

        if (abs(fa) < abs(fb)) //swap
        {
            double tmp = a;
            a = b;
            b = tmp;

            tmp = fa;
            fa = fb;
            fb = tmp;
        }


        double c = a;
        double fc = fa;
        boolean mflag = true;
        double s;
        double d = 0;//inital value dosnt matter, and will not be used

        double fs;

        do {
            if (fa != fc && fb != fc)//inverse quadratic interpolation
            {
                s = a * fb * fc / ((fa - fb) * (fa - fc)) + b * fa * fc / ((fb - fa) * (fb - fc)) + c * fa * fb / ((fc - fa) * (fc - fb));
            } else//secant rule
            {
                s = b - fb * (b - a) / (fb - fa);
            }


            //Determin wethor or not we must use bisection

            boolean cond1 = (s - (3 * a + b) / 4) * (s - b) >= 0;
            boolean cond2 = mflag && (abs(s - b) >= (abs(b - c) / 2));
            boolean cond3 = !mflag && (abs(s - b) >= (abs(c - d) / 2));
            boolean cond4 = mflag && (abs(b - c) < 2 * eps);
            boolean cond5 = !mflag && abs(c - d) < 2 * eps;

            if (cond1 || cond2 || cond3 || cond4 || cond5)//Bisection must be used
            {
                s = (a + b) / 2;
                mflag = true;
            } else
                mflag = false;

            fs = f.f(s);
            d = c;
            c = b;

            //adjust the interval accordingly
            if (fa * fs < 0) {
                b = s;
                fb = fs;
            } else {
                a = s;
                fa = fs;
            }

            if (abs(fa) < abs(fb))//swap
            {
                double tmp = a;
                a = b;
                b = tmp;

                tmp = fa;
                fa = fb;
                fb = tmp;

            }

        }
        while (fb != 0.0 && fs != 0.0 && abs(b - a) > 2 * eps && maxIterations-- > 0);


        return b;
    }

    @Override
    public double root(double eps, int maxIterations, double[] initialGuesses, Function1D f) {
        return root(eps, maxIterations, initialGuesses[0], initialGuesses[1], f);
    }

    @Override
    public int guessesNeeded() {
        return 2;
    }
}
