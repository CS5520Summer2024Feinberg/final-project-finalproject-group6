package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchFoodActivity extends AppCompatActivity {

    private EditText etFoodName;
    private Button btnSearch;
    private Button btnBack;
    private Spinner spinnerFoodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        etFoodName = findViewById(R.id.etFoodName);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        spinnerFoodList = findViewById(R.id.spinnerFoodList);

        // Initialize Spinner with food items
        String[] foodItems = {"Select a food", "egg", "coffee", "chicken", "beef", "pizza", "bread", "milk"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, foodItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodList.setAdapter(adapter);

        spinnerFoodList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    etFoodName.setText(parent.getItemAtPosition(position).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        btnSearch.setOnClickListener(v -> {
            String foodName = etFoodName.getText().toString();
            if (!foodName.isEmpty()) {
                Intent intent = new Intent(SearchFoodActivity.this, FoodNutritionInfoActivity.class);
                intent.putExtra("foodName", foodName);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please enter a food name or select one from the list.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}