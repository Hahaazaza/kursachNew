package org.example;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SQLAnalyzer implements SQLAnalyzerInterface {

    @Override
    public List<SQLInjectionReport> analyzeFile(String filePath) {
        List<SQLInjectionReport> reports = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return reports;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            boolean isSafe = true;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (isPotentialSQLInjection(line)) {
                    reports.add(new SQLInjectionReport(filePath, lineNumber, line));
                    isSafe = false;
                }
            }
            if (isSafe) {
                reports.add(new SQLInjectionReport(filePath, 0, "", "Код безопасен"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reports;
    }

    @Override
    public List<Callable<List<SQLInjectionReport>>> analyzeDirectory(String directoryPath) {
        List<Callable<List<SQLInjectionReport>>> tasks = new ArrayList<>();
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            return tasks;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".sql"));
        if (files != null) {
            for (File file : files) {
                tasks.add(() -> analyzeFile(file.getAbsolutePath()));
            }
        }
        return tasks;
    }

    private boolean isPotentialSQLInjection(String query) {
        query = query.trim();
        if (query.startsWith("--") || query.startsWith("/*") || query.startsWith("*/")) {
            return false; // Игнорируем комментарии
        }

        try {
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Statement statement = parserManager.parse(new StringReader(query));
            if (statement instanceof Insert) {
                Insert insert = (Insert) statement;
                // Проверка на наличие потенциально опасных элементов в INSERT-запросе
                return insert.getItemsList().toString().contains("'") || insert.getItemsList().toString().contains("--") ||
                        insert.getItemsList().toString().contains("/*") || insert.getItemsList().toString().contains("*/") ||
                        insert.getItemsList().toString().contains("OR") || insert.getItemsList().toString().contains("AND") ||
                        insert.getItemsList().toString().contains("UNION") || insert.getItemsList().toString().contains("SELECT");
            } else if (statement instanceof Select) {
                Select select = (Select) statement;
                // Проверка на наличие потенциально опасных элементов в SELECT-запросе
                return select.getSelectBody().toString().contains("'") || select.getSelectBody().toString().contains("--") ||
                        select.getSelectBody().toString().contains("/*") || select.getSelectBody().toString().contains("*/") ||
                        select.getSelectBody().toString().contains("OR") || select.getSelectBody().toString().contains("AND") ||
                        select.getSelectBody().toString().contains("UNION") || select.getSelectBody().toString().contains("SELECT");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
