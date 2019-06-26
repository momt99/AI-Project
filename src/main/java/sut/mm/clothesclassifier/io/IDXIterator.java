package sut.mm.clothesclassifier.io;

import sut.mm.clothesclassifier.utils.Pair;

import java.io.IOException;
import java.util.Iterator;

public class IDXIterator implements Iterator<Pair<IDXUnsignedByteData, Integer>> {
    IDXReader labelReader;
    IDXReader imgReader;
    int size;
    int i = 0;

    public IDXIterator(IDXReader labelReader, IDXReader imgReader) {
        this.labelReader = labelReader;
        this.imgReader = imgReader;
        size = labelReader.getDimensions()[0];
    }

    @Override
    public boolean hasNext() {
        return i < size;
    }

    @Override
    public Pair<IDXUnsignedByteData, Integer> next() {
        i++;
        try {
            return new Pair<>(new IDXUnsignedByteData(imgReader.nextData(2)),
                    new IDXUnsignedByteData(labelReader.nextData(0)).getElement());
        } catch (IOException e) {
            e.printStackTrace();
        }
        i = size;
        return null;
    }
}
