package com.business.expensetracker.service;

import com.business.expensetracker.model.Expense;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility component for serializing expense records to CSV format.
 * Full implementation is in task 13.
 * Requirements: 8.1, 8.2, 8.4
 */
@Component
public class CsvExporter {

    private static final String HEADER = "id,date,amount,category,description,paymentMethod\n";

    /**
     * Exports a list of expenses to a CSV string.
     * Columns: id, date, amount, category, description, paymentMethod
     * Special characters (commas, quotes, newlines) in description are properly escaped.
     *
     * @param expenses the list of expenses to export
     * @return a CSV-formatted string including the header row
     */
    public String export(List<Expense> expenses) {
        StringBuilder sb = new StringBuilder(HEADER);
        for (Expense e : expenses) {
            sb.append(e.getId()).append(',');
            sb.append(e.getExpenseDate()).append(',');
            sb.append(e.getAmount()).append(',');
            sb.append(e.getCategoryId()).append(',');
            sb.append(escapeCsvField(e.getDescription())).append(',');
            sb.append(e.getPaymentMethod()).append('\n');
        }
        return sb.toString();
    }

    /**
     * Escapes a CSV field value by wrapping it in double-quotes if it contains
     * commas, double-quotes, or newlines, and doubling any embedded double-quotes.
     */
    private String escapeCsvField(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
