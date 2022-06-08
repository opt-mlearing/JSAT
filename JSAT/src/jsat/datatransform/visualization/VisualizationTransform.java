package jsat.datatransform.visualization;

import java.io.Serializable;

import jsat.DataSet;
import jsat.datatransform.DataTransform;

/**
 * Visualization Transform is similar to the {@link DataTransform} interface,
 * except it can not necessarily be applied to new datapoints. Classes
 * implementing this interface are intended to create 2D or 3D versions of a
 * dataset that can be visualized easily.<br>
 * <br>
 * By default, all implementations will create a 2D projection of the data if
 * supported.
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public interface VisualizationTransform extends Cloneable, Serializable {
    /**
     * @return the number of dimensions that a dataset will be embedded down to
     */
    public int getTargetDimension();


    /**
     * Sets the target dimension to embed new dataset to. Many visualization
     * methods may only support a target of 2 or 3 dimensions, or only one of
     * those options. For that reason a boolean value will be returned
     * indicating if the target size was acceptable. If not, no change to the
     * object will occur.
     *
     * @param target the new target dimension size when {@link #transform(jsat.DataSet)
     *               } is called.
     * @return {@code true} if this transform supports that dimension and it was
     * set, {@code false} if the target dimension is unsupported and the
     * previous value will be used instead.
     */
    public boolean setTargetDimension(int target);


    /**
     * Transforms the given data set, returning a dataset of the same type.
     *
     * @param <Type> the dataset type
     * @param d      the data set to transform
     * @return the lower dimension dataset for visualization.
     */
    default public <Type extends DataSet> Type transform(DataSet<Type> d) {
        return transform(d, false);
    }

    /**
     * Transforms the given data set, returning a dataset of the same type.
     *
     * @param <Type>   the dataset type
     * @param d        the data set to transform
     * @param parallel {@code true} if transform should be done in parallel, or
     *                 {@code false} if it should use a single thread.
     * @return the lower dimension dataset for visualization.
     */
    public <Type extends DataSet> Type transform(DataSet<Type> d, boolean parallel);
}
