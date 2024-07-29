package edu.northeastern.numad24su_group6;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.northeastern.numad24su_group6.model.User;

public class FoodNutritionInfoActivity extends AppCompatActivity {

    private Button btnBack, btnAddFood;
    private TextView tvFoodName, tvCarbsValue, tvFatsValue, tvProteinsValue, tvCaloriesValue, tvSeekBarValue;
    private ImageView ivFoodImage;
    private SeekBar seekBarAmount;
    private ProgressBar progressBarCarbs, progressBarFats, progressBarProteins, progressBarCalories;

    private double foodCarbs, foodFats, foodProteins, foodCalories;
    private DatabaseReference userRef;
    private String userId;
    private User currentUser;

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
        progressBarCalories = findViewById(R.id.progressBarCalories);
        tvCarbsValue = findViewById(R.id.tvCarbsValue);
        tvFatsValue = findViewById(R.id.tvFatsValue);
        tvProteinsValue = findViewById(R.id.tvProteinsValue);
        tvCaloriesValue = findViewById(R.id.tvCaloriesValue);

        userId = getIntent().getStringExtra("userId");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Get data from the intent
        foodCarbs = getIntent().getDoubleExtra("foodCarbs", 0);
        foodFats = getIntent().getDoubleExtra("foodFats", 0);
        foodProteins = getIntent().getDoubleExtra("foodProteins", 0);
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

        fetchUserData();
    }

    private void fetchUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        currentUser = user;
                        // Set progress bars and text views with current and goal values
                        updateProgressBars(user);
                    } else {
                        Toast.makeText(FoodNutritionInfoActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(FoodNutritionInfoActivity.this, "User information not found", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FoodNutritionInfoActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateProgressBars(User user) {
        updateProgressBar(progressBarCarbs, tvCarbsValue, user.getCarbsGoal());
        updateProgressBar(progressBarFats, tvFatsValue, user.getFatGoal());
        updateProgressBar(progressBarProteins, tvProteinsValue, user.getProteinGoal());
        updateProgressBar(progressBarCalories, tvCaloriesValue, user.getCaloriesGoal());
    }

    private void updateProgressBar(ProgressBar progressBar, TextView textView, double goalValue) {
        if (textView == null || progressBar == null) {
            return;
        }

        double factor = seekBarAmount.getProgress() / 100.0;
        double currentValue = 0;

        if (progressBar == progressBarCarbs) {
            currentValue = foodCarbs * factor;
        } else if (progressBar == progressBarFats) {
            currentValue = foodFats * factor;
        } else if (progressBar == progressBarProteins) {
            currentValue = foodProteins * factor;
        } else if (progressBar == progressBarCalories) {
            currentValue = foodCalories * factor;
        }

        int progress = (int) ((currentValue / goalValue) * 100);
        progressBar.setProgress(progress);
        textView.setText(String.format("%.1f / %.1f", currentValue, goalValue));
    }

    private void updateNutritionValues(int amount) {
        double factor = amount / 100.0;
        double carbs = foodCarbs * factor;
        double fats = foodFats * factor;
        double proteins = foodProteins * factor;
        double calories = foodCalories * factor;

        if (tvCarbsValue != null) {
            tvCarbsValue.setText(String.format("%.1fg", carbs));
        }
        if (tvFatsValue != null) {
            tvFatsValue.setText(String.format("%.1fg", fats));
        }
        if (tvProteinsValue != null) {
            tvProteinsValue.setText(String.format("%.1fg", proteins));
        }
        if (tvCaloriesValue != null) {
            tvCaloriesValue.setText(String.format("%.1f Cal", calories));
        }

        if (currentUser != null) {
            updateProgressBars(currentUser);
        }
    }

    private void updateUserData() {
        if (userId == null) {
            Toast.makeText(this, "User data not loaded yet. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = getCurrentDate();
        DatabaseReference dateRef = userRef.child("dates").child(currentDate);

        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int amount = seekBarAmount.getProgress();
                double factor = amount / 100.0;

                float addedCarbs = (float) (foodCarbs * factor);
                float addedFats = (float) (foodFats * factor);
                float addedProteins = (float) (foodProteins * factor);
                float addedCalories = (float) (foodCalories * factor);

                float currentCarbs = dataSnapshot.child("currentCarbs").getValue(Float.class) == null ? addedCarbs : dataSnapshot.child("currentCarbs").getValue(Float.class) + addedCarbs;
                float currentFat = dataSnapshot.child("currentFat").getValue(Float.class) == null ? addedFats : dataSnapshot.child("currentFat").getValue(Float.class) + addedFats;
                float currentProtein = dataSnapshot.child("currentProtein").getValue(Float.class) == null ? addedProteins : dataSnapshot.child("currentProtein").getValue(Float.class) + addedProteins;
                float currentCalories = dataSnapshot.child("currentCalories").getValue(Float.class) == null ? addedCalories : dataSnapshot.child("currentCalories").getValue(Float.class) + addedCalories;

                Map<String, Object> dateUpdates = new HashMap<>();
                dateUpdates.put("currentCarbs", currentCarbs);
                dateUpdates.put("currentFat", currentFat);
                dateUpdates.put("currentProtein", currentProtein);
                dateUpdates.put("currentCalories", currentCalories);

                // Add food details to meal planner
                DatabaseReference mealPlannerRef = dateRef.child("mealPlanner").push();
                Map<String, Object> foodDetails = new HashMap<>();
                foodDetails.put("foodName", tvFoodName.getText().toString());
                foodDetails.put("carbs", addedCarbs);
                foodDetails.put("fats", addedFats);
                foodDetails.put("proteins", addedProteins);
                foodDetails.put("calories", addedCalories);
                foodDetails.put("amount", amount);
                mealPlannerRef.setValue(foodDetails);

                dateRef.updateChildren(dateUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(FoodNutritionInfoActivity.this, "Food added successfully", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(FoodNutritionInfoActivity.this, "Failed to add food", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FoodNutritionInfoActivity.this, "Failed to retrieve user data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
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
            foodCalories = savedInstanceState.getDouble("storedCalories");

            updateNutritionValues(seekBarAmount.getProgress());
        }
    }
}