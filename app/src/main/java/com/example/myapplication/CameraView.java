package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CameraView extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static String TAG = "MainActivity";
    JavaCameraView javaCameraView;
    Mat mRGBA, mRGBAT;
    int filterStrength;
    int edgeStrength;
    int dynamicStrength;
    int threshStrength;
    String segType;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(CameraView.this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    static {

        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV has been configured correctly.");
        }
        else {
            Log.d(TAG, "OpenCV is not working.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();

        edgeStrength = intent.getIntExtra("edgeStrength", 0);
        threshStrength = intent.getIntExtra("threshStrength", 0);
        dynamicStrength = intent.getIntExtra("dynamicStrength", 0);
        filterStrength = intent.getIntExtra("filterStrength", 0);
        segType = intent.getStringExtra("segType");

        javaCameraView = (JavaCameraView) findViewById(R.id.my_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);

        Toast.makeText(this,"Strength is" + String.valueOf(edgeStrength) + " Filter is " + String.valueOf(filterStrength), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat mat = inputFrame.gray();
        org.opencv.core.Size s = new Size((filterStrength * 2) + 1,filterStrength * 2 + 1);
        if (filterStrength > 0) {
            Imgproc.GaussianBlur(mat, mat, s, 2);
        }
        if (segType.equals("Sobel")) {
            Imgproc.Sobel(mat, mat, -1, 1, 1);
        }
        else if (segType.equals("Canny")) {
            Imgproc.Canny(mat, mat, 100, 80,3, false);
        }
        return mat;
        }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV has been configured correctly.");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
        else {
            Log.d(TAG, "OpenCV is not working.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0 , this, baseLoaderCallback);
        }
    }
}