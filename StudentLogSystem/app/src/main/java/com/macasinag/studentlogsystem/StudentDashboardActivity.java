package com.macasinag.studentlogsystem;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentDashboardActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private String studentNo;
    private TextView tvNoLogs, tvWelcomeName, tvWelcomeProgramYear;
    private LinearLayout layoutLogsContainer;
    private boolean isDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        prefs = getSharedPreferences("StudentLogPrefs", MODE_PRIVATE);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkMode = (currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        View rootLayout = findViewById(R.id.dashboardRoot);
        CardView cardProfile = findViewById(R.id.cardProfile);
        CardView cardActions = findViewById(R.id.cardActions);
        CardView cardHistory = findViewById(R.id.cardHistory);

        TextView tvDashboardTitle = findViewById(R.id.tvDashboardTitle);
        TextView tvDashboardStudentNo = findViewById(R.id.tvDashboardStudentNo);
        TextView tvDashboardLastname = findViewById(R.id.tvDashboardLastname);
        TextView tvDashboardFirstname = findViewById(R.id.tvDashboardFirstname);
        TextView tvDashboardMiddlename = findViewById(R.id.tvDashboardMiddlename);
        TextView tvDashboardProgram = findViewById(R.id.tvDashboardProgram);
        TextView tvDashboardYearLevel = findViewById(R.id.tvDashboardYearLevel);
        TextView tvHeadingActions = findViewById(R.id.tvHeadingActions);
        TextView tvHeadingHistory = findViewById(R.id.tvHeadingHistory);

        this.tvWelcomeName = findViewById(R.id.tvWelcomeName);
        this.tvWelcomeProgramYear = findViewById(R.id.tvWelcomeProgramYear);
        this.tvNoLogs = findViewById(R.id.tvNoLogs);
        this.layoutLogsContainer = findViewById(R.id.layoutLogsContainer);

        Button btnTimeIn = findViewById(R.id.btnTimeIn);
        Button btnTimeOut = findViewById(R.id.btnTimeOut);
        Button btnLogout = findViewById(R.id.btnLogout);
        ImageButton btnDarkMode = findViewById(R.id.btnDarkMode);

        if (isDarkMode) {
            if (rootLayout != null) rootLayout.setBackgroundResource(R.drawable.gradient_blue_to_dark);
            if (cardProfile != null) cardProfile.setCardBackgroundColor(0xD91C2541);
            if (cardActions != null) cardActions.setCardBackgroundColor(0xD91C2541);
            if (cardHistory != null) cardHistory.setCardBackgroundColor(0xD91C2541);

            if (tvDashboardTitle != null) tvDashboardTitle.setTextColor(0xFF38BDF8);
            if (tvWelcomeName != null) tvWelcomeName.setTextColor(Color.WHITE);
            if (tvDashboardStudentNo != null) tvDashboardStudentNo.setTextColor(0xFF38BDF8);
            if (tvDashboardLastname != null) tvDashboardLastname.setTextColor(Color.WHITE);
            if (tvDashboardFirstname != null) tvDashboardFirstname.setTextColor(Color.WHITE);
            if (tvDashboardMiddlename != null) tvDashboardMiddlename.setTextColor(Color.WHITE);
            if (tvDashboardProgram != null) tvDashboardProgram.setTextColor(0xFF38BDF8);
            if (tvDashboardYearLevel != null) tvDashboardYearLevel.setTextColor(0xFF38BDF8);
            if (tvHeadingActions != null) tvHeadingActions.setTextColor(0xFF38BDF8);
            if (tvHeadingHistory != null) tvHeadingHistory.setTextColor(0xFF38BDF8);

            if (btnTimeIn != null) btnTimeIn.setBackgroundColor(0xFF0284C7);
            if (btnTimeOut != null) btnTimeOut.setBackgroundColor(0xFF0369A1);
        } else {
            if (rootLayout != null) rootLayout.setBackgroundResource(R.drawable.gradient_blue_to_white);
            if (cardProfile != null) cardProfile.setCardBackgroundColor(0xF8FFFFFF);
            if (cardActions != null) cardActions.setCardBackgroundColor(0xF8FFFFFF);
            if (cardHistory != null) cardHistory.setCardBackgroundColor(0xF8FFFFFF);
        }

        if (btnDarkMode != null) {
            btnDarkMode.setOnClickListener(v -> {
                if (isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
            });
        }

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

                    if (tvWelcomeName != null) tvWelcomeName.setText("Welcome, " + firstname + " " + lastname);
                    if (tvWelcomeProgramYear != null) tvWelcomeProgramYear.setText(program + " • " + yearLevel);

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

                if (isDarkMode) {
                    logView.setTextColor(0xFF38BDF8);
                    logView.setBackgroundColor(0x331C2541);
                } else {
                    logView.setTextColor(0xFF1A237E);
                    logView.setBackgroundColor(0x0A0D47A1);
                }

                logView.setPadding(20, 14, 20, 14);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 4, 0, 8);
                logView.setLayoutParams(params);

                layoutLogsContainer.addView(logView);
            }
        }
    }
}
