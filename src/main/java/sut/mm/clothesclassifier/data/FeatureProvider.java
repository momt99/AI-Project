package sut.mm.clothesclassifier.data;

import org.apache.commons.math3.linear.RealVector;

public interface FeatureProvider<TData> {
    RealVector getFeatures(TData data);
}
