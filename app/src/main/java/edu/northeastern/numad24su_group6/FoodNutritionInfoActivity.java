package edu.northeastern.numad24su_group6;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.numad24su_group6.model.User;

public class FoodNutritionInfoActivity extends AppCompatActivity {

    private Button btnBack, btnAddFood;
    private TextView tvFoodName, tvCarbsValue, tvFatsValue, tvProteinsValue, tvFibersValue, tvCaloriesValue, tvSeekBarValue;
    private ImageView ivFoodImage;
    private SeekBar seekBarAmount;
    private ProgressBar progressBarCarbs, progressBarFats, progressBarProteins, progressBarFibers, progressBarCalories;

    private User user;
    private double foodCarbs, foodFats, foodProteins, foodFibers, foodCalories;
    private DatabaseReference userRef;
    private String username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_nutrition_info);

        btnBack = findViewById(R.id.btnBack);
        btnAddFood = findViewById(R.id.btnAddFood);
        tvFoodName = findViewById(R.id.tvFoodName);
        ivFoodImage = findViewById(R.id.ivFoodImage);
        seekBarAmount = findViewById(R.id.seekBarAmount);
        tvSeekBarValue = findViewById(R.id.tvSeekBarValue);

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

        // Initialize Firebase Database reference
        username = getIntent().getStringExtra("username");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(username);

        // Get data from the intent
        foodCarbs = getIntent().getDoubleExtra("foodCarbs", 0);
        foodFats = getIntent().getDoubleExtra("foodFats", 0);
        foodProteins = getIntent().getDoubleExtra("foodProteins", 0);
        foodFibers = getIntent().getDoubleExtra("foodFibers", 0);
        foodCalories = getIntent().getDoubleExtra("foodCalories", 0);
        String label = getIntent().getStringExtra("label");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        tvFoodName.setText(label);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(ivFoodImage);
        }

        btnBack.setOnClickListener(v -> finish());

        seekBarAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateNutritionValues(progress);
                tvSeekBarValue.setText(progress + "g");
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

        btnAddFood.setOnClickListener(v -> updateUserData());
    }

    private User getUser() {
        // Retrieve user data (for now, create a dummy user)
        return new User(25, "Male", 175, 70, 3);
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
        tvCaloriesValue.setText(String.format("%.1f Cal", calories));

        progressBarCarbs.setProgress((int) (carbs / user.getCarbs() * 100));
        progressBarFats.setProgress((int) (fats / user.getFats() * 100));
        progressBarProteins.setProgress((int) (proteins / user.getProteins() * 100));
        progressBarFibers.setProgress((int) (fibers / user.getWater() * 100));
        progressBarCalories.setProgress((int) (calories / user.getCalories() * 100));
    }

    private void updateUserData() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                float currentCarbs = task.getResult().child("currentCarbs").getValue(Float.class) + (float) foodCarbs;
                float currentFat = task.getResult().child("currentFat").getValue(Float.class) + (float) foodFats;
                float currentProtein = task.getResult().child("currentProtein").getValue(Float.class) + (float) foodProteins;
                float currentWater = task.getResult().child("currentWater").getValue(Float.class) + (float) foodFibers; // Example
                float currentCalories = task.getResult().child("currentCalories").getValue(Float.class) + (float) foodCalories;

                Map<String, Object> userUpdates = new HashMap<>();
                userUpdates.put("currentCarbs", currentCarbs);
                userUpdates.put("currentFat", currentFat);
                userUpdates.put("currentProtein", currentProtein);
                userUpdates.put("currentWater", currentWater); // Example
                userUpdates.put("currentCalories", currentCalories);

                userRef.updateChildren(userUpdates).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(FoodNutritionInfoActivity.this, "Food added successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(FoodNutritionInfoActivity.this, "Failed to add food", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_LONG).show();
            }
        });
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