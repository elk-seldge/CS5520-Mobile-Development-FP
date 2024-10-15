package edu.northeastern.yummycusine;
import android.Manifest;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetectPicture extends AppCompatActivity {
    private static final String TAG = "DetectPictureActivity";
    private ImageView imageView;
    private Uri currentPhotoUri = null;

    // Launcher for picking image from gallery
    private final ActivityResultLauncher<String> getContentLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::processImageUri
    );

    // Launcher for taking photo with camera
    private final ActivityResultLauncher<Uri> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    imageView.setImageURI(currentPhotoUri); // Display the image
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), currentPhotoUri);
                        uploadImageToCloudVision(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, "Error processing the image", e);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_picture);

        imageView = findViewById(R.id.imageView_preview);
        Button selectImageButton = findViewById(R.id.button_select_image);
        Button takePhotoButton = findViewById(R.id.button_take_photo);

        selectImageButton.setOnClickListener(v -> getContentLauncher.launch("image/*"));
        takePhotoButton.setOnClickListener(v -> takePhoto());
    }

    private void takePhoto() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            // Create file URI to save the image
            currentPhotoUri = createImageUri();
            if (currentPhotoUri != null) {
                takePictureLauncher.launch(currentPhotoUri); // Launch the camera with the file URI
            }
        }
    }

    private Uri createImageUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            Log.e(TAG, "Error creating file: " + imageFileName, e);
        }

        if (imageFile != null) {
            return FileProvider.getUriForFile(this,
                    "edu.northeastern.yummycusine.fileprovider",
                    imageFile);
        }
        return null;
    }

    private void processImageUri(Uri imageUri) {
        imageView.setImageURI(imageUri);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            uploadImageToCloudVision(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error reading image for uploading", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToCloudVision(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null. Cannot upload to Cloud Vision.");
            return;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        String jsonRequest = buildJsonRequest(encodedImage);
        RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyB_AcrloKOOg3aNuRg4_r4dwbdLfYii2gA")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonResponse = response.body().string();
                Log.d(TAG, "API Response: " + jsonResponse); // 添加这一行来打印整个响应内容

                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("responses");

                    if (jsonArray.length() > 0) {
                        JSONObject responseObj = jsonArray.getJSONObject(0);
                        JSONArray labels = responseObj.getJSONArray("labelAnnotations");
                        StringBuilder foodItems = new StringBuilder("Detected Food Items:\n");

                        for (int i = 0; i < labels.length(); i++) {
                            JSONObject label = labels.getJSONObject(i);
                            String description = label.getString("description");
                            double score = label.getDouble("score");

                            if (description.toLowerCase().contains("food") || score > 0.8) {
                                foodItems.append(description).append(" (Score: ").append(String.format("%.2f", score)).append(")\n");
                            }
                        }

                        runOnUiThread(() -> {
                            TextView resultView = findViewById(R.id.textView_result);
                            resultView.setText(foodItems.toString());
                        });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON response", e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to send request to Cloud Vision API", e);
            }
        });
    }

    private String buildJsonRequest(String encodedImage) {
        return "{"
                + "'requests': ["
                + "{'image': {'content': '" + encodedImage + "'},"
                + "'features': ["
                + "{'type': 'LABEL_DETECTION', 'maxResults': 10},"
                + "]}"
                + "]}";
    }
}
