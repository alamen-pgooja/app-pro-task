package com.apps.pro.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.apps.pro.R;
import com.apps.pro.models.RecognizeResponse;
import com.apps.pro.networking.Apis;
import com.apps.pro.networking.RetrofitService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity {
    ImageView addFace;
    EditText edtName, edtId;
    CardView btnRegister;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        addFace = findViewById(R.id.imgFace);
        edtName = findViewById(R.id.edtUserName);
        edtId = findViewById(R.id.edtUserId);
        btnRegister = findViewById(R.id.btnRegister);
        progress = findViewById(R.id.progress);
        addFace.setOnClickListener(e -> {
            captionsFace();
        });
        btnRegister.setOnClickListener(e -> {
            uploadImagesToServer();
        });
    }

    private void uploadImagesToServer() {
        progress.setVisibility(View.VISIBLE);
        RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, edtId.getText().toString());
        RequestBody name = RequestBody.create(okhttp3.MultipartBody.FORM, edtName.getText().toString());
        RequestBody requestFile = RequestBody.create(MediaType.parse("jpeg"), imageFile);
        RequestBody requestFile1 = RequestBody.create(MediaType.parse("jpeg"), imageFile);
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("jpeg"), imageFile);
        MultipartBody.Part image1 = MultipartBody.Part.createFormData("image1", imageFile.getName(), requestFile);
        MultipartBody.Part image2 = MultipartBody.Part.createFormData("image2", imageFile.getName(), requestFile1);
        MultipartBody.Part image3 = MultipartBody.Part.createFormData("image3", imageFile.getName(), requestFile2);
        RetrofitService.getClient().create(Apis.class).register(image1, image2,id, name,  image3).enqueue(new Callback<RecognizeResponse>() {
            @Override
            public void onResponse(Call<RecognizeResponse> call, Response<RecognizeResponse> response) {
                if (response.isSuccessful()) {
                    Log.i("TAG", response.body().toString());
                    progress.setVisibility(View.GONE);
                    Toast.makeText(RegistrationActivity.this, "User added successfully :)", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecognizeResponse> call, Throwable t) {
                Log.e("TAG", t.getMessage());
            }
        });
    }

    private void captionsFace() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, 10);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    File imageFile;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            addFace.setImageBitmap(imageBitmap);
            assert imageBitmap != null;
            File filesDir = this.getFilesDir();
            imageFile = new File(filesDir, "faceToAdd" + ".jpg");
            try {
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(imageFile));
                Log.i("TAG", "File" + imageFile.getName());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}