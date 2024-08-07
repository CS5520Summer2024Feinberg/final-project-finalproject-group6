package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.numad24su_group6.utils.NotificationScheduler;

public class MainActivity extends AppCompatActivity {
    private Button btnIntroduceApp;
    private Button btnFoodNutritionInfo;
    private Button btnCalculateCalories;
    private Button btnHealthInfo;
    private TextView userInfo;
    private DatabaseReference userRef;
    private String userId;
    private FirebaseAuth auth;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final int REQUEST_EXACT_ALARM_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login before setting the content view
        checkLogin();

        // Initialize UI components only if the user is logged in
        if (auth.getCurrentUser() != null) {
            setContentView(R.layout.activity_main);

            // Initialize buttons and other UI elements
            btnIntroduceApp = findViewById(R.id.btnIntroduceApp);
            btnFoodNutritionInfo = findViewById(R.id.btnFoodNutritionInfo);
            btnCalculateCalories = findViewById(R.id.btnCalculateCalories);
            btnHealthInfo = findViewById(R.id.btnHealthInfo);
            userInfo = findViewById(R.id.user_info);

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

            requestNotificationPermission();
        }
    }

    private void checkLogin() {
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            // No user is signed in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // User is signed in, get userId
            userId = auth.getCurrentUser().getUid();

            // Initialize Firebase Database reference
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Get the stored username and display it
            fetchUsername();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, "android.permission.POST_NOTIFICATIONS")
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS"}, REQUEST_NOTIFICATION_PERMISSION);
            } else {
                requestExactAlarmPermission();
            }
        } else {
            requestExactAlarmPermission();
        }
    }

    private void requestExactAlarmPermission() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.SCHEDULE_EXACT_ALARM")
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.SCHEDULE_EXACT_ALARM"}, REQUEST_EXACT_ALARM_PERMISSION);
        } else {
            new NotificationScheduler(this);
        }
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
                Integer age = dataSnapshot.child("age").getValue(Integer.class);
                String gender = dataSnapshot.child("gender").getValue(String.class);

                MenuItem emailItem = popupMenu.getMenu().findItem(R.id.menu_email);
                MenuItem ageItem = popupMenu.getMenu().findItem(R.id.menu_age);
                MenuItem genderItem = popupMenu.getMenu().findItem(R.id.menu_gender);

                if (email != null) emailItem.setTitle("Email: " + email);
                if (age != null) ageItem.setTitle("Age: " + age);
                if (gender != null) genderItem.setTitle("Gender: " + gender);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Notification permission granted
                new NotificationScheduler(this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        requestExactAlarmPermission();
                    }
                });
            } else {
                // Handle permission denial
            }
        } else if (requestCode == REQUEST_EXACT_ALARM_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Exact alarm permission granted
                new NotificationScheduler(this);
            } else {
                // Handle permission denial
            }
        }
    }

    private void logout() {
        auth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}