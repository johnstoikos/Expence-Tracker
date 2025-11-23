package com.example.expensetracker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvStatsTitle, tvTotalExpenses, tvAverage, tvMaxDay, tvMinDay;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvStatsTitle = findViewById(R.id.tvStatsTitle);
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses);
        tvAverage = findViewById(R.id.tvAverage);
        tvMaxDay = findViewById(R.id.tvMaxDay);
        tvMinDay = findViewById(R.id.tvMinDay);
        btnBack = findViewById(R.id.btnBack);

        String title = getIntent().getStringExtra("STATS_TITLE");
        double total = getIntent().getDoubleExtra("STATS_TOTAL", 0);
        double average = getIntent().getDoubleExtra("STATS_AVERAGE", 0);
        String maxDate = getIntent().getStringExtra("STATS_MAX_DATE");
        double maxAmount = getIntent().getDoubleExtra("STATS_MAX_AMOUNT", 0);
        String minDate = getIntent().getStringExtra("STATS_MIN_DATE");
        double minAmount = getIntent().getDoubleExtra("STATS_MIN_AMOUNT", 0);

        tvStatsTitle.setText(title);
        tvTotalExpenses.setText(String.format("%.2f€", total));
        tvAverage.setText(String.format("%.2f€", average));
        tvMaxDay.setText(maxDate + " (" + String.format("%.2f€", maxAmount) + ")");
        tvMinDay.setText(minDate + " (" + String.format("%.2f€", minAmount) + ")");

        btnBack.setOnClickListener(v -> finish());
    }
}
