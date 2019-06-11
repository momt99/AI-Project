package sut.mm.clothesclassifier.classifiers;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by mahdihs76 on 6/11/19.
 */
public class DualPerceptronModel<TData> extends SupervisedModel<TData> {

    protected int trainCount = 0;
    private int featuresCount;
    private int labelsCount;
    protected RealVector[] ws;
    protected RealVector alpha;

    public DualPerceptronModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount) {
        super(featureProvider);
        this.featuresCount = featuresCount;
        this.alpha = new ArrayRealVector(featuresCount, 0);
        this.ws = new RealVector[labelsCount];
        Random r = new Random();
        for (int i = 0; i < ws.length; i++) {
            ws[i] = new ArrayRealVector(featuresCount);
            ws[i].setEntry(0, 1);   //bias
            for (int j = 1; j < ws[i].getDimension(); j++)
                ws[i].setEntry(j, r.nextDouble());
        }
    }

    @Override
    protected void train(RealVector features, int label) {
        trainCount++;
        int maxIndex = predictBestLabel(features);
        if (maxIndex == label)
            return;

        alpha.setEntry(label, alpha.getEntry(label) + 1);
        alpha.setEntry(maxIndex, alpha.getEntry(maxIndex) - 1);
    }

    @Override
    protected int predictBestLabel(RealVector features) {
        return IntStream.range(0, labelsCount)
                .boxed()
                .max(Comparator.comparingDouble(i -> alpha.getEntry(i) * similarityFunction(features, ws[i])))
                .get();
    }

    private double similarityFunction(RealVector features, RealVector vector) {
        return vector.dotProduct(features);
    }
}
