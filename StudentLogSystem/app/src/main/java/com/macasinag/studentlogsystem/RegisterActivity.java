package com.macasinag.studentlogsystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etStudentNo, etLastname, etFirstname, etMiddlename, etRegUsername, etRegPassword;
    private AutoCompleteTextView autoCompleteProgram, autoCompleteYearLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etStudentNo = findViewById(R.id.etStudentNo);
        etLastname = findViewById(R.id.etLastname);
        etFirstname = findViewById(R.id.etFirstname);
        etMiddlename = findViewById(R.id.etMiddlename);
        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        autoCompleteProgram = findViewById(R.id.autoCompleteProgram);
        autoCompleteYearLevel = findViewById(R.id.autoCompleteYearLevel);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Setup Program Dropdown
        String[] programs = {"Bachelor of Science in Computer Science (BSCS)", "Bachelor of Science in Information Technology (BSIT)"};
        ArrayAdapter<String> programAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, programs);
        autoCompleteProgram.setAdapter(programAdapter);

        // Setup Year Level Dropdown
        String[] yearLevels = {"1st Year", "2nd Year", "3rd Year", "4th Year"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, yearLevels);
        autoCompleteYearLevel.setAdapter(yearAdapter);

        btnRegister.setOnClickListener(v -> {
            String studentNo = etStudentNo.getText() != null ? etStudentNo.getText().toString().trim() : "";
            String lastname = etLastname.getText() != null ? etLastname.getText().toString().trim() : "";
            String firstname = etFirstname.getText() != null ? etFirstname.getText().toString().trim() : "";
            String middlename = etMiddlename.getText() != null ? etMiddlename.getText().toString().trim() : "";
            String username = etRegUsername.getText() != null ? etRegUsername.getText().toString().trim() : "";
            String password = etRegPassword.getText() != null ? etRegPassword.getText().toString().trim() : "";
            String program = autoCompleteProgram.getText() != null ? autoCompleteProgram.getText().toString() : "";
            String yearLevel = autoCompleteYearLevel.getText() != null ? autoCompleteYearLevel.getText().toString() : "";

            if (studentNo.isEmpty() || lastname.isEmpty() || firstname.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("StudentLogPrefs", MODE_PRIVATE);
            if (prefs.contains("user_" + username)) {
                Toast.makeText(RegisterActivity.this, "Registration Failed. Username already exists.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save user data: password|studentNo|lastname|firstname|middlename|program|yearLevel
            String userData = password + "|" + studentNo + "|" + lastname + "|" + firstname + "|" + middlename + "|" + program + "|" + yearLevel;
            prefs.edit().putString("user_" + username, userData).apply();

            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBackToLogin.setOnClickListener(v -> finish());
    }
}
