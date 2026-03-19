package bdd;

import io.cucumber.java.ru.*;
import algorithms.LoopStatisticsAlgorithm;
import algorithms.StreamStatisticsAlgorithm;
import algorithms.StatisticsAlgorithm;
import samples.NormalSample;
import samples.UniformSample;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticsSteps {

    private StatisticsAlgorithm loopAlgorithm;
    private StatisticsAlgorithm streamAlgorithm;

    private List<Double> data;

    //результат последнего вычисления для каждого алгоритма
    private double loopResult;
    private double streamResult;

    //время выполнения в мс
    private long loopTimeMs;
    private long streamTimeMs;

    //перехваченное исключение для негативных сценариев
    private Exception thrownException;

    //вспом. методы
    private List<Double> parseValues(String valuesStr){
        String[] parts = valuesStr.split(",");
        Double[] values = new Double[parts.length];
        for (int i = 0; i < values.length; i++){
            values[i] = Double.parseDouble(parts[i].trim());
        }
        return Arrays.asList(values);
    }

    private StatisticsAlgorithm resolveAlgorithm(String name){
        return switch (name){
            case "Loop-based (Imperative)" -> loopAlgorithm;
            case "Stream-based (Functional)" -> streamAlgorithm;
            default -> throw new IllegalArgumentException("Неизвестный алгоритм " + name);
        };
    }

    //Given

    //Background: оба алгоритма инициализированы
    @Дано("оба алгоритма инициализированы: {string} и {string}")
    public void initBothAlgorithms(String loopName, String streamName){
        loopAlgorithm = new LoopStatisticsAlgorithm();
        streamAlgorithm = new StreamStatisticsAlgorithm();
    }

    @Дано("выборка содержит значения: {}")
    public void setSampleData(String valuesStr){
        data = parseValues(valuesStr);
    }

    @Дано("выборка пустая")
    public void setEmptySampleData(){
        data = Collections.emptyList();
    }

    //Given равномерная выборка сгенерирована размером <size> элементов в диапазоне от 0.0 до 1000.0
    @Дано("равномерная выборка сгенерирована размером {int} элементов в диапазоне от {double} до {double}")
    public void generateUniformSample(int size, double min, double max){
        UniformSample sample = new UniformSample(min, max);
        sample.generate(size);
        data = sample.getData();
    }

    //Given нормальная выборка сгенерирована со средним 0.0 и стандартным отклонением 1.0 размером <size> элементов
    @Дано("нормальная выборка сгенерирована со средним {double} и стандартным отклонением {double} размером {int} элементов")
    public void generateNormalSample(double mean, double stddev, int size){
        NormalSample sample = new NormalSample(mean, stddev);
        sample.generate(size);
        data = sample.getData();
    }

    //When вычисление конкретных статистик обоими алгоритмами
    @Когда("вычисляется минимум обоими алгоритмами")
    public void computeMin(){
        loopResult = loopAlgorithm.min(data);
        streamResult = streamAlgorithm.min(data);
    }

    @Когда("вычисляется максимум обоими алгоритмами")
    public void computeMax(){
        loopResult = loopAlgorithm.max(data);
        streamResult = streamAlgorithm.max(data);
    }

    @Когда("вычисляется среднее обоими алгоритмами")
    public void computeMean(){
        loopResult = loopAlgorithm.mean(data);
        streamResult = streamAlgorithm.mean(data);
    }

    @Когда("вычисляется медиана обоими алгоритмами")
    public void computeMedian() {
        loopResult = loopAlgorithm.median(data);
        streamResult = streamAlgorithm.median(data);
    }

    @Когда("вычисляется дисперсия обоими алгоритмами")
    public void computeVariance() {
        loopResult = loopAlgorithm.variance(data);
        streamResult = streamAlgorithm.variance(data);
    }

    @Когда("вычисляется асимметрия обоими алгоритмами")
    public void computeSkewness() {
        loopResult = loopAlgorithm.skewness(data);
        streamResult = streamAlgorithm.skewness(data);
    }

    @Когда("вычисляется эксцесс обоими алгоритмами")
    public void computeKurtosis() {
        loopResult = loopAlgorithm.kurtosis(data);
        streamResult = streamAlgorithm.kurtosis(data);
    }

    //When пользователь пытается вычислить минимум или эксцесс алгоритмом
    @Когда("пользователь пытается вычислить минимум алгоритмом {string}")
    public void tryComputeMin(String algorithmName){
        thrownException = null;
        try{
            resolveAlgorithm(algorithmName).min(data);
        }catch(Exception e){
            thrownException = e;
        }
    }

    @Когда("пользователь пытается вычислить эксцесс алгоритмом {string}")
    public void tryComputeKurtosis(String algorithmName){
        thrownException = null;
        try{
            resolveAlgorithm(algorithmName).kurtosis(data);
        }catch(Exception e){
            thrownException = e;
        }
    }

    //When измеряется время вычисления всех статистик алгоритмом
    @Когда("измеряется время вычисления всех статистик алгоритмом {string}")
    public void measureAllStatsTime(String algorithmName){
        StatisticsAlgorithm algo = resolveAlgorithm(algorithmName);
        long start = System.currentTimeMillis();
        loopResult = algo.min(data);
        double max = algo.max(data);
        double mean = algo.mean(data);
        double median = algo.median(data);
        double variance = algo.variance(data);
        double skewness = algo.skewness(data);
        double kurtosis = algo.kurtosis(data);
        long elapsed = System.currentTimeMillis()-start;

        if (algorithmName.contains("Loop")){
            loopTimeMs = elapsed;
            loopResult = mean;
        } else{
            streamTimeMs = elapsed;
            streamResult = mean;
        }
    }

}
