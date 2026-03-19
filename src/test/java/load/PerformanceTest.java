package load;

import algorithms.LoopStatisticsAlgorithm;
import algorithms.StatisticsAlgorithm;
import algorithms.StreamStatisticsAlgorithm;
import org.junit.jupiter.api.*;
import samples.NormalSample;
import samples.UniformSample;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PerformanceTest {

    private static final StatisticsAlgorithm loopAlgo = new LoopStatisticsAlgorithm();
    private static final StatisticsAlgorithm streamAlgo = new StreamStatisticsAlgorithm();

    private static final int[] SIZES = {1_000, 10_000, 100_000, 500_000, 200_000_000};
    private static final int WARMUP_RUNS = 3;
    private static final int MEASURE_RUNS = 5;


    // Вспомогательные методы

    private long measureMs(Runnable operation) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            operation.run();
        }
        long total = 0;
        for (int i = 0; i < MEASURE_RUNS; i++) {
            long start = System.currentTimeMillis();
            operation.run();
            total += System.currentTimeMillis() - start;
        }
        return total / MEASURE_RUNS;
    }

    private List<Double> generateUniform(int size) {
        long start = System.currentTimeMillis();
        UniformSample sample = new UniformSample(0.0, 1000.0);
        sample.generate(size);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  Генерация Uniform[" + size + "]: " + elapsed + " мс");
        return sample.getData();
    }

    private List<Double> generateNormal(int size) {
        long start = System.currentTimeMillis();
        NormalSample sample = new NormalSample(0.0, 1.0);
        sample.generate(size);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  Генерация Normal[" + size + "]: " + elapsed + " мс");
        return sample.getData();
    }

    private void profileAndPrint(String algoName, String sampleType, int size,
                                 StatisticsAlgorithm algo, List<Double> data) {
        System.out.println("  [" + algoName + " / " + sampleType + " / n=" + size + "]");
        System.out.println("    min:      " + measureMs(() -> algo.min(data)) + " мс");
        System.out.println("    max:      " + measureMs(() -> algo.max(data)) + " мс");
        System.out.println("    mean:     " + measureMs(() -> algo.mean(data)) + " мс");
        System.out.println("    median:   " + measureMs(() -> algo.median(data)) + " мс");
        System.out.println("    variance: " + measureMs(() -> algo.variance(data)) + " мс");
        System.out.println("    skewness: " + measureMs(() -> algo.skewness(data)) + " мс");
        System.out.println("    kurtosis: " + measureMs(() -> algo.kurtosis(data)) + " мс");
    }


    // Предварительный эксперимент

    @Test
    @Order(1)
    @DisplayName("Предварительный эксперимент: поиск максимального размера выборки")
    void preliminaryExperiment() {
        System.out.println("\n=== ПРЕДВАРИТЕЛЬНЫЙ ЭКСПЕРИМЕНТ ===");
        System.out.println("Цель: найти размер выборки при котором время >= 2 минут");

        int[] sizes = {100_000, 500_000, 1_000_000, 10_000_000, 50_000_000};
        long targetMs = 2 * 60 * 1000;

        for (int size : sizes) {
            System.out.println("\nРазмер: " + size);
            List<Double> data = generateUniform(size);

            long start = System.currentTimeMillis();
            loopAlgo.min(data);
            loopAlgo.max(data);
            loopAlgo.mean(data);
            loopAlgo.median(data);
            loopAlgo.variance(data);
            loopAlgo.skewness(data);
            loopAlgo.kurtosis(data);
            long elapsed = System.currentTimeMillis() - start;

            System.out.println("  Loop все операции: " + elapsed + " мс");

            if (elapsed >= targetMs) {
                System.out.println("  >>> Найден максимальный размер: " + size +
                        " (время: " + elapsed + " мс)");
                break;
            }
        }
    }


    // Профилирование: равномерная выборка


    @Test
    @Order(2)
    @DisplayName("Профилирование: равномерная выборка")
    void profileUniformSample() {
        System.out.println("\n=== ПРОФИЛИРОВАНИЕ: РАВНОМЕРНАЯ ВЫБОРКА ===");

        for (int size : SIZES) {
            System.out.println("\nРазмер: " + size);
            List<Double> data = generateUniform(size);
            profileAndPrint("Loop", "Uniform", size, loopAlgo, data);
            profileAndPrint("Stream", "Uniform", size, streamAlgo, data);

            Assertions.assertEquals(
                    loopAlgo.mean(data), streamAlgo.mean(data), 0.0001,
                    "Mean должен совпадать для size=" + size
            );
        }
    }


    // Профилирование: нормальная выборка


    @Test
    @Order(3)
    @DisplayName("Профилирование: нормальная выборка")
    void profileNormalSample() {
        System.out.println("\n=== ПРОФИЛИРОВАНИЕ: НОРМАЛЬНАЯ ВЫБОРКА ===");

        for (int size : SIZES) {
            System.out.println("\nРазмер: " + size);
            List<Double> data = generateNormal(size);
            profileAndPrint("Loop", "Normal", size, loopAlgo, data);
            profileAndPrint("Stream", "Normal", size, streamAlgo, data);

            Assertions.assertEquals(
                    loopAlgo.mean(data), streamAlgo.mean(data), 0.0001,
                    "Mean должен совпадать для size=" + size
            );
        }
    }


    // Сводный итог


    @Test
    @Order(4)
    @DisplayName("Сводный итог: mean для всех размеров и типов")
    void summaryMean() {
        System.out.println("\n=== СВОДНЫЙ ИТОГ: ОПЕРАЦИЯ MEAN ===");

        for (String type : new String[]{"Uniform", "Normal"}) {
            System.out.println("\nТип: " + type);
            for (int size : SIZES) {
                List<Double> data = type.equals("Uniform")
                        ? generateUniform(size)
                        : generateNormal(size);

                long loopMs   = measureMs(() -> loopAlgo.variance(data));
                long streamMs = measureMs(() -> streamAlgo.variance(data));
                long diff     = streamMs - loopMs;
                String faster = diff > 0 ? "Loop быстрее" : diff < 0 ? "Stream быстрее" : "Равно";

                System.out.println("  n=" + size +
                        " | Loop: " + loopMs + " мс" +
                        " | Stream: " + streamMs + " мс" +
                        " | " + faster + " на " + Math.abs(diff) + " мс");
            }
        }
    }
}