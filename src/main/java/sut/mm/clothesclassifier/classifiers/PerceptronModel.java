package sut.mm.clothesclassifier.classifiers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.data.FeatureProvider;
import sut.mm.clothesclassifier.utils.Pair;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PerceptronModel<TData> extends SupervisedModel<TData> {

    private final int featuresCount;
    private int labelsCount;
    private double learningRate;
    protected int trainCount = 0;

    protected RealVector[] ws;

    public PerceptronModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount, double learningRate) {
        super(featureProvider);
        this.featuresCount = featuresCount;
        this.labelsCount = labelsCount;
        this.learningRate = learningRate;
        this.ws = new RealVector[labelsCount];
        Random r = new Random();
        for (int i = 0; i < ws.length; i++) {
            ws[i] = new ArrayRealVector(featuresCount);
//            ws[i].setEntry(0, 1);   //bias
//            for (int j = 1; j < ws[i].getDimension(); j++)
//                ws[i].setEntry(j, r.nextDouble());
        }
    }

    public PerceptronModel(FeatureProvider<TData> featureProvider, int featuresCount, int labelsCount) {
        this(featureProvider, featuresCount, labelsCount, 1);
    }

    public List<RealVector> getWeights() {
        return Arrays.asList(ws);
    }

    @Override
    public void train(RealVector features, int label) {
        trainCount++;
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
        this.ws = deserializer.fromJson(tree.get("ws"), ArrayRealVector[].class);
        this.labelsCount = this.ws.length;
        this.learningRate = tree.get("learningRate").getAsDouble();
        this.trainCount = tree.get("trainCount").getAsInt();
    }

    @Override
    public String toString() {
        return String.format("Perceptron-%07d-%f", trainCount, learningRate);
    }
}
