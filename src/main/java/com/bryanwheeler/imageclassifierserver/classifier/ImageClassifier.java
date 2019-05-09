package com.bryanwheeler.imageclassifierserver.classifier;

import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ImageClassifier {

    private final String modelFile =
            "/Users/student19/IdeaProjects/imageclassifierserver/src/main/resources/files/tensorflow_graph.pb";
    private final String labelsFile =
            "/Users/student19/IdeaProjects/imageclassifierserver/src/main/resources/files/image_labels.txt";
    private String imageFilePath;
    private byte[] imageFileArray = null;
    private String result;

    public ImageClassifier(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }
    public ImageClassifier(byte[] imageFileArray) { this.imageFileArray = imageFileArray; }

    private String getModelFile() { return modelFile; }
    private String getLabelsFile() { return labelsFile; }
    private String getImageFilePath() { return imageFilePath; }
    private byte[] getImageFileArray() { return imageFileArray; }
    public String getResult() { return result; }
    private void setResult(String result) { this.result = result; }

    public void classifyImage() {

        String result;
        List<String> labels;
        byte[] graphDef;
        byte[] imageBytes;

        graphDef = readAllBytesOrExit(Paths.get(this.getModelFile()));
        labels = readAllLinesOrExit(Paths.get(this.getLabelsFile()));

        if (this.getImageFileArray() == null) {
            imageBytes = readAllBytesOrExit(Paths.get(this.getImageFilePath()));
        } else {
            imageBytes = this.getImageFileArray();
        }

        try (Tensor image = Tensor.create(imageBytes)) {
            float[] labelProbabilities = executeInceptionGraph(graphDef, image);
            int bestLableIdx = maxIndex(labelProbabilities);
            result = String.format(
                    "%s (%.2f%%)",
                    labels.get(bestLableIdx), labelProbabilities[bestLableIdx] * 100f
            );
            this.setResult(result);
            System.out.println(result);
        }
    }

    private int maxIndex(float[] probabilities) {
        int best = 0;
        for(int i = 1; i < probabilities.length; ++i) {
            if(probabilities[i] > probabilities[best]){
                best = i;
            }
        }
        return best;
    }

    private List<String> readAllLinesOrExit(Path path) {
        try{
            return Files.readAllLines(path, Charset.forName("UTF-8"));
        }catch (IOException e){
            System.err.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static float[] executeInceptionGraph(byte[] grahDef, Tensor image) {
        try (Graph g = new Graph()) {
            g.importGraphDef(grahDef);
            try (Session s = new Session(g)) {
                try (Tensor<float[]> result = (Tensor<float[]>) s.runner()
                        .feed("DecodeJpeg/contents", image)
                        .fetch("softmax").run().get(0)) {
                    final long[] rShape = result.shape();
                    if (result.numDimensions() != 2 || rShape[0] != 1) {
                        throw new RuntimeException(
                                String.format(
                                        "Tensor shape error: shape %s", Arrays.toString(rShape)
                                )
                        );
                    }
                    int nLabels = (int) rShape[1];
                    return result.copyTo(new float[1][nLabels])[0];
                }
            }
        }
    }

    private static byte[] readAllBytesOrExit(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

//    public static void main(String[] args) {
//        ImageClassifier imageClassifier = new ImageClassifier(
//                "/Users/student19/IdeaProjects/imageclassifierserver/src/main/resources/images/dog.jpeg");
//        imageClassifier.classifyImage();
//    }

}
