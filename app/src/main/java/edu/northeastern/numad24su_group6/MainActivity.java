package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnIntroduceApp;
    private Button btnFoodNutritionInfo;
    private Button btnCalculateCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnIntroduceApp = findViewById(R.id.btnIntroduceApp);
        btnFoodNutritionInfo = findViewById(R.id.btnFoodNutritionInfo);
        btnCalculateCalories = findViewById(R.id.btnCalculateCalories);

        btnIntroduceApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, IntroduceAppActivity.class);
                startActivity(intent);
            }
        });

        btnFoodNutritionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FoodNutritionInfoActivity.class);
                startActivity(intent);
            }
        });

        btnCalculateCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalculateCaloriesActivity.class);
                startActivity(intent);
            }
        });
    }
}
