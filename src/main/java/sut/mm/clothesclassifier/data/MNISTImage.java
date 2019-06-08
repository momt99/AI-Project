package sut.mm.clothesclassifier.data;

import org.apache.commons.math3.linear.ArrayRealVector;
import sut.mm.clothesclassifier.io.IDXUnsignedByteData;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MNISTImage {
    public static final int WIDTH = 28, HEIGHT = 28;
    public static final FeatureProvider<MNISTImage> featureProvider = img -> new ArrayRealVector(
            IntStream.concat(
                    IntStream.of(1),    //bias
                    Arrays.stream(img.pixels)
                            .flatMapToInt(Arrays::stream))
                    .mapToDouble(value -> value * 1.0 / 255.0)
                    .toArray());


    private int[][] pixels;

    public MNISTImage(IDXUnsignedByteData idxData) {
        if (idxData.getDimensions().get(0) != WIDTH ||
                idxData.getDimensions().get(1) != HEIGHT)
            throw new IllegalArgumentException(String.format("Size must be: %dx%d", WIDTH, HEIGHT));
        pixels = new int[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                pixels[x][y] = idxData.getElement(x, y);
    }

}
