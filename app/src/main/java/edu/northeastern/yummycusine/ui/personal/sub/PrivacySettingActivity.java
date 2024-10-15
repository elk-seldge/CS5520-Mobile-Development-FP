package edu.northeastern.yummycusine.ui.personal.sub;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import edu.northeastern.yummycusine.R;

public class PrivacySettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_privacy_setting);

        EditText permissionStatusEditText = findViewById(R.id.permission_status_edit_text);
        updatePermissionStatus(permissionStatusEditText);
    }

    private void updatePermissionStatus(EditText editText) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            editText.setText("Location Permission: Granted");
        } else {
            editText.setText("Location Permission: Denied");
        }
    }
}