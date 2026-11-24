package com.macasinag.studentlogsystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentDashboardActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private String studentNo;
    private TextView tvNoLogs, tvWelcomeName, tvWelcomeProgramYear;
    private LinearLayout layoutLogsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        prefs = getSharedPreferences("StudentLogPrefs", MODE_PRIVATE);

        TextView tvDashboardStudentNo = findViewById(R.id.tvDashboardStudentNo);
        TextView tvDashboardLastname = findViewById(R.id.tvDashboardLastname);
        TextView tvDashboardFirstname = findViewById(R.id.tvDashboardFirstname);
        TextView tvDashboardMiddlename = findViewById(R.id.tvDashboardMiddlename);
        TextView tvDashboardProgram = findViewById(R.id.tvDashboardProgram);
        TextView tvDashboardYearLevel = findViewById(R.id.tvDashboardYearLevel);

        this.tvWelcomeName = findViewById(R.id.tvWelcomeName);
        this.tvWelcomeProgramYear = findViewById(R.id.tvWelcomeProgramYear);
        this.tvNoLogs = findViewById(R.id.tvNoLogs);
        this.layoutLogsContainer = findViewById(R.id.layoutLogsContainer);

        Button btnTimeIn = findViewById(R.id.btnTimeIn);
        Button btnTimeOut = findViewById(R.id.btnTimeOut);
        Button btnLogout = findViewById(R.id.btnLogout);

        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            String userData = prefs.getString("user_" + username, null);
            if (userData != null) {
                // userData format: password|studentNo|lastname|firstname|middlename|program|yearLevel
                String[] parts = userData.split("\\|", -1);
                if (parts.length >= 7) {
                    studentNo = parts[1];
                    String lastname = parts[2];
                    String firstname = parts[3];
                    String program = parts[5];
                    String yearLevel = parts[6];

                    if (tvDashboardStudentNo != null) tvDashboardStudentNo.setText(studentNo);
                    if (tvDashboardLastname != null) tvDashboardLastname.setText(lastname);
                    if (tvDashboardFirstname != null) tvDashboardFirstname.setText(firstname);
                    if (tvDashboardMiddlename != null) tvDashboardMiddlename.setText(parts[4].isEmpty() ? "N/A" : parts[4]);
                    if (tvDashboardProgram != null) tvDashboardProgram.setText(program);
                    if (tvDashboardYearLevel != null) tvDashboardYearLevel.setText(yearLevel);

                    if (tvWelcomeName != null) tvWelcomeName.setText("Welcome, " + firstname);

                    String shortProgram = program;
                    if (shortProgram != null && shortProgram.contains(" (")) {
                        shortProgram = shortProgram.substring(0, shortProgram.indexOf(" ("));
                    }
                    if (tvWelcomeProgramYear != null) tvWelcomeProgramYear.setText(shortProgram + " • " + yearLevel);

                    loadLogHistory();
                }
            }
        }

        if (btnTimeIn != null) btnTimeIn.setOnClickListener(v -> recordLog("Time In"));
        if (btnTimeOut != null) btnTimeOut.setOnClickListener(v -> recordLog("Time Out"));
        if (btnLogout != null) btnLogout.setOnClickListener(v -> finish());
    }

    private void recordLog(String logType) {
        if (studentNo == null || studentNo.isEmpty()) {
            Toast.makeText(this, "Student number not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String logEntry = logType + "|" + timestamp;

        String existingLogs = prefs.getString("logs_" + studentNo, "");
        String updatedLogs = existingLogs.isEmpty() ? logEntry : existingLogs + "---" + logEntry;
        prefs.edit().putString("logs_" + studentNo, updatedLogs).apply();

        Toast.makeText(this, logType + " recorded successfully at " + timestamp, Toast.LENGTH_SHORT).show();
        loadLogHistory();
    }

    private void loadLogHistory() {
        if (studentNo == null) return;
        layoutLogsContainer.removeAllViews();

        String existingLogs = prefs.getString("logs_" + studentNo, "");
        if (existingLogs.isEmpty()) {
            tvNoLogs.setVisibility(View.VISIBLE);
            return;
        }

        tvNoLogs.setVisibility(View.GONE);
        String[] logEntries = existingLogs.split("---");
        // Display in reverse order (newest first)
        for (int i = logEntries.length - 1; i >= 0; i--) {
            String entry = logEntries[i];
            String[] parts = entry.split("\\|");
            if (parts.length >= 2) {
                String type = parts[0];
                String timestamp = parts[1];

                TextView logView = new TextView(this);
                logView.setText("🕒  [" + type + "]  " + timestamp);
                logView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                logView.setTextColor(0xFF0B2545);

                android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
                bg.setColor(0xFFF1F5F9);
                bg.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()));
                bg.setStroke(1, 0xFFE2E8F0);
                logView.setBackground(bg);

                int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
                logView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
                params.setMargins(0, marginPx, 0, marginPx);
                logView.setLayoutParams(params);

                layoutLogsContainer.addView(logView);
            }
        }
    }
}
