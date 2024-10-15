package edu.northeastern.yummycusine;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class UploadRecipeActivity extends AppCompatActivity {
    private EditText editRecipeName, editIngredients, editInstructions;
    private Button btnUploadImage, btnSubmitRecipe;
    private Uri imageUri;  // URI for uploaded image

    // Create an ActivityResultLauncher
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Handle the returned Uri
                    imageUri = uri;
                    btnUploadImage.setText("Image Selected");
                }
            });

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        editRecipeName = findViewById(R.id.editRecipeName);
        editIngredients = findViewById(R.id.editIngredients);
        editInstructions = findViewById(R.id.editInstructions);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSubmitRecipe = findViewById(R.id.btnSubmitRecipe);

        btnUploadImage.setOnClickListener(v -> {
            // Open the image selector
            mGetContent.launch("image/*");
        });

        btnSubmitRecipe.setOnClickListener(v -> {
            String name = editRecipeName.getText().toString();
            String ingredients = editIngredients.getText().toString();
            String instructions = editInstructions.getText().toString();

            if (imageUri != null) {
                uploadRecipeToFirebase(name, ingredients, instructions, imageUri);
            } else {
                Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getDisplayName(); // Returns user's display name
        } else {
            return "Unknown User"; // Default or error case
        }
    }

    private void uploadRecipeToFirebase(String name, String ingredients, String instructions, Uri imageUri) {
        // Reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());

        // Upload image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Call function to save recipe details to Firestore
                    saveRecipeToFirestore(name, ingredients, instructions, imageUrl);
                }))
                .addOnFailureListener(e -> Toast.makeText(UploadRecipeActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveRecipeToFirestore(String name, String ingredients, String instructions, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = getUserName();
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("name", name);
        recipe.put("ingredients", ingredients);
        recipe.put("instructions", instructions);
        recipe.put("imageUrl", imageUrl);
        recipe.put("user", username);

        // Add a new document with a generated ID
        db.collection("recipes")
                .add(recipe)
                .addOnSuccessListener(documentReference -> Toast.makeText(UploadRecipeActivity.this, "Recipe Uploaded Successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(UploadRecipeActivity.this, "Error adding document: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
