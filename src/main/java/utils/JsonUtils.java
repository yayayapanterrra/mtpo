package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import json.SampleData;

import java.io.*;
import java.nio.file.*;

public class JsonUtils {

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private JsonUtils() {
    }

    public static void saveToJson(SampleData sampleData, String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            gson.toJson(sampleData, writer);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить JSON: " + e.getMessage());
        }
    }

    public static SampleData loadFromJson(String filename) {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename));
             JsonReader jsonReader = new JsonReader(reader)) {
            return gson.fromJson(jsonReader, SampleData.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Не удалось загрузить JSON: файл пуст или имеет неверный формат");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить JSON: " + e.getMessage());
        }
    }

    public static String toJson(SampleData sampleData) {
        return gson.toJson(sampleData);
    }
}