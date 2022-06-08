package jsat.classifiers.trees;

import jsat.DataSet;

/**
 * @author Edward Raff <Raff.Edward@gmail.com>
 */
public class ImportanceByUses implements TreeFeatureImportanceInference {
    private boolean weightByDepth;

    public ImportanceByUses(boolean weightByDepth) {
        this.weightByDepth = weightByDepth;
    }

    public ImportanceByUses() {
        this(true);
    }


    @Override
    public <Type extends DataSet> double[] getImportanceStats(TreeLearner model, DataSet<Type> data) {
        double[] features = new double[data.getNumFeatures()];

        visit(model.getTreeNodeVisitor(), 0, features);

        return features;
    }

    private void visit(TreeNodeVisitor node, int curDepth, double[] features) {
        if (node == null)//invalid path was added, skip
            return;

        for (int feature : node.featuresUsed())
            if (weightByDepth)
                features[feature] += Math.pow(2, -curDepth);
            else
                features[feature]++;

        if (!node.isLeaf()) {
            for (int i = 0; i < node.childrenCount(); i++)
                visit(node.getChild(i), curDepth + 1, features);
        }
    }

}
