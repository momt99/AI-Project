package sut.mm.clothesclassifier.classifiers;

import com.sun.org.apache.xml.internal.utils.IntVector;
import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;

import java.util.ArrayList;

public class DualPerceptronModel<TData> extends PerceptronModel<TData> {

    private ArrayList<RealVector> perceptionCache = new ArrayList<>();
    private static final double MAX_PERCEPTION = 60000;
    private IntVector[] alphas;

    public DualPerceptronModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount) {
        super(featureProvider, featuresCount, labelsCount);

        // alpha int vector for every label
        alphas = new IntVector[labelsCount];

        // initiate every class alpha vector to <1: 0,2: 0,...,MAX_PERCEPTION: 0>
        for (IntVector alpha : alphas) {
            for (int j = 0; j < MAX_PERCEPTION; j++) {
                alpha.setElementAt(0, j);
            }
        }
    }

    @Override
    protected void train(RealVector features, int label) {

        perceptionCache.add(features);

        int maxIndex = predictBestLabel(features);
        if (maxIndex == label)
            return;

        IntVector vector = alphas[maxIndex];
        IntVector vectorStar = alphas[label];

        // alpha(y,n) = alpha(y,n) - 1
        vector.setElementAt(vector.elementAt(trainCount) - 1, trainCount);
        // alpha(y*,n) = alpha(y*,n) + 1
        vectorStar.setElementAt(vectorStar.elementAt(trainCount) + 1, trainCount);

        trainCount++;
    }

    @Override
    public int predictBestLabel(RealVector features) {
        int max = Integer.MIN_VALUE;
        int maxLabel = Integer.MIN_VALUE;

        for (int i = 0; i < trainCount + 1; i++) {

            int currentResult = 0;
            IntVector currentVector = alphas[i];

            for (int j = 0; j < currentVector.size(); j++) {
                int currentValue = currentVector.elementAt(j);
                if (currentValue != 0){
                    currentResult += currentValue * kernel(perceptionCache.get(i), features);
                }
            }

            if (currentResult > max) {
                max = currentResult;
                maxLabel = i;
            }
        }

        return maxLabel;
    }

    private double kernel(RealVector vector1, RealVector vector2){
        return vector1.dotProduct(vector2);
    }

}
