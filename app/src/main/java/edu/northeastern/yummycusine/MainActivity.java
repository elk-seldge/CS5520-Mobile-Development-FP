package edu.northeastern.yummycusine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import edu.northeastern.yummycusine.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private Button btnLoginLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView btnDetectPicture = findViewById(R.id.detect_picture_btn);
        btnDetectPicture.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetectPicture.class);
            startActivity(intent);
        });



        ImageView searchView = findViewById(R.id.Search_btn);
        searchView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);  // Ensure you have a NavHostFragment in your layout

        // Finding TextViews and setting up listeners
        findViewById(R.id.home_text).setOnClickListener(view -> navController.navigate(R.id.navigation_home));
        findViewById(R.id.event_text).setOnClickListener(view -> navController.navigate(R.id.navigation_event));
        findViewById(R.id.favourite_text).setOnClickListener(view -> navController.navigate(R.id.navigation_favourite));
        findViewById(R.id.personal_text).setOnClickListener(view -> navController.navigate(R.id.navigation_personal));

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);

        FloatingActionButton fab = findViewById(R.id.btnUploadRecipe);
        fab.setOnClickListener(view -> {
            // Handle FAB click
            Intent intent = new Intent(MainActivity.this, UploadRecipeActivity.class);
            startActivity(intent);
        });
        System.out.println("setup success");

/*

        mAuth = FirebaseAuth.getInstance();
        btnLoginLogout = findViewById(R.id.btnLoginLogout);

        updateButtonUI(mAuth.getCurrentUser());

        btnLoginLogout.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // User is logged in, handle logout
                logoutUser();
            } else {
                // No user is logged in, handle login
                startActivity(new Intent(BHActivity.this, LoginActivity.class));
            }
        });

         */
    }


    /*
    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        updateButtonUI(null);
        Toast.makeText(BHActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void updateButtonUI(FirebaseUser user) {
        if (user != null) {
            btnLoginLogout.setText("Log Out");
        } else {
            btnLoginLogout.setText("Log In");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Update button text based on user authentication state
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateButtonUI(currentUser);
    }

     */
}
