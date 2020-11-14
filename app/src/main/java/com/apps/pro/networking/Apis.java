package com.apps.pro.networking;

import com.apps.pro.models.RecognizeResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Apis {

    @Multipart
    @POST("recognize")
    Call<RecognizeResponse> recognize(@Part MultipartBody.Part image);

    @Multipart
    @POST("add")
    Call<RecognizeResponse> register(
            @Part MultipartBody.Part image1,
            @Part MultipartBody.Part image2,
            @Part("id") RequestBody id,
            @Part("name") RequestBody name,
            @Part MultipartBody.Part image3
    );
}
