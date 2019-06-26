package sut.mm.clothesclassifier;

import org.apache.commons.math3.linear.RealVector;
import sut.mm.clothesclassifier.classifiers.DualPerceptronModel;
import sut.mm.clothesclassifier.classifiers.PerceptronModel;
import sut.mm.clothesclassifier.data.MNISTImage;
import sut.mm.clothesclassifier.io.IDXReader;
import sut.mm.clothesclassifier.utils.Pair;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class KernelizedTrainer {
    public static void main(String[] args) throws IOException {
        DualPerceptronModel<MNISTImage> model = new DualPerceptronModel<>(MNISTImage.featureProvider,
                MNISTImage.featuresCount,
                10);

        ArrayList<Pair<RealVector, Integer>> list;

        try (IDXReader labelReader = new IDXReader(new BufferedInputStream(new FileInputStream(FilePaths.TRAIN_LABEL_PATH), 1024 * 1024 * 10));
             IDXReader imgReader = new IDXReader(new BufferedInputStream(new FileInputStream(FilePaths.TRAIN_DATA_PATH), 1024 * 1024 * 10))) {
            list = StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Test2.IDXIterator(labelReader, imgReader), Spliterator.ORDERED),
                    false)
                    .map(pair -> new Pair<>(MNISTImage.featureProvider.getFeatures(new MNISTImage(pair.getFirst())), pair.getSecond()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        System.out.println("Initialization finished");

        int counter = 0;
        for (Pair<RealVector, Integer> pair : list) {
            model.train(pair.getFirst(), pair.getSecond());
            counter++;
            if (counter % 100 == 0)
                System.out.println(counter);
            if (counter == 10000) {
                float percent = model.evaluateRaw(list.subList(10000, 20000));
                System.out.println(percent);
                return;
            }
        }
    }
}
