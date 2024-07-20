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
        setContentView(R.layout.activity_main);

        btnIntroduceApp = findViewById(R.id.btnIntroduceApp);
        btnFoodNutritionInfo = findViewById(R.id.btnFoodNutritionInfo);
        btnCalculateCalories = findViewById(R.id.btnCalculateCalories);
        btnHealthInfo = findViewById(R.id.btnHealthInfo);
        btnUserLogin = findViewById(R.id.btnUserLogin);
        userInfo = findViewById(R.id.user_info);

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Get the username passed during login and display it
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        if (username != null) {
            userInfo.setText(username);
        }

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
        String age = sharedPreferences.getString("age", "Age not found");
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
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

