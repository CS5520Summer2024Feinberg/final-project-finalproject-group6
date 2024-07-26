package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFoodActivity extends AppCompatActivity {

    private EditText etFoodName;
    private Button btnSearch, btnBack;
    private TextView tvPrompt;
    private Spinner spinnerFoodList;
    private ImageView ivHintIcon;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        username = getIntent().getStringExtra("username");

        etFoodName = findViewById(R.id.etFoodName);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        tvPrompt = findViewById(R.id.tvPrompt);
        ivHintIcon = findViewById(R.id.ivHintIcon);
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
                searchFoodInfo(foodName);
            } else {
                Toast.makeText(this, "Please enter a food name or select one from the list.", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void searchFoodInfo(String foodName) {
        String url = "https://api.edamam.com/api/food-database/v2/parser?ingr=" + foodName +
                "&app_id=6231d22c&app_key=941e53aa60d8ea46eda9140decd0bbc7";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray hints = response.getJSONArray("hints");
                            if (hints.length() > 0) {
                                JSONObject food = hints.getJSONObject(0).getJSONObject("food");
                                String label = food.getString("label");
                                JSONObject nutrients = food.getJSONObject("nutrients");
                                double foodCalories = nutrients.optDouble("ENERC_KCAL", 0);
                                double foodCarbs = nutrients.optDouble("CHOCDF", 0);
                                double foodFats = nutrients.optDouble("FAT", 0);
                                double foodProteins = nutrients.optDouble("PROCNT", 0);
                                double foodFibers = nutrients.optDouble("FIBTG", 0);
                                String imageUrl = food.optString("image", "");

                                // Pass the food data to the FoodNutritionInfoActivity
                                Intent intent = new Intent(SearchFoodActivity.this, FoodNutritionInfoActivity.class);
                                intent.putExtra("label", label);
                                intent.putExtra("foodCalories", foodCalories);
                                intent.putExtra("foodCarbs", foodCarbs);
                                intent.putExtra("foodFats", foodFats);
                                intent.putExtra("foodProteins", foodProteins);
                                intent.putExtra("foodFibers", foodFibers);
                                intent.putExtra("imageUrl", imageUrl);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            } else {
                                showError("No nutrition information found.");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showError("Error parsing food information.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showError("Error retrieving food information.");
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}