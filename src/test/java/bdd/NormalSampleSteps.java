package bdd;

import io.cucumber.java.ru.*;
import samples.NormalSample;
import algorithms.LoopStatisticsAlgorithm;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NormalSampleSteps {

    private NormalSample sample;
    private Exception thrownException;

    //Given

    //Background нормальная выборка создана со средним 0.0 и стандартным отклонением 1.0
    @Дано("нормальная выборка создана со средним {double} И стандартным отклонением {double}")
    public void createNormalSample(double mean, double stddev) {
        sample = new NormalSample(mean, stddev);
        sample.setAlgorithm(new LoopStatisticsAlgorithm());
    }

    //When выборка сгенерирована со средним 5.0 и стандартным отклонением 2.0 размером 500 элементов
    @Когда("выборка сгенерирована со средним {double} и стандартным отклонением {double} размером {int} элементов")
    public void generateSampleWithParams(double mean, double stddev, int n) {
        sample = new NormalSample(mean, stddev);
        sample.setAlgorithm(new LoopStatisticsAlgorithm());
        sample.generate(n);
    }

    //When пользователь пытается создать нормальную выборку со стандартным отклонением -1.0 размером 100 элементов
    @Когда("пользователь пытается создать нормальную выборку со стандартным отклонением {double} размером {int} элементов")
    public void tryGenerateSample(double stddev, int n) {
        try {
            sample = new NormalSample();
            sample.setAlgorithm(new LoopStatisticsAlgorithm());
            sample.generate(n);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    //Then выборка яввляется нормально распределенной на уровне значимости {double}
    @Тогда("выборка является нормально распределенной на уровне значимости {double}")
    public void checkNormality(double alpha) {
        assertTrue(sample.isNormal(alpha),
                "Выборка должны быть нормально распределенной на уровне значимости " + alpha);
    }

    //Then среднее выборки близко к 0.0 с точностью 0.15
    @Тогда("среднее выборки близко к {double} с точностью {double}")
    public void checkMean(double expected, double delta) {
        double actual = sample.mean();
        assertEquals(expected, actual, delta,
                "Среднее " + actual + " должно быть близко к " + expected);
    }

    //Then стандартное отклонение выборки близко к 1.0 с точностью 0.5
    @Тогда("выборка не имеет среднее близкое к {double} с точностью {double}")
    public void checkMeanNotCloseTo(double unexpected, double delta) {
        double actual = sample.mean();
        assertNotEquals(unexpected, actual, delta,
                "Среднее " + actual + " не должно быть близко к " + unexpected);
    }

    @Тогда("первый квартиль меньше медианы")
    public void checkQ1LessThanMedian() {
        List<Double> quartiles = sample.quartiles();
        double q1 = quartiles.get(0);
        double median = quartiles.get(1);
        assertTrue(q1 < median, "Первый квартиль " + q1 + " должен быть меньше медианы " + median);
    }

    @Тогда("медиана меньше третьего квартиля")
    public void checkMedianLessThanQ3() {
        List<Double> quartiles = sample.quartiles();
        double q3 = quartiles.get(2);
        double median = quartiles.get(1);
        assertTrue(q3 > median, "Медиана " + median + " должна быть меньше третьего квартиля " + q3);

    }

    // Then размер выборки равен 500
    @Тогда("размер выборки равен {int}")
    public void checkSize(int expected) {
        assertEquals(expected, sample.getData().size(), "Размер выборки должен быть " + expected);
    }

    @Тогда("генерация завершается с ошибкой {string}")
    public void checkException(String expectedMessage) {
        assertNotNull(thrownException, "Ожидалось исключение с сообщением: " + expectedMessage);
        assertTrue(thrownException.getMessage().contains(expectedMessage),
                "Сообщение об ошибке должно содержать: " + expectedMessage +
                        "\nФактическое: " + thrownException.getMessage());
    }

}
