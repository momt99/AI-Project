package sut.mm.clothesclassifier.classifiers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xml.internal.utils.IntVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DualPerceptronModel<TData> extends PerceptronModel<TData> {

    private transient ArrayList<RealVector> perceptionCache = new ArrayList<>();
    private static final int MAX_PERCEPTION = 60000;
    private RealVector[] alphas;

    public DualPerceptronModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount) {
        super(featureProvider, featuresCount, labelsCount);

        // alpha int vector for every label
        alphas = new RealVector[labelsCount];

        // initiate every class alpha vector to <1: 0,2: 0,...,MAX_PERCEPTION: 0>
        for (int i = 0; i < labelsCount; i++) {
            RealVector alpha = new ArrayRealVector(MAX_PERCEPTION);
            alphas[i] = alpha;
        }
    }

    @Override
    public void train(RealVector features, int label) {

        perceptionCache.add(features);

        int maxIndex = predictBestLabel(features);
        if (maxIndex == label) {
            trainCount++;
            return;
        }

        RealVector vector = alphas[maxIndex];
        RealVector vectorStar = alphas[label];

        // alpha(y,n) = alpha(y,n) - 1
        vector.setEntry(trainCount, vector.getEntry(trainCount) - 1);
        // alpha(y*,n) = alpha(y*,n) + 1
        vectorStar.setEntry(trainCount, vectorStar.getEntry(trainCount) + 1);

        trainCount++;
    }

    @Override
    public int predictBestLabel(RealVector features) {
        double max = Integer.MIN_VALUE;
        int maxLabel = Integer.MIN_VALUE;

        for (int i = 0; i < alphas.length; i++) {

            double currentResult = 0;
            RealVector currentVector = alphas[i];

            for (int j = 0; j < trainCount; j++) {
                double currentValue = currentVector.getEntry(j);
                if (currentValue != 0) {
                    currentResult += currentValue * kernel(perceptionCache.get(j), features);
                }
            }

            if (currentResult > max) {
                max = currentResult;
                maxLabel = i;
            }
        }

        return maxLabel;
    }

    private double kernel(RealVector vector1, RealVector vector2) {
        return vector1.dotProduct(vector2);
    }

    @Override
    public void save(String path) throws IOException {
        try (FileWriter writer = new FileWriter(Paths.get(path, toString() + ".json").toFile())) {
            new Gson().toJson(this, writer);
        }
    }

    @Override
    public void load(String path) throws IOException {
        try (FileReader reader = new FileReader(path)) {
            loadFromJson(new JsonParser().parse(reader).getAsJsonObject());
        }
    }

    protected void loadFromJson(JsonObject tree) {
        Gson deserializer = new Gson();
        this.alphas = deserializer.fromJson(tree.get("alphas"), ArrayRealVector[].class);
        this.trainCount = tree.get("trainCount").getAsInt();
    }

    @Override
    public String toString() {
        return String.format("Kernelized-%07d", trainCount);
    }
}
