package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageSelect extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;

    Button selectImage;
    Button applySegmentation;
    ImageView loadedImage;
    Uri imageUri;
    Bitmap selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

        selectImage = findViewById(R.id.imgBtn);
        loadedImage = findViewById(R.id.loadedImage);
        applySegmentation = findViewById(R.id.applySeg);

        Intent intent = getIntent();

        String segType = intent.getStringExtra("segType");
        String filterStrength = intent.getStringExtra("filterStrength");
        String segmentationStrength = intent.getStringExtra("segmentationStrength");
        String cannyUpper = intent.getStringExtra("cannyUpper");
        String cannyLower = intent.getStringExtra("cannyLower");

        String threshVal = intent.getStringExtra("threshVal");
        String threshType = intent.getStringExtra("threshType");
        String maxBinary = intent.getStringExtra("maxBinary");

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        applySegmentation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageSelect.this, ImageSegment.class);
                intent.putExtra("ImagePath", imageUri);
                intent.putExtra("filterStrength", filterStrength);
                intent.putExtra("segmentationStrength", segmentationStrength);
                intent.putExtra("segType", segType);
                if(segType.equals("Canny")){
                    intent.putExtra("cannyLower", cannyLower);
                    intent.putExtra("cannyUpper", cannyUpper);
                }
                if(segType.equals("Threshold")){
                    intent.putExtra("threshVal", threshVal);
                    intent.putExtra("threshType", threshType);
                    intent.putExtra("maxBinary", maxBinary);
                }
                startActivity(intent);
            }
        });
    }

    public void selectImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                loadedImage.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ImageSelect.this, "No Image Selected", Toast.LENGTH_LONG).show();
            }
        }
    }
}