package edu.northeastern.numad24su_group6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.numad24su_group6.model.User;
import edu.northeastern.numad24su_group6.utils.Constants;

public class HealthInfoActivity extends AppCompatActivity {
    private EditText ageEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private Spinner activityLevelSpinner;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private Spinner goalSpinner;
    private TextView resultTextView;
    private TextView carbsTextView;
    private TextView proteinsTextView;
    private TextView fatsTextView;
    private TextView waterTextView;
    private Button calculateButton;
    private int activityLevel;
    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_info);

        userId = getIntent().getStringExtra("userId");

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "User information not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        ageEditText = findViewById(R.id.ageEditText);
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        goalSpinner = findViewById(R.id.goalSpinner);
        resultTextView = findViewById(R.id.resultTextView);
        carbsTextView = findViewById(R.id.carbsTextView);
        proteinsTextView = findViewById(R.id.proteinsTextView);
        fatsTextView = findViewById(R.id.fatsTextView);
        waterTextView = findViewById(R.id.waterTextView);
        calculateButton = findViewById(R.id.calculateButton);

        setupActivityLevelSpinner(); // Set up the activity level spinner
        setupGoalSpinner(); // Set up the goal spinner

        calculateButton.setOnClickListener(v -> calculateCalories());

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }

        fetchUserData();
    }

    private void setupActivityLevelSpinner() {
        List<String> activityDescriptions = new ArrayList<>(Constants.ACTIVITY_LEVEL_DESCRIPTIONS.values());
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, activityDescriptions);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(activityAdapter);
        activityLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activityLevel = position + 1; // Assuming activity levels are 1-indexed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupGoalSpinner() {
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Constants.GOAL_DESCRIPTIONS);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);
    }

    private void fetchUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        populateUserInfo(user);
                    } else {
                        Toast.makeText(HealthInfoActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HealthInfoActivity.this, "User information not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HealthInfoActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void populateUserInfo(User user) {
        ageEditText.setText(String.valueOf(user.getAge()));
        heightEditText.setText(String.valueOf(user.getHeight()));
        weightEditText.setText(String.valueOf(user.getWeight()));
        if ("Male".equalsIgnoreCase(user.getSex())) {
            maleRadioButton.setChecked(true);
        } else if ("Female".equalsIgnoreCase(user.getSex())) {
            femaleRadioButton.setChecked(true);
        }
        activityLevelSpinner.setSelection(user.getActivityLevel() - 1);
        // Any additional user info population
    }

    private void calculateCalories() {
        try {
            int age = Integer.parseInt(ageEditText.getText().toString());
            double height = Double.parseDouble(heightEditText.getText().toString());
            double weight = Double.parseDouble(weightEditText.getText().toString());
            String sex = maleRadioButton.isChecked() ? "Male" : "Female";
            String goal = goalSpinner.getSelectedItem().toString();

            User user = new User(age, sex, height, weight, activityLevel);
            user.calculateBasalMetabolicRate();
            user.calculateDailyCalorieNeeds();
            double caloriesGoal = user.calculateCaloriesGoal(goal);

            resultTextView.setText("Your estimated daily calorie needs are " + Math.round(caloriesGoal) + " calories!");

            // Calculate and display macronutrient values
            user.calculateMacronutrients();

            carbsTextView.setText("Carbs: " + Math.round(user.getCarbs()) + " g");
            proteinsTextView.setText("Proteins: " + Math.round(user.getProteins()) + " g");
            fatsTextView.setText("Fats: " + Math.round(user.getFats()) + " g");
            waterTextView.setText("Water: " + Math.round(user.getWater()) + " ml");

            // Save user data to Firebase
            saveUserToFirebase(user);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserToFirebase(User user) {
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("age", user.getAge());
        userUpdates.put("height", user.getHeight());
        userUpdates.put("weight", user.getWeight());
        userUpdates.put("activityLevel", user.getActivityLevel());
        userUpdates.put("caloriesGoal", user.getCalories());
        userUpdates.put("carbsGoal", user.getCarbs());
        userUpdates.put("proteinGoal", user.getProteins());
        userUpdates.put("fatGoal", user.getFats());
        userUpdates.put("waterGoal", user.getWater());

        userRef.updateChildren(userUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("age", ageEditText.getText().toString());
        outState.putString("height", heightEditText.getText().toString());
        outState.putString("weight", weightEditText.getText().toString());
        outState.putInt("activityLevel", activityLevelSpinner.getSelectedItemPosition());
        outState.putBoolean("male", maleRadioButton.isChecked());
        outState.putBoolean("female", femaleRadioButton.isChecked());
        outState.putInt("goal", goalSpinner.getSelectedItemPosition());
        outState.putString("result", resultTextView.getText().toString());
        outState.putString("carbs", carbsTextView.getText().toString());
        outState.putString("proteins", proteinsTextView.getText().toString());
        outState.putString("fats", fatsTextView.getText().toString());
        outState.putString("water", waterTextView.getText().toString());
    }

    private void restoreInstanceState(Bundle savedInstanceState) {
        ageEditText.setText(savedInstanceState.getString("age"));
        heightEditText.setText(savedInstanceState.getString("height"));
        weightEditText.setText(savedInstanceState.getString("weight"));
        activityLevelSpinner.setSelection(savedInstanceState.getInt("activityLevel"));
        maleRadioButton.setChecked(savedInstanceState.getBoolean("male"));
        femaleRadioButton.setChecked(savedInstanceState.getBoolean("female"));
        goalSpinner.setSelection(savedInstanceState.getInt("goal"));
        resultTextView.setText(savedInstanceState.getString("result"));
        carbsTextView.setText(savedInstanceState.getString("carbs"));
        proteinsTextView.setText(savedInstanceState.getString("proteins"));
        fatsTextView.setText(savedInstanceState.getString("fats"));
        waterTextView.setText(savedInstanceState.getString("water"));
    }
}