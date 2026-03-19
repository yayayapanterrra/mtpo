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

import static org.mockito.ArgumentMatchers.anyList;

@DisplayName("Тестирование StreamStatisticsAlgorithm")
public class StreamStatisticsAlgorithmTest {
    private final StatisticsAlgorithm loopAlgorithm = new LoopStatisticsAlgorithm();
    private final StatisticsAlgorithm streamAlgorithm = new StreamStatisticsAlgorithm();

    //ГРАНИЧНЫЕ ЗНАЧЕНИЯ (BoundaryValue)
    @Test
    @DisplayName("Граничное значение: пустой список должен выбрасывать исключение")
    void testMinEmptyList_BoundaryValue() {
        //граничное значение 0 элементов
        assertThrows(RuntimeException.class, () -> streamAlgorithm.min(Collections.emptyList()));
    }

    @Test
    @DisplayName("Граничное значение: один элемент")
    void testMinSingleElement_BoundaryValue() {
        //граничное значение 1 элемент
        List<Double> data = Collections.singletonList(5.0);
        double result = streamAlgorithm.min(data);
        assertEquals(5.0, result, 0.0001);
    }

    @Test
    @DisplayName("Граничное значение: 1 элемент для дисперсии должен выбрасывать исключение")
    void testVarianceOneElement_BoundaryValue() {
        //меньше минимального: 1 элемент
        List<Double> data = Collections.singletonList(5.0);
        assertThrows(RuntimeException.class, () -> streamAlgorithm.variance(data));
    }

    @Test
    @DisplayName("Граничное значение: 3 элемента для эксцесса должен выбрасывать исключение")
    void testKurtosisThreeElements_BoundaryValue() {
        //меньше минимального: 3 элемента
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0);
        assertThrows(RuntimeException.class, () -> streamAlgorithm.kurtosis(data));
    }

    // КЛАССЫ ЭКВИВАЛЕНТНОСТИ (Equivalence Partitioning)
    @ParameterizedTest
    @ValueSource(doubles = {1.0, 5.5, 100.0})
    @DisplayName("Класс эквивалентности: положительные числа")
    void testMeanPositiveNumbers_EquivalenceClass(double value) {
        List<Double> data = Arrays.asList(value, value, value);
        double result = streamAlgorithm.mean(data);
        assertThat(result, is(closeTo(value, 0.0001)));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1.0, -5.5, -100.0})
    @DisplayName("Класс эквивалентности: отрицательные числа")
    void testMeanNegativeNumbers_EquivalenceClass(double value) {
        List<Double> data = Arrays.asList(value, value, value);
        double result = streamAlgorithm.mean(data);
        assertThat(result, is(closeTo(value, 0.0001)));
    }

    @Test
    @DisplayName("Медиана: нечетное количество (3 элемента)")
    void testMedianOddCount() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0);
        assertEquals(2.0, streamAlgorithm.median(data), 0.0001);
    }

    @Test
    @DisplayName("Медиана: четное количество (4 элемента)")
    void testMedianEvenCount() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        assertEquals(2.5, streamAlgorithm.median(data), 0.0001);
    }

    //ПОКРЫТИЕ ОПЕРАТОРОВ (Statement Testing)
    @Test
    @DisplayName("Покрытие операторов: все методы должны выполниться")
    void testAllMethodExecute_StatementCoverage() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        //каждый метод выполнится хотя бы один раз
        double min = streamAlgorithm.min(data);
        double max = streamAlgorithm.max(data);
        double mean = streamAlgorithm.mean(data);
        double median = streamAlgorithm.median(data);
        double variance = streamAlgorithm.variance(data);
        double skewness = streamAlgorithm.skewness(data);
        double kurtosis = streamAlgorithm.kurtosis(data);
        String name = streamAlgorithm.getName();

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
        double result = streamAlgorithm.median(data);
        assertEquals(2.0, result, 0.0001);
    }

    @Test
    @DisplayName("Покрытие ветвлений: медиана для нечетного количества (if)")
    void testMedianEvenCount_BranchCoverage() {
        //n%2==0
        List<Double> data = Arrays.asList(1.0, 3.0, 2.0, 4.0);
        double result = streamAlgorithm.median(data);
        assertEquals(2.5, result, 0.0001);
    }

    @Test
    @DisplayName("Покрытие ветвлений: isEmpty() - true/false")
    void testIsEmpty_BranchCoverage() {
        //пустой список
        assertThrows(RuntimeException.class, () -> streamAlgorithm.min(Collections.emptyList()));

        //заполненный список
        List<Double> data = Collections.singletonList(1.0);
        assertDoesNotThrow(() -> streamAlgorithm.min(data));
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

    }

    //ASSUMPTIONS
    @Test
    @DisplayName("Assumption: тест имеет смысл только для положительных размеров")
    void testWithAssumptionOnSize() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0);

        //предположение: тест имеет смысл только если данных больше 2
        assumeTrue(data.size() > 2, "Требуется минимум 3 элемента");

        double variance = streamAlgorithm.variance(data);
        assertTrue(variance >= 0, "Дисперсия не может быть отрицательной");
    }

    @Test
    @DisplayName("Assumption: пропускаем тест если выборка пустая")
    void testWithAssumptionOnEmptyList() {
        List<Double> data = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);

        //предположение: данные не должны быть пустыми
        assumeFalse(data.isEmpty(), "Данные не должны быть пустыми");

        double mean = streamAlgorithm.mean(data);
        assertThat(mean, is(greaterThan(0.0)));
    }

    //MOCKS
    @Test
    @DisplayName("Mock: создание мок объекта StatisticsAlgorithm")
    void testMockStatisticsAlgorithm() {
        StatisticsAlgorithm mockAlg = Mockito.mock(StatisticsAlgorithm.class);
        assertNotNull(mockAlg);

        double result = mockAlg.mean(Collections.emptyList());
        assertEquals(0.0, result, 0.0001);
    }

    @Test
    @DisplayName("Stub: настройка поведения мок объекта")
    void testStubStatisticsAlgorithm() {
        StatisticsAlgorithm stubAlg = Mockito.mock(StatisticsAlgorithm.class);

        when(stubAlg.mean(anyList())).thenReturn(42.0);
        when(stubAlg.min(anyList())).thenReturn(1.0);
        when(stubAlg.max(anyList())).thenReturn(100.0);

        assertEquals(42.0, stubAlg.mean(Collections.emptyList()), 0.0001);
        assertEquals(1.0, stubAlg.min(Collections.emptyList()), 0.0001);
        assertEquals(100.0, stubAlg.max(Collections.emptyList()), 0.0001);

        verify(stubAlg, times(1)).mean(anyList());
        verify(stubAlg, times(1)).min(anyList());
        verify(stubAlg, times(1)).max(anyList());
    }

    @Test
    @DisplayName("Spy: шпион на реальном объекте StreamStatisticsAlgorithm")
    void testSpyStatisticsAlgorithm() {
        StreamStatisticsAlgorithm spyAlg = Mockito.spy(new StreamStatisticsAlgorithm());

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

        double mean = streamAlgorithm.mean(data);
        double min = streamAlgorithm.min(data);
        double max = streamAlgorithm.max(data);

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
        assertThrows(NullPointerException.class, () -> streamAlgorithm.mean(null));
    }

    @Test
    @DisplayName("Негативный сценарий: эксцесс при нулевой дисперсии")
    void testKurtosisZeroVariance() {
        List<Double> data = Arrays.asList(5.0, 5.0, 5.0, 5.0); //дисперсия 0, т.к. элементы одинаковые
        assertThrows(RuntimeException.class, () -> streamAlgorithm.kurtosis(data));
    }
}
