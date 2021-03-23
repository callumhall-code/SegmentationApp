package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageSegment extends AppCompatActivity {

    private static final String TAG = "Loaded Image Segment";
    ImageView segmentedImageView;
    Uri imageUri;
    //Bitmap loadedImg;
    Bitmap segmentedImg;
    Mat mRgba;
    String segType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_segment);

        segType = getIntent().getStringExtra("segType");
        imageUri = getIntent().getParcelableExtra("ImagePath");
        Toast.makeText(this, "Path is " + imageUri.getPath() + " Segmentation type is " + segType, Toast.LENGTH_LONG).show();

        Button saveSeg = findViewById(R.id.saveSeg);
        saveSeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(segmentedImg);
            }
        });

    }

    static {

        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV has been configured correctly.");
        }
        else {
            Log.d(TAG, "OpenCV is not working.");
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS : {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mRgba = new Mat();
                    loadDisplayImg();
                }
                default: {
                    super.onManagerConnected(status);
                }
            }
        }
    };

    public void loadDisplayImg() {
        segmentedImageView = findViewById(R.id.segmentedImage);
        int filterStrength = getIntent().getIntExtra("filterStrength", 0);
        Toast.makeText(this, "Filter strength is " + String.valueOf(filterStrength), Toast.LENGTH_LONG).show();

        try {
            InputStream is = getContentResolver().openInputStream(imageUri);
            Bitmap loadedImg = BitmapFactory.decodeStream(is);
            Mat mat = new Mat();
            Utils.bitmapToMat(loadedImg, mat);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

            if (filterStrength > 0) {
                org.opencv.core.Size s = new Size((filterStrength * 2) + 1, filterStrength * 2 + 1);
                Imgproc.GaussianBlur(mat, mat, s, 2);
            }

            if (segType.equals("Sobel")) {
                Imgproc.Sobel(mat, mat, -1, 1,1);
            }
            else if (segType.equals("Canny")) {
                int lowerThresh = getIntent().getIntExtra("cannyLower", 50);
                int upperThresh = getIntent().getIntExtra("cannyUpper", 150);
                Toast.makeText(this, "Lower is " + String.valueOf(lowerThresh) + " Upper is " + String.valueOf(upperThresh), Toast.LENGTH_LONG).show();
                Imgproc.Canny(mat, mat, lowerThresh, upperThresh,3, false);
            }
            segmentedImg = Bitmap.createBitmap(mat.cols(),mat.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, segmentedImg);
            segmentedImageView.setImageBitmap(segmentedImg);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV has been configured correctly.");
            mLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
        else {
            Log.d(TAG, "OpenCV is not working.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0 , this, mLoaderCallback);
        }
    }

    private void saveImage(Bitmap bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            ContentValues values = contentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name));
            values.put(MediaStore.Images.Media.IS_PENDING, true);

            Uri uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, this.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    this.getContentResolver().update(uri, values, null, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + '/' + getString(R.string.app_name));

            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private ContentValues contentValues() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        }
        return values;
    }

    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}