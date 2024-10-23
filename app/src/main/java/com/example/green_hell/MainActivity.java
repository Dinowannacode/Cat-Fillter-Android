package com.example.green_hell;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.example.green_hell.itemCustom.ItemForEars;
import com.example.green_hell.itemCustom.ItemForEyes;
import com.example.green_hell.itemCustom.ItemForNose;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.green_hell.Network.ApiService;
import com.example.green_hell.Network.Network;
public class MainActivity extends AppCompatActivity {
    private HorizontalScrollView currentlyVisibleScrollView = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button buttonUpload;
    private ApiService apiService;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private HorizontalScrollView horizontalScrollView_Eyes;
    private HorizontalScrollView horizontalScrollView_Nose;
    private HorizontalScrollView horizontalScrollView_Ears;

    private ItemForEyes itemForEyes;
    private ItemForNose itemForNose;
    private ItemForEars itemForEars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestStoragePermission();

        // Liên kết các thành phần UI
        horizontalScrollView_Eyes = findViewById(R.id.horizontalScrollView_Eyes);
        horizontalScrollView_Nose = findViewById(R.id.horizontalScrollView_Nose);
        horizontalScrollView_Ears = findViewById(R.id.horizontalScrollView_Ears);
        imageView = findViewById(R.id.imageView1);
        buttonUpload = findViewById(R.id.buttonUpload);

        // Liên kết ItemForEyes từ layout XML
        itemForEyes = findViewById(R.id.itemForEyes);
        itemForEyes.setOriginalImageView(imageView);

        // Liên kết ItemForNose từ layout XML
        itemForNose = findViewById(R.id.itemForNose);
        itemForNose.setOriginalImageView(imageView);

        // Liên kết ItemForEars từ layout XML
        itemForEars = findViewById(R.id.itemForEars);
        itemForEars.setOriginalImageView(imageView);

        // Tạo đối tượng ApiService
        apiService = Network.getApiService();

        // Thiết lập các sự kiện nhấn nút
        buttonUpload.setOnClickListener(view -> openFileChooser());
        findViewById(R.id.cardView1).setOnClickListener(view -> showItemsForCard(1));
        findViewById(R.id.cardView2).setOnClickListener(view -> showItemsForCard(2));
        findViewById(R.id.cardView3).setOnClickListener(view -> showItemsForCard(3));
        findViewById(R.id.cardView4).setOnClickListener(view -> showItemsForCard(4));

        // Thiết lập lựa chọn cho class Eyes
        setUpGlassesSelection();

        // Thiết lập lựa chọn cho class Nose
        setUpNoseSelection();
        // Thiết lập lựa chọn cho class Ears
        setUpEarsSelection();
    }



    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                uploadImage(imageUri);
                Toast.makeText(this, "Your cat is so cute !!!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        try {
            // Bước 1: Chuyển đổi Uri thành Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Bước 2: Thay đổi kích thước Bitmap
            int newWidth = 640;  // kích thước mong muốn
            int newHeight = 640; // kích thước mong muốn
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            // Bước 3: Chuyển đổi Bitmap thành File
            File file = new File(getCacheDir(), "resized_image.jpg"); // lưu trữ tệp ảnh trong bộ nhớ cache
            FileOutputStream out = new FileOutputStream(file);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            // Bước 4: Gửi tệp lên server
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            Call<ResponseBody> call = apiService.uploadImage(body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBodyString = response.body().string();
                            Log.d("Network", "Upload successful: " + responseBodyString);
                            Gson gson = new Gson();
                            List<DetectionResult> detectionResults = gson.fromJson(responseBodyString, new TypeToken<List<DetectionResult>>(){}.getType());
                            Log.d("Network", "Số lượng kết quả phát hiện: " + detectionResults.size());
                            handleApiResponse(detectionResults);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("Network", "Upload failed: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Network", "Error: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleApiResponse(List<DetectionResult> detectionResults) {
        if (detectionResults == null || detectionResults.isEmpty()) {
            Log.d("Network", "Không có kết quả phát hiện nào được trả về.");
            return;
        }

        for (DetectionResult result : detectionResults) {
            Log.d("Network", "Class: " + result.getClassName());
            Log.d("Network", "Confidence: " + result.getConfidence());
            Log.d("Network", "Bounding Box: " + result.getBbox());
            itemForEyes.addDetectionResult(result);
            itemForNose.addDetectionResult(result);
            itemForEars.addDetectionResult(result);

        }
    }



    private void showItemsForCard(int cardIndex) {
        HorizontalScrollView selectedScrollView = null;
        switch (cardIndex) {
            case 1:
                selectedScrollView = horizontalScrollView_Eyes;
                itemForEyes.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Showing Eye Items", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                selectedScrollView = horizontalScrollView_Nose;
                itemForNose.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Showing Nose Items", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                selectedScrollView = horizontalScrollView_Ears;
                itemForEars.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Showing Ear Items", Toast.LENGTH_SHORT).show();
                break;
        }

        if (currentlyVisibleScrollView != null && currentlyVisibleScrollView != selectedScrollView) {
            currentlyVisibleScrollView.setVisibility(View.GONE);
            Toast.makeText(this, "Hide Items", Toast.LENGTH_SHORT).show();
        }

        if (selectedScrollView != null) {
            if (selectedScrollView.getVisibility() == View.VISIBLE) {
                selectedScrollView.setVisibility(View.GONE);
                currentlyVisibleScrollView = null;
            } else {
                selectedScrollView.setVisibility(View.VISIBLE);
                currentlyVisibleScrollView = selectedScrollView;
            }
        }
    }

    private void setUpGlassesSelection() {
        findViewById(R.id.glassesItem01).setOnClickListener(v -> {
            itemForEyes.setOverlayBitmap(R.drawable.glasses_01);
            itemForEyes.invalidate(); // Yêu cầu vẽ lại
            Toast.makeText(MainActivity.this, "Item for eyes selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.glassesItem02).setOnClickListener(v -> {
            itemForEyes.setOverlayBitmap(R.drawable.glasses_02);
            itemForEyes.invalidate(); // Yêu cầu vẽ lại
            Toast.makeText(MainActivity.this, "Item for eyes selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.glassesItem03).setOnClickListener(v -> {
            itemForEyes.setOverlayBitmap(R.drawable.glasses_03);
            itemForEyes.invalidate(); // Yêu cầu vẽ lại
            Toast.makeText(MainActivity.this, "Item for eyes selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.glassesItem04).setOnClickListener(v -> {
            itemForEyes.setOverlayBitmap(R.drawable.glasses_04);
            itemForEyes.invalidate(); // Yêu cầu vẽ lại
            Toast.makeText(MainActivity.this, "Item for eyes selected.", Toast.LENGTH_SHORT).show();
        });
    }

    private void setUpNoseSelection() {
        findViewById(R.id.nose_01).setOnClickListener(v -> {
            itemForNose.setOverlayBitmap(R.drawable.nose_item01);
            itemForNose.invalidate();
            Toast.makeText(MainActivity.this, "Item for nose selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.nose_02).setOnClickListener(v -> {
            itemForNose.setOverlayBitmap(R.drawable.nose_item02);
            itemForNose.invalidate();
            Toast.makeText(MainActivity.this, "Item for nose selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.nose_03).setOnClickListener(v -> {
            itemForNose.setOverlayBitmap(R.drawable.nose_item03);
            itemForNose.invalidate();
            Toast.makeText(MainActivity.this, "Item for nose selected.", Toast.LENGTH_SHORT).show();
        });
    }

    private void setUpEarsSelection() {
        findViewById(R.id.ears_01).setOnClickListener(v -> {
            itemForEars.setOverlayBitmap(R.drawable.ears_item01);
            Toast.makeText(MainActivity.this, "Item for ears selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.ears_02).setOnClickListener(v -> {
            itemForEars.setOverlayBitmap(R.drawable.ears_item02);
            Toast.makeText(MainActivity.this, "Item for ears selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.ears_03).setOnClickListener(v -> {
            itemForEars.setOverlayBitmap(R.drawable.ears_item03);
            Toast.makeText(MainActivity.this, "Item for ears selected.", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.ears_04).setOnClickListener(v -> {
            itemForEars.setOverlayBitmap(R.drawable.ears_item04);
            Toast.makeText(MainActivity.this, "Item for ears selected.", Toast.LENGTH_SHORT).show();
        });
    }


}
