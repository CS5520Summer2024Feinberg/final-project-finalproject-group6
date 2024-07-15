package edu.northeastern.numad24su_group6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import edu.northeastern.numad24su_group6.model.User;

public class HealthInfoActivity extends AppCompatActivity {
    private EditText ageEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private EditText activityLevelEditText;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private TextView resultTextView;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_info);

        ageEditText = findViewById(R.id.ageEditText);
        heightEditText = findViewById(R.id.heightEditText);
        weightEditText = findViewById(R.id.weightEditText);
        activityLevelEditText = findViewById(R.id.activityLevelEditText);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        resultTextView = findViewById(R.id.resultTextView);
        calculateButton = findViewById(R.id.calculateButton);

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
            int activityLevel = Integer.parseInt(activityLevelEditText.getText().toString());
            String sex = maleRadioButton.isChecked() ? "Male" : "Female";

            User user = new User(age, sex, height, weight, activityLevel);
            user.calculateBasalMetabolicRate();
            user.calculateDailyCalorieNeeds();
            resultTextView.setText(user.toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}