package jsat.classifiers.trees;

import java.io.Serializable;

import jsat.DataSet;

/**
 * This interface exists for implementing the importance of features from tree
 * based models.
 *
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public interface TreeFeatureImportanceInference extends Serializable {

    /**
     * @param <Type>
     * @param model  the tree model to infer feature importance from
     * @param data   the dataset to use for importance inference. Should be either
     *               a Classification or Regression dataset, depending on the type of the
     *               model.
     * @return a double array with one entry for each feature. Numeric features
     * start first, followed by categorical features. Larger values indicate
     * higher importance, and all values must be non-negative. Otherwise, no
     * constraints are placed on the output of this function.
     */
    public <Type extends DataSet> double[] getImportanceStats(TreeLearner model, DataSet<Type> data);
}
