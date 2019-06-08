package sut.mm.clothesclassifier.io;

public class InconsistentDimensionsException extends RuntimeException {
    public InconsistentDimensionsException(int expected) {
        super("Coordinates must be in size of " + expected);
    }
}
