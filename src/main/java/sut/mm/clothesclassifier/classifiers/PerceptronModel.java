package sut.mm.clothesclassifier.classifiers;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;
import sut.mm.clothesclassifier.utils.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PerceptronModel<TData> extends SupervisedModel<TData> {

    private final int featuresCount;
    private final int labelsCount;

    private RealVector[] ws;

    public PerceptronModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount) {
        super(featureProvider);
        this.featuresCount = featuresCount;
        this.labelsCount = labelsCount;
        this.ws = new RealVector[featuresCount];
        for (int i = 0; i < ws.length; i++) {
            ws[i] = new ArrayRealVector(featuresCount);
            ws[i].setEntry(0, 1);   //bias
        }
    }

    @Override
    protected void train(RealVector features, int label) {
        int maxIndex = predictBestLabel(features);
        if (maxIndex == label)
            return;

        ws[maxIndex].subtract(features);
        ws[label].add(features);
    }

    @Override
    public int predictBestLabel(RealVector features) {
        return IntStream.range(0, ws.length)
                .boxed()
                .max(Comparator.comparingDouble(i -> ws[i].dotProduct(features)))
                .get();
    }

    @Override
    public List<Pair<Integer, Double>> predict(RealVector features) {
        return IntStream.range(0, ws.length)
                .mapToObj(i -> new Pair<>(i, ws[i].dotProduct(features)))
                .collect(Collectors.toList());
    }
}
