package bdd;

import io.cucumber.java.After;
import io.cucumber.java.ru.*;
import json.SampleData;
import samples.NormalSample;
import samples.Sample;
import samples.UniformSample;
import utils.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonSteps {
    private UniformSample uniformSample;
    private NormalSample normalSample;

    //текущая активная выборка
    private samples.Sample currentSample;

    //загруженные из json данные
    private SampleData loadedData;

    //статистики исходной выборки до сохранения
    private double originalMean;
    private double originalVariance;
    private double originalMin;
    private double originalMax;

    //перехваченное исключение
    private Exception thrownException;

    //список временных файлов для удаления после теста
    private final List<String> tempFiles = new ArrayList<>();

    //Удаление временных файлов после каждого сценария
    @After
    public void cleanupFiles(){
        for (String filename:tempFiles){
            try{
                Files.deleteIfExists(Paths.get(filename));
            }catch (IOException ignored){

            }
        }
        tempFiles.clear();
    }

    //Given
    @Дано("рабочая директория для json-файлов доступна для записи")
    public void checkWorkingDirectory(){
        assertTrue(Files.isWritable(Paths.get(".")),
                "Рабочая директория должна быть доступна для записи");
    }

    //равномерная выборка с минимумом 0.0 и максимумом 10.0 сгенерирована размером 50 элементов
    @Дано("равномерная выборка с минимумом {double} и максимумом {double} сгенерирована размером {int} элементов")
    public void generateUniformSample(double min, double max, int size){
        uniformSample = new UniformSample(min, max);
        uniformSample.generate(size);
        currentSample = uniformSample;
    }

    //нормальная выборка со средним 5.0 отклонение 2.0 сгенерирована размером 100 элементов
    @Дано("нормальная выборка со средним {double} и стандартным отклонением {double} сгенерирована " +
            "размером {int} элементов")
    public void generateNormalSample(double mean, double stddev, int size){
        normalSample = new NormalSample(mean, stddev);
        normalSample.generate(size);
        currentSample = normalSample;
    }

    //Given файл corrupted.json содержит невалидный json: "{{не валидный json"
    @Дано("файл {string} содержит невалидный JSON: {string}")
    public void createCorruptedFile(String filename, String content) throws IOException{
        Files.writeString(Paths.get(filename), content);
        tempFiles.add(filename);
    }


    //When

    //выборка сохраняется в файл uniform_test.json
    @Когда("выборка сохраняется в файл {string}")
    public void saveSample(String filename){
        currentSample.saveToJson(filename);
        tempFiles.add(filename);
    }

    //выборка загружается из файла uniform_test.json
    @Когда("выборка загружается из файла {string}")
    public void loadSample(String filename){
        loadedData = JsonUtils.loadFromJson(filename);
    }

    @Когда("вычисляются статистики исходной выборки")
    public void computeOriginalStats(){
        originalMean = currentSample.mean();
        originalMax = currentSample.max();
        originalMin = currentSample.min();
        originalVariance = currentSample.variance();
    }

    //пользователь пытается загрузить выборку из файла nonexistent.json
    @Когда("пользователь пытается загрузить выборку из файла {string}")
    public void tryLoadSample(String filename){
        thrownException = null;
        try {
            loadedData = JsonUtils.loadFromJson(filename);
        } catch (Exception e){
            thrownException = e;
        }
    }

    //пользоватиель пытается сохранить выборку в файл "/nonexistent/path/file.json
    @Когда("пользователь пытается сохранить выборку в файл {string}")
    public void trySaveSample(String filename){
        thrownException = null;
        try {
            currentSample.saveToJson(filename);
        }catch (Exception e){
            thrownException = e;
        }
    }

    //Then

    //файл uniform_test.json существует на диске
    @Тогда("файл {string} существует на диске")
    public void checkFileExists(String filename){
        assertTrue(Files.exists(Paths.get(filename)), "Файл " + filename + " должен существовать на диске");
    }

    //тип загруженной выборки равен UniformSample
    @Тогда("тип загруженной выборки равен {string}")
    public void checkLoadedType(String expectedType){
        assertNotNull(loadedData, "Данные должны быть загружены");
        assertEquals(expectedType, loadedData.sampleType, "Тип выборки должен быть "+expectedType);
    }

    //размер выборки равен 50
    @Тогда("размер загруженной выборки равен {int}")
    public void checkLoadedSize(int expectedSize){
        assertNotNull(loadedData, "Данные должны быть загружены");
        assertEquals(expectedSize, loadedData.data.size(), "Размер выборки должен быть " + expectedSize);
    }

    //параметр минимума загруженной выборки равен 0.0 с точностью 0.0001
    @Тогда("параметр минимума загруженной выборки равен {double} с точностью {double}")
    public void checkLoadedMinParam(double expected, double delta){
        assertEquals(expected, loadedData.minVal, delta, "Параметр минимума должен быть " + expected);
    }

    //параметр максимума загруженной выборки равен 10.0 с точностью 0.0001
    @Тогда("параметр максимума загруженной выборки равен {double} с точностью {double}")
    public void checkLoadedMaxParam(double expected, double delta){
        assertEquals(expected, loadedData.maxVal, delta, "Параметр минимума должен быть " + expected);
    }

    //параметр среднего загруженной выборки равен 5.0 с точностью 0.0001
    @Тогда("параметр среднего загруженной выборки равен {double} с точностью {double}")
    public void checkLoadedMeanParam(double expected, double delta){
        assertEquals(expected, loadedData.meanVal, delta, "Параметр минимума должен быть " + expected);
    }

    //параметр отклонения загруженной выборки равен 2.0 с точностью 0.0001
    @Тогда("параметр стандартного отклонения загруженной выборки равен {double} с точностью {double}")
    public void checkLoadedStddevParam(double expected, double delta){
        assertEquals(expected, loadedData.stddevVal, delta, "Параметр минимума должен быть " + expected);
    }

    @Тогда("данные загруженной выборки совпадают с исходными с точностью {double}")
    public void checkLoadedDataMatchesOriginal(double delta){
        List<Double> original = currentSample.getData();
        assertNotNull(loadedData.data, "Данные загруженной выборки не должны быть null");
        assertEquals(original.size(), loadedData.data.size(), "Размеры выборок должны совпадать");
        for (int i = 0; i<original.size();i++){
            assertEquals(original.get(i), loadedData.data.get(i), delta,
                    "Элемент ["+i+"] должен совпадать");
        }
    }

    @Тогда("среднее загруженной выборки совпадает с исходным с точностью {double}")
    public void checkLoadedMeanMatchesOriginal(double delta){
        UniformSample loaded = new UniformSample();
        loaded.setData(loadedData.data);
        assertEquals(originalMean, loaded.mean(), delta,
                "Среднее загруженной выборки должно совпадать с исходным");
    }

    @Тогда("дисперсия загруженной выборки совпадает с исходной с точностью {double}")
    public void checkLoadedVarianceMatchesOriginal(double delta){
        UniformSample loaded = new UniformSample();
        loaded.setData(loadedData.data);
        assertEquals(originalVariance, loaded.variance(), delta,
                "Дисперсия загруженной выборки должно совпадать с исходным");
    }

    @Тогда("минимум загруженной выборки совпадает с исходным с точностью {double}")
    public void checkLoadedMinMatchesOriginal(double delta){
        UniformSample loaded = new UniformSample();
        loaded.setData(loadedData.data);
        assertEquals(originalMin, loaded.min(), delta,
                "Минимум загруженной выборки должно совпадать с исходным");
    }

    @Тогда("максимум загруженной выборки совпадает с исходным с точностью {double}")
    public void checkLoadedMaxMatchesOriginal(double delta){
        UniformSample loaded = new UniformSample();
        loaded.setData(loadedData.data);
        assertEquals(originalMax, loaded.max(), delta,
                "Максимум загруженной выборки должно совпадать с исходным");
    }

    @Тогда("загрузка завершается с ошибкой {string}")
    public void checkLoadException(String expectedMessage){
        assertNotNull(thrownException, "Ожидалось исключение с сообщением "+expectedMessage);
        assertTrue(thrownException.getMessage().contains(expectedMessage),
                "Сообщение об ошибке должно содержать: "+expectedMessage+
                "\nФактическое: "+thrownException.getMessage());
    }

    @Тогда("сохранение завершается с ошибкой {string}")
    public void checkSaveException(String expectedMessage){
        assertNotNull(thrownException, "Ожидалось исключение с сообщением "+expectedMessage);
        assertTrue(thrownException.getMessage().contains(expectedMessage),
                "Сообщение об ошибке должно содержать: "+expectedMessage+
                        "\nФактическое: "+thrownException.getMessage());
    }

    //But данные в памяти не изменились
    @Тогда("данные в памяти не изменились")
    public void checkDataNotChanged(){
        assertNull(loadedData, "Данные в памяти не должны были измениться после неудачной загрузки");
    }

}
