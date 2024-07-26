package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private EditText ageInput;
    private EditText genderInput;
    private Button createUserButton;
    private Button backButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        auth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        ageInput = findViewById(R.id.age_input);
        genderInput = findViewById(R.id.gender_input);
        createUserButton = findViewById(R.id.create_user_button);
        backButton = findViewById(R.id.back_button);

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String email = emailInput.getText().toString();
                String age = ageInput.getText().toString();
                String gender = genderInput.getText().toString();

                if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && !age.isEmpty() && !gender.isEmpty()) {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(CreateUserActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userId = task.getResult().getUser().getUid();
                                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference userRef = database.getReference("users").child(userId);

                                        Map<String, Object> userUpdates = new HashMap<>();
                                        userUpdates.put("username", username);
                                        userUpdates.put("email", email);
                                        userUpdates.put("age", Integer.parseInt(age));
                                        userUpdates.put("gender", gender);
                                        userUpdates.put("carbsGoal", 200f); // Example initial values
                                        userUpdates.put("currentCarbs", 0f);
                                        userUpdates.put("fatGoal", 70f);
                                        userUpdates.put("currentFat", 0f);
                                        userUpdates.put("proteinGoal", 150f);
                                        userUpdates.put("currentProtein", 0f);
                                        userUpdates.put("waterGoal", 3000f); // in ml
                                        userUpdates.put("currentWater", 0f); // in ml
                                        userUpdates.put("caloriesGoal", 2000f);
                                        userUpdates.put("currentCalories", 0f);
                                        userUpdates.put("is_logged_in", true);

                                        userRef.updateChildren(userUpdates).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(CreateUserActivity.this, "User created successfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                                                intent.putExtra("username", username);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(CreateUserActivity.this, "Failed to create user", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(CreateUserActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(CreateUserActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}