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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.numad24su_group6.utils.Notification;

public class MainActivity extends AppCompatActivity {
    private Button btnIntroduceApp;
    private Button btnFoodNutritionInfo;
    private Button btnCalculateCalories;
    private Button btnHealthInfo;
    private TextView userInfo;
    private DatabaseReference userRef;
    private String userId;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init notification
        Notification.getInstance(this);

//        Notification.getInstance(MainActivity.this).testNotification(60, "This is a test notification!");

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            // No user is signed in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            // User is signed in, get userId
            userId = auth.getCurrentUser().getUid();
        }

        // Initialize Firebase Database reference
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Initialize buttons and other UI elements
        btnIntroduceApp = findViewById(R.id.btnIntroduceApp);
        btnFoodNutritionInfo = findViewById(R.id.btnFoodNutritionInfo);
        btnCalculateCalories = findViewById(R.id.btnCalculateCalories);
        btnHealthInfo = findViewById(R.id.btnHealthInfo);
        userInfo = findViewById(R.id.user_info);

        // Get the stored username and display it
        fetchUsername();

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
                intent.putExtra("userId", userId); // Pass userId to SearchFoodActivity
                startActivity(intent);
            }
        });

        btnCalculateCalories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalculateCaloriesActivity.class);
                intent.putExtra("userId", userId); // Pass userId to CalculateCaloriesActivity
                startActivity(intent);
            }
        });

        btnHealthInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HealthInfoActivity.class);
                intent.putExtra("userId", userId); // Pass userId to HealthInfoActivity
                startActivity(intent);
            }
        });
    }

    private void fetchUsername() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                if (username != null) {
                    userInfo.setText(username);
                } else {
                    userInfo.setText("User");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());

        // Update the menu with user information
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("email").getValue(String.class);
                int age = dataSnapshot.child("age").getValue(Integer.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);

                MenuItem emailItem = popupMenu.getMenu().findItem(R.id.menu_email);
                MenuItem ageItem = popupMenu.getMenu().findItem(R.id.menu_age);
                MenuItem genderItem = popupMenu.getMenu().findItem(R.id.menu_gender);

                emailItem.setTitle("Email: " + email);
                ageItem.setTitle("Age: " + age);
                genderItem.setTitle("Gender: " + gender);

                popupMenu.show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
            }
        });

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
    }

    private void logout() {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}