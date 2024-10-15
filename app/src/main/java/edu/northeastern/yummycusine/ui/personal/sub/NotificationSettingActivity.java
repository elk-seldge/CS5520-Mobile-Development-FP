package edu.northeastern.yummycusine.ui.personal.sub;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.yummycusine.R;

public class NotificationSettingActivity extends AppCompatActivity {

    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_setting);
        aSwitch = findViewById(R.id.switch_notification);
        loadSwitchState();

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSwitchState(isChecked);
            if (isChecked) {
                enableNotifications();
            } else {
                disableNotifications();
            }
        });
    }

    private void loadSwitchState() {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isChecked = preferences.getBoolean("NotifyEnabled", false);
        aSwitch.setChecked(isChecked);
    }

    private void saveSwitchState(boolean isChecked) {
        SharedPreferences preferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("NotifyEnabled", isChecked);
        editor.apply();
    }

    private void enableNotifications() {
        // Code to enable notifications
        Toast.makeText(this, "Notification Allowed", Toast.LENGTH_SHORT).show();
    }

    private void disableNotifications() {
        // Code to disable notifications
        Toast.makeText(this, "Notification Prohibited", Toast.LENGTH_SHORT).show();
    }
}