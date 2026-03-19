package json;

import java.util.List;

public class SampleData {

    public String sampleType;
    public List<Double> data;
    public double minVal;
    public double maxVal;
    public double meanVal;
    public double stddevVal;

    public SampleData() {
    }

    public SampleData(String sampleType, List<Double> data) {
        this.sampleType = sampleType;
        this.data = data;
    }

    public SampleData withUniformParams(double min, double max) {
        this.minVal = min;
        this.maxVal = max;
        return this;
    }

    public SampleData withNormalParams(double mean, double stddev) {
        this.meanVal = mean;
        this.stddevVal = stddev;
        return this;
    }
}