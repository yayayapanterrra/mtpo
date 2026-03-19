package samples;

import algorithms.LoopStatisticsAlgorithm;
import algorithms.StreamStatisticsAlgorithm;
import json.*;
import utils.*;
import samples.*;

public class Main {

    public static void main(String[] args) {
        UniformSample uniformSample = null;
        NormalSample normalSample = null;
        FileSample fileSample = null;
        ConsoleSample consoleSample = null;

        while (true) {
            System.out.println("\n=====================================");
            System.out.println("       Статистический калькулятор");
            System.out.println("=====================================");
            System.out.println("1. Ввести выборку вручную");
            System.out.println("2. Создать равномерную выборку");
            System.out.println("3. Создать нормальную выборку");
            System.out.println("4. Загрузить из JSON");
            System.out.println("5. Вывести показатели");
            System.out.println("6. Сохранить в JSON");
            System.out.println("7. Оценка взаимозависимости двух выборок");
            System.out.println("8. Выход");
            System.out.println("=====================================");

            int choice = ReadFunctions.readInt("Выберите пункт меню: ", 0, 10);

            switch (choice) {
                case 0: {
                    System.out.println("\n----ТЕСТИРОВАНИЕ----");
                    System.out.println("Запустите тесты через Maven: mvn test");
                    break;
                }
                case 1: {
                    int n = ReadFunctions.readInt("Введите количество элементов: ", 1, 100000);
                    consoleSample = new ConsoleSample();
                    consoleSample.generate(n);
                    System.out.println("\nВыборка сохранена");
                    break;
                }
                case 2: {
                    int n = ReadFunctions.readInt("Размер выборки: ", 1, 100000);
                    double a = ReadFunctions.readDouble("Минимум: ");
                    double b = ReadFunctions.readDouble("Максимум: ", a, Double.MAX_VALUE);
                    uniformSample = new UniformSample(a, b);
                    uniformSample.generate(n);
                    System.out.println("Равномерная выборка создана, n = " + n);
                    break;
                }
                case 3: {
                    int n = ReadFunctions.readInt("Размер выборки: ", 1, 100000);
                    double mean = ReadFunctions.readDouble("Среднее значение: ");
                    double stddev = ReadFunctions.readDouble("Стандартное отклонение: ", 0, Double.MAX_VALUE);
                    normalSample = new NormalSample(mean, stddev);
                    normalSample.generate(n);
                    System.out.println("Нормальная выборка создана, n = " + n);
                    break;
                }
                case 4: {
                    try {
                        String filename = ReadFunctions.readString("Введите имя файла (.json): ");
                        SampleData sampleData = JsonUtils.loadFromJson(filename);

                        switch (sampleData.sampleType) {
                            case "UniformSample":
                                uniformSample = new UniformSample(sampleData.minVal, sampleData.maxVal);
                                uniformSample.loadFromJson(filename);
                                System.out.println("Загружено UniformSample: " + uniformSample.getData().size() + " элементов");
                                break;
                            case "NormalSample":
                                normalSample = new NormalSample(sampleData.meanVal, sampleData.stddevVal);
                                normalSample.loadFromJson(filename);
                                System.out.println("Загружено NormalSample: " + normalSample.getData().size() + " элементов");
                                break;
                            case "FileSample":
                            case "ConsoleSample":
                                fileSample = new FileSample();
                                fileSample.loadFromJson(filename);
                                System.out.println("Загружено: " + fileSample.getData().size() + " элементов");
                                break;
                            default:
                                System.out.println("Неизвестный тип выборки: " + sampleData.sampleType);
                        }
                    } catch (RuntimeException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                }
                case 5: {
                    if (consoleSample != null) {
                        System.out.println("\nВведенная выборка:");
                        consoleSample.showCharact();
                    }
                    if (uniformSample != null) {
                        System.out.println("\nРавномерная выборка:");
                        uniformSample.showCharact();
                    }
                    if (normalSample != null) {
                        System.out.println("\nНормальная выборка:");
                        normalSample.showCharact();
                    }
                    if (fileSample != null) {
                        System.out.println("\nВыборка из файла:");
                        fileSample.showCharact();
                    }
                    break;
                }
                case 6: {
                    int sampleChoice = ReadFunctions.readInt(
                            "\nВыберите выборку для сохранения в JSON: \n1 - Равномерная\n2 - Нормальная\n3 - Из файла\n4 - Введенная\nВведите число: ", 1, 4);

                    Sample selectedSample = pickSample(sampleChoice, uniformSample, normalSample, fileSample, consoleSample);

                    if (selectedSample != null) {
                        try {
                            String filename = ReadFunctions.readString("Введите имя файла (.json): ");
                            selectedSample.saveToJson(filename.endsWith(".json") ? filename : filename + ".json");
                            System.out.println("Выборка сохранена в JSON");
                        } catch (RuntimeException e) {
                            System.out.println("Ошибка: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Сначала создайте выборку");
                    }
                    break;
                }
                case 7: {
                    int a = ReadFunctions.readInt(
                            "Выберите первую выборку:\n1 - Равномерная\n2 - Нормальная\n3 - Из файла\n4 - Введенная\nВыбор: ", 1, 4);
                    int b = ReadFunctions.readInt("Выберите вторую выборку: ", 1, 4);

                    Sample s1 = pickSample(a, uniformSample, normalSample, fileSample, consoleSample);
                    Sample s2 = pickSample(b, uniformSample, normalSample, fileSample, consoleSample);

                    if (s1 == null || s2 == null) {
                        System.out.println("Одна из выборок не существует");
                        break;
                    }
                    try {
                        double r = StatsUtils.correlation(s1, s2);
                        System.out.printf("Коэффициент корреляции Пирсона: %.4f%n", r);
                    } catch (RuntimeException e) {
                        System.out.println("Ошибка: " + e.getMessage());
                    }
                    break;
                }

                case 8: {
                    System.out.println("Выход...");
                    return;
                }
                default:
                    System.out.println("Неверный выбор!");
                    break;
            }
        }
    }

    private static Sample pickSample(int choice, UniformSample uniform, NormalSample normal, FileSample file, ConsoleSample console) {
        return switch (choice) {
            case 1 -> uniform;
            case 2 -> normal;
            case 3 -> file;
            case 4 -> console;
            default -> null;
        };
    }
}