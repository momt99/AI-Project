package sut.mm.clothesclassifier.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class IDXReader {
    private DataInputStream input;

    private IDXDataType dataType;
    private int[] dimensions;

    public IDXReader(InputStream input) throws IOException {
        this.input = new DataInputStream(input);
        init();
    }

    private void init() throws IOException {
        readMagic();
        for (int i = 0; i < dimensions.length; i++)
            dimensions[i] = input.readInt();
    }

    private void readMagic() throws IOException {
        //Skipping the first two zero bytes
        input.readByte();
        input.readByte();

        dataType = IDXDataType.getByTypeValue(input.readUnsignedByte());
        dimensions = new int[input.readUnsignedByte()];
    }

    public IDXData nextData(int dimensionsCount) throws IOException {
        int readCount = IntStream.range(dimensions.length - dimensionsCount, dimensions.length)
                .map(i -> dimensions[i])
                .reduce(1, (left, right) -> left * right);

        byte[][] raw = new byte[readCount][dataType.getSize()];
        for (byte[] element : raw)
            if (input.read(element) != element.length)
                throw new IOException();
        return new IDXData(Arrays.copyOfRange(dimensions, dimensions.length - dimensionsCount, dimensions.length)
                , raw);
    }

}
