package edu.northeastern.numad24su_group6;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class UserProfileActivity extends AppCompatActivity {
    private TextView username;
    private TextView email;
    private TextView age;
    private TextView gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);

        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String storedUsername = sharedPreferences.getString("username", "");
        String storedEmail = sharedPreferences.getString("email", "");
        String storedAge = sharedPreferences.getString("age", "");
        String storedGender = sharedPreferences.getString("gender", "");

        username.setText("Username: " + storedUsername);
        email.setText("Email: " + storedEmail);
        age.setText("Age: " + storedAge);
        gender.setText("Gender: " + storedGender);
    }
}
