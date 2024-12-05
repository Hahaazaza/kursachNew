package org.example;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.JSQLParserException;

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
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            int lineNumber = 0;
            boolean isSafe = true;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                sqlBuilder.append(line).append(" ");
                if (line.trim().endsWith(";")) {
                    String[] queries = sqlBuilder.toString().split(";");
                    for (String query : queries) {
                        query = query.trim();
                        if (!query.isEmpty()) {
                            try {
                                if (isPotentialSQLInjection(query)) {
                                    reports.add(new SQLInjectionReport(filePath, lineNumber, query));
                                    isSafe = false;
                                }
                            } catch (JSQLParserException | ParseException e) {
                                reports.add(new SQLInjectionReport(filePath, lineNumber, query, "Ошибка парсинга: " + e.getMessage()));
                                isSafe = false;
                            }
                        }
                    }
                    sqlBuilder.setLength(0); // Очищаем буфер для следующего запроса
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

    private boolean isPotentialSQLInjection(String query) throws JSQLParserException, ParseException {
        query = query.trim();
        if (query.isEmpty() || query.startsWith("--") || query.startsWith("/*") || query.startsWith("*/")) {
            return false; // Игнорируем комментарии и пустые строки
        }

        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement = parserManager.parse(new StringReader(query));

        // Регулярные выражения для поиска потенциально опасных элементов
        String[] dangerousPatterns = {
                "'.*'", // Поиск одиночных кавычек
                "--.*", // Поиск комментариев
                "/\\*.*\\*/", // Поиск блоковых комментариев
                "OR\\s+[0-9]+\\s*=\\s*[0-9]+", // Поиск условий OR
                "AND\\s+[0-9]+\\s*=\\s*[0-9]+", // Поиск условий AND
                "UNION\\s+SELECT", // Поиск UNION SELECT
                "SELECT\\s+.*\\s+FROM" // Поиск SELECT ... FROM
        };

        for (String pattern : dangerousPatterns) {
            if (query.matches(pattern)) {
                return true;
            }
        }

        // Дополнительная проверка для INSERT и SELECT запросов
        if (statement instanceof Insert) {
            Insert insert = (Insert) statement;
            return insert.getItemsList().toString().contains("'") ||
                    insert.getItemsList().toString().contains("--") ||
                    insert.getItemsList().toString().contains("/*") ||
                    insert.getItemsList().toString().contains("*/") ||
                    insert.getItemsList().toString().contains("OR") ||
                    insert.getItemsList().toString().contains("AND") ||
                    insert.getItemsList().toString().contains("UNION") ||
                    insert.getItemsList().toString().contains("SELECT");
        } else if (statement instanceof Select) {
            Select select = (Select) statement;
            return select.getSelectBody().toString().contains("'") ||
                    select.getSelectBody().toString().contains("--") ||
                    select.getSelectBody().toString().contains("/*") ||
                    select.getSelectBody().toString().contains("*/") ||
                    select.getSelectBody().toString().contains("OR") ||
                    select.getSelectBody().toString().contains("AND") ||
                    select.getSelectBody().toString().contains("UNION") ||
                    select.getSelectBody().toString().contains("SELECT");
        }

        return false;
    }
}
