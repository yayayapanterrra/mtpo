package samples;

import json.SampleData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NormalSample extends Sample {

    private double meanVal;
    private double stddevVal;

    private static final Random random = new Random();

    public NormalSample() {
        super();
        this.meanVal = 0.0;
        this.stddevVal = 1.0;
    }

    public NormalSample(double mean, double stddev) {
        super();
        this.meanVal = mean;
        this.stddevVal = stddev;
    }

    @Override
    public void generate(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Размер выборки должен быть положительным");
        }
        if (stddevVal <= 0) {
            throw new IllegalArgumentException("Стандартное отклонение должно быть положительным");
        }

        data.clear();

        if (data instanceof ArrayList) {
            ((ArrayList<Double>) data).ensureCapacity(n);
        }

        for (int i = 0; i < n; i++) {
            double randomValue = meanVal + stddevVal * random.nextGaussian();
            data.add(randomValue);
        }
    }

    @Override
    protected SampleData createSampleData() {
        return new SampleData("NormalSample", new ArrayList<>(data))
                .withNormalParams(meanVal, stddevVal);
    }

    @Override
    protected void restoreFromSampleData(SampleData sampleData) {
        this.meanVal = sampleData.meanVal;
        this.stddevVal = sampleData.stddevVal;
    }

    public double getMeanVal() {
        return meanVal;
    }

    public void setMeanVal(double meanVal) {
        this.meanVal = meanVal;
    }

    public double getStddevVal() {
        return stddevVal;
    }

    public void setStddevVal(double stddevVal) {
        this.stddevVal = stddevVal;
    }

    @Override
    public String toString() {
        return String.format("NormalSample[mean=%.2f, stddev=%.2f, size=%d]",
                meanVal, stddevVal, data.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NormalSample that = (NormalSample) obj;
        return Double.compare(that.meanVal, meanVal) == 0 &&
                Double.compare(that.stddevVal, stddevVal) == 0 &&
                data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(meanVal);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(stddevVal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + data.hashCode();
        return result;
    }
}