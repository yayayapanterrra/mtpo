package algorithms;

import java.util.List;

public interface StatisticsAlgorithm {

    double min(List<Double> data);

    double max(List<Double> data);

    double mean(List<Double> data);

    double median(List<Double> data);

    double variance(List<Double> data);

    double skewness(List<Double> data);

    double kurtosis(List<Double> data);

    String getName();
}