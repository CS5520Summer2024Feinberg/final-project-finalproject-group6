package edu.northeastern.numad24su_group6;

import android.os.Bundle;
import android.util.Log;
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
    private TextView tvSodiumValue, tvPotassiumValue, tvCalciumValue, tvIronValue, tvVitaminAValue, tvVitaminBValue, tvVitaminCValue, tvVitaminDValue, tvVitaminEValue;
    private ImageView ivFoodImage;
    private SeekBar seekBarAmount;
    private ProgressBar progressBarCarbs, progressBarFats, progressBarProteins, progressBarCalories;
    private ProgressBar progressBarSodium, progressBarPotassium, progressBarCalcium, progressBarIron, progressBarVitaminA, progressBarVitaminB, progressBarVitaminC, progressBarVitaminD, progressBarVitaminE;

    private double foodCarbs, foodFats, foodProteins, foodCalories;
    private double foodSodium, foodPotassium, foodCalcium, foodIron, foodVitaminA, foodVitaminB, foodVitaminC, foodVitaminD, foodVitaminE;
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
        progressBarSodium = findViewById(R.id.progressBarSodium);
        progressBarPotassium = findViewById(R.id.progressBarPotassium);
        progressBarCalcium = findViewById(R.id.progressBarCalcium);
        progressBarIron = findViewById(R.id.progressBarIron);
        progressBarVitaminA = findViewById(R.id.progressBarVitaminA);
        progressBarVitaminB = findViewById(R.id.progressBarVitaminB);
        progressBarVitaminC = findViewById(R.id.progressBarVitaminC);
        progressBarVitaminD = findViewById(R.id.progressBarVitaminD);
        progressBarVitaminE = findViewById(R.id.progressBarVitaminE);

        tvCarbsValue = findViewById(R.id.tvCarbsValue);
        tvFatsValue = findViewById(R.id.tvFatsValue);
        tvProteinsValue = findViewById(R.id.tvProteinsValue);
        tvCaloriesValue = findViewById(R.id.tvCaloriesValue);
        tvSodiumValue = findViewById(R.id.tvSodiumValue);
        tvPotassiumValue = findViewById(R.id.tvPotassiumValue);
        tvCalciumValue = findViewById(R.id.tvCalciumValue);
        tvIronValue = findViewById(R.id.tvIronValue);
        tvVitaminAValue = findViewById(R.id.tvVitaminAValue);
        tvVitaminBValue = findViewById(R.id.tvVitaminBValue);
        tvVitaminCValue = findViewById(R.id.tvVitaminCValue);
        tvVitaminDValue = findViewById(R.id.tvVitaminDValue);
        tvVitaminEValue = findViewById(R.id.tvVitaminEValue);

        userId = getIntent().getStringExtra("userId");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Get data from the intent
        foodCarbs = getIntent().getDoubleExtra("foodCarbs", 0);
        foodFats = getIntent().getDoubleExtra("foodFats", 0);
        foodProteins = getIntent().getDoubleExtra("foodProteins", 0);
        foodCalories = getIntent().getDoubleExtra("foodCalories", 0);
        foodSodium = getIntent().getDoubleExtra("foodSodium", 0);
        foodPotassium = getIntent().getDoubleExtra("foodPotassium", 0);
        foodCalcium = getIntent().getDoubleExtra("foodCalcium", 0);
        foodIron = getIntent().getDoubleExtra("foodIron", 0);
        foodVitaminA = getIntent().getDoubleExtra("foodVitaminA", 0);
        foodVitaminB = getIntent().getDoubleExtra("foodVitaminB", 0);
        foodVitaminC = getIntent().getDoubleExtra("foodVitaminC", 0);
        foodVitaminD = getIntent().getDoubleExtra("foodVitaminD", 0);
        foodVitaminE = getIntent().getDoubleExtra("foodVitaminE", 0);

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
        updateProgressBar(progressBarCarbs, tvCarbsValue, foodCarbs, user.getCarbsGoal());
        updateProgressBar(progressBarFats, tvFatsValue, foodFats, user.getFatGoal());
        updateProgressBar(progressBarProteins, tvProteinsValue, foodProteins, user.getProteinGoal());
        updateProgressBar(progressBarCalories, tvCaloriesValue, foodCalories, user.getCaloriesGoal());
        updateProgressBar(progressBarSodium, tvSodiumValue, foodSodium, user.getSodiumGoal());
        updateProgressBar(progressBarPotassium, tvPotassiumValue, foodPotassium, user.getPotassiumGoal());
        updateProgressBar(progressBarCalcium, tvCalciumValue, foodCalcium, user.getCalciumGoal());
        updateProgressBar(progressBarIron, tvIronValue, foodIron, user.getIronGoal());
        updateProgressBar(progressBarVitaminA, tvVitaminAValue, foodVitaminA, user.getVitaminAGoal());
        updateProgressBar(progressBarVitaminB, tvVitaminBValue, foodVitaminB, user.getVitaminBGoal());
        updateProgressBar(progressBarVitaminC, tvVitaminCValue, foodVitaminC, user.getVitaminCGoal());
        updateProgressBar(progressBarVitaminD, tvVitaminDValue, foodVitaminD, user.getVitaminDGoal());
        updateProgressBar(progressBarVitaminE, tvVitaminEValue, foodVitaminE, user.getVitaminEGoal());
    }

    private void updateProgressBar(ProgressBar progressBar, TextView textView, double nutrientValue, double goalValue) {
        if (textView == null || progressBar == null) {
            return;
        }

        double factor = seekBarAmount.getProgress() / 100.0;
        double currentValue = nutrientValue * factor;

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
        double sodium = foodSodium * factor;
        double potassium = foodPotassium * factor;
        double calcium = foodCalcium * factor;
        double iron = foodIron * factor;
        double vitaminA = foodVitaminA * factor;
        double vitaminB = foodVitaminB * factor;
        double vitaminC = foodVitaminC * factor;
        double vitaminD = foodVitaminD * factor;
        double vitaminE = foodVitaminE * factor;

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
        if (tvSodiumValue != null) {
            tvSodiumValue.setText(String.format("%.1f mg", sodium));
        }
        if (tvPotassiumValue != null) {
            tvPotassiumValue.setText(String.format("%.1f mg", potassium));
        }
        if (tvCalciumValue != null) {
            tvCalciumValue.setText(String.format("%.1f mg", calcium));
        }
        if (tvIronValue != null) {
            tvIronValue.setText(String.format("%.1f mg", iron));
        }
        if (tvVitaminAValue != null) {
            tvVitaminAValue.setText(String.format("%.1f mcg", vitaminA));
        }
        if (tvVitaminBValue != null) {
            tvVitaminBValue.setText(String.format("%.1f mcg", vitaminB));
        }
        if (tvVitaminCValue != null) {
            tvVitaminCValue.setText(String.format("%.1f mg", vitaminC));
        }
        if (tvVitaminDValue != null) {
            tvVitaminDValue.setText(String.format("%.1f mcg", vitaminD));
        }
        if (tvVitaminEValue != null) {
            tvVitaminEValue.setText(String.format("%.1f mg", vitaminE));
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
                float addedSodium = (float) (foodSodium * factor);
                float addedPotassium = (float) (foodPotassium * factor);
                float addedCalcium = (float) (foodCalcium * factor);
                float addedIron = (float) (foodIron * factor);
                float addedVitaminA = (float) (foodVitaminA * factor);
                float addedVitaminB = (float) (foodVitaminB * factor);
                float addedVitaminC = (float) (foodVitaminC * factor);
                float addedVitaminD = (float) (foodVitaminD * factor);
                float addedVitaminE = (float) (foodVitaminE * factor);

                float currentCarbs = dataSnapshot.child("currentCarbs").getValue(Float.class) == null ? addedCarbs : dataSnapshot.child("currentCarbs").getValue(Float.class) + addedCarbs;
                float currentFat = dataSnapshot.child("currentFat").getValue(Float.class) == null ? addedFats : dataSnapshot.child("currentFat").getValue(Float.class) + addedFats;
                float currentProtein = dataSnapshot.child("currentProtein").getValue(Float.class) == null ? addedProteins : dataSnapshot.child("currentProtein").getValue(Float.class) + addedProteins;
                float currentCalories = dataSnapshot.child("currentCalories").getValue(Float.class) == null ? addedCalories : dataSnapshot.child("currentCalories").getValue(Float.class) + addedCalories;
                float currentSodium = dataSnapshot.child("currentSodium").getValue(Float.class) == null ? addedSodium : dataSnapshot.child("currentSodium").getValue(Float.class) + addedSodium;
                float currentPotassium = dataSnapshot.child("currentPotassium").getValue(Float.class) == null ? addedPotassium : dataSnapshot.child("currentPotassium").getValue(Float.class) + addedPotassium;
                float currentCalcium = dataSnapshot.child("currentCalcium").getValue(Float.class) == null ? addedCalcium : dataSnapshot.child("currentCalcium").getValue(Float.class) + addedCalcium;
                float currentIron = dataSnapshot.child("currentIron").getValue(Float.class) == null ? addedIron : dataSnapshot.child("currentIron").getValue(Float.class) + addedIron;
                float currentVitaminA = dataSnapshot.child("currentVitaminA").getValue(Float.class) == null ? addedVitaminA : dataSnapshot.child("currentVitaminA").getValue(Float.class) + addedVitaminA;
                float currentVitaminB = dataSnapshot.child("currentVitaminB").getValue(Float.class) == null ? addedVitaminB : dataSnapshot.child("currentVitaminB").getValue(Float.class) + addedVitaminB;
                float currentVitaminC = dataSnapshot.child("currentVitaminC").getValue(Float.class) == null ? addedVitaminC : dataSnapshot.child("currentVitaminC").getValue(Float.class) + addedVitaminC;
                float currentVitaminD = dataSnapshot.child("currentVitaminD").getValue(Float.class) == null ? addedVitaminD : dataSnapshot.child("currentVitaminD").getValue(Float.class) + addedVitaminD;
                float currentVitaminE = dataSnapshot.child("currentVitaminE").getValue(Float.class) == null ? addedVitaminE : dataSnapshot.child("currentVitaminE").getValue(Float.class) + addedVitaminE;

                Map<String, Object> dateUpdates = new HashMap<>();
                dateUpdates.put("currentCarbs", currentCarbs);
                dateUpdates.put("currentFat", currentFat);
                dateUpdates.put("currentProtein", currentProtein);
                dateUpdates.put("currentCalories", currentCalories);
                dateUpdates.put("currentSodium", currentSodium);
                dateUpdates.put("currentPotassium", currentPotassium);
                dateUpdates.put("currentCalcium", currentCalcium);
                dateUpdates.put("currentIron", currentIron);
                dateUpdates.put("currentVitaminA", currentVitaminA);
                dateUpdates.put("currentVitaminB", currentVitaminB);
                dateUpdates.put("currentVitaminC", currentVitaminC);
                dateUpdates.put("currentVitaminD", currentVitaminD);
                dateUpdates.put("currentVitaminE", currentVitaminE);

                // Add food details to meal planner
                DatabaseReference mealPlannerRef = dateRef.child("mealPlanner").push();
                Map<String, Object> foodDetails = new HashMap<>();
                foodDetails.put("foodName", tvFoodName.getText().toString());
                foodDetails.put("carbs", addedCarbs);
                foodDetails.put("fats", addedFats);
                foodDetails.put("proteins", addedProteins);
                foodDetails.put("calories", addedCalories);
                foodDetails.put("amount", amount);
                foodDetails.put("sodium", addedSodium);
                foodDetails.put("potassium", addedPotassium);
                foodDetails.put("calcium", addedCalcium);
                foodDetails.put("iron", addedIron);
                foodDetails.put("vitaminA", addedVitaminA);
                foodDetails.put("vitaminB", addedVitaminB);
                foodDetails.put("vitaminC", addedVitaminC);
                foodDetails.put("vitaminD", addedVitaminD);
                foodDetails.put("vitaminE", addedVitaminE);
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
        outState.putDouble("storedSodium", foodSodium);
        outState.putDouble("storedPotassium", foodPotassium);
        outState.putDouble("storedCalcium", foodCalcium);
        outState.putDouble("storedIron", foodIron);
        outState.putDouble("storedVitaminA", foodVitaminA);
        outState.putDouble("storedVitaminB", foodVitaminB);
        outState.putDouble("storedVitaminC", foodVitaminC);
        outState.putDouble("storedVitaminD", foodVitaminD);
        outState.putDouble("storedVitaminE", foodVitaminE);
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
            foodSodium = savedInstanceState.getDouble("storedSodium");
            foodPotassium = savedInstanceState.getDouble("storedPotassium");
            foodCalcium = savedInstanceState.getDouble("storedCalcium");
            foodIron = savedInstanceState.getDouble("storedIron");
            foodVitaminA = savedInstanceState.getDouble("storedVitaminA");
            foodVitaminB = savedInstanceState.getDouble("storedVitaminB");
            foodVitaminC = savedInstanceState.getDouble("storedVitaminC");
            foodVitaminD = savedInstanceState.getDouble("storedVitaminD");
            foodVitaminE = savedInstanceState.getDouble("storedVitaminE");

            updateNutritionValues(seekBarAmount.getProgress());
        }
    }
}
