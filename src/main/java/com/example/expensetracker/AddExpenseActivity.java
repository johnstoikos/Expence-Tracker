package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etDescription, etAmount, etDate;
    private Spinner spinnerCategory;
    private Button btnSave;
    private ExpenseManager expenseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        expenseManager = new ExpenseManager(this);

        etDescription = findViewById(R.id.etDescription);
        etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etDate = findViewById(R.id.etDate);
        btnSave = findViewById(R.id.btnSave);

        // Setup Spinner
        String[] categories = {"Φαγητό", "Μεταφορές", "Έξοδα Σπιτιου", "Ψυχαγωγία", "Άλλα"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Αυτόματη σημερινή ημερομηνία
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
        etDate.setText(today);

        // Setup Date Picker
        etDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Setup Save Button
        btnSave.setOnClickListener(v -> {
            saveExpense();
        });
    }

    private void saveExpense() {
        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String date = etDate.getText().toString().trim();

        if (description.isEmpty() || amountStr.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ Συμπληρώστε Όλα τα Πεδία", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            // Δημιουργία νέου expense
            Expense expense = new Expense(description, amount, category, date);

            // Αποθήκευση με SharedPreferences
            boolean result = expenseManager.addExpense(expense);

            if (result) {
                Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Go back to MainActivity
            } else {
                Toast.makeText(this, "Error saving expense", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
        }
    }
}