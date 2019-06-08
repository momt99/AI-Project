package sut.mm.clothesclassifier.classifiers;

import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;
import sut.mm.clothesclassifier.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class SupervisedModel<TData> {
    protected FeatureProvider<TData> featureProvider;

    public SupervisedModel(FeatureProvider<TData> featureProvider) {
        this.featureProvider = featureProvider;
    }

    /**
     * Inputs the data and trains the model
     *
     * @param data  the data which provides features
     * @param label the expected label
     */
    public void train(TData data, int label) {
        train(featureProvider.getFeatures(data), label);
    }

    protected abstract void train(RealVector features, int label);

    /**
     * Predicts the best label for the given data
     *
     * @param data the data which provides features
     * @return the label with the best score
     */
    public int predictBestLabel(TData data) {
        return predictBestLabel(featureProvider.getFeatures(data));
    }

    protected abstract int predictBestLabel(RealVector features);

    /**
     * Predicts a score for each possible label
     *
     * @param data the data which provides features
     * @return the labels with their scores
     */
    public List<Pair<Integer, Double>> predict(TData data) {
        return predict(featureProvider.getFeatures(data));
    }

    public List<Pair<Integer, Double>> predict(RealVector features) {
        throw new UnsupportedOperationException();
    }

    /**
     * Check the trained model against a test set
     *
     * @param testData The set including the data to be tested and expected labels
     * @return The percentage of correct predicts
     */
    public float evaluate(Iterable<Pair<TData, Integer>> testData) {
        int count = 0, correct = 0;
        for (Pair<TData, Integer> testDatum : testData) {
            count++;
            if (predictBestLabel(testDatum.getFirst()) == testDatum.getSecond())
                correct++;
        }
        return correct * 1f / count;
    }
}
