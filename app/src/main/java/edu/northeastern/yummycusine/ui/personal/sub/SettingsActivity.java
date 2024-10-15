package edu.northeastern.yummycusine.ui.personal.sub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import edu.northeastern.yummycusine.R;

public class SettingsActivity extends AppCompatActivity {

    RelativeLayout info_update_btn, notification_setting_btn, privacy_setting_btn;
    Button clean_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        clean_btn = findViewById(R.id.button_clean_data);
        //info_update_btn = findViewById(R.id.info_update_btn);
        //notification_setting_btn = findViewById(R.id.notification_setting_btn);
        //privacy_setting_btn = findViewById(R.id.privacy_setting_btn);

        clean_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCache(v);
            }
        });

        /*
        info_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PersonalInfoUpdateActivity.class);
                startActivity(intent);
            }
        });



        notification_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, NotificationSettingActivity.class);
                startActivity(intent);
            }
        });

        privacy_setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, PrivacySettingActivity.class);
                startActivity(intent);
            }
        });
        */
    }

    public void clearCache(View view) {
        try {
            File cacheDirectory = getCacheDir();
            deleteDir(cacheDirectory);
            Toast.makeText(this, "Cache cleared!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error clearing cache!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}