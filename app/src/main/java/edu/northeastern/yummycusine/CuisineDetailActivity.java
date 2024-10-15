package edu.northeastern.yummycusine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CuisineDetailActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech textToSpeech;
    private Button speakButton;
    private TextView detailInstructions;
    private Button btnLike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine_detail);
        detailInstructions = findViewById(R.id.detailInstructions);
        textToSpeech = new TextToSpeech(this, this);
        speakButton = findViewById(R.id.speakButton);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String user = intent.getStringExtra("user");
        String ingredients = intent.getStringExtra("ingredients");
        String instructions = intent.getStringExtra("instructions");
        String imageUrl = intent.getStringExtra("imageUrl");

        // Create a Cuisine object from the intent data
        Cuisine cuisine = new Cuisine(name, user, ingredients, instructions, imageUrl);

        btnLike = findViewById(R.id.btnLike);
        setButtonText(cuisine);
        btnLike.setOnClickListener(v -> toggleLikeRecipe(cuisine));

        ImageView detailImage = findViewById(R.id.detailImage);
        TextView detailName = findViewById(R.id.detailName);
        TextView detailUser = findViewById(R.id.detailUserName);
        TextView detailIngredients = findViewById(R.id.detailIngredients);
        TextView detailInstructions = findViewById(R.id.detailInstructions);

        // Get the passed data from intent
        detailName.setText(name);
        detailUser.setText("Uploaded By: " + user);
        detailIngredients.setText(ingredients);
        detailInstructions.setText(instructions);
        Glide.with(this).load(imageUrl).into(detailImage);
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakButton.setEnabled(true);
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }
    private void speakOut() {
        String text = detailInstructions.getText().toString();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
    @Override
    public void onDestroy() {
        // 关闭TextToSpeech
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void setButtonText(Cuisine cuisine) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference likedRef = db.collection("users").document(user.getUid())
                    .collection("likedRecipes").document(cuisine.getName());
            likedRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    btnLike.setText("Unlike");
                }
            });
        }
    }

    private void toggleLikeRecipe(Cuisine cuisine) { // Pass the entire Cuisine object
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference likedRef = db.collection("users").document(user.getUid())
                    .collection("likedRecipes").document(cuisine.getName()); // Using recipe name as document ID for simplicity

            likedRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    likedRef.delete(); // If already liked, unlike it
                    btnLike.setText("Like");
                } else {
                    likedRef.set(cuisine); // Store the whole cuisine object
                    btnLike.setText("Unlike");
                }
            });
        } else {
            startActivity(new Intent(this, LoginActivity.class)); // If not logged in, go to login
        }
    }

}