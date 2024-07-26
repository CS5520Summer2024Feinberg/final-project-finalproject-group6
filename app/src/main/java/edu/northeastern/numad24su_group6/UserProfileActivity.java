package edu.northeastern.numad24su_group6;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity {
    private TextView username;
    private TextView email;
    private TextView age;
    private TextView gender;
    private DatabaseReference userRef;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);

        currentUsername = getIntent().getStringExtra("username");
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUsername);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String storedUsername = dataSnapshot.child("username").getValue(String.class);
                String storedEmail = dataSnapshot.child("email").getValue(String.class);
                int storedAge = dataSnapshot.child("age").getValue(Integer.class);
                String storedGender = dataSnapshot.child("gender").getValue(String.class);

                username.setText("Username: " + storedUsername);
                email.setText("Email: " + storedEmail);
                age.setText("Age: " + storedAge);
                gender.setText("Gender: " + storedGender);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserProfileActivity.this, "Failed to load user data", Toast.LENGTH_LONG).show();
            }
        });
    }
}