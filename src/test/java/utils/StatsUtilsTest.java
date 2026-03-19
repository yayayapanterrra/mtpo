package utils;

import algorithms.LoopStatisticsAlgorithm;
import algorithms.StreamStatisticsAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import samples.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@DisplayName("Тестирование StatsUtils")
public class StatsUtilsTest {

    //ГРАНИЧНЫЕ ЗНАЧЕНИЯ (Boundary Value Analysis)
    @Test
    @DisplayName("Граничное значение: пустые выборки должны выбрасывать исключение")
    void testCorrelationEmptyData_BoundaryValue(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Collections.emptyList());
        sample2.setData(Collections.emptyList());

        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    @Test
    @DisplayName("Граничное значение: один элемент в выборке должен выбрасывать исключение")
    void testCorrelationSingleElement_BoundaryValue(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0));
        sample2.setData(Arrays.asList(2.0));

        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    @Test
    @DisplayName("Граничное значение: два элемента - минимум для корреляции")
    void testCorrelationTwoElements_BoundaryValue(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0));
        sample2.setData(Arrays.asList(1.0, 2.0));

        double result = StatsUtils.correlation(sample1, sample2);
        assertEquals(1.0, result, 0.0001, "Коэффициент корреляции должен быть 1.0 для идентичных выборок");
    }

    @Test
    @DisplayName("Граничное значение: разные размеры выборок должны выбрасывать исключение")
    void testCorrelationDifferentSizes_BoundaryValue(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0));
        sample2.setData(Arrays.asList(1.0, 2.0));

        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    //КЛАССЫ ЭКВИВАЛЕНТНОСТИ (Equivalence Partitioning)
    @Test
    @DisplayName("Класс эквивалентности: положительная корреляция")
    void testCorrelationPositive_EquivalenceClass(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        // Положительная корреляция: x растет, y растет
        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(2.0, 4.0, 6.0, 8.0, 10.0)); // y = 2x

        double result = StatsUtils.correlation(sample1, sample2);
        assertThat(result, is(closeTo(1.0, 0.0001)));
    }

    @Test
    @DisplayName("Класс эквивалентности: отрицательная корреляция")
    void testCorrelationNegative_EquivalenceClass(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        // Отрицательная корреляция: x растет, y уменьшается
        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(10.0, 8.0, 6.0, 4.0, 2.0)); // y = 12 - 2x

        double result = StatsUtils.correlation(sample1, sample2);
        assertThat(result, is(closeTo(-1.0, 0.0001)));
    }

    @Test
    @DisplayName("Класс эквивалентности: нулевая дисперсия второй выборки")
    void testCorrelationZeroVariance_EquivalenceClass(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        // Нулевая дисперсия: независимые переменные
        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(5.0, 5.0, 5.0, 5.0, 5.0)); // konstante

        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10, 20})
    @DisplayName("Класс эквивалентности: разные размеры выборок для положительной корреляции")
    void testCorrelationVariousSizes_EquivalenceClass(int size){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        List<Double> data1 = new java.util.ArrayList<>();
        List<Double> data2 = new java.util.ArrayList<>();

        for (int i = 0; i < size; i++) {
            data1.add((double) i);
            data2.add((double) i * 2);
        }

        sample1.setData(data1);
        sample2.setData(data2);

        double result = StatsUtils.correlation(sample1, sample2);
        assertThat(result, is(closeTo(1.0, 0.0001)));
    }

    //ПОКРЫТИЕ ОПЕРАТОРОВ (Statement Testing)
    @Test
    @DisplayName("Покрытие операторов: все операции корреляции выполняются")
    void testAllStatements_StatementCoverage(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(2.0, 4.0, 5.0, 8.0, 9.0));

        //выполнение корреляции охватит все операторы
        double correlation = StatsUtils.correlation(sample1, sample2);

        assertAll("Все операторы выполнены",
                ()->assertNotNull(correlation),
                ()->assertTrue(correlation >= -1.0 && correlation <= 1.0),
                ()->assertFalse(Double.isNaN(correlation))
        );
    }

    //ПОКРЫТИЕ ВЕТВЛЕНИЙ (Branch Testing)
    @Test
    @DisplayName("Покрытие ветвлений: проверка размера выборок (ветвь if)")
    void testBranchDifferentSizes_BranchCoverage(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0));
        sample2.setData(Arrays.asList(1.0, 2.0));

        //ветвь: размеры не совпадают
        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    @Test
    @DisplayName("Покрытие ветвлений: проверка нулевой дисперсии первой выборки")
    void testBranchZeroVarianceFirst_BranchCoverage(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        // Первая выборка имеет нулевую дисперсию (все значения одинаковые)
        sample1.setData(Arrays.asList(5.0, 5.0, 5.0, 5.0));
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0));

        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    @Test
    @DisplayName("Покрытие ветвлений: проверка нулевой дисперсии второй выборки")
    void testBranchZeroVarianceSecond_BranchCoverage(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        // Вторая выборка имеет нулевую дисперсию
        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        sample2.setData(Arrays.asList(5.0, 5.0, 5.0, 5.0));

        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    //СРАВНЕНИЕ АЛГОРИТМОВ
    @Test
    @DisplayName("Сравнение алгоритмов: Loop vs Stream для корреляции")
    void testAlgorithmComparison(){
        // Создание выборок с Loop алгоритмом
        UniformSample sample1Loop = new UniformSample();
        UniformSample sample2Loop = new UniformSample();
        sample1Loop.setAlgorithm(new LoopStatisticsAlgorithm());
        sample2Loop.setAlgorithm(new LoopStatisticsAlgorithm());

        // Создание выборок со Stream алгоритмом
        UniformSample sample1Stream = new UniformSample();
        UniformSample sample2Stream = new UniformSample();
        sample1Stream.setAlgorithm(new StreamStatisticsAlgorithm());
        sample2Stream.setAlgorithm(new StreamStatisticsAlgorithm());

        List<Double> data1 = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        List<Double> data2 = Arrays.asList(2.0, 4.0, 6.0, 8.0, 10.0);

        sample1Loop.setData(data1);
        sample2Loop.setData(data2);
        sample1Stream.setData(data1);
        sample2Stream.setData(data2);

        double correlationLoop = StatsUtils.correlation(sample1Loop, sample2Loop);
        double correlationStream = StatsUtils.correlation(sample1Stream, sample2Stream);

        assertEquals(correlationLoop, correlationStream, 0.0001,
                "Результаты корреляции должны совпадать независимо от алгоритма");
    }

    //ASSUMPTIONS
    @Test
    @DisplayName("Assumption: тест имеет смысл только для положительных размеров")
    void testAssumptionPositiveSize(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(2.0, 4.0, 6.0, 8.0, 10.0));

        //предположение: выборки имеют положительный размер
        assumeTrue(sample1.getData().size() > 0, "Выборка должна содержать данные");

        double result = StatsUtils.correlation(sample1, sample2);
        assertTrue(result >= -1.0 && result <= 1.0, "Корреляция должна быть в диапазоне [-1, 1]");
    }

    @Test
    @DisplayName("Assumption: тест имеет смысл только если выборки одного размера")
    void testAssumptionSameSizeRequired(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0));
        sample2.setData(Arrays.asList(5.0, 6.0, 7.0, 8.0));

        //предположение: размеры совпадают
        assumeTrue(sample1.getData().size() == sample2.getData().size(),
                "Выборки должны быть одного размера");

        double result = StatsUtils.correlation(sample1, sample2);
        assertNotNull(result);
    }

    //НЕГАТИВНЫЕ СЦЕНАРИИ
    @Test
    @DisplayName("Негативный сценарий: null в качестве первой выборки")
    void testNullFirstSample_NegativeScenario(){
        assertThrows(NullPointerException.class, ()->StatsUtils.correlation(null, new UniformSample()));
    }

    @Test
    @DisplayName("Негативный сценарий: null в качестве второй выборки")
    void testNullSecondSample_NegativeScenario(){
        assertThrows(NullPointerException.class, ()->StatsUtils.correlation(new UniformSample(), null));
    }

    @Test
    @DisplayName("Негативный сценарий: выборка с одинаковыми значениями в обеих")
    void testIdenticalValues_NegativeScenario(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        // Обе выборки имеют нулевую дисперсию
        sample1.setData(Arrays.asList(5.0, 5.0, 5.0, 5.0));
        sample2.setData(Arrays.asList(3.0, 3.0, 3.0, 3.0));

        assertThrows(RuntimeException.class, ()->StatsUtils.correlation(sample1, sample2));
    }

    //MATCHERS
    @Test
    @DisplayName("Matchers: проверка диапазона корреляции")
    void testCorrelationRange_Matchers(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(2.0, 3.0, 5.0, 8.0, 9.0));

        double result = StatsUtils.correlation(sample1, sample2);

        //matcher: результат в диапазоне [-1, 1]
        assertThat(result, is(greaterThanOrEqualTo(-1.0)));
        assertThat(result, is(lessThanOrEqualTo(1.0)));
    }

    @Test
    @DisplayName("Matchers: проверка идентичных выборок")
    void testIdenticalSamples_Matchers(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));

        double result = StatsUtils.correlation(sample1, sample2);

        //matcher: идеальная положительная корреляция
        assertThat(result, is(closeTo(1.0, 0.0001)));
    }

    //ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ
    @Test
    @DisplayName("Сценарий: слабая положительная корреляция")
    void testWeakPositiveCorrelation(){
        NormalSample sample1 = new NormalSample(0, 1);
        NormalSample sample2 = new NormalSample(0, 1);

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        sample2.setData(Arrays.asList(1.5, 2.2, 3.1, 3.9, 5.1));

        double result = StatsUtils.correlation(sample1, sample2);

        assertThat(result, is(greaterThan(0.0)));
        assertThat(result, is(lessThan(1.0)));
    }

    @Test
    @DisplayName("Сценарий: умеренная положительная корреляция")
    void testModeratePositiveCorrelation(){
        UniformSample sample1 = new UniformSample();
        UniformSample sample2 = new UniformSample();

        sample1.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0));
        sample2.setData(Arrays.asList(1.0, 3.0, 2.0, 4.0, 5.0, 6.0));

        double result = StatsUtils.correlation(sample1, sample2);

        assertTrue(result > 0.5 && result < 1.0, "Корреляция должна быть умеренной");
    }
}