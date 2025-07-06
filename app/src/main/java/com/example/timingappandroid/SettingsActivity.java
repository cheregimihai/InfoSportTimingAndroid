package com.example.timingappandroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private EditText ip1EditText;
    private EditText ip2EditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ip1EditText = findViewById(R.id.ipAddress1EditText);
        ip2EditText = findViewById(R.id.ipAddress2EditText);
        saveButton = findViewById(R.id.saveButton);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        ip1EditText.setText(prefs.getString("shelly_ip_1", ""));
        ip2EditText.setText(prefs.getString("shelly_ip_2", ""));

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit()
                        .putString("shelly_ip_1", ip1EditText.getText().toString().trim())
                        .putString("shelly_ip_2", ip2EditText.getText().toString().trim())
                        .apply();
                finish();
            }
        });
    }
}
