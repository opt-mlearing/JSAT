package jsat.datatransform;

import java.util.Arrays;

import jsat.DataSet;
import jsat.classifiers.CategoricalData;
import jsat.classifiers.DataPoint;
import jsat.linear.Matrix;
import jsat.linear.Vec;

/**
 * This class is used as a base class for simple linear projections of a
 * dataset. You must pass in the projection you want to use at construction. It
 * should be used only if you need a temporary transform object (will not be
 * saved) and know how you want to transform it, or to extend for use by another
 * class.
 *
 * @author Edward Raff
 */
public class ProjectionTransform implements DataTransform {
    protected Matrix P;
    protected Vec b;

    /**
     * @param P the projection matrix
     * @param b an offset to apply after projection (i.e., bias terms)
     */
    public ProjectionTransform(Matrix P, Vec b) {
        this.P = P;
        this.b = b;
    }

    public ProjectionTransform(ProjectionTransform toClone) {
        this(toClone.P.clone(), toClone.b.clone());
    }

    @Override
    public DataPoint transform(DataPoint dp) {
        Vec x_new = P.multiply(dp.getNumericalValues());
        x_new.mutableAdd(b);

        DataPoint newDP = new DataPoint(
                x_new,
                Arrays.copyOf(dp.getCategoricalValues(), dp.numCategoricalValues()),
                CategoricalData.copyOf(dp.getCategoricalData()));
        return newDP;
    }

    @Override
    public void fit(DataSet data) {
        //NOP
    }

    @Override
    public ProjectionTransform clone() {
        return new ProjectionTransform(this);
    }

}
