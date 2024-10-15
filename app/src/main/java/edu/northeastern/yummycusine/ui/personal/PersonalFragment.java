package edu.northeastern.yummycusine.ui.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.northeastern.yummycusine.R;

public class PersonalFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth mAuth;
    RelativeLayout user_help_btn,about_btn,setting_btn, help_btn_logged_in, about_us_btn_logged_in,
            settings_btn_logged_in;
    Button loggin_btn, button_logout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    public static PersonalFragment newInstance() {
        return new PersonalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view;

        if (isUserLoggedIn()) {
            // Load logged-in layout
            view = inflater.inflate(R.layout.fragment_personal, container, false);

            // Find views
            ImageView appImageView = view.findViewById(R.id.app_img);
            TextView usernameTextView = view.findViewById(R.id.personal_username);
            TextView greetingTextView = view.findViewById(R.id.Greeting);

            // Set user-specific data
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // Set username and greeting
                usernameTextView.setText(user.getDisplayName());  // Assuming the name is available
                greetingTextView.setText(getString(R.string.hello_username, user.getDisplayName()));

                // Set the user's profile picture if available
                if (user.getPhotoUrl() != null) {
                    Glide.with(this)  // Make sure to include Glide dependency in your build.gradle
                            .load(user.getPhotoUrl())
                            .into(appImageView);
                } else {
                    appImageView.setImageResource(R.mipmap.personal_guest);  // A default placeholder
                }
            }

            // Additional initialization code for buttons and other views
            initializeLoggedInView(view);
        } else {
            // Load not logged-in layout
            view = inflater.inflate(R.layout.fragment_personal_not_logged_in, container, false);

            // Additional initialization code for buttons and other views
            initializeLoggedOutView(view);
        }

        return view;
    }

    private void initializeLoggedInView(View view) {
        // Initialize buttons and set onClick listeners
        //BH_btn = view.findViewById(R.id.BH_btn);
        help_btn_logged_in = view.findViewById(R.id.help_btn_logged_in);
        about_us_btn_logged_in = view.findViewById(R.id.about_us_btn_logged_in);
        settings_btn_logged_in = view.findViewById(R.id.settings_btn_logged_in);
        button_logout = view.findViewById(R.id.button_logout);

        //BH_btn.setOnClickListener(this);
        help_btn_logged_in.setOnClickListener(this);
        about_us_btn_logged_in.setOnClickListener(this);
        settings_btn_logged_in.setOnClickListener(this);
        button_logout.setOnClickListener(this);
    }

    private void initializeLoggedOutView(View view) {
        // Initialize buttons and set onClick listeners for not logged-in layout
        user_help_btn = view.findViewById(R.id.help_btn);
        about_btn = view.findViewById(R.id.about_us_btn);
        setting_btn = view.findViewById(R.id.settings_btn);
        loggin_btn = view.findViewById(R.id.button_loggin);

        user_help_btn.setOnClickListener(this);
        about_btn.setOnClickListener(this);
        setting_btn.setOnClickListener(this);
        loggin_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (isUserLoggedIn()) {
            if (v.getId() == R.id.help_btn_logged_in) {
                Intent intent1 = new Intent(getContext(), edu.northeastern.yummycusine.ui.personal.sub.UserHelpActivity.class);
                startActivity(intent1);
            } else if (v.getId() == R.id.about_us_btn_logged_in) {
                Intent intent2 = new Intent(getContext(), edu.northeastern.yummycusine.ui.personal.sub.AboutUsActivity.class);
                startActivity(intent2);
            } else if (v.getId() == R.id.settings_btn_logged_in) {
                Intent intent3 = new Intent(getContext(), edu.northeastern.yummycusine.ui.personal.sub.SettingsActivity.class);
                startActivity(intent3);
            } else if (v.getId() == R.id.button_logout) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // User is logged in, handle logout
                    logoutUser();
                }
                Intent intent4 = new Intent(getContext(), edu.northeastern.yummycusine.MainActivity.class);
                startActivity(intent4);
            }
        } else {
            if (v.getId() == R.id.help_btn) {
                Intent intent1 = new Intent(getContext(), edu.northeastern.yummycusine.ui.personal.sub.UserHelpActivity.class);
                startActivity(intent1);
            } else if (v.getId() == R.id.about_us_btn) {
                Intent intent2 = new Intent(getContext(), edu.northeastern.yummycusine.ui.personal.sub.AboutUsActivity.class);
                startActivity(intent2);
            } else if (v.getId() == R.id.settings_btn) {
                Intent intent3 = new Intent(getContext(), edu.northeastern.yummycusine.ui.personal.sub.SettingsActivity.class);
                startActivity(intent3);
            } else if (v.getId() == R.id.button_loggin) {
                Intent intent4 = new Intent(getContext(), edu.northeastern.yummycusine.LoginActivity.class);
                startActivity(intent4);
            }

        }
    }

    private boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

}