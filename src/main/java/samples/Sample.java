package samples;

import algorithms.StatisticsAlgorithm;
import algorithms.LoopStatisticsAlgorithm;
import json.SampleData;
import utils.JsonUtils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public abstract class Sample {

    protected List<Double> data;
    private StatisticsAlgorithm algorithm = new LoopStatisticsAlgorithm();

    public Sample() {
        this.data = new ArrayList<>();
    }

    public Sample(StatisticsAlgorithm algorithm) {
        this.data = new ArrayList<>();
        this.algorithm = algorithm;
    }

    public void setData(List<Double> values){
        this.data.clear();
        this.data.addAll(values);
    }

    public abstract void generate(int n);

    public List<Double> getData() {
        return Collections.unmodifiableList(data);
    }

    public void setAlgorithm(StatisticsAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public StatisticsAlgorithm getAlgorithm() {
        return this.algorithm;
    }

    public double min() {
        return algorithm.min(data);
    }

    public double max() {
        return algorithm.max(data);
    }

    public double mean() {
        return algorithm.mean(data);
    }

    public double median() {
        return algorithm.median(data);
    }

    public double variance() {
        return algorithm.variance(data);
    }

    public double skewness() {
        return algorithm.skewness(data);
    }

    public double kurtosis() {
        return algorithm.kurtosis(data);
    }

    public List<Double> quartiles() {
        if (data.isEmpty()) {
            throw new RuntimeException("Пустая выборка");
        }

        List<Double> temp = new ArrayList<>(data);
        Collections.sort(temp);

        int n = temp.size();
        double q1 = getQuantile(temp, 0.25);
        double q2 = median();
        double q3 = getQuantile(temp, 0.75);

        return Arrays.asList(q1, q2, q3);
    }

    private double getQuantile(List<Double> sortedData, double q) {
        int n = sortedData.size();
        double pos = q * (n - 1);
        int index = (int) pos;
        double frac = pos - index;

        if (index + 1 < n) {
            return sortedData.get(index) + frac * (sortedData.get(index + 1) - sortedData.get(index));
        } else {
            return sortedData.get(index);
        }
    }

    public boolean isNormal(double alpha) {
        double jb = jarqueBera();
        double critical;

        if (alpha == 0.10) critical = 4.605;
        else if (alpha == 0.05) critical = 5.991;
        else if (alpha == 0.01) critical = 9.210;
        else throw new RuntimeException("Неподдерживаемый уровень значимости: " + alpha);

        return jb < critical;
    }

    public double jarqueBera() {
        if (data.size() < 3) {
            throw new RuntimeException("Недостаточно данных для теста Jarque-Bera");
        }
        double n = (double) data.size();
        double s = skewness();
        double k = kurtosis();
        return (n / 6.0) * (s * s + (k * k) / 4.0);
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            for (double x : data) {
                writer.write(String.valueOf(x));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + e.getMessage());
        }
    }

    public void loadFromFile(String filename) {
        data.clear();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    data.add(Double.parseDouble(line.trim()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить файл: " + e.getMessage());
        }

        if (data.isEmpty()) {
            throw new RuntimeException("Файл пуст или не содержит чисел");
        }
    }

    public void saveToJson(String filename) {
        SampleData sampleData = createSampleData();
        JsonUtils.saveToJson(sampleData, filename);
    }

    public void loadFromJson(String filename) {
        SampleData sampleData = JsonUtils.loadFromJson(filename);
        data.clear();
        data.addAll(sampleData.data);
        restoreFromSampleData(sampleData);
    }

    protected SampleData createSampleData() {
        return new SampleData(this.getClass().getSimpleName(), new ArrayList<>(data));
    }

    protected void restoreFromSampleData(SampleData sampleData) {
    }

    public void showCharact() {
        System.out.println("=======================================");
        System.out.println("  Статистика выборки: " + this.getClass().getSimpleName());
        System.out.println("  Алгоритм: " + algorithm.getName());
        System.out.println("=======================================");
        System.out.println("  Размер выборки: " + data.size());
        System.out.println("---------------------------------------");
        System.out.printf("  Минимум:        %.4f%n", this.min());
        System.out.printf("  Максимум:       %.4f%n", this.max());
        System.out.printf("  Среднее:        %.4f%n", this.mean());
        System.out.printf("  Медиана:        %.4f%n", this.median());
        System.out.println("---------------------------------------");

        List<Double> q = this.quartiles();
        System.out.printf("  Квартиль 0.25:  %.4f%n", q.get(0));
        System.out.printf("  Квартиль 0.50:  %.4f%n", q.get(1));
        System.out.printf("  Квартиль 0.75:  %.4f%n", q.get(2));
        System.out.println("---------------------------------------");

        System.out.printf("  Дисперсия:      %.4f%n", this.variance());
        System.out.printf("  Асимметрия:     %.4f%n", this.skewness());
        System.out.printf("  Эксцесс:        %.4f%n", this.kurtosis());
        System.out.println("=======================================");

        try {
            double jb = this.jarqueBera();
            boolean isNormal = this.isNormal(0.05);
            System.out.printf("  Тест Jarque-Bera: %.4f%n", jb);
            System.out.println("  Нормальность: " + (isNormal ? "не отвергается" : "отвергается"));
        } catch (Exception e) {
            System.out.println("  Тест на нормальность: невозможно проверить");
        }
        System.out.println("=======================================");
    }

    @Override
    public String toString() {
        return String.format("%s[size=%d, algorithm=%s]",
                this.getClass().getSimpleName(),
                data.size(),
                algorithm.getName());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Sample sample = (Sample) obj;
        return data.equals(sample.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}