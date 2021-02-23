package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import java.io.IOException;
import java.io.InputStream;

public class ImageSegment extends AppCompatActivity {

    private static final String TAG = "Loaded Image Segment";
    ImageView segmentedImage;
    Uri imageUri;
    Bitmap segImg;
    Mat mRgba;
    String segType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_segment2);

        segType = getIntent().getStringExtra("segType");
        imageUri = getIntent().getParcelableExtra("ImagePath");
        Toast.makeText(this, "Path is " + imageUri.getPath() + " Segmentation type is " + segType, Toast.LENGTH_LONG).show();

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
        segmentedImage = findViewById(R.id.segmentedImage);
        int filterStrength = getIntent().getIntExtra("filterStrength", 1);
        Toast.makeText(this, "Filter strength is " + String.valueOf(filterStrength), Toast.LENGTH_LONG).show();

        try {
            InputStream is = getContentResolver().openInputStream(imageUri);
            segImg = BitmapFactory.decodeStream(is);
            Mat mat = new Mat();
            Utils.bitmapToMat(segImg, mat);
            org.opencv.core.Size s = new Size((filterStrength * 2) + 1,filterStrength * 2 + 1);
            Imgproc.GaussianBlur(mat, mat, s, 2);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);

            if (segType.equals("Sobel")) {
                Imgproc.Sobel(mat, mat, -1, 1,1);
            }
            else if (segType.equals("Canny")) {
                Imgproc.Canny(mat, mat, 255 / 3, 255);
            }

            Bitmap bm = Bitmap.createBitmap(mat.cols(),mat.rows(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, bm);
            segmentedImage.setImageBitmap(bm);
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
}