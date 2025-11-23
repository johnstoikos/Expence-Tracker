package com.example.expensetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseManager {
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "expenses_data";
    private static final String EXPENSES_KEY = "expenses_list";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ExpenseManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public boolean addExpense(Expense expense) {
        try {
            List<Expense> expenses = getAllExpenses();

            expenses.add(expense);

            JSONArray jsonArray = new JSONArray();
            for (Expense exp : expenses) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("description", exp.getDescription());
                jsonObject.put("amount", exp.getAmount());
                jsonObject.put("category", exp.getCategory());
                jsonObject.put("date", exp.getDate());
                jsonArray.put(jsonObject);
            }

            sharedPreferences.edit().putString(EXPENSES_KEY, jsonArray.toString()).apply();
            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String jsonString = sharedPreferences.getString(EXPENSES_KEY, "[]");

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Expense expense = new Expense();
                expense.setDescription(jsonObject.getString("description"));
                expense.setAmount(jsonObject.getDouble("amount"));
                expense.setCategory(jsonObject.getString("category"));
                expense.setDate(jsonObject.getString("date"));
                expenses.add(expense);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public double getTotalExpenses() {
        List<Expense> expenses = getAllExpenses();
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    public Statistics getStatisticsForPeriod(String startDate, String endDate) {
        List<Expense> allExpenses = getAllExpenses();
        List<Expense> periodExpenses = new ArrayList<>();
        double total = 0;
        double maxAmount = 0;
        double minAmount = Double.MAX_VALUE;
        String maxDate = "";
        String minDate = "";

        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);

            for (Expense expense : allExpenses) {
                Date expenseDate = dateFormat.parse(expense.getDate());
                if (!expenseDate.before(start) && !expenseDate.after(end)) {
                    periodExpenses.add(expense);
                    total += expense.getAmount();

                    String currentDate = expense.getDate();
                    double dailyTotal = getDailyTotal(currentDate, allExpenses);

                    if (dailyTotal > maxAmount) {
                        maxAmount = dailyTotal;
                        maxDate = currentDate;
                    }
                    if (dailyTotal < minAmount && dailyTotal > 0) {
                        minAmount = dailyTotal;
                        minDate = currentDate;
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        int days = getDaysBetween(startDate, endDate);
        double average = days > 0 ? total / days : 0;

        return new Statistics(total, average, maxDate, maxAmount, minDate, minAmount, periodExpenses.size());
    }

    public Statistics getWeeklyStats() {
        Calendar calendar = Calendar.getInstance();
        String endDate = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.DAY_OF_YEAR, -6);
        String startDate = dateFormat.format(calendar.getTime());

        return getStatisticsForPeriod(startDate, endDate);
    }

    public Statistics getMonthlyStats() {
        Calendar calendar = Calendar.getInstance();
        String endDate = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.MONTH, -1);
        String startDate = dateFormat.format(calendar.getTime());

        return getStatisticsForPeriod(startDate, endDate);
    }

    private double getDailyTotal(String date, List<Expense> expenses) {
        double total = 0;
        for (Expense expense : expenses) {
            if (expense.getDate().equals(date)) {
                total += expense.getAmount();
            }
        }
        return total;
    }

    private int getDaysBetween(String startDate, String endDate) {
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            long difference = end.getTime() - start.getTime();
            return (int) (difference / (1000 * 60 * 60 * 24)) + 1;
        } catch (ParseException e) {
            return 0;
        }
    }

    public static class Statistics {
        public double total;
        public double average;
        public String maxDate;
        public double maxAmount;
        public String minDate;
        public double minAmount;
        public int expenseCount;

        public Statistics(double total, double average, String maxDate, double maxAmount,
                          String minDate, double minAmount, int expenseCount) {
            this.total = total;
            this.average = average;
            this.maxDate = maxDate;
            this.maxAmount = maxAmount;
            this.minDate = minDate;
            this.minAmount = minAmount;
            this.expenseCount = expenseCount;
        }
    }
}