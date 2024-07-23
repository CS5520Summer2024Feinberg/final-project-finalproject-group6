package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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

import edu.northeastern.numad24su_group6.model.User;

public class FoodNutritionInfoActivity extends AppCompatActivity {

    private TextView tvFoodName, tvError, tvPrompt, tvCarbsValue, tvFatsValue, tvProteinsValue, tvFibersValue, tvCaloriesValue;
    private ImageView ivFoodImage;
    private SeekBar seekBarAmount;
    private ProgressBar progressBarCarbs, progressBarFats, progressBarProteins, progressBarFibers, progressBarCalories;
    private Button btnAddFood, btnBack;

    private User user;
    private double foodCarbs, foodFats, foodProteins, foodFibers, foodCalories;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_nutrition_info);

        tvFoodName = findViewById(R.id.tvFoodName);
        // tvError = findViewById(R.id.tvError);
        tvPrompt = findViewById(R.id.tvPrompt);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        seekBarAmount = findViewById(R.id.seekBarAmount);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnBack = findViewById(R.id.btnBack);

        progressBarCarbs = findViewById(R.id.progressBarCarbs);
        progressBarFats = findViewById(R.id.progressBarFats);
        progressBarProteins = findViewById(R.id.progressBarProteins);
        progressBarFibers = findViewById(R.id.progressBarFibers);
        progressBarCalories = findViewById(R.id.progressBarCalories);
        tvCarbsValue = findViewById(R.id.tvCarbsValue);
        tvFatsValue = findViewById(R.id.tvFatsValue);
        tvProteinsValue = findViewById(R.id.tvProteinsValue);
        tvFibersValue = findViewById(R.id.tvFibersValue);
        tvCaloriesValue = findViewById(R.id.tvCaloriesValue);

        user = getUser(); // Fetch user details from the intent or database

        Intent intent = getIntent();
        String foodName = intent.getStringExtra("foodName");
        if (foodName != null) {
            searchFoodInfo(foodName);
        }

        btnBack.setOnClickListener(v -> finish());

        seekBarAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateNutritionValues(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing
            }
        });

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    private User getUser() {
        // Retrieve user data (for now, create a dummy user)
        return new User(25, "Male", 175, 70, 3);
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
                                foodCalories = nutrients.optDouble("ENERC_KCAL", 0);
                                foodCarbs = nutrients.optDouble("CHOCDF", 0);
                                foodFats = nutrients.optDouble("FAT", 0);
                                foodProteins = nutrients.optDouble("PROCNT", 0);
                                foodFibers = nutrients.optDouble("FIBTG", 0);
                                String imageUrl = food.optString("image", "");

                                // Update UI
                                tvFoodName.setText(label);
                                if (!imageUrl.isEmpty()) {
                                    Glide.with(FoodNutritionInfoActivity.this).load(imageUrl).into(ivFoodImage);
                                    ivFoodImage.setVisibility(View.VISIBLE);
                                    ivFoodImage.setTag(imageUrl);
                                } else {
                                    ivFoodImage.setVisibility(View.GONE);
                                }

                                updateNutritionValues(seekBarAmount.getProgress());

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

    private void updateNutritionValues(int amount) {
        double factor = amount / 100.0;
        double carbs = foodCarbs * factor;
        double fats = foodFats * factor;
        double proteins = foodProteins * factor;
        double fibers = foodFibers * factor;
        double calories = foodCalories * factor;

        tvCarbsValue.setText(String.format("%.1fg", carbs));
        tvFatsValue.setText(String.format("%.1fg", fats));
        tvProteinsValue.setText(String.format("%.1fg", proteins));
        tvFibersValue.setText(String.format("%.1fg", fibers));
        tvCaloriesValue.setText(String.format("%.1f kcal", calories));

        progressBarCarbs.setProgress((int) (carbs / user.getCarbs() * 100));
        progressBarFats.setProgress((int) (fats / user.getFats() * 100));
        progressBarProteins.setProgress((int) (proteins / user.getProteins() * 100));
        progressBarFibers.setProgress((int) (fibers / user.getWater() * 100));
        progressBarCalories.setProgress((int) (calories / user.getCalories() * 100));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        ivFoodImage.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("foodName", tvFoodName.getText().toString());
        outState.putString("imageUrl", ivFoodImage.getTag() != null ? ivFoodImage.getTag().toString() : "");
        outState.putInt("seekBarAmount", seekBarAmount.getProgress());
        outState.putDouble("storedCarbs", foodCarbs);
        outState.putDouble("storedFats", foodFats);
        outState.putDouble("storedProteins", foodProteins);
        outState.putDouble("storedFibers", foodFibers);
        outState.putDouble("storedCalories", foodCalories);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            tvFoodName.setText(savedInstanceState.getString("foodName"));
            if (savedInstanceState.getString("imageUrl") != null) {
                Glide.with(this).load(savedInstanceState.getString("imageUrl")).into(ivFoodImage);
                ivFoodImage.setTag(savedInstanceState.getString("imageUrl"));
            }
            seekBarAmount.setProgress(savedInstanceState.getInt("seekBarAmount"));
            foodCarbs = savedInstanceState.getDouble("storedCarbs");
            foodFats = savedInstanceState.getDouble("storedFats");
            foodProteins = savedInstanceState.getDouble("storedProteins");
            foodFibers = savedInstanceState.getDouble("storedFibers");
            foodCalories = savedInstanceState.getDouble("storedCalories");

            updateNutritionValues(seekBarAmount.getProgress());
        }
    }
}