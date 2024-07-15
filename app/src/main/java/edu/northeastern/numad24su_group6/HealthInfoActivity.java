package edu.northeastern.numad24su_group6;

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
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
