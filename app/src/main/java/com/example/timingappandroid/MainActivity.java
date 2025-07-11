
package com.example.timingappandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView timerTextView;
    private EditText manualTimeInput;
    private Button startPauseButton, minusFiveButton, minusOneButton, plusOneButton, plusFiveButton, settingsButton;

    private Handler handler = new Handler();
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean isRunning = false;

    private final int[] stopTimes = {1800, 3600, 3900, 4200, 4500, 4800}; // 30:00, 60:00, 65:00, 70:00, 75:00, 80:00 in seconds
    private int nextStopIndex = 0;

    private int settingsClickCount = 0;
    private long lastSettingsClickTime = 0L;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int totalSecs = (int) (updatedTime / 1000);
            int mins = totalSecs / 60;
            int secs = totalSecs % 60;

            timerTextView.setText(String.format("%02d:%02d", mins, secs));

            if (nextStopIndex < stopTimes.length && totalSecs >= stopTimes[nextStopIndex]) {
                pauseTimer();
                flashRelay();
                nextStopIndex++;
            }

            if (isRunning) {
                handler.postDelayed(this, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerTextView = findViewById(R.id.timerTextView);
        startPauseButton = findViewById(R.id.startPauseButton);
        minusFiveButton = findViewById(R.id.minusFiveButton);
        minusOneButton = findViewById(R.id.minusOneButton);
        plusOneButton = findViewById(R.id.plusOneButton);
        plusFiveButton = findViewById(R.id.plusFiveButton);
        manualTimeInput = findViewById(R.id.manualTimeInput);
        settingsButton = findViewById(R.id.settingsButton);

        restoreState();

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - lastSettingsClickTime < 600) {
                    settingsClickCount++;
                } else {
                    settingsClickCount = 1;
                }
                lastSettingsClickTime = now;
                if (settingsClickCount >= 5) {
                    settingsClickCount = 0;
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            }
        });

        startPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        minusFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustTime(-5000);
            }
        });

        minusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustTime(-1000);
            }
        });

        plusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustTime(1000);
            }
        });

        plusFiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustTime(5000);
            }
        });

        manualTimeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    updateTimeFromInput(manualTimeInput.getText().toString().trim());
                }
            }
        });

        manualTimeInput.setOnEditorActionListener((v, actionId, event) -> {
            updateTimeFromInput(manualTimeInput.getText().toString().trim());
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreState();
    }

    private void restoreState() {
        SharedPreferences timerPrefs = getSharedPreferences("timer_state", MODE_PRIVATE);
        timeSwapBuff = timerPrefs.getLong("timeSwapBuff", 0L);
        startTime = timerPrefs.getLong("startTime", System.currentTimeMillis());
        isRunning = timerPrefs.getBoolean("isRunning", false);
        nextStopIndex = timerPrefs.getInt("nextStopIndex", 0);

        if (isRunning) {
            updatedTime = timeSwapBuff + (System.currentTimeMillis() - startTime);
            startPauseButton.setText("Pause");
            handler.postDelayed(updateTimerThread, 0);
        } else {
            updatedTime = timeSwapBuff;
            handler.removeCallbacks(updateTimerThread);
            startPauseButton.setText("Start");
        }

        int secs = (int) (updatedTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        timerTextView.setText(String.format("%02d:%02d", mins, secs));
        updateNextStopIndex(updatedTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences timerPrefs = getSharedPreferences("timer_state", MODE_PRIVATE);
        timerPrefs.edit()
                .putLong("startTime", startTime)
                .putLong("timeSwapBuff", timeSwapBuff)
                .putBoolean("isRunning", isRunning)
                .putInt("nextStopIndex", nextStopIndex)
                .apply();
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimerThread, 0);
        startPauseButton.setText("Pause");
        isRunning = true;
    }

    private void pauseTimer() {
        timeSwapBuff += timeInMilliseconds;
        handler.removeCallbacks(updateTimerThread);
        startPauseButton.setText("Start");
        isRunning = false;
    }

    private void adjustTime(long adjustment) {
        long current = isRunning ?
                timeSwapBuff + (System.currentTimeMillis() - startTime)
                : updatedTime;
        long newTime = current + adjustment;

        for (int stopTime : stopTimes) {
            long boundary = stopTime * 1000L;
            if (current < boundary && newTime >= boundary) {
                newTime = boundary;
            } else if (current > boundary && newTime <= boundary) {
                newTime = boundary;
            }
        }

        if (newTime < 0) {
            newTime = 0;
        }

        if (newTime / 1000 > 4800) { // 80:00
            newTime = 4800000;
        }

        updatedTime = newTime;
        if (isRunning) {
            timeSwapBuff = updatedTime - (System.currentTimeMillis() - startTime);
        } else {
            timeSwapBuff = updatedTime;
        }

        int secs = (int) (updatedTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        timerTextView.setText(String.format("%02d:%02d", mins, secs));

        updateNextStopIndex(updatedTime);
    }

    private void updateTimeFromInput(String value) {
        long millis = parseTimeInput(value);
        if (millis >= 0) {
            if (millis > 4800000) {
                millis = 4800000;
            }
            updatedTime = millis;
            timeSwapBuff = updatedTime;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            timerTextView.setText(String.format("%02d:%02d", mins, secs));
            updateNextStopIndex(updatedTime);
        }
    }


    private long parseTimeInput(String value) {
        if (value.contains(":")) {
            String[] parts = value.split(":");
            if (parts.length == 2) {
                try {
                    int mins = Integer.parseInt(parts[0]);
                    int secs = Integer.parseInt(parts[1]);
                    return (mins * 60L + secs) * 1000L;
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        } else {
            try {
                int secs = Integer.parseInt(value);
                return secs * 1000L;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    private void updateNextStopIndex(long currentMillis) {
        int currentSecs = (int) (currentMillis / 1000);
        int index = stopTimes.length; // default if past last stop
        for (int i = 0; i < stopTimes.length; i++) {
            if (currentSecs < stopTimes[i]) {
                index = i;
                break;
            }
        }
        nextStopIndex = index;
    }

    private void flashRelay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
                String ip1 = prefs.getString("shelly_ip_1", "192.168.1.200");
                String ip2 = prefs.getString("shelly_ip_2", "192.168.1.201");
                String[] ips = {ip1, ip2};
                // Trigger all relays on first
                for (String shellyIp : ips) {
                    if (shellyIp == null || shellyIp.isEmpty()) continue;
                    try {
                        URL url = new URL("http://" + shellyIp + "/color/0?turn=on&red=255&green=0&blue=0");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        try {
                            conn.getResponseCode();
                        } finally {
                            conn.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Keep the relays on for 5 seconds before turning them off
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Now turn all relays off
                for (String shellyIp : ips) {
                    if (shellyIp == null || shellyIp.isEmpty()) continue;
                    try {
                        URL url = new URL("http://" + shellyIp + "/color/0?turn=off");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        try {
                            conn.getResponseCode();
                        } finally {
                            conn.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
