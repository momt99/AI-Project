package sut.mm.clothesclassifier.data;

import org.apache.commons.math3.linear.ArrayRealVector;
import sut.mm.clothesclassifier.io.IDXUnsignedByteData;

import java.util.Arrays;

public class MNISTImage {
    private static int WIDTH = 28, HEIGHT = 28;
    public static FeatureProvider<MNISTImage> featureProvider = img -> new ArrayRealVector(Arrays.stream(img.pixels)
            .flatMapToInt(Arrays::stream)
            .mapToDouble(value -> value * 1.0)
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
