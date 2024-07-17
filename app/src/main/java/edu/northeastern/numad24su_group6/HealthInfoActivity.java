package edu.northeastern.numad24su_group6;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import edu.northeastern.numad24su_group6.model.User;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_info);

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

        // Set up the activity level spinner
        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_levels_array, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(activityAdapter);
        activityLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                activityLevel = position + 1; // activity levels are 1-indexed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateCalories();
            }
        });

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
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

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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