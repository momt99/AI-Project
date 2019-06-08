package sut.mm.clothesclassifier.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IDXData {
    protected final ArrayList<Integer> dimensions;
    protected final byte[][] rawData;

    public IDXData(int[] dimensions, byte[][] rawData) {
        this(Arrays.stream(dimensions).boxed().collect(Collectors.toList()), rawData);
    }

    public IDXData(List<Integer> dimensions, byte[][] rawData) {
        this.dimensions = new ArrayList<>(dimensions);
        this.dimensions.trimToSize();
        this.rawData = rawData;
    }

    public List<Integer> getDimensions() {
        return Collections.unmodifiableList(dimensions);
    }

    public byte[][] getAllRawElements() {
        return rawData;
    }

    /**
     * A shortcut method for 1D data to improve the performance
     */
    public byte[] getRawElement(int i) {
        if (dimensions.size() != 2)
            throw new InconsistentDimensionsException(dimensions.size());
        return rawData[i];
    }

    /**
     * A shortcut method for 2D data to improve the performance
     */
    public byte[] getRawElement(int x, int y) {
        if (dimensions.size() != 2)
            throw new InconsistentDimensionsException(dimensions.size());
        return rawData[x * dimensions.get(1) + y];
    }

    public byte[] getRawElement(int... location) {
        if (location.length != dimensions.size())
            throw new InconsistentDimensionsException(dimensions.size());

        int index = 0;
        for (int i = 0; i < dimensions.size(); i++)
            index = index * dimensions.get(i) + location[i];
        return rawData[index];
    }
}
