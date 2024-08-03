package edu.northeastern.numad24su_group6;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private TextView tvSelectedDate;
    private PieChart carbsPieChart;
    private PieChart fatPieChart;
    private PieChart proteinPieChart;
    private TextView carbsValueText;
    private TextView fatValueText;
    private TextView proteinValueText;
    private TextView caloriesValueText;
    private ProgressBar calorieProgressBar;
    private Button addFoodButton;
    private Button backButton;
    private DatabaseReference userRef;
    private String userId;
    private User currentUser;

    private float carbsGoal;
    private float fatGoal;
    private float proteinGoal;
    private float caloriesGoal;
    private RecyclerView mealPlannerRecyclerView;
    private MealPlannerAdapter mealPlannerAdapter;
    private List<Meal> mealList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_calories);


        findViewById(R.id.sharedImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap screenshot = ScreenshotUtils.getInstance().takeScreenshot(CalculateCaloriesActivity.this);
                Uri screenshotUri = ScreenshotUtils.getInstance().saveScreenshot(CalculateCaloriesActivity.this, screenshot);
                ScreenshotUtils.getInstance().shareScreenshot(CalculateCaloriesActivity.this, screenshotUri);
            }
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

        // Initialize TextViews
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        tvSelectedDate.setOnClickListener(v -> showDatePickerDialog());

        // Initialize PieCharts
        carbsPieChart = findViewById(R.id.carbsPieChart);
        fatPieChart = findViewById(R.id.fatPieChart);
        proteinPieChart = findViewById(R.id.proteinPieChart);

        // Initialize TextViews
        carbsValueText = findViewById(R.id.carbsValue);
        fatValueText = findViewById(R.id.fatValue);
        proteinValueText = findViewById(R.id.proteinValue);
        caloriesValueText = findViewById(R.id.caloriesValue);

        // Initialize ProgressBar
        calorieProgressBar = findViewById(R.id.calorieProgressBar);

        // Initialize Buttons
        addFoodButton = findViewById(R.id.btnAddFood);
        backButton = findViewById(R.id.btnBack);

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

        // Retrieve user data from Firebase and set goals and current consumption
        retrieveUserData();

        mealPlannerRecyclerView = findViewById(R.id.mealPlannerRecyclerView);
        mealPlannerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealPlannerAdapter = new MealPlannerAdapter(mealList);
        mealPlannerRecyclerView.setAdapter(mealPlannerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Retrieve and update meal planner data whenever the activity resumes
        String currentDate = tvSelectedDate.getText().toString();
        retrieveUserDataForDate(currentDate);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                    tvSelectedDate.setText(selectedDate);
                    retrieveUserDataForDate(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void retrieveUserData() {
        userRef.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful() && userTask.getResult().exists()) {
                currentUser = userTask.getResult().getValue(User.class);

                // Store user goals
                carbsGoal = (float) currentUser.getCarbsGoal();
                fatGoal = (float) currentUser.getFatGoal();
                proteinGoal = (float) currentUser.getProteinGoal();
                caloriesGoal = (float) currentUser.getCaloriesGoal();

                String currentDate = getCurrentDate();
                tvSelectedDate.setText(currentDate);
                retrieveUserDataForDate(currentDate);
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

                float currentCarbs = dataSnapshot.child("currentCarbs").getValue(Float.class) != null ? dataSnapshot.child("currentCarbs").getValue(Float.class) : 0;
                float currentFat = dataSnapshot.child("currentFat").getValue(Float.class) != null ? dataSnapshot.child("currentFat").getValue(Float.class) : 0;
                float currentProtein = dataSnapshot.child("currentProtein").getValue(Float.class) != null ? dataSnapshot.child("currentProtein").getValue(Float.class) : 0;
                float currentCalories = dataSnapshot.child("currentCalories").getValue(Float.class) != null ? dataSnapshot.child("currentCalories").getValue(Float.class) : 0;

                setupPieChart(carbsPieChart, carbsValueText, currentCarbs, carbsGoal);
                setupPieChart(fatPieChart, fatValueText, currentFat, fatGoal);
                setupPieChart(proteinPieChart, proteinValueText, currentProtein, proteinGoal);
                setupCalorieProgressBar(calorieProgressBar, caloriesValueText, currentCalories, caloriesGoal);

                // Retrieve meal planner data
                retrieveMealPlannerData(dateRef.child("mealPlanner"));
            } else {
                setupPieChart(carbsPieChart, carbsValueText, 0, carbsGoal);
                setupPieChart(fatPieChart, fatValueText, 0, fatGoal);
                setupPieChart(proteinPieChart, proteinValueText, 0, proteinGoal);
                setupCalorieProgressBar(calorieProgressBar, caloriesValueText, 0, caloriesGoal);
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
                mealPlannerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CalculateCaloriesActivity.this, "Failed to load meal planner data", Toast.LENGTH_LONG).show();
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
        textView.setText(String.format("%.1f / %.1f", currentValue, goalValue));
    }

    private void setupCalorieProgressBar(ProgressBar progressBar, TextView textView, float currentValue, float goalValue) {
        int progress = (int) ((currentValue / goalValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%.1f / %.1f", currentValue, goalValue));
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}