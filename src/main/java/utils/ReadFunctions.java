package utils;

import java.util.Scanner;

public class ReadFunctions {

    private static Scanner scanner = new Scanner(System.in);


    public static Scanner getScanner(){
        return scanner;
    }

    private ReadFunctions() {
    }

    public static int readInt(String prompt, int min, int max) {
        int value;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Число должно быть от " + min + " до " + max);
                }
            } else {
                System.out.println("Неверный ввод. Попробуйте еще раз");
                scanner.next();
            }
        }
    }

    public static int readInt(String prompt) {
        return readInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static double readDouble(String prompt, double min, double max) {
        double value;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                value = scanner.nextDouble();
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Число должно быть от " + min + " до " + max);
                }
            } else {
                System.out.println("Неверный ввод. Попробуйте еще раз");
                scanner.next();
            }
        }
    }

    public static double readDouble(String prompt) {
        return readDouble(prompt, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.next();
    }
}