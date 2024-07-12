package edu.northeastern.numad24su_group6;

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

public class FoodNutritionInfoActivity extends AppCompatActivity {

    private EditText etFoodName;
    private Button btnSearch, btnBack;
    private TextView tvFoodName, tvNutritionInfo, tvError, tvPrompt;
    private ImageView ivFoodImage;
    private Spinner spinnerFoodList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_nutrition_info);

        etFoodName = findViewById(R.id.etFoodName);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvNutritionInfo = findViewById(R.id.tvNutritionInfo);
        tvError = findViewById(R.id.tvError);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        spinnerFoodList = findViewById(R.id.spinnerFoodList);
        tvPrompt = findViewById(R.id.tvPrompt);

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

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
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
                                double calories = nutrients.optDouble("ENERC_KCAL", 0);
                                double carbs = nutrients.optDouble("CHOCDF", 0);
                                double fats = nutrients.optDouble("FAT", 0);
                                double proteins = nutrients.optDouble("PROCNT", 0);
                                double fibers = nutrients.optDouble("FIBTG", 0);
                                double sugars = nutrients.optDouble("SUGAR", 0);
                                String imageUrl = food.optString("image", "");

                                // Update UI
                                tvFoodName.setText(label);
                                tvNutritionInfo.setText("Calories: " + calories + "kcal\n" +
                                        "Carbs: " + carbs + "g\n" +
                                        "Fats: " + fats + "g\n" +
                                        "Proteins: " + proteins + "g\n" +
                                        "Fibers: " + fibers + "g\n" +
                                        "Sugars: " + sugars + "g");
                                tvFoodName.setVisibility(View.VISIBLE);
                                tvNutritionInfo.setVisibility(View.VISIBLE);
                                tvError.setVisibility(View.GONE);

                                if (!imageUrl.isEmpty()) {
                                    Glide.with(FoodNutritionInfoActivity.this).load(imageUrl).into(ivFoodImage);
                                    ivFoodImage.setVisibility(View.VISIBLE);
                                    ivFoodImage.setTag(imageUrl);
                                } else {
                                    ivFoodImage.setVisibility(View.GONE);
                                }
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
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        tvFoodName.setVisibility(View.GONE);
        tvNutritionInfo.setVisibility(View.GONE);
        ivFoodImage.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("foodName", etFoodName.getText().toString());
        outState.putString("foodLabel", tvFoodName.getText().toString());
        outState.putString("nutritionInfo", tvNutritionInfo.getText().toString());
        outState.putString("imageUrl", ivFoodImage.getTag() != null ? ivFoodImage.getTag().toString() : "");
        outState.putBoolean("imageVisible", ivFoodImage.getVisibility() == View.VISIBLE);
        outState.putBoolean("infoVisible", tvFoodName.getVisibility() == View.VISIBLE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            String foodName = savedInstanceState.getString("foodName");
            String foodLabel = savedInstanceState.getString("foodLabel");
            String nutritionInfo = savedInstanceState.getString("nutritionInfo");
            String imageUrl = savedInstanceState.getString("imageUrl");
            boolean imageVisible = savedInstanceState.getBoolean("imageVisible");
            boolean infoVisible = savedInstanceState.getBoolean("infoVisible");

            etFoodName.setText(foodName);
            tvFoodName.setText(foodLabel);
            tvNutritionInfo.setText(nutritionInfo);
            if (infoVisible) {
                tvFoodName.setVisibility(View.VISIBLE);
                tvNutritionInfo.setVisibility(View.VISIBLE);
            }
            if (imageVisible && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(ivFoodImage);
                ivFoodImage.setVisibility(View.VISIBLE);
                ivFoodImage.setTag(imageUrl);
            }
        }
    }
}
