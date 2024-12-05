package org.example;

import java.util.List;
import java.util.concurrent.Callable;

public interface SQLAnalyzerInterface {
    List<SQLInjectionReport> analyzeFile(String filePath);
    List<Callable<List<SQLInjectionReport>>> analyzeDirectory(String directoryPath);
}
