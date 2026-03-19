package bdd;

import io.cucumber.java.ru.*;
import samples.UniformSample;
import algorithms.LoopStatisticsAlgorithm;
import algorithms.StreamStatisticsAlgorithm;
import utils.StatsUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CorrelationSteps {
    private UniformSample firstSample;
    private UniformSample secondSample;
    private double correlationResult;
    private double savedCorrelationResult;
    private Exception thrownException;

    //Вспом. метод парсит строку "1.0, 2.0, 3.0" в список Double
    private List<Double> parseValues(String valuesStr) {
        String[] parts = valuesStr.split(",");
        Double[] values = new Double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Double.parseDouble(parts[i].trim());
        }
        return Arrays.asList(values);
    }

    //Given

    //Background: алгоритм вычисления статистик установлен как "Loop-based (Imperative)"
    @Дано("алгоритм вычисления статистик установлен как {string}")
    public void setAlgorithm(String algorithmName) {
        firstSample = new UniformSample();
        secondSample = new UniformSample();
        switch (algorithmName) {
            case "Loop-based (Imperative)" -> {
                firstSample.setAlgorithm(new LoopStatisticsAlgorithm());
                secondSample.setAlgorithm(new LoopStatisticsAlgorithm());
            }
            case "Stream-based (Functional)" -> {
                firstSample.setAlgorithm(new StreamStatisticsAlgorithm());
                secondSample.setAlgorithm(new StreamStatisticsAlgorithm());
            }
            default -> throw new IllegalArgumentException("Неизвестный алгоритм " + algorithmName);
        }
    }

    //Given первая выборка содержит значения: 1.0, 2.0, 3.0, 4.0, 5.0
    @Дано("первая выборка содержит значения: {}")
    public void setFirstSampleData(String valuesStr) {
        firstSample.setData(parseValues(valuesStr));
    }

    //And вторая выборка содержит значения: 1.0, 2.0, 3.0, 4.0, 5.0
    @Дано("вторая выборка содержит значения: {}")
    public void setSecondSampleData(String valuesStr) {
        secondSample.setData(parseValues(valuesStr));
    }

    // When

    @Когда("вычисляется корреляция между выборками")
    public void computeCorrelation() {
        correlationResult = StatsUtils.correlation(firstSample, secondSample);
    }

    @Когда("пользователь пытается вычислить корреляцию между выборками")
    public void tryComputeCorrelation() {
        thrownException = null;
        try {
            correlationResult = StatsUtils.correlation(firstSample, secondSample);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    //When вычисляется корреляция с алгоритмом Loop-based (Imperative)
    @Когда("вычисляется корреляция с алгоритмом {string}")
    public void computeCorrelationWithAlgorithm(String algorithmName) {
        switch (algorithmName) {
            case "Loop-based (Imperative)" -> {
                firstSample.setAlgorithm(new LoopStatisticsAlgorithm());
                secondSample.setAlgorithm(new LoopStatisticsAlgorithm());
            }

            case "Stream-based (Functional)" -> {
                firstSample.setAlgorithm(new StreamStatisticsAlgorithm());
                secondSample.setAlgorithm(new StreamStatisticsAlgorithm());
            }

            default -> throw new IllegalArgumentException("Неизвестный алгоритм: " + algorithmName);
        }
        correlationResult = StatsUtils.correlation(firstSample, secondSample);
    }

    //Then коэффициент корреляции равен 1.0 с точностью 0.0001
    @Тогда("коэффициент корреляции равен {double} с точностью {double}")
    public void checkCorrelationEquals(double expected, double delta) {
        assertEquals(expected, correlationResult, delta,
                "Коэффициент корреляции " + correlationResult + " должен быть равен " + expected);
    }

    //Then коэффициент корреляции находится в диапазоне от -1.0 до 1.0
    @Тогда("коэффициент корреляции находится в диапазоне от {double} до {double}")
    public void checkCorrelationRange(double min, double max) {
        assertTrue(correlationResult >= min && correlationResult <= max,
                "Коэффициент корреляции " + correlationResult + " должен быть в диапазоне [" + min + ", " + max + "]");
    }


    @Тогда("вычисление завершается с ошибкой {string}")
    public void checkException(String expectedMessage) {
        assertNotNull(thrownException, "Ожидалось исключение с сообщением: " + expectedMessage);
        assertTrue(thrownException.getMessage().contains(expectedMessage),
                "Сообщение об ошибке должно содержать: " + expectedMessage +
                        "\nСодержимое: " + thrownException.getMessage());
    }

    //But корреляция не была вычислена
    @Тогда("корреляция не была вычислена")
    public void checkCorrelationNotComputed() {
        assertNotNull(thrownException, "Корреляция не должна быть вычислена, ожидалось исключение");
    }

    @Тогда("результат корреляции сохранен")
    public void saveCorrelationResult() {
        savedCorrelationResult = correlationResult;
    }

    @Тогда("текущий результат корреляции совпадает с сохраненным с точностью {double}")
    public void checkCorrelationMatchesSaved(double delta) {
        assertEquals(savedCorrelationResult, correlationResult, delta,
                "Текущий результат корреляции " + correlationResult + " должен совпадать с сохраненным "
                        + savedCorrelationResult);
    }

}
