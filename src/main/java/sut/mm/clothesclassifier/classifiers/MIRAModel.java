package sut.mm.clothesclassifier.classifiers;

import com.google.gson.JsonObject;
import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;

import java.io.IOException;

public class MIRAModel<TData> extends PerceptronModel<TData> {

    private double margin;

    public MIRAModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount, double margin) {
        super(featureProvider, featuresCount, labelsCount);
        this.margin = margin;
    }

    @Override
    protected void train(RealVector features, int label) {
        trainCount++;
        int maxIndex = predictBestLabel(features);
        if (maxIndex == label)
            return;

        double t = (ws[maxIndex].subtract(ws[label]).dotProduct(features) + margin) /
                (2 * features.dotProduct(features));
        features.mapMultiplyToSelf(t);
        ws[maxIndex] = ws[maxIndex].subtract(features);
        ws[label] = ws[label].add(features);
    }

    @Override
    protected void loadFromJson(JsonObject tree) {
        super.loadFromJson(tree);
        this.margin = tree.get("margin").getAsDouble();
    }

    @Override
    public String toString() {
        return String.format("MIRA-%d-%f", trainCount, margin);
    }
}
