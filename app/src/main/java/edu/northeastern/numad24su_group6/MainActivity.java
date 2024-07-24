package edu.northeastern.numad24su_group6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button btnIntroduceApp;
    private Button btnFoodNutritionInfo;
    private Button btnCalculateCalories;
    private Button btnHealthInfo;
    private Button btnUserLogin;
    private TextView userInfo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

        if (!isLoggedIn) {
            // Redirect to LoginActivity if not logged in
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Finish MainActivity to prevent going back to it
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialize buttons and other UI elements
        btnIntroduceApp = findViewById(R.id.btnIntroduceApp);
        btnFoodNutritionInfo = findViewById(R.id.btnFoodNutritionInfo);
        btnCalculateCalories = findViewById(R.id.btnCalculateCalories);
        btnHealthInfo = findViewById(R.id.btnHealthInfo);
        btnUserLogin = findViewById(R.id.btnUserLogin);
        userInfo = findViewById(R.id.user_info);

        // Get the stored username and display it
        String username = sharedPreferences.getString("username", "user");
        userInfo.setText(username);

        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

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
                Intent intent = new Intent(MainActivity.this, SearchFoodActivity.class);
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

        btnHealthInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HealthInfoActivity.class);
                startActivity(intent);
            }
        });

        btnUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());

        // Update the menu with user information
        String email = sharedPreferences.getString("email", "Email not found");
        int age = sharedPreferences.getInt("age", 0);
        String gender = sharedPreferences.getString("gender", "Gender not found");

        MenuItem emailItem = popupMenu.getMenu().findItem(R.id.menu_email);
        MenuItem ageItem = popupMenu.getMenu().findItem(R.id.menu_age);
        MenuItem genderItem = popupMenu.getMenu().findItem(R.id.menu_gender);

        emailItem.setTitle("Email: " + email);
        ageItem.setTitle("Age: " + age);
        genderItem.setTitle("Gender: " + gender);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_logout) {
                    logout();
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_logged_in", false);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

