package com.example.uploadfile;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private Button pickImgButton;
    private Button uploadButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileImageView = findViewById(R.id.profileImageView); // ImageView để hiển thị ảnh
        pickImgButton = findViewById(R.id.pickImg); // Nút chọn ảnh
        uploadButton = findViewById(R.id.upload); // Nút upload ảnh

        // Xử lý sự kiện khi chọn ảnh
        pickImgButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Xử lý sự kiện khi upload ảnh
        uploadButton.setOnClickListener(v -> {
            if (imageUri != null) {
                File file = new File(getRealPathFromURI(imageUri));
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), ServiceAPI.Key);

                // Thực hiện upload
                ServiceAPI.serviceapi.uploadImage(apiKey, body).enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                            Log.d("ketqua", "onResponse: " + response.body().getData().getDisplay_url());
                            // Hiển thị ảnh từ URL
                            Glide.with(MainActivity.this).load(response.body().getData().getDisplay_url()).into(profileImageView);
                        } else {
                            Toast.makeText(MainActivity.this, "Upload thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Có lỗi xảy ra: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Vui lòng chọn ảnh trước!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức lấy đường dẫn thực từ URI
    private String getRealPathFromURI(Uri uri) {
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            int columnIndex = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(profileImageView); // Hiển thị ảnh đã chọn
        }
    }
}
