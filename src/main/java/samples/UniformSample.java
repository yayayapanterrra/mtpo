package samples;

import json.SampleData;

import java.util.ArrayList;
import java.util.List;

public class UniformSample extends Sample {

    private double minVal;
    private double maxVal;

    public UniformSample() {
        super();
        this.minVal = 0.0;
        this.maxVal = 1.0;
    }

    public UniformSample(double min, double max) {
        super();
        this.minVal = min;
        this.maxVal = max;
    }

    @Override
    public void generate(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Размер выборки должен быть положительным");
        }
        if (minVal >= maxVal) {
            throw new IllegalArgumentException("minVal должен быть меньше maxVal");
        }

        data.clear();

        if (data instanceof ArrayList) {
            ((ArrayList<Double>) data).ensureCapacity(n);
        }

        for (int i = 0; i < n; i++) {
            double randomValue = minVal + (maxVal - minVal) * Math.random();
            data.add(randomValue);
        }
    }

    @Override
    protected SampleData createSampleData() {
        return new SampleData("UniformSample", new ArrayList<>(data))
                .withUniformParams(minVal, maxVal);
    }

    @Override
    protected void restoreFromSampleData(SampleData sampleData) {
        this.minVal = sampleData.minVal;
        this.maxVal = sampleData.maxVal;
    }

    public double getMinVal() {
        return minVal;
    }

    public void setMinVal(double minVal) {
        this.minVal = minVal;
    }

    public double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(double maxVal) {
        this.maxVal = maxVal;
    }

    @Override
    public String toString() {
        return String.format("UniformSample[min=%.2f, max=%.2f, size=%d]",
                minVal, maxVal, data.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UniformSample that = (UniformSample) obj;
        return Double.compare(that.minVal, minVal) == 0 &&
                Double.compare(that.maxVal, maxVal) == 0 &&
                data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(minVal);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxVal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + data.hashCode();
        return result;
    }
}
