package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SQLInjectionAnalyzer {
    public static void main(String[] args) {
        UserInteraction userInteraction = new UserInteraction();
        SQLAnalyzerInterface sqlAnalyzer = new SQLAnalyzer();
        ReportGeneratorInterface reportGenerator = new ReportGenerator();
        boolean continueRunning = true;

        while (continueRunning) {
            int mode = userInteraction.getMode();

            if (mode == 3) {
                continueRunning = false;
                continue;
            }

            List<SQLInjectionReport> reports = new ArrayList<>();

            if (mode == 1) {
                String filePath = userInteraction.getFilePath();
                reports = sqlAnalyzer.analyzeFile(filePath);
                if (reports.isEmpty()) {
                    System.out.println("Ошибка: Файл не найден или некорректный путь.");
                }
            } else if (mode == 2) {
                String directoryPath = userInteraction.getDirectoryPath();
                List<Callable<List<SQLInjectionReport>>> tasks = sqlAnalyzer.analyzeDirectory(directoryPath);
                if (tasks.isEmpty()) {
                    System.out.println("Ошибка: Директория не найдена или некорректный путь.");
                } else {
                    ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                    try {
                        List<Future<List<SQLInjectionReport>>> futures = executor.invokeAll(tasks);
                        for (Future<List<SQLInjectionReport>> future : futures) {
                            reports.addAll(future.get());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    } finally {
                        executor.shutdown();
                    }
                }
            }

            if (!reports.isEmpty()) {
                reportGenerator.generateReport(reports);
            }

            continueRunning = userInteraction.shouldContinue();
        }

        System.out.println("Программа завершена.");
    }
}
