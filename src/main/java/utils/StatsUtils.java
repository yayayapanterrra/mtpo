package utils;

import samples.Sample;

import java.util.List;

public class StatsUtils {

    private StatsUtils() {
    }

    public static double correlation(Sample a, Sample b) {
        List<Double> x = a.getData();
        List<Double> y = b.getData();

        if (x.size() != y.size()) {
            throw new RuntimeException("Выборки должны быть одного размера");
        }
        if (x.size() < 2) {
            throw new RuntimeException("Недостаточно данных");
        }

        double meanX = a.mean();
        double meanY = b.mean();

        double num = 0.0;
        double sx = 0.0;
        double sy = 0.0;

        for (int i = 0; i < x.size(); i++) {
            double dx = x.get(i) - meanX;
            double dy = y.get(i) - meanY;
            num += dx * dy;
            sx += dx * dx;
            sy += dy * dy;
        }

        if (sx == 0.0 || sy == 0.0) {
            throw new RuntimeException("Нулевая дисперсия одной из выборок");
        }

        return num / Math.sqrt(sx * sy);
    }
}