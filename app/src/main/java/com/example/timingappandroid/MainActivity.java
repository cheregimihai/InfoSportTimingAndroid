
package com.example.timingappandroid;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startPauseButton, minusFiveButton, minusOneButton, plusOneButton, plusFiveButton;

    private Handler handler = new Handler();
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private boolean isRunning = false;

    private final int[] stopTimes = {1800, 3600, 3900, 4200, 4500, 4800}; // 30:00, 60:00, 65:00, 70:00, 75:00, 80:00 in seconds

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = System.currentTimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;

            timerTextView.setText(String.format("%02d:%02d", mins, secs));

            for (int stopTime : stopTimes) {
                if (secs == stopTime) {
                    pauseTimer();
                    flashRelay();
                }
            }

            handler.postDelayed(this, 0);
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
        long newTime = updatedTime + adjustment;

        if (newTime < 0) {
            newTime = 0;
        }

        for (int stopTime : stopTimes) {
            if (updatedTime < stopTime * 1000 && newTime >= stopTime * 1000) {
                newTime = stopTime * 1000;
            }
        }

        if (newTime / 1000 > 4800) { // 80:00
            newTime = 4800000;
        }

        updatedTime = newTime;
        int secs = (int) (updatedTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        timerTextView.setText(String.format("%02d:%02d", mins, secs));
        timeSwapBuff = updatedTime;
    }

    private void flashRelay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Replace with your Shelly device's IP address
                    String shellyIp = "192.168.1.200"; 
                    URL url = new URL("http://" + shellyIp + "/relay/0?turn=on");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.disconnect();

                    Thread.sleep(1000);

                    url = new URL("http://" + shellyIp + "/relay/0?turn=off");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.disconnect();

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
