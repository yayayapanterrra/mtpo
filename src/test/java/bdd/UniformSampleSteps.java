package bdd;

import io.cucumber.java.ru.*;
import samples.UniformSample;
import algorithms.LoopStatisticsAlgorithm;
import algorithms.StreamStatisticsAlgorithm;

import static org.junit.jupiter.api.Assertions.*;


public class UniformSampleSteps {
    private UniformSample sample;
    private double minParam;
    private double maxParam;
    private Exception thrownException;

    //Given
    @Дано("алгоритм обработки установлен как {string}")
    public void setAlgorithm(String algorithmName) {
        sample = new UniformSample();
        switch (algorithmName) {
            case "Loop-based (Imperative)" -> sample.setAlgorithm(new LoopStatisticsAlgorithm());
            case "Stream-based (Functional)" -> sample.setAlgorithm(new StreamStatisticsAlgorithm());
            default -> throw new IllegalArgumentException("Неизвестный алгоритм: " + algorithmName);
        }
    }

    @Дано("пользователь задает минимальное значение {double} и максимальное значение {double}")
    public void setRange(double min, double max) {
        this.minParam = min;
        this.maxParam = max;
        sample = new UniformSample(min, max);
        sample.setAlgorithm(new LoopStatisticsAlgorithm());
    }

    //When
    @Когда("пользователь генерирует выборку размером {int} элементов")
    public void generateSample(int n) {
        sample.generate(n);
    }

    @Когда("пользователь генерирует выборку размером {int} элемент")
    public void generateSampleSingle(int n) {
        sample.generate(n);
    }

    @Когда("пользователь пытается сгенерировать выборку размером {int} элементов")
    public void tryGenerateSample(int n) {
        try {
            sample = new UniformSample(minParam, maxParam);
            sample.generate(n);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    //Then
    @Тогда("выборка содержит ровно {int} элементов")
    public void checkSize(int expected) {
        assertEquals(expected, sample.getData().size(), "Размер выборки должен быть " + expected);
    }

    @Тогда("минимальное значение выборки не больше {double}")
    public void checkMax(double expectedMax) {
        double actualMax = sample.max();
        assertTrue(actualMax == expectedMax, "Максимум " + actualMax + " должен быть не больше " + expectedMax);
    }

    @Тогда("максимальное значение выборки не меньше {double}")
    public void checkMin(double expectedMin) {
        double actualMin = sample.min();
        assertTrue(actualMin >= expectedMin, "Минимум " + actualMin + " должен быть не меньше " + expectedMin);

    }

    @Тогда("среднее значение близко к {double} с точностью {double}")
    public void checkMean(double expected, double delta) {
        double actualMean = sample.mean();
        assertEquals(expected, actualMean, delta, "Среднее " + actualMean + " должно быть близко к " + expected);
    }

    @Тогда("генерация завершается с ошибкой {string}")
    public void checkException(String expectedMessage) {
        assertNotNull(thrownException, "Ожидалось исключение с сообщением: " + expectedMessage);
        assertTrue(thrownException.getMessage().contains(expectedMessage), "Сообщение об ошибке должно содержать: "
                + expectedMessage + "\nФактическое: " + thrownException.getMessage());
    }
}
