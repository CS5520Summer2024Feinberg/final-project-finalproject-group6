package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

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

        // Initialize Button
        addFoodButton = findViewById(R.id.btnAddFood);

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
}