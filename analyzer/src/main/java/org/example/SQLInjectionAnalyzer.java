package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLInjectionAnalyzer {
    private static final Logger logger = Logger.getLogger(SQLInjectionAnalyzer.class.getName());

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

            switch (mode) {
                case 1:
                    handleFileMode(userInteraction, sqlAnalyzer, reports);
                    break;
                case 2:
                    handleDirectoryMode(userInteraction, sqlAnalyzer, reports);
                    break;
                default:
                    logger.log(Level.SEVERE, "Некорректный режим: " + mode);
            }

            if (!reports.isEmpty()) {
                reportGenerator.generateReport(reports);
            }

            continueRunning = userInteraction.shouldContinue();
        }

        System.out.println("Программа завершена.");
    }

    private static void handleFileMode(UserInteraction userInteraction, SQLAnalyzerInterface sqlAnalyzer, List<SQLInjectionReport> reports) {
        String filePath = userInteraction.getFilePath();
        reports.addAll(sqlAnalyzer.analyzeFile(filePath));
        if (reports.isEmpty()) {
            logger.log(Level.SEVERE, "Ошибка: Файл не найден или некорректный путь.");
        }
    }

    private static void handleDirectoryMode(UserInteraction userInteraction, SQLAnalyzerInterface sqlAnalyzer, List<SQLInjectionReport> reports) {
        String directoryPath = userInteraction.getDirectoryPath();
        List<Callable<List<SQLInjectionReport>>> tasks = sqlAnalyzer.analyzeDirectory(directoryPath);
        if (tasks.isEmpty()) {
            logger.log(Level.SEVERE, "Ошибка: Директория не найдена или некорректный путь.");
        } else {
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            try {
                List<Future<List<SQLInjectionReport>>> futures = executor.invokeAll(tasks);
                for (Future<List<SQLInjectionReport>> future : futures) {
                    reports.addAll(future.get());
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, "Ошибка при выполнении задач", e);
            } finally {
                executor.shutdown();
            }
        }
    }
}
