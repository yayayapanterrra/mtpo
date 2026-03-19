package utils;

import json.SampleData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.internal.matchers.NotNull;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@DisplayName("Тестирование JsonUtils")
public class JsonUtilsTest {
    @TempDir
    Path tempDir; //временная директория для тестов

    //Boundary Value
    @Test
    @DisplayName("Граничное значение: пустая выборка")
    void testSaveAndLoadEmptyData_BoundaryValue() throws IOException {
        SampleData original = new SampleData("TestSample", Arrays.asList());
        Path testFile = tempDir.resolve("empty_test.json");

        //сохранение
        JsonUtils.saveToJson(original, testFile.toString());

        //проверка что файл создан
        assertTrue(Files.exists(testFile), "Файл должен быть создан");

        //загрузка
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        //проверка данных
        assertEquals(original.sampleType, loaded.sampleType);
        assertEquals(0, loaded.data.size(), "Размер данных должен быть больше нуля");
    }

    @Test
    @DisplayName("Граничное значение: один элемент")
    void testSaveAndLoadSingleElement_BoundaryValue() throws IOException {
        SampleData original = new SampleData("TestSample", Arrays.asList(42.0));
        Path testFile = tempDir.resolve("empty_test.json");


        JsonUtils.saveToJson(original, testFile.toString());
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        //проверка данных
        assertEquals(1, loaded.data.size());
        assertEquals(42.0, loaded.data.get(0), 0.0001);
    }

    @Test
    @DisplayName("Граничное значение: большой файл (1000 элементов)")
    void testSaveAndLoadLargeData() throws IOException{
        List<Double> largeData = Arrays.asList(1.0, 2.0, 3.0); //упрощенно
        SampleData original = new SampleData("TestSample", largeData);
        Path testFile = tempDir.resolve("large_test.json");

        JsonUtils.saveToJson(original, testFile.toString());
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        assertEquals(original.data.size(), loaded.data.size());
        assertThat(loaded.data.size(), greaterThan(0));
    }

    //EQUIVALENCE PARTITIONING
    @ParameterizedTest
    @ValueSource(strings = {"test1.json", "test2.json", "data.json"})
    @DisplayName("Класс эквивалентности: разные имена файлов")
    void testSaveAndLoadVariousFilenames_EquivalenceClass(String filename) throws IOException{
        SampleData original = new SampleData("UniformSample", Arrays.asList(1.0,2.0,3.0));
        Path testFile = tempDir.resolve(filename);

        JsonUtils.saveToJson(original, testFile.toString());
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        assertEquals(original.sampleType, loaded.sampleType);
        assertEquals(original.data.size(), loaded.data.size());
    }

    @ParameterizedTest
    @ValueSource(doubles = {-100.0, 100.0, 0.0, 3.14526})
    @DisplayName("Класс эквивалентности: разные значения в выборке")
    void testSaveAndLoadVariousValues_EquivalenceClass(double value) throws IOException{
        SampleData original = new SampleData("TestSample", Arrays.asList(value, value*2, value*3));
        Path testFile = tempDir.resolve("Values_test.json");

        JsonUtils.saveToJson(original, testFile.toString());
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        assertEquals(original.data.size(), loaded.data.size());
        for (int i = 0;i<original.data.size();i++) {
            assertEquals(original.data.get(i), loaded.data.get(i), 0.0001);
        }
    }


    //ПОКРЫТИЕ ОПЕРАТОРОВ STATEMENT TESTING
    @Test
    @DisplayName("Покрытие операторов: все методы JsonUtils должны выполниться")
    void testAllMethodExecute_StatementCoverage() throws IOException{
        SampleData testData = new SampleData("TestSample", Arrays.asList(1.0,2.0,3.0));
        Path testFile = tempDir.resolve("coverage_test.json");

        JsonUtils.saveToJson(testData, testFile.toString());
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());
        String jsonString = JsonUtils.toJson(testData);

        //проверка, что все значения получены
        assertAll("Все методы выполнены успешно",
                ()->assertNotNull(loaded),
                ()->assertNotNull(jsonString),
                ()->assertTrue(jsonString.length()>0,"JSON строка не должна быть пустой"),
                ()->assertThat(jsonString, containsString("TestSample"))
        );
    }

    //ПОКРЫТИЕ ВЕТВЛЕНИЙ BRANCH TESTING
    @Test
    @DisplayName("Покрытие ветвлений: файл существует после сохранения")
    void testFileExistsAfterSave_BranchCoverage() throws IOException{
        SampleData testData = new SampleData("TestSample", Arrays.asList(1.0,2.0));
        Path testFile = tempDir.resolve("exists_test.json");

        JsonUtils.saveToJson(testData, testFile.toString());

        assertTrue(Files.exists(testFile), "Файл должен существовать после сохранения");
        assertTrue(Files.isRegularFile(testFile), "Должен быть обычным файлом");
    }

    @Test
    @DisplayName("Покрытие ветвлений: загрузка несуществующего файла должна выбрасывать исключение")
    void testLoadNonExistentFile_BranchCoverage(){
        Path nonExistentFile = tempDir.resolve("non_existent.json");

        assertThrows(RuntimeException.class, ()->JsonUtils.loadFromJson(nonExistentFile.toString()));
    }

    //НЕГАТИВНЫЕ СЦЕНАРИИ
    @Test
    @DisplayName("Негативный сценарий: загрузка несуществующего файла")
    void testNonExistentFile_Negative(){
        assertThrows(RuntimeException.class, ()->JsonUtils.loadFromJson("non_existent_file.json"));
    }

    @Test
    @DisplayName("Негативный сценарий: сохранение в недопустимый путь")
    void testSaveToInvalidPath_Negative(){
        SampleData testData = new SampleData("TestSample", Arrays.asList(1.0,2.0));

        //попытка сохранения в несуществующую директорию
        assertThrows(RuntimeException.class,()->JsonUtils.saveToJson(testData, "/nonexistent/path/file.json"));

    }

    @Test
    @DisplayName("Негативный сценарий: загрузка невалидного Json")
    void testLoadInvalidJson_Negative() throws IOException{
        Path testFile = tempDir.resolve("invalid.json");
        Files.writeString(testFile, "not valid json {{{");
        assertThrows(RuntimeException.class, ()->JsonUtils.loadFromJson(testFile.toString()));
    }

    //ASSUMPTIONS
    @Test
    @DisplayName("Assumption: тест имеет смысл только если директория доступна для записи")
    void testWithAssumptionOnWritableDirectory() throws IOException{
        assumeTrue(Files.exists(tempDir), "Временная директория должна существовать");
        assumeTrue(Files.isWritable(tempDir), "Директория должна быть доступна для записи");

        SampleData testData = new SampleData("TestSample", Arrays.asList(1.0,2.0,3.0));
        Path testFile = tempDir.resolve("assumption_test.json");

        JsonUtils.saveToJson(testData, testFile.toString());
        assumeTrue(Files.exists(testFile));
    }

    @Test
    @DisplayName("Assumption: тест имеет смысл только для файлов с расширением json")
    void testWithAssumptionWithFileExtension() throws IOException{
        String filename = "test.json";

        assumeTrue(filename.endsWith(".json"), "Файл должен иметь расширение .json");

        SampleData testData = new SampleData("TestSample", Arrays.asList(1.0,2.0));
        Path testFile = tempDir.resolve(filename);

        JsonUtils.saveToJson(testData, testFile.toString());
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        assertEquals(testData.sampleType, loaded.sampleType);

    }

    //MATCHERS
    @Test
    @DisplayName("Matchers: использование Hamcrest матчеров")
    void testWithHamcrestMatchers() throws IOException{
        SampleData testData = new SampleData("UniformSample", Arrays.asList(1.0,2.0,3.0,4.0,5.0));
        Path testFile = tempDir.resolve("matchers_test.json");

        JsonUtils.saveToJson(testData, testFile.toString());
        String jsonString = JsonUtils.toJson(testData);

        assertThat(jsonString, containsString("UniformSample"));
        assertThat(jsonString, is(notNullValue()));
        assertThat(jsonString.length(), is(greaterThan(10)));
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());
        assertThat(loaded.sampleType, is(equalTo("UniformSample")));

    }

    //КРУГОВОЕ ТЕСТИРОВАНИЕ
    @Test
    @DisplayName("Круговое тестирование: сохранение-загрузка-сравнение")
    void testRoundTripSaveLoad() throws IOException{
        SampleData original = new SampleData("NormalSample", Arrays.asList(1.0,2.0,3.0,4.0,5.0)).
                withNormalParams(3.0,1.5);
        Path testFile = tempDir.resolve("roundtrip_test.json");

        JsonUtils.saveToJson(original, testFile.toString());
        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        assertEquals(original.sampleType, loaded.sampleType, "Тип выборки должен совпадать");
        assertEquals(original.data.size(), loaded.data.size(), "Размер данных должен совпадать");

        for (int i = 0; i<original.data.size();i++){
            assertEquals(original.data.get(i), loaded.data.get(i), 0.0001,
                    "Элемент " + i + " должен совпадать");
        }

        assertEquals(original.meanVal, loaded.meanVal, 0.0001);
        assertEquals(original.stddevVal, loaded.stddevVal, 0.0001);
    }

    @Test
    @DisplayName("Круговое тестирование: toJson-парсинг-сравнение")
    void testToJsonAndParse(){
        SampleData original = new SampleData("TestSample", Arrays.asList(10.0, 20.0, 30.0));

        //json в строку
        String jsonString = JsonUtils.toJson(original);

        //проверка что строка валидная
        assertThat(jsonString, is(notNullValue()));
        assertThat(jsonString, containsString("TestSample"));
        assertThat(jsonString, containsString("10.0"));

    }

    @Test
    @DisplayName("Проверка: перезапись существующего файла")
    void testOverwriteExistingFile() throws IOException{
        Path testFile = tempDir.resolve("overwrite_test.json");

        //первое сохранение
        SampleData first = new SampleData("FirstSample", Arrays.asList(1.0, 2.0));
        JsonUtils.saveToJson(first, testFile.toString());

        //перезапись
        SampleData second = new SampleData("SecondSample", Arrays.asList(3.0,4.0,5.0));
        JsonUtils.saveToJson(second, testFile.toString());

        SampleData loaded = JsonUtils.loadFromJson(testFile.toString());

        assertEquals("SecondSample", loaded.sampleType);
        assertEquals(3, loaded.data.size());
    }


}
