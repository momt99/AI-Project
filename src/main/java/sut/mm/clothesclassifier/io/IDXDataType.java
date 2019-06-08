package sut.mm.clothesclassifier.io;

import java.util.Arrays;

public enum IDXDataType {
    UBYTE(0x08, 1, Byte.class),
    SBYTE(0x09, 1, Byte.class),
    SHORT(0x0B, 2, Short.class),
    INT(0x0C, 4, Integer.class),
    FLOAT(0x0D, 4, Float.class),
    DOUBLE(0x0E, 8, Double.class);

    private final int value;
    private final int size;
    private final Class<? extends Number> javaClass;

    IDXDataType(int value, int size, Class<? extends Number> javaClass) {
        this.value = value;
        this.size = size;
        this.javaClass = javaClass;
    }

    public static IDXDataType getByTypeValue(int value) {
        return Arrays.stream(IDXDataType.values())
                .filter(idxDataType -> idxDataType.value == value)
                .findFirst()
                .orElse(null);
    }

    public int getSize() {
        return size;
    }
}
