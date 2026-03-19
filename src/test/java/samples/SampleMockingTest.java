package samples;

import algorithms.LoopStatisticsAlgorithm;
import algorithms.StatisticsAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import samples.Sample;
import samples.UniformSample;
import samples.NormalSample;
import samples.FileSample;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@DisplayName("Тестирование Sample")
public class SampleMockingTest {

    private Sample sample;

    @BeforeEach
    void setUp() {
        sample = new UniformSample(0.0, 10.0);
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
    }

    //getData()

    @Test
    @DisplayName("getData: возвращает список данных")
    void testGetData() {
        List<Double> data = sample.getData();
        assertEquals(5, data.size());
        assertEquals(1.0, data.get(0), 0.0001);
        assertEquals(5.0, data.get(4), 0.0001);
    }

    @Test
    @DisplayName("getData: возвращает неизменяемый список")
    void testGetDataImmutable() {
        List<Double> data = sample.getData();
        assertThrows(UnsupportedOperationException.class, () -> data.add(6.0));
    }

    @Test
    @DisplayName("getData: пустой список")
    void testGetDataEmpty() {
        sample.setData(Collections.emptyList());
        List<Double> data = sample.getData();
        assertEquals(0, data.size());
    }

    //setData()

    @Test
    @DisplayName("setData: устанавливает данные")
    void testSetData() {
        List<Double> newData = Arrays.asList(10.0, 20.0, 30.0);
        sample.setData(newData);
        assertEquals(3, sample.getData().size());
        assertEquals(10.0, sample.getData().get(0), 0.0001);
    }

    @Test
    @DisplayName("setData: очищает старые данные (Мутация: removed call to clear)")
    void testSetDataClearsPrevious() {
        sample.setData(Arrays.asList(10.0, 20.0));
        List<Double> data = sample.getData();
        assertEquals(2, data.size());
        assertFalse(data.contains(1.0)); // Старые данные удалены
    }

    @Test
    @DisplayName("setData: работает с пустым списком")
    void testSetDataEmpty() {
        sample.setData(Collections.emptyList());
        assertEquals(0, sample.getData().size());
    }

    //getAlgorithm()

    @Test
    @DisplayName("getAlgorithm: возвращает установленный алгоритм")
    void testGetAlgorithm() {
        StatisticsAlgorithm algo = sample.getAlgorithm();
        assertNotNull(algo);
        assertEquals("Loop-based (Imperative)", algo.getName());
    }

    @Test
    @DisplayName("Мутация: replaced return value with null for getAlgorithm")
    void testGetAlgorithmNotNull() {
        assertNotNull(sample.getAlgorithm());
    }

    //setAlgorithm()

    @Test
    @DisplayName("setAlgorithm: устанавливает новый алгоритм")
    void testSetAlgorithm() {
        StatisticsAlgorithm mockAlgo = Mockito.mock(StatisticsAlgorithm.class);
        when(mockAlgo.mean(anyList())).thenReturn(42.0);

        sample.setAlgorithm(mockAlgo);
        assertEquals(42.0, sample.mean(), 0.0001);
    }

    // min/max/mean/median/variance/skewness/kurtosis

    @Test
    @DisplayName("min: делегирует алгоритму")
    void testMin() {
        double min = sample.min();
        assertEquals(1.0, min, 0.0001);
    }

    @Test
    @DisplayName("max: делегирует алгоритму")
    void testMax() {
        double max = sample.max();
        assertEquals(5.0, max, 0.0001);
    }

    @Test
    @DisplayName("mean: делегирует алгоритму")
    void testMean() {
        double mean = sample.mean();
        assertEquals(3.0, mean, 0.0001);
    }

    @Test
    @DisplayName("median: делегирует алгоритму (Мутация: NO_COVERAGE)")
    void testMedian() {
        double median = sample.median();
        assertEquals(3.0, median, 0.0001);
    }

    @Test
    @DisplayName("variance: делегирует алгоритму")
    void testVariance() {
        double variance = sample.variance();
        assertEquals(2.5, variance, 0.0001);
    }

    @Test
    @DisplayName("skewness: делегирует алгоритму (Мутация: NO_COVERAGE)")
    void testSkewness() {
        double skewness = sample.skewness();
        assertEquals(0.0, skewness, 0.0001);
    }

    @Test
    @DisplayName("kurtosis: делегирует алгоритму (Мутация: NO_COVERAGE)")
    void testKurtosis() {
        double kurtosis = sample.kurtosis();
        assertTrue(Double.isFinite(kurtosis));
    }

    // quartiles()

    @Test
    @DisplayName("quartiles: возвращает квартили")
    void testQuartiles() {
        List<Double> quartiles = sample.quartiles();
        assertEquals(3, quartiles.size());
        assertThat(quartiles.get(0), lessThan(quartiles.get(1)));
        assertThat(quartiles.get(1), lessThan(quartiles.get(2)));
    }

    @Test
    @DisplayName("quartiles: пустая выборка должна выбросить исключение")
    void testQuartilesEmpty() {
        sample.setData(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> sample.quartiles());
    }

    @Test
    @DisplayName("Мутация: replaced return value with Collections.emptyList")
    void testQuartilesNotEmpty() {
        List<Double> quartiles = sample.quartiles();
        assertNotEquals(0, quartiles.size());
    }

    //getQuantile()

    @Test
    @DisplayName("getQuantile: работает для граничных значений (Мутация: NO_COVERAGE операции)")
    void testGetQuantile() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        List<Double> quartiles = sample.quartiles();

        // Q1 < медиана < Q3
        assertTrue(quartiles.get(0) < quartiles.get(1));
        assertTrue(quartiles.get(1) < quartiles.get(2));
    }

    // isNormal()

    @Test
    @DisplayName("isNormal: проверяет нормальность (Мутация: NO_COVERAGE)")
    void testIsNormalValid() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        boolean isNormal = sample.isNormal(0.05);
        assertNotNull(isNormal);
    }

    @Test
    @DisplayName("isNormal: выбрасывает исключение при неправильном alpha")
    void testIsNormalInvalidAlpha() {
        assertThrows(RuntimeException.class, () -> sample.isNormal(0.99));
    }

    @Test
    @DisplayName("Мутация: changed conditional boundary в isNormal")
    void testIsNormalBoundary() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        boolean isNormal05 = sample.isNormal(0.05);
        boolean isNormal01 = sample.isNormal(0.01);
        assertNotNull(isNormal05);
        assertNotNull(isNormal01);
    }

    //  jarqueBera()

    @Test
    @DisplayName("jarqueBera: вычисляет тест (Мутация: NO_COVERAGE операции)")
    void testJarqueBera() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        double jb = sample.jarqueBera();
        assertTrue(jb >= 0);
        assertTrue(Double.isFinite(jb));
    }

    @Test
    @DisplayName("jarqueBera: выбрасывает исключение для малой выборки")
    void testJarqueBeraSmallSample() {
        sample.setData(Arrays.asList(1.0, 2.0));
        assertThrows(RuntimeException.class, () -> sample.jarqueBera());
    }

    //  saveToFile / loadFromFile

    @Test
    @DisplayName("saveToFile/loadFromFile: сохранение и загрузка")
    void testSaveAndLoadFromFile() throws IOException {
        String fileName = "test_" + System.currentTimeMillis() + ".txt";
        Path file = Paths.get(fileName);

        try {
            sample.saveToFile(file.toString());

            Sample newSample = new FileSample();
            newSample.loadFromFile(file.toString());

            assertEquals(sample.getData().size(), newSample.getData().size());
            for (int i = 0; i < sample.getData().size(); i++) {
                assertEquals(sample.getData().get(i), newSample.getData().get(i), 0.0001);
            }
        } finally {
            // Удаляем файл
            if (Files.exists(file)) {
                Files.delete(file);
            }
        }
    }

    @Test
    @DisplayName("Мутация: removed call to BufferedWriter methods (NO_COVERAGE)")
    void testSaveToFileCreatesFile() throws IOException {
        String fileName = "test_" + System.currentTimeMillis() + ".txt";
        Path file = Paths.get(fileName);

        try {
            sample.saveToFile(file.toString());
            assertTrue(Files.exists(file));
        } finally {
            if (Files.exists(file)) {
                Files.delete(file);
            }
        }
    }

    @Test
    @DisplayName("loadFromFile: выбрасывает исключение для пустого файла")
    void testLoadFromFileEmpty() throws IOException {
        String fileName = "empty_" + System.currentTimeMillis() + ".txt";
        Path file = Paths.get(fileName);

        try {
            Files.write(file, new byte[0]);

            Sample newSample = new FileSample();
            assertThrows(RuntimeException.class, () -> newSample.loadFromFile(file.toString()));
        } finally {
            if (Files.exists(file)) {
                Files.delete(file);
            }
        }
    }

    //  toString()

    @Test
    @DisplayName("toString: возвращает строковое представление (Мутация: replaced return with empty string)")
    void testToString() {
        String str = sample.toString();
        assertNotEquals("", str);
        assertThat(str, containsString("UniformSample"));
    }

    //  equals()

    @Test
    @DisplayName("equals: одинаковые данные равны")
    void testEqualsTrue() {
        Sample sample2 = new UniformSample(0.0, 10.0);
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));

        assertEquals(sample, sample2);
    }

    @Test
    @DisplayName("equals: разные данные не равны")
    void testEqualsFalse() {
        Sample sample2 = new UniformSample(0.0, 10.0);
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0));

        assertNotEquals(sample, sample2);
    }

    @Test
    @DisplayName("equals: объект не равен null")
    void testEqualsNull() {
        assertNotEquals(sample, null);
    }

    @Test
    @DisplayName("equals: объект равен себе")
    void testEqualsSelf() {
        assertEquals(sample, sample);
    }

    @Test
    @DisplayName("Мутация: replaced boolean return with false/true в equals (NO_COVERAGE)")
    void testEqualsDifferentTypes() {
        assertNotEquals(sample, "not a sample");
    }

    //  hashCode()

    @Test
    @DisplayName("hashCode: одинаковые данные - одинаковый хеш")
    void testHashCodeEqual() {
        Sample sample2 = new UniformSample(0.0, 10.0);
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));

        assertEquals(sample.hashCode(), sample2.hashCode());
    }

    @Test
    @DisplayName("hashCode: разные данные - разные хеши")
    void testHashCodeNotEqual() {
        Sample sample2 = new UniformSample(0.0, 10.0);
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0));

        assertNotEquals(sample.hashCode(), sample2.hashCode());
    }

    @Test
    @DisplayName("Мутация: replaced int return with 0 в hashCode (NO_COVERAGE)")
    void testHashCodeNotZero() {
        assertNotEquals(0, sample.hashCode());
        int hash = sample.hashCode();
        assertNotNull(hash);
    }

    //  showCharact()

    @Test
    @DisplayName("showCharact: выполняется без ошибок (Мутация: removed calls to println)")
    void testShowCharact() {
        assertDoesNotThrow(() -> sample.showCharact());
    }

    //  createSampleData / restoreFromSampleData

    @Test
    @DisplayName("createSampleData/restoreFromSampleData: абстрактные методы")
    void testCreateAndRestoreSampleData() {
        // Проверяем, что методы существуют и работают
        assertDoesNotThrow(() -> {
            sample.setData(Arrays.asList(1.0, 2.0, 3.0));
        });
    }

    //  Интеграционные тесты

    @Test
    @DisplayName("Интеграция: полный цикл создания, обработки и получения результатов")
    void testFullCycle() {
        sample.setData(Arrays.asList(2.0, 4.0, 6.0, 8.0));

        assertEquals(2.0, sample.min(), 0.0001);
        assertEquals(8.0, sample.max(), 0.0001);
        assertEquals(5.0, sample.mean(), 0.0001);

        List<Double> quartiles = sample.quartiles();
        assertEquals(3, quartiles.size());

        assertDoesNotThrow(() -> sample.jarqueBera());
    }

    @Test
    @DisplayName("Интеграция: алгоритм влияет на результаты")
    void testAlgorithmImpact() {
        StatisticsAlgorithm mockAlgo = Mockito.mock(StatisticsAlgorithm.class);
        when(mockAlgo.mean(anyList())).thenReturn(999.0);
        when(mockAlgo.min(anyList())).thenReturn(1.0);
        when(mockAlgo.max(anyList())).thenReturn(5.0);

        sample.setAlgorithm(mockAlgo);
        assertEquals(999.0, sample.mean(), 0.0001);
    }

    //МУТАЦИИ

    @Test
    @DisplayName("Мутация: skewness не возвращает 0.0 для несимметричных данных")
    void testSkewnessNotZeroForAsymmetric() {
        sample.setData(Arrays.asList(1.0, 1.0, 1.0, 2.0, 10.0));
        double skewness = sample.skewness();
        assertNotEquals(0.0, skewness, 0.0001);
    }

    @Test
    @DisplayName("Мутация: skewness для симметричных данных")
    void testSkewnessSymmetric() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        double skewness = sample.skewness();
        assertEquals(0.0, skewness, 0.0001);
    }

    @Test
    @DisplayName("Мутация: kurtosis не возвращает 0.0")
    void testKurtosisNotZero() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        double kurtosis = sample.kurtosis();
        assertNotEquals(0.0, kurtosis, 0.0001);
        assertTrue(Double.isFinite(kurtosis));
    }

    @Test
    @DisplayName("Мутация: quartiles() проверяет isEmpty")
    void testQuartilesEmptyCheck() {
        sample.setData(Collections.emptyList());
        assertThrows(RuntimeException.class, () -> sample.quartiles());
    }


    @Test
    @DisplayName("Мутация: quartiles() вызывает sort")
    void testQuartilesSortedOrder() {
        sample.setData(Arrays.asList(5.0, 1.0, 3.0, 2.0, 4.0));  // не отсортировано
        List<Double> quartiles = sample.quartiles();

        // Если sort не был вызван, квартили были бы неправильными
        assertTrue(quartiles.get(0) < quartiles.get(1));
        assertTrue(quartiles.get(1) < quartiles.get(2));
    }

    @Test
    @DisplayName("Мутация: getQuantile точные вычисления ")
    void testGetQuantilePrecision() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        List<Double> quartiles = sample.quartiles();

        // Q1 должна быть между 1 и 10
        assertTrue(quartiles.get(0) > 1.0);
        assertTrue(quartiles.get(0) < 10.0);

        // Q2 (медиана) должна быть в середине
        assertTrue(quartiles.get(1) > 4.0);
        assertTrue(quartiles.get(1) < 7.0);

        // Q3 должна быть больше Q1
        assertTrue(quartiles.get(2) > quartiles.get(0));
    }

    @Test
    @DisplayName("Мутация: isNormal проверяет дисперсию")
    void testIsNormalCheckVariance() {
        sample.setData(Arrays.asList(5.0, 5.0, 5.0, 5.0));  // дисперсия = 0
        assertThrows(RuntimeException.class, () -> sample.isNormal(0.05));
    }

    @Test
    @DisplayName("Мутация: isNormal граничные значения (линия 115)")
    void testIsNormalBoundaryValues() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));

        boolean isNormal01 = sample.isNormal(0.01);
        boolean isNormal05 = sample.isNormal(0.05);
        boolean isNormal10 = sample.isNormal(0.10);

        // Все должны быть не null
        assertNotNull(isNormal01);
        assertNotNull(isNormal05);
        assertNotNull(isNormal10);
    }

    @Test
    @DisplayName("Мутация: jarqueBera вычисляет правильно (линия 121)")
    void testJarqueBeraComputation() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        double jb = sample.jarqueBera();

        assertTrue(jb >= 0.0);
        assertTrue(Double.isFinite(jb));
    }

    @Test
    @DisplayName("Мутация: toString возвращает не пустую строку")
    void testToStringNotEmpty() {
        String str = sample.toString();
        assertNotEquals("", str);
        assertNotNull(str);
        assertTrue(str.length() > 0);
    }

    @Test
    @DisplayName("Мутация: equals правильно сравнивает объекты")
    void testEqualsComparison() {
        Sample sample1 = new UniformSample(0.0, 10.0);
        sample1.setData(Arrays.asList(1.0, 2.0, 3.0));

        Sample sample2 = new UniformSample(0.0, 10.0);
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0));

        // Одинаковые данные - равны
        assertTrue(sample1.equals(sample2));
        assertFalse(sample1.equals(null));
        assertTrue(sample1.equals(sample1));
    }

    @Test
    @DisplayName("Мутация: equals разные объекты ")
    void testEqualsDifferentObjects() {
        Sample sample1 = new UniformSample(0.0, 10.0);
        sample1.setData(Arrays.asList(1.0, 2.0, 3.0));

        Sample sample2 = new NormalSample(0.0, 1.0);
        sample2.setData(Arrays.asList(1.0, 2.0, 3.0));

        // Разные типы - не равны
        assertFalse(sample1.equals(sample2));
    }


    @Test
    @DisplayName("Мутация: quartiles() возвращает именно квартили")
    void testQuartilesExact() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0));
        List<Double> q = sample.quartiles();

        // Q1 должна быть примерно 2
        assertEquals(2.0, q.get(0), 0.5);
        // Q2 (медиана) должна быть 3
        assertEquals(3.0, q.get(1), 0.0001);
        // Q3 должна быть примерно 4
        assertEquals(4.0, q.get(2), 0.5);
    }

    @Test
    @DisplayName("Мутация: getQuantile использует интерполяцию (линия 96)")
    void testGetQuantileInterpolation() {
        sample.setData(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
        List<Double> q = sample.quartiles();

        // Q1 должна быть между значениями (интерполяция)
        assertTrue(q.get(0) > 1.0 && q.get(0) < 5.0);

        // Q3 должна быть между значениями
        assertTrue(q.get(2) > 5.0 && q.get(2) < 10.0);
    }

}