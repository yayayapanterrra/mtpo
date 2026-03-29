package fuzz;

import algorithms.LoopStatisticsAlgorithm;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;

import java.util.ArrayList;
import java.util.List;

//целевая ошибка cwe-369 divide by zero в методе skewness()
//если все элементы одинаковы - дисперсия 0. метод возвращает NaN вместо исключения

public class LoopAlgorithmFuzzTest {
    private final LoopStatisticsAlgorithm algorithm = new LoopStatisticsAlgorithm();

    @FuzzTest
    void fuzzSkewness(FuzzedDataProvider data) {
        // Jazzer сам решает какой размер и какие числа подавать
        int size = data.consumeInt(2, 20);

        List<Double> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(data.consumeDouble());
        }

        try {
            double result = algorithm.skewness(list);

            //при данных с одинаковыми значениями result = NaN
            if (Double.isNaN(result)) {
                throw new AssertionError(
                        "skewness() вернул NaN - деление на ноль при нулевой дисперсии" +
                                "Входные данные: " + list
                );
            }

            if (Double.isInfinite(result)) {
                throw new AssertionError(
                        "skewness() вернул Infinity. Входные данные: " + list
                );
            }
        } catch (RuntimeException e) {

        }

    }
}
