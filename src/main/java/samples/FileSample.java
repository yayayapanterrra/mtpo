package samples;

import json.SampleData;

import java.util.ArrayList;
import java.util.List;

public class FileSample extends Sample {

    public FileSample() {
        super();
    }

    public FileSample(List<Double> values) {
        super();
        this.data.addAll(values);
    }

    @Override
    public void generate(int n) {
    }

    public void setData(List<Double> values) {
        data.clear();
        data.addAll(values);
    }

    @Override
    protected SampleData createSampleData() {
        return new SampleData("FileSample", new ArrayList<>(data));
    }

    @Override
    protected void restoreFromSampleData(SampleData sampleData) {
    }

    @Override
    public String toString() {
        return String.format("FileSample[size=%d]", data.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FileSample that = (FileSample) obj;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
