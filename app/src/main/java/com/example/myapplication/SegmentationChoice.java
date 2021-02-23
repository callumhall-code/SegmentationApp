package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class SegmentationChoice extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String filterStrength = "1"; //defaults to 1, so filter will work if not selected
    Boolean isCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segmentation_choice);

        //Whether to lead to camera view or image view
        Intent intent = getIntent();
        isCamera = intent.getBooleanExtra("isCamera", false);

        Spinner filterSpinner = (Spinner) findViewById(R.id.filter_spinner);
        Spinner edgeSpinner = (Spinner) findViewById(R.id.edge_spinner);
        Spinner cannySpinner = (Spinner) findViewById(R.id.canny_spinner);
        Spinner threshSpinner = (Spinner) findViewById(R.id.thresh_spinner);
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.dynamic_spinner);

        //Loads values into array
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(SegmentationChoice.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.segmentationValues));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //loads array values into spinner
        filterSpinner.setAdapter(myAdapter);
        edgeSpinner.setAdapter(myAdapter);
        cannySpinner.setAdapter(myAdapter);
        threshSpinner.setAdapter(myAdapter);
        dynamicSpinner.setAdapter(myAdapter);

        //sets default position of spinner to not in use
        filterSpinner.setSelection(1, false);
        edgeSpinner.setSelection(0, false);
        cannySpinner.setSelection(0,false);
        threshSpinner.setSelection(0, false);
        dynamicSpinner.setSelection(0, false);

        //listener to act when spinner item selected
        filterSpinner.setOnItemSelectedListener(this);
        edgeSpinner.setOnItemSelectedListener(this);
        cannySpinner.setOnItemSelectedListener(this);
        threshSpinner.setOnItemSelectedListener(this);
        dynamicSpinner.setOnItemSelectedListener(this);
    }

   @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
        String segmentationStrength = parent.getItemAtPosition(position).toString();
        int id = parent.getId();
        Intent intent;

        if (isCamera) {
            intent = new Intent(SegmentationChoice.this, CameraView.class);
       }
        else {
            intent = new Intent(SegmentationChoice.this, ImageSelect.class);
        }

        switch (id) {
            case R.id.filter_spinner: {
                filterStrength = parent.getSelectedItem().toString();
                Toast.makeText(this, "Filter is " + filterStrength, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.edge_spinner: {
                Toast.makeText(this, "Strength is " + segmentationStrength + " Filter is " + filterStrength + " isCamera is " + String.valueOf(isCamera), Toast.LENGTH_LONG).show();
                Intent edgeIntent = intent;
                edgeIntent.putExtra("segStrength", Integer.valueOf(segmentationStrength));
                edgeIntent.putExtra("segType", "Sobel");
                edgeIntent.putExtra("filterStrength", Integer.valueOf(filterStrength));
                startActivity(edgeIntent);
                break;
            }
            case R.id.canny_spinner: {
                Toast.makeText(this, "Strength is " + segmentationStrength + " Filter is " + filterStrength + " isCamera is " + String.valueOf(isCamera), Toast.LENGTH_LONG).show();
                Intent cannyIntent = intent;
                cannyIntent.putExtra("segStrength", Integer.valueOf(segmentationStrength));
                cannyIntent.putExtra("segType", "Canny");
                cannyIntent.putExtra("filterStrength", Integer.valueOf(filterStrength));
                startActivity(cannyIntent);
                break;
            }
            case R.id.thresh_spinner: {
                Toast.makeText(this, "Strength is " + segmentationStrength + " Filter is " + filterStrength + " isCamera is " + String.valueOf(isCamera), Toast.LENGTH_LONG).show();
                Intent threshIntent = intent;
                threshIntent.putExtra("segStrength", Integer.valueOf(segmentationStrength));
                threshIntent.putExtra("segType", "Threshold");
                threshIntent.putExtra("filterStrength", Integer.valueOf(filterStrength));
                startActivity(threshIntent);
                break;
            }
            case R.id.dynamic_spinner: {
                Toast.makeText(this, "Strength is " + segmentationStrength + " Filter is " + filterStrength + " isCamera is " + String.valueOf(isCamera), Toast.LENGTH_LONG).show();
                Intent dynamicIntent = intent;
                dynamicIntent.putExtra("segStrength", Integer.valueOf(segmentationStrength));
                dynamicIntent.putExtra("segType", "Dynamic");
                dynamicIntent.putExtra("filterStrength", Integer.valueOf(filterStrength));
                startActivity(dynamicIntent);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}