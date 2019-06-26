package sut.mm.clothesclassifier.data;

import org.apache.commons.math3.linear.*;
import sut.mm.clothesclassifier.io.IDXUnsignedByteData;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MNISTImage {
    public static final int WIDTH = 28, HEIGHT = 28;
    public static final int featuresCount = WIDTH * HEIGHT * 3 + 1;

    public static final FeatureProvider<MNISTImage> featureProvider = new FeatureProvider<MNISTImage>() {
        private static final double LOGISTIC_K = 10;

        private double intense(double input) {
            return 1.0 / (1.0 + Math.pow(Math.E, LOGISTIC_K * (input - 0.5)));
        }

        @Override
        public RealVector getFeatures(MNISTImage img) {

            ArrayRealVector retVal = new ArrayRealVector(featuresCount);
            int lastIndex = 0;
            retVal.setEntry(lastIndex++, 1);
            for (int x = 0; x < WIDTH; x++)
                for (int y = 0; y < HEIGHT; y++)
                    retVal.setEntry(lastIndex++, img.getPixel(x, y));

            for (int x = 0; x < WIDTH; x++)
                for (int y = 0; y < HEIGHT; y++) {
                    int count = 0;
                    double sum = 0;
                    for (int i = Math.max(0, x - 2); i < Math.min(x + 2, WIDTH - 1); i++)
                        for (int j = Math.max(0, y - 2); j < Math.min(y + 2, HEIGHT - 1); j++) {
                            count++;
                            sum += intense(img.getPixel(x, y) - img.getPixel(i, j));
                        }
                    retVal.setEntry(lastIndex++, (sum - 1) / (count - 1));
                }

            //edge detection
            {
                RealMatrix gx = MatrixUtils.createRealMatrix(new double[][]{
                        {-1, 0, 1},
                        {-2, 0, 2},
                        {-1, 0, 1}});
                RealMatrix gy = MatrixUtils.createRealMatrix(new double[][]{
                        {-1, -2, -1},
                        {0, 0, 0},
                        {1, 2, 1}});
                for (int i = 1; i < HEIGHT - 1; i++) {
                    for (int j = 1; j < WIDTH - 1; j++) {
                        RealMatrix window = img.pixels.getSubMatrix(i - 1, i + 1, j - 1, j + 1);
                        retVal.setEntry(lastIndex++,
                                intense(Math.sqrt(
                                        Math.pow(window.walkInRowOrder(new Convolver(gx)), 2) +
                                                Math.pow(window.walkInRowOrder(new Convolver(gy)), 2))));
                    }
                }
            }

            return retVal;
        }

        class Convolver extends DefaultRealMatrixPreservingVisitor {
            RealMatrix other;
            double sum = 0;

            public Convolver(RealMatrix other) {
                this.other = other;
            }

            public void visit(int row, int column, double value) {
                sum += other.getEntry(row, column) * value;
            }

            @Override
            public double end() {
                return sum;
            }
        }

    };

    private RealMatrix pixels;

    public MNISTImage(IDXUnsignedByteData idxData) {
        if (idxData.getDimensions().get(0) != WIDTH ||
                idxData.getDimensions().get(1) != HEIGHT)
            throw new IllegalArgumentException(String.format("Size must be: %dx%d", WIDTH, HEIGHT));
        pixels = MatrixUtils.createRealMatrix(HEIGHT, WIDTH);
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                pixels.setEntry(y, x, idxData.getElement(y, x) / 255.0);
    }

    public double getPixel(int x, int y) {
        return pixels.getEntry(y, x);
    }
}
