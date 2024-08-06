package edu.northeastern.numad24su_group6;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.table.TableData;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.numad24su_group6.model.Meal;
import edu.northeastern.numad24su_group6.model.User;
import edu.northeastern.numad24su_group6.utils.ScreenshotUtils;

public class CalculateCaloriesActivity extends AppCompatActivity {

    private static final String KEY_SELECTED_DATE = "selectedDate";

    private TextView tvSelectedDate;
    private PieChart carbsPieChart, fatPieChart, proteinPieChart, sodiumPieChart, potassiumPieChart, calciumPieChart, ironPieChart, vitaminAPieChart, vitaminBPieChart, vitaminCPieChart, vitaminDPieChart, vitaminEPieChart;
    private TextView carbsValueText, fatValueText, proteinValueText, caloriesValueText, sodiumValueText, potassiumValueText, calciumValueText, ironValueText, vitaminAValueText, vitaminBValueText, vitaminCValueText, vitaminDValueText, vitaminEValueText;
    private ProgressBar calorieProgressBar;
    private Button addFoodButton, backButton;
    private DatabaseReference userRef;
    private String userId;
    private User currentUser;
    private String selectedDate;

    private float carbsGoal, fatGoal, proteinGoal, caloriesGoal, sodiumGoal, potassiumGoal, calciumGoal, ironGoal, vitaminAGoal, vitaminBGoal, vitaminCGoal, vitaminDGoal, vitaminEGoal;

    private SmartTable<Meal> smartTable;
    private List<Meal> mealList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_calories);

        // Initialize SmartTable
        smartTable = findViewById(R.id.smartTable);

        findViewById(R.id.sharedImage).setOnClickListener(view -> {
            ScrollView scrollView = findViewById(R.id.scrollView);
            Bitmap screenshot = ScreenshotUtils.getInstance().takeScreenshotOfScrollView(scrollView);
            Uri screenshotUri = ScreenshotUtils.getInstance().saveScreenshot(CalculateCaloriesActivity.this, screenshot);
            ScreenshotUtils.getInstance().shareScreenshot(CalculateCaloriesActivity.this, screenshotUri);
        });

        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User information not found. Please log in again.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(CalculateCaloriesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Initialize TextViews and other views
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        carbsPieChart = findViewById(R.id.carbsPieChart);
        fatPieChart = findViewById(R.id.fatPieChart);
        proteinPieChart = findViewById(R.id.proteinPieChart);
        sodiumPieChart = findViewById(R.id.sodiumPieChart);
        potassiumPieChart = findViewById(R.id.potassiumPieChart);
        calciumPieChart = findViewById(R.id.calciumPieChart);
        ironPieChart = findViewById(R.id.ironPieChart);
        vitaminAPieChart = findViewById(R.id.vitaminAPieChart);
        vitaminBPieChart = findViewById(R.id.vitaminBPieChart);
        vitaminCPieChart = findViewById(R.id.vitaminCPieChart);
        vitaminDPieChart = findViewById(R.id.vitaminDPieChart);
        vitaminEPieChart = findViewById(R.id.vitaminEPieChart);

        carbsValueText = findViewById(R.id.carbsValue);
        fatValueText = findViewById(R.id.fatValue);
        proteinValueText = findViewById(R.id.proteinValue);
        caloriesValueText = findViewById(R.id.caloriesValue);
        sodiumValueText = findViewById(R.id.sodiumValue);
        potassiumValueText = findViewById(R.id.potassiumValue);
        calciumValueText = findViewById(R.id.calciumValue);
        ironValueText = findViewById(R.id.ironValue);
        vitaminAValueText = findViewById(R.id.vitaminAValue);
        vitaminBValueText = findViewById(R.id.vitaminBValue);
        vitaminCValueText = findViewById(R.id.vitaminCValue);
        vitaminDValueText = findViewById(R.id.vitaminDValue);
        vitaminEValueText = findViewById(R.id.vitaminEValue);

        calorieProgressBar = findViewById(R.id.calorieProgressBar);
        addFoodButton = findViewById(R.id.btnAddFood);
        backButton = findViewById(R.id.btnBack);

        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());
        addFoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalculateCaloriesActivity.this, SearchFoodActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(CalculateCaloriesActivity.this, MainActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        if (savedInstanceState != null) {
            selectedDate = savedInstanceState.getString(KEY_SELECTED_DATE);
        } else {
            selectedDate = getCurrentDate();
        }

        tvSelectedDate.setText(selectedDate);
        retrieveUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveUserDataForDate(selectedDate);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            tvSelectedDate.setText(selectedDate);
            retrieveUserDataForDate(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void retrieveUserData() {
        userRef.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && userTask.getResult().exists()) {
                currentUser = userTask.getResult().getValue(User.class);

                carbsGoal = (float) currentUser.getCarbsGoal();
                fatGoal = (float) currentUser.getFatGoal();
                proteinGoal = (float) currentUser.getProteinGoal();
                caloriesGoal = (float) currentUser.getCaloriesGoal();
                sodiumGoal = (float) currentUser.getSodiumGoal();
                potassiumGoal = (float) currentUser.getPotassiumGoal();
                calciumGoal = (float) currentUser.getCalciumGoal();
                ironGoal = (float) currentUser.getIronGoal();
                vitaminAGoal = (float) currentUser.getVitaminAGoal();
                vitaminBGoal = (float) currentUser.getVitaminBGoal();
                vitaminCGoal = (float) currentUser.getVitaminCGoal();
                vitaminDGoal = (float) currentUser.getVitaminDGoal();
                vitaminEGoal = (float) currentUser.getVitaminEGoal();

                retrieveUserDataForDate(selectedDate);
            } else {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void retrieveUserDataForDate(String date) {
        DatabaseReference dateRef = userRef.child("dates").child(date);
        dateRef.get().addOnCompleteListener(dateTask -> {
            if (dateTask.isSuccessful()) {
                DataSnapshot dataSnapshot = dateTask.getResult();
                updateNutrientData(dataSnapshot);
                retrieveMealPlannerData(dateRef.child("mealPlanner"));
            } else {
                resetNutrientData();
            }
        });
    }

    private void retrieveMealPlannerData(DatabaseReference mealPlannerRef) {
        mealPlannerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealList.clear();
                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    mealList.add(meal);
                }
                Log.d("MealData", "Meals loaded: " + mealList.size());
                setupSmartTable(mealList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CalculateCaloriesActivity.this, "Failed to load meal planner data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSmartTable(List<Meal> meals) {
        if (meals.isEmpty()) {
            // Clear the table if no meals are available
            smartTable.setTableData(new TableData<>("Meal Planner", new ArrayList<>(), new Column[0]));
            return;
        }

        // Define columns using the nutrition names from the Meal class
        Column<String> foodNameColumn = new Column<>("Food Name", "foodName");
        Column<Float> carbsColumn = new Column<>("Carbs", "carbs");
        Column<Float> fatsColumn = new Column<>("Fats", "fats");
        Column<Float> proteinsColumn = new Column<>("Proteins", "proteins");
        Column<Float> caloriesColumn = new Column<>("Calories", "calories");
        Column<Integer> amountColumn = new Column<>("Amount", "amount");
        Column<Float> sodiumColumn = new Column<>("Sodium", "sodium");
        Column<Float> potassiumColumn = new Column<>("Potassium", "potassium");
        Column<Float> calciumColumn = new Column<>("Calcium", "calcium");
        Column<Float> ironColumn = new Column<>("Iron", "iron");
        Column<Float> vitaminAColumn = new Column<>("Vitamin A", "vitaminA");
        Column<Float> vitaminBColumn = new Column<>("Vitamin B", "vitaminB");
        Column<Float> vitaminCColumn = new Column<>("Vitamin C", "vitaminC");
        Column<Float> vitaminDColumn = new Column<>("Vitamin D", "vitaminD");
        Column<Float> vitaminEColumn = new Column<>("Vitamin E", "vitaminE");

        TableData<Meal> tableData = new TableData<>("Meal Planner", meals, foodNameColumn, carbsColumn, fatsColumn, proteinsColumn, caloriesColumn, amountColumn, sodiumColumn, potassiumColumn, calciumColumn, ironColumn, vitaminAColumn, vitaminBColumn, vitaminCColumn, vitaminDColumn, vitaminEColumn);
        smartTable.setTableData(tableData);
    }

    private void setupPieChart(PieChart pieChart, TextView textView, float currentValue, float goalValue, String unit) {
        List<PieEntry> entries = new ArrayList<>();
        float percentage;
        if (currentValue >= goalValue) {
            percentage = 100; // Fill the pie chart if the goal is exceeded
            entries.add(new PieEntry(percentage));
            entries.add(new PieEntry(0)); // No remaining part
        } else {
            percentage = (currentValue / goalValue) * 100;
            entries.add(new PieEntry(percentage));
            entries.add(new PieEntry(100 - percentage));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");

        List<Integer> colors = new ArrayList<>();
        if (currentValue >= goalValue) {
            colors.add(ColorTemplate.COLORFUL_COLORS[2]); // Color for exceeding goal
            colors.add(android.graphics.Color.LTGRAY); // Color for remaining part, which is not displayed
        } else {
            colors.add(ColorTemplate.COLORFUL_COLORS[3]); // Consumed part color
            colors.add(android.graphics.Color.LTGRAY); // Remaining part color
        }
        dataSet.setColors(colors);
        dataSet.setDrawValues(false); // Disable slice labels

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.invalidate(); // refresh

        textView.setText(String.format("%.1f %s / %.1f %s", currentValue, unit, goalValue, unit));
    }

    private void setupCalorieProgressBar(ProgressBar progressBar, TextView textView, float currentValue, float goalValue, String unit) {
        int progress = (int) ((currentValue / goalValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%.1f %s / %.1f %s", currentValue, unit, goalValue, unit));
    }

    private void updateNutrientData(DataSnapshot dataSnapshot) {
        float currentCarbs = getFloatValue(dataSnapshot, "currentCarbs");
        float currentFat = getFloatValue(dataSnapshot, "currentFat");
        float currentProtein = getFloatValue(dataSnapshot, "currentProtein");
        float currentCalories = getFloatValue(dataSnapshot, "currentCalories");
        float currentSodium = getFloatValue(dataSnapshot, "currentSodium");
        float currentPotassium = getFloatValue(dataSnapshot, "currentPotassium");
        float currentCalcium = getFloatValue(dataSnapshot, "currentCalcium");
        float currentIron = getFloatValue(dataSnapshot, "currentIron");
        float currentVitaminA = getFloatValue(dataSnapshot, "currentVitaminA");
        float currentVitaminB = getFloatValue(dataSnapshot, "currentVitaminB");
        float currentVitaminC = getFloatValue(dataSnapshot, "currentVitaminC");
        float currentVitaminD = getFloatValue(dataSnapshot, "currentVitaminD");
        float currentVitaminE = getFloatValue(dataSnapshot, "currentVitaminE");

        setupPieChart(carbsPieChart, carbsValueText, currentCarbs, carbsGoal, "g");
        setupPieChart(fatPieChart, fatValueText, currentFat, fatGoal, "g");
        setupPieChart(proteinPieChart, proteinValueText, currentProtein, proteinGoal, "g");
        setupPieChart(sodiumPieChart, sodiumValueText, currentSodium, sodiumGoal, "mg");
        setupPieChart(potassiumPieChart, potassiumValueText, currentPotassium, potassiumGoal, "mg");
        setupPieChart(calciumPieChart, calciumValueText, currentCalcium, calciumGoal, "mg");
        setupPieChart(ironPieChart, ironValueText, currentIron, ironGoal, "mg");
        setupPieChart(vitaminAPieChart, vitaminAValueText, currentVitaminA, vitaminAGoal, "IU");
        setupPieChart(vitaminBPieChart, vitaminBValueText, currentVitaminB, vitaminBGoal, "mg");
        setupPieChart(vitaminCPieChart, vitaminCValueText, currentVitaminC, vitaminCGoal, "mg");
        setupPieChart(vitaminDPieChart, vitaminDValueText, currentVitaminD, vitaminDGoal, "IU");
        setupPieChart(vitaminEPieChart, vitaminEValueText, currentVitaminE, vitaminEGoal, "mg");
        setupCalorieProgressBar(calorieProgressBar, caloriesValueText, currentCalories, caloriesGoal, "kcal");
    }

    private void resetNutrientData() {
        setupPieChart(carbsPieChart, carbsValueText, 0, carbsGoal, "g");
        setupPieChart(fatPieChart, fatValueText, 0, fatGoal, "g");
        setupPieChart(proteinPieChart, proteinValueText, 0, proteinGoal, "g");
        setupPieChart(sodiumPieChart, sodiumValueText, 0, sodiumGoal, "mg");
        setupPieChart(potassiumPieChart, potassiumValueText, 0, potassiumGoal, "mg");
        setupPieChart(calciumPieChart, calciumValueText, 0, calciumGoal, "mg");
        setupPieChart(ironPieChart, ironValueText, 0, ironGoal, "mg");
        setupPieChart(vitaminAPieChart, vitaminAValueText, 0, vitaminAGoal, "IU");
        setupPieChart(vitaminBPieChart, vitaminBValueText, 0, vitaminBGoal, "mg");
        setupPieChart(vitaminCPieChart, vitaminCValueText, 0, vitaminCGoal, "mg");
        setupPieChart(vitaminDPieChart, vitaminDValueText, 0, vitaminDGoal, "IU");
        setupPieChart(vitaminEPieChart, vitaminEValueText, 0, vitaminEGoal, "mg");
        setupCalorieProgressBar(calorieProgressBar, caloriesValueText, 0, caloriesGoal, "kcal");
    }

    private float getFloatValue(DataSnapshot dataSnapshot, String key) {
        return dataSnapshot.child(key).getValue(Float.class) != null ? dataSnapshot.child(key).getValue(Float.class) : 0;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SELECTED_DATE, selectedDate);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            selectedDate = savedInstanceState.getString(KEY_SELECTED_DATE);
            tvSelectedDate.setText(selectedDate);
            retrieveUserDataForDate(selectedDate);
        }
    }
}
