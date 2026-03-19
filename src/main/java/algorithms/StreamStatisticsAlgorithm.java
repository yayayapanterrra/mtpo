package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamStatisticsAlgorithm implements StatisticsAlgorithm {

    @Override
    public String getName() {
        return "Stream-based (Functional)";
    }

    @Override
    public double min(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        return data.stream().mapToDouble(Double::doubleValue).min().orElseThrow();
    }

    @Override
    public double max(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        return data.stream().mapToDouble(Double::doubleValue).max().orElseThrow();
    }

    @Override
    public double mean(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        return data.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
    }

    @Override
    public double median(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        List<Double> temp = new ArrayList<>(data);
        Collections.sort(temp);
        int n = temp.size();
        int mid = n / 2;
        if (n % 2 == 0) {
            return (temp.get(mid - 1) + temp.get(mid)) / 2.0;
        } else {
            return temp.get(mid);
        }
    }

    @Override
    public double variance(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        if (data.size() < 2) throw new RuntimeException("Недостаточно данных");
        double m = mean(data);
        double sum = data.stream()
                .mapToDouble(x -> (x - m) * (x - m))
                .sum();
        return sum / (data.size() - 1);
    }

    @Override
    public double skewness(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        double m = mean(data);
        double v = variance(data);
        double sum = data.stream()
                .mapToDouble(x -> Math.pow(x - m, 3))
                .sum();
        return sum / data.size() / Math.pow(v, 1.5);
    }

    @Override
    public double kurtosis(List<Double> data) {
        int n = data.size();
        if (n < 4) throw new RuntimeException("Недостаточно данных для эксцесса");
        double mean = data.stream().mapToDouble(Double::doubleValue).average().orElseThrow();
        double sx2 = data.stream()
                .mapToDouble(x -> (x - mean) * (x - mean))
                .sum() / (n - 1);
        if (sx2 == 0.0) throw new RuntimeException("Дисперсия равна нулю");
        double sum4 = data.stream()
                .mapToDouble(x -> {
                    double d = x - mean;
                    return d * d * d * d;
                })
                .sum();
        double sum4Norm = sum4 / Math.pow(sx2, 2);
        return (n * (n + 1.0) / ((n - 1) * (n - 2) * (n - 3))) * sum4Norm
                - 3.0 * (n - 1) * (n - 1) / ((n - 2) * (n - 3));
    }
}