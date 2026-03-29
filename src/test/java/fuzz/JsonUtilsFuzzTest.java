package fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import io.cucumber.java.eo.Do;
import org.junit.jupiter.api.io.TempDir;
import utils.JsonUtils;
import json.SampleData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

//целевая ошибка cwe-476 null pointer dereference в методе loadFromJson()
//если передать null как имя - выбрасывается NullPointerException, catch не перехватывает, потому что не IOException

public class JsonUtilsFuzzTest {
    @TempDir
    Path tempDir;

    @FuzzTest
    void fuzzLoadFromJson(FuzzedDataProvider data) {
        // Явно добавляем null как один из вариантов
        // consumeBoolean() — Jazzer быстро попробует оба варианта
        String filename = data.consumeBoolean() ? null : data.consumeString(100);

        try {
            JsonUtils.loadFromJson(filename);
        } catch (RuntimeException e) {
            if (e instanceof NullPointerException) {
                // Это баг CWE-476 — пробрасываем наружу
                throw e;
            }
            // Остальные RuntimeException — ожидаемы
        }
    }

    @FuzzTest
    void fuzzSaveAndLoad(FuzzedDataProvider data) {
        //проверяем, ччто размер не меняется после сохранения и загрузки
        String sampleType = data.consumeString(20);
        int size = data.consumeInt(0, 10);

        List<Double> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double val = data.consumeDouble();
            if (Double.isFinite(val)) {
                values.add(val);
            }
        }

        SampleData original = new SampleData(sampleType, values);
        String tempFile = tempDir.resolve("fuzz_test.json").toString();

        try {
            JsonUtils.saveToJson(original, tempFile);
            SampleData loaded = JsonUtils.loadFromJson(tempFile);

            if (loaded.data != null && original.data != null) {
                if (loaded.data.size() != original.data.size()) {
                    throw new AssertionError("Размер данных не совпадает " +
                            "Было: " + original.data.size() +
                            ", стало: " + loaded.data.size());
                }
            }
        } catch (RuntimeException e) {

        }

    }

}
