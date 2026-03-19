package samples;

import json.SampleData;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleSample extends Sample {

    public ConsoleSample() {
        super();
    }

    @Override
    public void generate(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Размер выборки должен быть положительным");
        }

        data.clear();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите " + n + " чисел выборки (каждое с новой строки):");

        int count = 0;
        while (count < n) {
            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                data.add(value);
                count++;
            } else {
                String invalid = scanner.next();
                System.out.println("Ошибка ввода. Введите число ещё раз:");
            }
        }
    }

    public void setData(List<Double> values) {
        data.clear();
        data.addAll(values);
    }

    @Override
    protected SampleData createSampleData() {
        return new SampleData("ConsoleSample", new ArrayList<>(data));
    }

    @Override
    protected void restoreFromSampleData(SampleData sampleData) {
    }

    @Override
    public String toString() {
        return String.format("ConsoleSample[size=%d]", data.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ConsoleSample that = (ConsoleSample) obj;
        return data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}