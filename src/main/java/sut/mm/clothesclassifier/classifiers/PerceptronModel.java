package sut.mm.clothesclassifier.classifiers;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;
import sut.mm.clothesclassifier.utils.Pair;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PerceptronModel<TData> extends SupervisedModel<TData> {

    private final int featuresCount;
    private final int labelsCount;
    private final double learningRate;

    private RealVector[] ws;

    public PerceptronModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount, double learningRate) {
        super(featureProvider);
        this.featuresCount = featuresCount;
        this.labelsCount = labelsCount;
        this.learningRate = learningRate;
        this.ws = new RealVector[labelsCount];
        Random r = new Random();
        for (int i = 0; i < ws.length; i++) {
            ws[i] = new ArrayRealVector(featuresCount);
            ws[i].setEntry(0, 1);   //bias
            for (int j = 1; j < ws[i].getDimension(); j++)
                ws[i].setEntry(j, r.nextDouble());
        }
    }

    public List<RealVector> getWeights() {
        return Arrays.asList(ws);
    }

    @Override
    protected void train(RealVector features, int label) {
        int maxIndex = predictBestLabel(features);
        if (maxIndex == label)
            return;

        features.mapMultiplyToSelf(learningRate);
        ws[maxIndex] = ws[maxIndex].subtract(features);
        ws[label] = ws[label].add(features);
    }

    @Override
    public int predictBestLabel(RealVector features) {
        return IntStream.range(0, labelsCount)
                .boxed()
                .max(Comparator.comparingDouble(i -> ws[i].dotProduct(features)))
                .get();
    }

    @Override
    public List<Pair<Integer, Double>> predict(RealVector features) {
        return IntStream.range(0, labelsCount)
                .mapToObj(i -> new Pair<>(i, ws[i].dotProduct(features)))
                .collect(Collectors.toList());
    }
}
