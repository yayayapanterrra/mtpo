package algorithm;

import algorithms.LoopStatisticsAlgorithm;
import algorithms.StatisticsAlgorithm;
import algorithms.StreamStatisticsAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyList;


@DisplayName("Тестирование LoopStatisticsAlgorithm")
public class LoopStatisticsAlgorithmTest {
    private final StatisticsAlgorithm loopAlgorithm = new LoopStatisticsAlgorithm();
    private final StatisticsAlgorithm streamAlgorithm = new StreamStatisticsAlgorithm();

    //ГРАНИЧНЫЕ ЗНАЧЕНИЯ (BoundaryValue)
    @Test
    @DisplayName("Граничное значение: пустой список должен выбрасывать исключение")
    void testMinEmptyList_BoundaryValue() {
        //граничное значение 0 элементов
        assertThrows(RuntimeException.class, () -> loopAlgorithm.min(Collections.emptyList()));
    }

    @Test
    @DisplayName("Граничное значение: один элемент")
    void testMinSingleElement_BoundaryValue() {
        //граничное значение 1 элемент
        List<Double> data = Collections.singletonList(5.0);
        double result = loopAlgorithm.min(data);
        assertEquals(5.0, result, 0.0001);
    }

    @Test
    @DisplayName("Граничное значение: 1 элемент для дисперсии должен выбрасывать исключение")
    void testVarianceOneElement_BoundaryValue() {
        //меньше минимального: 1 элемент
        List<Double> data = Collections.singletonList(5.0);
        assertThrows(RuntimeException.class, () -> loopAlgorithm.variance(data));
    }

    @Test
    @DisplayName("Граничное значение: 3 элемента для эксцесса должен выбрасывать исключение")
    void testKurtosisThreeElements_BoundaryValue() {
        //меньше минимального: 3 элемента
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0);
        assertThrows(RuntimeException.class, () -> loopAlgorithm.kurtosis(data));
    }

    // КЛАССЫ ЭКВИВАЛЕНТНОСТИ (Equivalence Partitioning)
    @ParameterizedTest
    @ValueSource(doubles = {1.0, 5.5, 100.0})
    @DisplayName("Класс эквивалентности: положительные числа")
    void testMeanPositiveNumbers_EquivalenceClass(double value) {
        List<Double> data = Arrays.asList(value, value, value);
        double result = loopAlgorithm.mean(data);
        assertThat(result, is(closeTo(value, 0.0001)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -5.5, -100.0})
    @DisplayName("Класс эквивалентности: отрицательные числа")
    void testMeanNegativeNumbers_EquivalenceClass(double value) {
        List<Double> data = Arrays.asList(value, value, value);
        double result = loopAlgorithm.mean(data);
        assertThat(result, is(closeTo(value, 0.0001)));
    }

    @Test
    @DisplayName("Медиана: нечетное количество (3 элемента)")
    void testMedianOddCount() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0);
        assertEquals(2.0, loopAlgorithm.median(data), 0.0001);
    }

    @Test
    @DisplayName("Медиана: четное количество (4 элемента)")
    void testMedianEvenCount() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        assertEquals(2.5, loopAlgorithm.median(data), 0.0001);
    }

    //ПОКРЫТИЕ ОПЕРАТОРОВ (Statement Testing)
    @Test
    @DisplayName("Покрытие операторов: все методы должны выполниться")
    void testAllMethodExecute_StatementCoverage() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        //каждый метод выполнится хотя бы один раз
        double min = loopAlgorithm.min(data);
        double max = loopAlgorithm.max(data);
        double mean = loopAlgorithm.mean(data);
        double median = loopAlgorithm.median(data);
        double variance = loopAlgorithm.variance(data);
        double skewness = loopAlgorithm.skewness(data);
        double kurtosis = loopAlgorithm.kurtosis(data);
        String name = loopAlgorithm.getName();

        //проверка, что все значения получены
        assertAll("Все методы выполнены успешно",
                () -> assertNotNull(min),
                () -> assertNotNull(max),
                () -> assertNotNull(mean),
                () -> assertNotNull(median),
                () -> assertNotNull(variance),
                () -> assertNotNull(skewness),
                () -> assertNotNull(kurtosis),
                () -> assertNotNull(name));
    }

    //ПОКРЫТИЕ ВЕТВЛЕНИЙ (Branch Testing)
    @Test
    @DisplayName("Покрытие ветвлений: медиана для нечетного количества (else)")
    void testMedianOddCount_BranchCoverage() {
        //n%2!=0
        List<Double> data = Arrays.asList(1.0, 3.0, 2.0);
        double result = loopAlgorithm.median(data);
        assertEquals(2.0, result, 0.0001);
    }

    @Test
    @DisplayName("Покрытие ветвлений: медиана для нечетного количества (if)")
    void testMedianEvenCount_BranchCoverage() {
        //n%2==0
        List<Double> data = Arrays.asList(1.0, 3.0, 2.0, 4.0);
        double result = loopAlgorithm.median(data);
        assertEquals(2.5, result, 0.0001);
    }

    @Test
    @DisplayName("Покрытие ветвлений: isEmpty() - true/false")
    void testIsEmpty_BranchCoverage() {
        //пустой список
        assertThrows(RuntimeException.class, () -> loopAlgorithm.min(Collections.emptyList()));

        //заполненный список
        List<Double> data = Collections.singletonList(1.0);
        assertDoesNotThrow(() -> loopAlgorithm.min(data));
    }

    //СРАВНЕНИЕ АЛГОРИТМОВ
    @Test
    @DisplayName("Сравнение алгоритмов: Loop и Stream должны давать одинаковые результаты")
    void testLoopVsStreamAlgorithmComparison() {
        List<Double> data = Arrays.asList(1.0, 5.0, 3.0, 9.0, 2.0, 7.0, 4.0);

        double loopMin = loopAlgorithm.min(data);
        double streamMin = streamAlgorithm.min(data);
        assertEquals(loopMin, streamMin, 0.0001, "min должен совпадать");

        double loopMax = loopAlgorithm.max(data);
        double streamMax = streamAlgorithm.max(data);
        assertEquals(loopMax, streamMax, 0.0001, "max должен совпадать");

        double loopMean = loopAlgorithm.mean(data);
        double streamMean = streamAlgorithm.mean(data);
        assertEquals(loopMean, streamMean, 0.0001, "mean должен совпадать");

        double loopMedian = loopAlgorithm.median(data);
        double streamMedian = streamAlgorithm.median(data);
        assertEquals(loopMedian, streamMedian, 0.0001, "median должен совпадать");

        double loopVariance = loopAlgorithm.variance(data);
        double streamVariance = streamAlgorithm.variance(data);
        assertEquals(loopVariance, streamVariance, 0.0001, "variance должен совпадать");
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 20, 50})
    @DisplayName("Сравнение алгоритмов на разных размерах выборки")
    void testLoopVsStreamVariousSizes(int size) {
        List<Double> data = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            data.add((double) i);
        }

        double loopResult = loopAlgorithm.mean(data);
        double streamResult = streamAlgorithm.mean(data);

        assertEquals(loopResult, streamResult, 0.0001,
                "Результаты должны совпадать для размера" + size);
    }

    //ASSUMPTIONS
    @Test
    @DisplayName("Assumption: тест имеет смысл только для положительных размеров")
    void testWithAssumptionOnSize() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0);

        //предположение: тест имеет смысл только если данных больше 2
        assumeTrue(data.size() > 2, "Требуется минимум 3 элемента");

        double variance = loopAlgorithm.variance(data);
        assertTrue(variance >= 0, "Дисперсия не может быть отрицательной");
    }

    @Test
    @DisplayName("Assumption: пропускаем тест если выборка пустая")
    void testWithAssumptionOnEmptyList() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        //предположение: данные не должны быть пустыми
        assumeFalse(data.isEmpty(), "Данные не должны быть пустыми");

        double mean = loopAlgorithm.mean(data);
        assertThat(mean, is(greaterThan(0.0)));
    }

    //MOCKS
    @Test
    @DisplayName("Mock: создание мока StatisticsAlgorithm")
    void testMockStatisticsAlgorithm() {
        //создаем пустой мок
        StatisticsAlgorithm mockAlg = Mockito.mock(StatisticsAlgorithm.class);

        //проверка, что мок создан
        assertNotNull(mockAlg);

        //вызов метода (возврат default для double-0.0)
        double result = mockAlg.mean(Collections.emptyList());
        assertEquals(0.0, result, 0.0001);

    }

    @Test
    @DisplayName("Stub: настройка поведения мок объекта")
    void testStubStatisticsAlgorithm() {
        //stub: мок с настроенным поведением
        StatisticsAlgorithm stubAlg = Mockito.mock(StatisticsAlgorithm.class);

        //настройка возвращаемого значения
        when(stubAlg.mean(anyList())).thenReturn(42.0);
        when(stubAlg.min(anyList())).thenReturn(1.0);
        when(stubAlg.max(anyList())).thenReturn(100.0);

        //проверка возвращаемых значений
        assertEquals(42.0, stubAlg.mean(Collections.emptyList()), 0.0001);
        assertEquals(1.0, stubAlg.min(Collections.emptyList()), 0.0001);
        assertEquals(100.0, stubAlg.max(Collections.emptyList()), 0.0001);

        verify(stubAlg, times(1)).mean(anyList());
        verify(stubAlg, times(1)).min(anyList());
        verify(stubAlg, times(1)).max(anyList());
    }

    @Test
    @DisplayName("Spy: шпион на реальном объекте LoopStatisticsAlgorithm")
    void testSpyStatisticsAlgorithm() {
        //создаем шпиона на реальном объекте
        LoopStatisticsAlgorithm spyAlg = Mockito.spy(new LoopStatisticsAlgorithm());

        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        double mean = spyAlg.mean(data);
        verify(spyAlg, atLeastOnce()).mean(data);
        assertEquals(3.0, mean, 0.0001);
    }

    //MATCHERS
    @Test
    @DisplayName("Matchers: использование Hamcrest матчеров")
    void testWithHamcrestMatchers() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        double mean = loopAlgorithm.mean(data);
        double min = loopAlgorithm.min(data);
        double max = loopAlgorithm.max(data);

        //matcher 1 closeTo
        assertThat(mean, is(closeTo(3.0, 0.0001)));

        //matcher 2 greater/lessthan
        assertThat(min, lessThan(mean));
        assertThat(max, greaterThan(mean));

        //matcher 3 equalto
        assertThat(min, is(equalTo(1.0)));
        assertThat(max, is(equalTo(5.0)));
    }

    //НЕГАТИВНЫЕ СЦЕНАРИИ
    @Test
    @DisplayName("Негативный сценарий: null должен выбрасывать исключение")
    void testWithNullData() {
        assertThrows(NullPointerException.class, () -> loopAlgorithm.mean(null));
    }

    @Test
    @DisplayName("Негативный сценарий: эксцесс при нулевой дисперсии")
    void testKurtosisZeroVariance() {
        List<Double> data = Arrays.asList(5.0, 5.0, 5.0, 5.0); //дисперсия 0, т.к. элементы одинаковые
        assertThrows(RuntimeException.class, () -> loopAlgorithm.kurtosis(data));
    }

    // ==================== МУТАЦИОННЫЕ ТЕСТЫ ====================

    @Test
    @DisplayName("Мутация: getName должен вернуть точную строку")
    void testGetNameExact() {
        assertEquals("Loop-based (Imperative)", loopAlgorithm.getName());
        assertNotEquals("", loopAlgorithm.getName());
        assertNotEquals("Stream-based", loopAlgorithm.getName());
    }

    @Test
    @DisplayName("Мутация: min с дубликатами минимума")
    void testMinWithDuplicateValues() {
        List<Double> data = Arrays.asList(3.0, 1.0, 1.0, 5.0);
        assertEquals(1.0, loopAlgorithm.min(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: max с дубликатами максимума")
    void testMaxWithDuplicateValues() {
        List<Double> data = Arrays.asList(1.0, 5.0, 5.0, 3.0);
        assertEquals(5.0, loopAlgorithm.max(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: min граничное значение (0 и отрицательное)")
    void testMinWithZeroAndNegative() {
        List<Double> data = Arrays.asList(0.0, -5.0, 3.0);
        assertEquals(-5.0, loopAlgorithm.min(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: max граничное значение (0 и отрицательное)")
    void testMaxWithZeroAndNegative() {
        List<Double> data = Arrays.asList(0.0, -5.0, 3.0);
        assertEquals(3.0, loopAlgorithm.max(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: variance точное значение")
    void testVariancePreciseValue() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        double variance = loopAlgorithm.variance(data);
        assertEquals(2.5, variance, 0.0001);
    }

    @Test
    @DisplayName("Мутация: variance два элемента")
    void testVarianceTwoElements() {
        List<Double> data = Arrays.asList(1.0, 3.0);
        double variance = loopAlgorithm.variance(data);
        assertEquals(2.0, variance, 0.0001);
    }

    @Test
    @DisplayName("Мутация: skewness не пустой, не пусто")
    void testSkewnessNotEmpty() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        double skewness = loopAlgorithm.skewness(data);
        assertEquals(0.0, skewness, 0.0001);  // симметричное распределение
    }

    @Test
    @DisplayName("Мутация: skewness отрицательная асимметрия")
    void testSkewnessNegativeSkew() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 10.0);
        double skewness = loopAlgorithm.skewness(data);
        assertTrue(skewness > 0);  // положительная асимметрия (хвост вправо)
    }

    @Test
    @DisplayName("Мутация: kurtosis граничное значение (4 элемента)")
    void testKurtosisMinElements() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        double kurtosis = loopAlgorithm.kurtosis(data);
        assertTrue(Double.isFinite(kurtosis));
    }

    @Test
    @DisplayName("Мутация: kurtosis больше элементов")
    void testKurtosisManyElements() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);
        double kurtosis = loopAlgorithm.kurtosis(data);
        assertTrue(Double.isFinite(kurtosis));
        assertTrue(Math.abs(kurtosis) < 10);  // должен быть в разумном диапазоне
    }

    @Test
    @DisplayName("Мутация: min отрицательные числа")
    void testMinNegativeNumbers() {
        List<Double> data = Arrays.asList(-5.0, -1.0, -10.0);
        assertEquals(-10.0, loopAlgorithm.min(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: max отрицательные числа")
    void testMaxNegativeNumbers() {
        List<Double> data = Arrays.asList(-5.0, -1.0, -10.0);
        assertEquals(-1.0, loopAlgorithm.max(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: median с дублирующимся значением")
    void testMedianWithDuplicates() {
        List<Double> data = Arrays.asList(1.0, 2.0, 2.0, 3.0, 4.0);
        assertEquals(2.0, loopAlgorithm.median(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: mean точное значение")
    void testMeanPreciseValue() {
        List<Double> data = Arrays.asList(2.0, 4.0, 6.0);
        assertEquals(4.0, loopAlgorithm.mean(data), 0.0001);
    }

    @Test
    @DisplayName("Мутация: variance < 2 элементов")
    void testVarianceLessThanTwoElements() {
        List<Double> data = Collections.singletonList(5.0);
        assertThrows(RuntimeException.class, () -> loopAlgorithm.variance(data));
    }

    @Test
    @DisplayName("Мутация: skewness с очень малой дисперсией")
    void testSkewnessSmallVariance() {
        List<Double> data = Arrays.asList(1.0, 1.001, 1.002, 1.003);
        double skewness = loopAlgorithm.skewness(data);
        assertTrue(Double.isFinite(skewness));
    }

    @Test
    @DisplayName("Мутация: сравнение Loop vs Stream для всех методов")
    void testAllMethodsLoopVsStream() {
        List<Double> data = Arrays.asList(2.5, 3.5, 1.5, 4.5, 2.0);

        assertEquals(loopAlgorithm.min(data), streamAlgorithm.min(data), 0.0001);
        assertEquals(loopAlgorithm.max(data), streamAlgorithm.max(data), 0.0001);
        assertEquals(loopAlgorithm.mean(data), streamAlgorithm.mean(data), 0.0001);
        assertEquals(loopAlgorithm.median(data), streamAlgorithm.median(data), 0.0001);
        assertEquals(loopAlgorithm.variance(data), streamAlgorithm.variance(data), 0.0001);
        assertEquals(loopAlgorithm.skewness(data), streamAlgorithm.skewness(data), 0.0001);
        assertEquals(loopAlgorithm.kurtosis(data), streamAlgorithm.kurtosis(data), 0.0001);
    }

}
