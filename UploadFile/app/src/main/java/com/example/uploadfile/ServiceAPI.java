package com.example.uploadfile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceAPI {

    String Key = "8828c3d24efb4be40ddb8a068b8b8ef9";
    String base_url = "https://api.imgbb.com/1/";

    Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();

    ServiceAPI serviceapi = new Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ServiceAPI.class);

    // Phương thức upload ảnh
    @Multipart
    @POST("upload")
    Call<UploadResponse> uploadImage(
            @Part("key") RequestBody apiKey, // API key
            @Part MultipartBody.Part image // Ảnh tải lên
    );
}
