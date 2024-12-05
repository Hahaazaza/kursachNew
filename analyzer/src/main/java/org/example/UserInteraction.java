package org.example;

import java.util.Scanner;

public class UserInteraction {

    private final Scanner scanner;

    public UserInteraction() {
        this.scanner = new Scanner(System.in);
    }

    public int getMode() {
        int mode = -1;
        while (mode != 1 && mode != 2 && mode != 3) {
            System.out.println("Выберите режим работы (1 - анализ одного файла, 2 - анализ директории, 3 - завершить работу):");
            try {
                mode = scanner.nextInt();
                scanner.nextLine(); // Считываем оставшийся символ новой строки
            } catch (java.util.InputMismatchException e) {
                System.out.println("Неверный ввод. Пожалуйста, введите 1, 2 или 3.");
                scanner.nextLine(); // Очистка буфера ввода
            }
        }
        return mode;
    }

    public String getFilePath() {
        System.out.println("Введите путь до файла .sql:");
        return scanner.nextLine();
    }

    public String getDirectoryPath() {
        System.out.println("Введите путь до директории с файлами .sql:");
        return scanner.nextLine();
    }

    public boolean shouldContinue() {
        String continueInput;
        while (true) {
            System.out.println("Хотите продолжить? (y/n):");
            continueInput = scanner.nextLine().trim().toLowerCase();
            if (continueInput.equals("y") || continueInput.equals("n")) {
                break;
            } else {
                System.out.println("Неверный ввод. Пожалуйста, введите 'y' или 'n'.");
            }
        }
        return continueInput.equals("y");
    }
}
