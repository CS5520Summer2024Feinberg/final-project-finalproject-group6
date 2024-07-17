package edu.northeastern.numad24su_group6;

import android.os.Bundle;
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

    private PieChart caloriesPieChart;
    private PieChart fatPieChart;
    private PieChart proteinPieChart;
    private PieChart waterPieChart;
    private TextView caloriesValueText;
    private TextView fatValueText;
    private TextView proteinValueText;
    private TextView waterValueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_calories);

        // Initialize PieCharts
        caloriesPieChart = findViewById(R.id.caloriesPieChart);
        fatPieChart = findViewById(R.id.fatPieChart);
        proteinPieChart = findViewById(R.id.proteinPieChart);
        waterPieChart = findViewById(R.id.waterPieChart);

        // Initialize TextViews
        caloriesValueText = findViewById(R.id.caloriesValue);
        fatValueText = findViewById(R.id.fatValue);
        proteinValueText = findViewById(R.id.proteinValue);
        waterValueText = findViewById(R.id.waterValue);

        // Set goals and current consumption
        float caloriesGoal = 2000f;
        float currentCalories = 1500f;

        float fatGoal = 70f;
        float currentFat = 50f;

        float proteinGoal = 150f;
        float currentProtein = 100f;

        float waterGoal = 3000f; // in ml
        float currentWater = 2000f; // in ml

        // Set up PieCharts with goals and current consumption
        setupPieChart(caloriesPieChart, caloriesValueText, currentCalories, caloriesGoal);
        setupPieChart(fatPieChart, fatValueText, currentFat, fatGoal);
        setupPieChart(proteinPieChart, proteinValueText, currentProtein, proteinGoal);
        setupPieChart(waterPieChart, waterValueText, currentWater, waterGoal);
    }

    private void setupPieChart(PieChart pieChart, TextView textView, float currentValue, float goalValue) {
        List<PieEntry> entries = new ArrayList<>();
        float percentage = (currentValue / goalValue) * 100;
        entries.add(new PieEntry(percentage));
        entries.add(new PieEntry(100 - percentage));

        PieDataSet dataSet = new PieDataSet(entries, "");
        List<Integer> colors = new ArrayList<>();
        colors.add(ColorTemplate.COLORFUL_COLORS[0]); // Consumed part color
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
}