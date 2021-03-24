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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class CameraView extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static String TAG = "MainActivity";
    JavaCameraView javaCameraView;
    Mat mRGBA, mRGBAT;
    int filterStrength;
    String segType;
    int segmentationStrength;
    int upperForCanny;
    int lowerForCanny;

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

        segType = getIntent().getStringExtra("segType");
        //--- WORKS ---Toast.makeText(getBaseContext(), "segType is " + segType, Toast.LENGTH_LONG).show();
        if(segType.equals("Canny")){
            upperForCanny = Integer.valueOf(getIntent().getStringExtra("cannyUpper"));
            lowerForCanny = Integer.valueOf(getIntent().getStringExtra("cannyLower"));
        }
        // --- WORKS ---Toast.makeText(getBaseContext(), "upper is " + String.valueOf(upperForCanny), Toast.LENGTH_LONG).show();
        // --- WORKS ---Toast.makeText(getBaseContext(), "lower is " + String.valueOf(lowerForCanny), Toast.LENGTH_LONG).show();
        // --- WORKS ---Toast.makeText(getBaseContext(), "segstrength is " + String.valueOf(segmentationStrength), Toast.LENGTH_LONG).show();
        filterStrength = ((Integer.valueOf(getIntent().getStringExtra("filterStrength")) * 2) + 1);
        segmentationStrength = ((Integer.valueOf(getIntent().getStringExtra("segmentationStrength")) * 2) + 1);
        // --- WORKS ---Toast.makeText(getBaseContext(), "filter strength is " + String.valueOf(filterStrength), Toast.LENGTH_LONG).show();

        javaCameraView = (JavaCameraView) findViewById(R.id.my_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
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
        org.opencv.core.Size s = new Size(filterStrength,filterStrength);
        //Imgproc.GaussianBlur(mat, mat, s, 2);

        if (segType.equals("Sobel")) {
            Imgproc.GaussianBlur(mat, mat, s, 0,0, Core.BORDER_DEFAULT);
            Mat matX= new Mat();
            Mat matY = new Mat();
            Mat absMatX = new Mat();
            Mat absMatY = new Mat();
            Imgproc.Sobel(mat, matX, CvType.CV_16S, 1, 0, segmentationStrength,1,0, Core.BORDER_DEFAULT);
            Imgproc.Sobel(mat, matY, CvType.CV_16S, 0, 1, segmentationStrength,1,0, Core.BORDER_DEFAULT);
            Core.convertScaleAbs(matX, absMatX);
            Core.convertScaleAbs(matY, absMatY);
            Core.addWeighted(absMatX, 0.5, absMatY, 0.5, 0, mat);
        }
        else if (segType.equals("Canny")) {
            Imgproc.Canny(mat, mat, lowerForCanny, upperForCanny,segmentationStrength, false);
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