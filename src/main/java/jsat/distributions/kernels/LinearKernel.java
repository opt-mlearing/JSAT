package jsat.distributions.kernels;

import jsat.linear.Vec;

/**
 * Provides a linear kernel function, which computes the normal dot product.
 * k(x,y) = x.y + c
 *
 * @author Edward Raff
 */
public class LinearKernel extends BaseKernelTrick {

    private static final long serialVersionUID = -1870181048970135367L;
    private double c;

    /**
     * Creates a new Linear Kernel that computes the dot product and offsets it by a specified value
     *
     * @param c the positive bias term for the dot product
     */
    public LinearKernel(double c) {
        this.c = c;
    }

    /**
     * Creates a new Linear Kernel with an added bias term of 1
     */
    public LinearKernel() {
        this(1);
    }

    /**
     * The positive bias term added to the result of the dot product
     *
     * @param c the added product term
     */
    public void setC(double c) {
        if (c < 0 || Double.isInfinite(c) || Double.isNaN(c))
            throw new IllegalArgumentException("C must be a positive constant, not " + c);
        this.c = c;
    }

    /**
     * Returns the positive additive term
     *
     * @return the positive additive term
     */
    public double getC() {
        return c;
    }


    @Override
    public double eval(Vec a, Vec b) {
        return a.dot(b) + c;
    }

    @Override
    public String toString() {
        return "Linear Kernel (c=" + c + ")";
    }

    @Override
    public LinearKernel clone() {
        return new LinearKernel(c);
    }
}
