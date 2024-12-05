package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ReportGenerator implements ReportGeneratorInterface {

    @Override
    public void generateReport(List<SQLInjectionReport> reports) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonReport = gson.toJson(reports);
        System.out.println("Отчет о проверке SQL-кода:");
        System.out.println(jsonReport);

        try (FileWriter writer = new FileWriter("sql_injection_report.json")) {
            writer.write(jsonReport);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
