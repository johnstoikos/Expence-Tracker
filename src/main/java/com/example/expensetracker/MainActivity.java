package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnAddExpense, btnWeeklyStats, btnMonthlyStats;
    private ListView lvExpenses;
    private TextView tvTotal;
    private List<Expense> expensesList;
    private ArrayAdapter<Expense> adapter;
    private ExpenseManager expenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expenseManager = new ExpenseManager(this);

        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnWeeklyStats = findViewById(R.id.btnWeeklyStats);
        btnMonthlyStats = findViewById(R.id.btnMonthlyStats);
        lvExpenses = findViewById(R.id.lvExpenses);
        tvTotal = findViewById(R.id.tvTotal);

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        btnWeeklyStats.setOnClickListener(v -> showStats("weekly"));

        btnMonthlyStats.setOnClickListener(v -> showStats("monthly"));

        expensesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expensesList);
        lvExpenses.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateExpensesList();
    }

    private void updateExpensesList() {
        List<Expense> expenses = expenseManager.getAllExpenses();
        double total = expenseManager.getTotalExpenses();

        expensesList.clear();
        expensesList.addAll(expenses);
        adapter.notifyDataSetChanged();

        tvTotal.setText(String.format("%.2f€", total));
    }

    private void showStats(String statsType) {
        ExpenseManager.Statistics stats;
        String title;

        if (statsType.equals("weekly")) {
            stats = expenseManager.getWeeklyStats();
            title = "Weekly Statistics";
        } else {
            stats = expenseManager.getMonthlyStats();
            title = "Monthly Statistics";
        }

        Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
        intent.putExtra("STATS_TITLE", title);
        intent.putExtra("STATS_TOTAL", stats.total);
        intent.putExtra("STATS_AVERAGE", stats.average);
        intent.putExtra("STATS_MAX_DATE", stats.maxDate);
        intent.putExtra("STATS_MAX_AMOUNT", stats.maxAmount);
        intent.putExtra("STATS_MIN_DATE", stats.minDate);
        intent.putExtra("STATS_MIN_AMOUNT", stats.minAmount);
        startActivity(intent);
    }
}