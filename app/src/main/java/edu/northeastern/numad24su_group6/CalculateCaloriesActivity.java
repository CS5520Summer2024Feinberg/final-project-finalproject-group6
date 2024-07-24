package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalculateCaloriesActivity extends AppCompatActivity {

    private PieChart carbsPieChart;
    private PieChart fatPieChart;
    private PieChart proteinPieChart;
    private PieChart waterPieChart;
    private TextView carbsValueText;
    private TextView fatValueText;
    private TextView proteinValueText;
    private TextView waterValueText;
    private TextView caloriesValueText;
    private ProgressBar calorieProgressBar;
    private Button addFoodButton;
    private Button backButton;
    private RecyclerView dateRecyclerView;
    private Spinner monthSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_calories);

        // Initialize PieCharts
        carbsPieChart = findViewById(R.id.carbsPieChart);
        fatPieChart = findViewById(R.id.fatPieChart);
        proteinPieChart = findViewById(R.id.proteinPieChart);
        waterPieChart = findViewById(R.id.waterPieChart);

        // Initialize TextViews
        carbsValueText = findViewById(R.id.carbsValue);
        fatValueText = findViewById(R.id.fatValue);
        proteinValueText = findViewById(R.id.proteinValue);
        waterValueText = findViewById(R.id.waterValue);
        caloriesValueText = findViewById(R.id.caloriesValue);

        // Initialize ProgressBar
        calorieProgressBar = findViewById(R.id.calorieProgressBar);

        // Initialize Buttons
        addFoodButton = findViewById(R.id.btnAddFood);
        backButton = findViewById(R.id.btnBack);

        // Initialize RecyclerView
        dateRecyclerView = findViewById(R.id.dateRecyclerView);
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize Spinner
        monthSpinner = findViewById(R.id.monthSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getMonths());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);

        // Set OnItemSelectedListener for the Spinner
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDatesForMonth(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set goals and current consumption
        float carbsGoal = 200f;
        float currentCarbs = 150f;

        float fatGoal = 70f;
        float currentFat = 50f;

        float proteinGoal = 150f;
        float currentProtein = 100f;

        float waterGoal = 3000f; // in ml
        float currentWater = 2000f; // in ml

        float caloriesGoal = 2000f;
        float currentCalories = 1500f;

        // Set up PieCharts with goals and current consumption
        setupPieChart(carbsPieChart, carbsValueText, currentCarbs, carbsGoal);
        setupPieChart(fatPieChart, fatValueText, currentFat, fatGoal);
        setupPieChart(proteinPieChart, proteinValueText, currentProtein, proteinGoal);
        setupPieChart(waterPieChart, waterValueText, currentWater, waterGoal);

        // Update ProgressBar and TextView for calories
        setupCalorieProgressBar(calorieProgressBar, caloriesValueText, currentCalories, caloriesGoal);

        // Set OnClickListener for the Add Food Button
        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculateCaloriesActivity.this, FoodNutritionInfoActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculateCaloriesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupPieChart(PieChart pieChart, TextView textView, float currentValue, float goalValue) {
        List<PieEntry> entries = new ArrayList<>();
        float percentage = (currentValue / goalValue) * 100;
        entries.add(new PieEntry(percentage));
        entries.add(new PieEntry(100 - percentage));

        PieDataSet dataSet = new PieDataSet(entries, "");
        List<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.COLORFUL_COLORS[3]); // Consumed part color
        colors.add(android.graphics.Color.WHITE); // Remaining part color
        dataSet.setColors(colors);
        dataSet.setDrawValues(false); // Disable slice labels

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Remove description label and legend
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.invalidate(); // refresh

        // Set the TextView to show "consumed / total"
        textView.setText(String.format("%s / %s", currentValue, goalValue));
    }

    private void setupCalorieProgressBar(ProgressBar progressBar, TextView textView, float currentValue, float goalValue) {
        int progress = (int) ((currentValue / goalValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%s / %s", currentValue, goalValue));
    }

    private List<String> getMonths() {
        List<String> months = new ArrayList<>();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH, i);
            months.add(monthFormat.format(calendar.getTime()));
        }
        return months;
    }

    private void updateDatesForMonth(int month) {
        List<String> dates = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("E\nd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < daysInMonth; i++) {
            dates.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        DateAdapter dateAdapter = new DateAdapter(dates, date -> Toast.makeText(this, "Selected date: " + date, Toast.LENGTH_SHORT).show());
        dateRecyclerView.setAdapter(dateAdapter);
    }
}
