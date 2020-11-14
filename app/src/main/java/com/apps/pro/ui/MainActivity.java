package com.apps.pro.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.pro.R;
import com.apps.pro.models.RecognizeResponse;
import com.apps.pro.networking.Apis;
import com.apps.pro.networking.RetrofitService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    CardView btnFace;
    ProgressBar progress;
    TextView tvName, tvId, tvMassage;
    View view;
    ImageView imgRegistration;
    TextToSpeech speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        btnFace = findViewById(R.id.btnCaptior);
        progress = findViewById(R.id.progress);
        tvName = findViewById(R.id.tvName);
        tvMassage = findViewById(R.id.tvMassage);
        imgRegistration = findViewById(R.id.imgRegistration);
        tvId = findViewById(R.id.tvId);
        view = findViewById(R.id.view);
        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.UK);
                }
            }
        });

        btnFace.setOnClickListener(e -> {
            view.setVisibility(View.GONE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera access is required!!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 10);
                return;
            }
            captionsFace();
        });
        imgRegistration.setOnClickListener(e -> {
            startActivity(new Intent(this, RegistrationActivity.class));
        });

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void captionsFace() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((ImageView) findViewById(R.id.imgFace)).setImageBitmap(imageBitmap);
            assert imageBitmap != null;
            uploadImageToServer(imageBitmap);
        }
    }

    private void uploadImageToServer(Bitmap imageBitmap) {
        File filesDir = this.getFilesDir();
        File imageFile = new File(filesDir, "face" + ".jpg");
        try {
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(imageFile));
            Log.i("TAG", "File" + imageFile.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        progress.setVisibility(View.VISIBLE);
        RequestBody requestFile = RequestBody.create(MediaType.parse("jpg"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
        RetrofitService.getClient().create(Apis.class).recognize(body).enqueue(new Callback<RecognizeResponse>() {
            @Override
            public void onResponse(Call<RecognizeResponse> call, Response<RecognizeResponse> response) {
                Log.i("TAG", response.body().toString());
                progress.setVisibility(View.GONE);
                if (!response.body().getData().getName().equals("Unknown") || response.body().getData().getName().equals("null")) {
                    view.setVisibility(View.VISIBLE);
                    tvMassage.setText(response.message());
                    tvName.setText("Name: " + response.body().getData().getName());
                    tvId.setText("ID: " + response.body().getData().getEid());
                    speech.speak("hello "+response.body().getData().getName()+"The time now is "+ Calendar.getInstance().getTime(), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Toast.makeText(MainActivity.this, "User Unknown", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                }
            }

            @Override
            public void onFailure(Call<RecognizeResponse> call, Throwable t) {
                Log.e("TAG", t.getMessage());

            }
        });
    }

}