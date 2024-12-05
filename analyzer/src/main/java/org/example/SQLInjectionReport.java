package org.example;

public class SQLInjectionReport {
    private final String fileName;
    private final int lineNumber;
    private final String query;
    private final String message;

    public SQLInjectionReport(String fileName, int lineNumber, String query) {
        this(fileName, lineNumber, query, "Потенциальная SQL инъекция найдена");
    }

    public SQLInjectionReport(String fileName, int lineNumber, String query, String message) {
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.query = query;
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getQuery() {
        return query;
    }

    public String getMessage() {
        return message;
    }
}
