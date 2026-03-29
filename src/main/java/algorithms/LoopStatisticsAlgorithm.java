package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoopStatisticsAlgorithm implements StatisticsAlgorithm {

    @Override
    public String getName() {
        return "Loop-based (Imperative)";
    }

    @Override
    public double min(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        double min = Double.MAX_VALUE;
        for (double x : data) {
            if (x < min) min = x;
        }
        return min;
    }

    @Override
    public double max(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        double max = -Double.MAX_VALUE;
        for (double x : data) {
            if (x > max) max = x;
        }
        return max;
    }

    @Override
    public double mean(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        double sum = 0.0;
        for (double x : data) {
            sum += x;
        }
        return sum / data.size();
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
        double sum = 0.0;
        for (double x : data) {
            sum += (x - m) * (x - m);
        }
        return sum / (data.size() - 1);
    }

    @Override
    public double skewness(List<Double> data) {
        if (data.isEmpty()) throw new RuntimeException("Пустая выборка");
        double m = mean(data);
        double v = variance(data);
        double sum = 0.0;
        for (double x : data) {
            sum += Math.pow(x - m, 3);
        }
        //для лр3 нет проверки if (v==0.0) throw new RuntimeException()
        return sum / data.size() / Math.pow(v, 1.5);
    }

    @Override
    public double kurtosis(List<Double> data) {
        int n = data.size();
        if (n < 4) throw new RuntimeException("Недостаточно данных для эксцесса");
        double sum1 = 0.0;
        for (double x : data) sum1 += x;
        double mean = sum1 / n;
        double sx2 = 0.0;
        double sum4 = 0.0;
        for (double x : data) {
            double d = x - mean;
            sx2 += d * d;
            sum4 += d * d * d * d;
        }
        sx2 /= (n - 1);
        if (sx2 == 0.0) throw new RuntimeException("Дисперсия равна нулю");
        double sum4Norm = sum4 / Math.pow(sx2, 2);
        return (n * (n + 1.0) / ((n - 1) * (n - 2) * (n - 3))) * sum4Norm
                - 3.0 * (n - 1) * (n - 1) / ((n - 2) * (n - 3));
    }
}