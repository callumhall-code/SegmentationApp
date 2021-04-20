package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SegmentationChoice extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String filterStrength = "1"; //defaults to 1, so filter will work if not selected
    String segmentationStrength = "1";
    Boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segmentation_choice);

        //Whether to lead to camera view or image view
        Intent intent = getIntent();
        isCamera = intent.getBooleanExtra("isCamera", false);

        Toast.makeText(this, "Is Camera is " + isCamera.toString(), Toast.LENGTH_LONG).show();

        Spinner filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
        Spinner strengthSpinner = (Spinner) findViewById(R.id.strength_spinner);
        Button beginSobel = findViewById(R.id.sobel_button);
        Button beginCanny = findViewById(R.id.canny_button);
        Button beginThresh = findViewById(R.id.thresh_button);
        Button beginDynamic = findViewById(R.id.dynamic_button);

        beginCanny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cannyIntent = new Intent(SegmentationChoice.this, CannySelection.class);
                cannyIntent.putExtra("filterStrength", filterStrength);
                cannyIntent.putExtra("segmentationStrength", segmentationStrength);
                cannyIntent.putExtra("isCamera", isCamera);
                startActivity(cannyIntent);
            }
        });

        beginSobel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sobelIntent;
                if (isCamera) {
                    sobelIntent = new Intent(SegmentationChoice.this, CameraView.class);
                }
                else {
                    sobelIntent = new Intent(SegmentationChoice.this, ImageSelect.class);
                }
                sobelIntent.putExtra("filterStrength", filterStrength);
                sobelIntent.putExtra("segmentationStrength", segmentationStrength);
                sobelIntent.putExtra("segType", "Sobel");
                startActivity(sobelIntent);
            }
        });

        beginThresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent threshIntent = new Intent(SegmentationChoice.this, ThresholdChoice.class);
                threshIntent.putExtra("filterStrength", filterStrength);
                threshIntent.putExtra("isCamera", isCamera);
                startActivity(threshIntent);
            }
        });

        //Loads values into array
        List<String> strengthList = new ArrayList<String>();
        for(int i = 1; i<=10; i++) {
            strengthList.add(String.valueOf(i));
        }
        ArrayAdapter<String> strengthAdapter = new ArrayAdapter<String>(SegmentationChoice.this, android.R.layout.simple_list_item_1, strengthList);

        //loads array values into spinner
        filterSpinner.setAdapter(strengthAdapter);
        strengthSpinner.setAdapter(strengthAdapter);

        //sets default position of spinner to not in use
        filterSpinner.setSelection(0, false);
        strengthSpinner.setSelection(0, false);

        //listener to act when spinner item selected
        filterSpinner.setOnItemSelectedListener(this);
        strengthSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
        int id = parent.getId();

        switch (id) {
            case R.id.filter_spinner: {
                filterStrength = parent.getSelectedItem().toString();
                Toast.makeText(this, "Filter is " + filterStrength, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.strength_spinner: {
                segmentationStrength = parent.getSelectedItem().toString();
                Toast.makeText(this, "Segmentation strength is " + segmentationStrength, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}