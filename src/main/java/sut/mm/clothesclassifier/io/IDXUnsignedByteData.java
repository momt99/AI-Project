package sut.mm.clothesclassifier.io;

import java.util.Arrays;
import java.util.List;

public class IDXUnsignedByteData extends IDXData {

    private static int convertToUnsignedByte(byte signed) {
        return 0xff & signed;
    }

    public IDXUnsignedByteData(IDXData base) {
        this(base.dimensions, base.rawData);
    }

    public IDXUnsignedByteData(List<Integer> dimensions, byte[][] rawData) {
        super(dimensions, rawData);
        if (rawData[0].length != IDXDataType.UBYTE.getSize())
            throw new IllegalArgumentException("Data type must be unsigned byte.");
    }

    public int[] getAllElements() {
        return Arrays.stream(getAllRawElements())
                .mapToInt(bytes -> convertToUnsignedByte(bytes[0]))
                .toArray();
    }

    /**
     * A shortcut method for 1D data to improve the performance
     */
    public int getElement(int i) {
        byte[] raw = getRawElement(i);
        return convertToUnsignedByte(raw[0]);
    }

    /**
     * A shortcut method for 2D data to improve the performance
     */
    public int getElement(int x, int y) {
        byte[] raw = getRawElement(x, y);
        return convertToUnsignedByte(raw[0]);
    }

    public int getElement(int... locations) {
        byte[] raw = getRawElement(locations);
        return convertToUnsignedByte(raw[0]);
    }

}
