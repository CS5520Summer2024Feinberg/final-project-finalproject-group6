package edu.northeastern.numad24su_group6;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    private Spinner genderSpinner;
    private Button createUserButton;
    private Button backButton;
    private FirebaseAuth auth;

    private static final String DEFAULT_MALE_AGE = "25"; // Default age for male
    private static final String DEFAULT_FEMALE_AGE = "23"; // Default age for female

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        auth = FirebaseAuth.getInstance();

        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        emailInput = findViewById(R.id.email_input);
        ageInput = findViewById(R.id.age_input);
        genderSpinner = findViewById(R.id.gender_spinner);
        createUserButton = findViewById(R.id.create_user_button);
        backButton = findViewById(R.id.back_button);

        setupGenderSpinner();

        createUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String email = emailInput.getText().toString();
                String ageString = ageInput.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();

                // Set default age based on gender if age is not provided
                int age;
                if (ageString.isEmpty()) {
                    if (gender.equals("Male")) {
                        age = Integer.parseInt(DEFAULT_MALE_AGE);
                    } else {
                        age = Integer.parseInt(DEFAULT_FEMALE_AGE);
                    }
                } else {
                    age = Integer.parseInt(ageString);
                }

                if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
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
                                        userUpdates.put("age", age);
                                        userUpdates.put("gender", gender);
                                        userUpdates.put("carbsGoal", 300f); // Example initial values
                                        userUpdates.put("currentCarbs", 0f);
                                        userUpdates.put("fatGoal", 70f);
                                        userUpdates.put("currentFat", 0f);
                                        userUpdates.put("proteinGoal", 50f);
                                        userUpdates.put("currentProtein", 0f);
                                        userUpdates.put("caloriesGoal", 2000f);
                                        userUpdates.put("currentCalories", 0f);
                                        userUpdates.put("sodiumGoal", 2300f);
                                        userUpdates.put("currentSodium", 0f);
                                        userUpdates.put("potassiumGoal", 4700f);
                                        userUpdates.put("currentPotassium", 0f);
                                        userUpdates.put("calciumGoal", 1000f);
                                        userUpdates.put("currentCalcium", 0f);
                                        userUpdates.put("ironGoal", 18f);
                                        userUpdates.put("currentIron", 0f);
                                        userUpdates.put("vitaminAGoal", 900f);
                                        userUpdates.put("currentVitaminA", 0f);
                                        userUpdates.put("vitaminBGoal", 1.3f);
                                        userUpdates.put("currentVitaminB", 0f);
                                        userUpdates.put("vitaminCGoal", 90f);
                                        userUpdates.put("currentVitaminC", 0f);
                                        userUpdates.put("vitaminDGoal", 20f);
                                        userUpdates.put("currentVitaminD", 0f);
                                        userUpdates.put("vitaminEGoal", 15f);
                                        userUpdates.put("currentVitaminE", 0f);
                                        userUpdates.put("is_logged_in", true);

                                        userRef.updateChildren(userUpdates).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Toast.makeText(CreateUserActivity.this, "User created successfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                                                intent.putExtra("userId", userId);
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

    private void setupGenderSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Male", "Female"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
        genderSpinner.setSelection(0); // Default selection is "Male"
    }
}
