package edu.northeastern.numad24su_group6;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateUserActivity extends AppCompatActivity {
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText emailInput;
    private EditText ageInput;
    private EditText genderInput;
    private Button createUserButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

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
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putString("email", email);
                    editor.putString("age", age);
                    editor.putString("gender", gender);
                    editor.apply();

                    Toast.makeText(CreateUserActivity.this, "User created successfully", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(CreateUserActivity.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
