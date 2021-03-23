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

public class CannySelection extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Boolean isCamera;
    String lowStrength = "50"; //defaults if button clicked before another number is selected
    String upperStrength;
    Intent intent;
    int segmentationStrength;
    int filterStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canny_selection);

        isCamera = getIntent().getBooleanExtra("isCamera", false);
        segmentationStrength = getIntent().getIntExtra("segmentationStrength", 1);
        filterStrength = getIntent().getIntExtra("filterStrength", 1);
        Toast.makeText(this, "isCamera is " + String.valueOf(isCamera) + " segmentation strength is " + String.valueOf(segmentationStrength) + " filter strength is " + String.valueOf(filterStrength), Toast.LENGTH_SHORT).show();

        Spinner lowSpinner = (Spinner) findViewById(R.id.lower_spinner);
        Spinner upperSpinner = (Spinner) findViewById(R.id.upper_spinner);
        Button ratio = findViewById(R.id.ratioBtn);
        Button start = findViewById(R.id.goBtn);

        //Choose which activity to move to next
        if (isCamera){
            intent = new Intent(CannySelection.this, CameraView.class);
        }
        else {
            intent = new Intent(CannySelection.this, ImageSelect.class);
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(upperStrength) < Integer.valueOf(lowStrength)){
                    upperStrength = String.valueOf(Integer.valueOf(lowStrength) * 2);
                }
                Intent btnIntent = intent;
                btnIntent.putExtra("segStrength", segmentationStrength);
                btnIntent.putExtra("segType", "Canny");
                btnIntent.putExtra("filterStrength", filterStrength);
                btnIntent.putExtra("cannyUpper", upperStrength);
                btnIntent.putExtra("cannyLower", lowStrength);
                startActivity(btnIntent);
            }
        });

        ratio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUpper = String.valueOf(Integer.valueOf(upperStrength) * 3);
                Intent btnIntent = intent;
                btnIntent.putExtra("segStrength", segmentationStrength);
                btnIntent.putExtra("segType", "Canny");
                btnIntent.putExtra("filterStrength", filterStrength);
                btnIntent.putExtra("cannyUpper", newUpper);
                btnIntent.putExtra("cannyLower", lowStrength);
                startActivity(btnIntent);
            }
        });

        //Load values into lower threshold array spinner 0-100
        List<String> lowerThreshList = new ArrayList<String>();
        for(int i = 0; i<=100; i++) {
            lowerThreshList.add(String.valueOf(i));
        }
        ArrayAdapter<String> lowerThreshAdapter = new ArrayAdapter<String>(CannySelection.this, android.R.layout.simple_list_item_1, lowerThreshList);

        //Load for filter spinner
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(CannySelection.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.segmentationValues));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Load values into upper threshold array spinner 0-300
        List<String> upperThreshList = new ArrayList<String>();
        for(int i = 0; i<=300; i++) {
            upperThreshList.add(String.valueOf(i));
        }
        ArrayAdapter<String> upperThreshAdapter = new ArrayAdapter<String>(CannySelection.this, android.R.layout.simple_list_item_1, upperThreshList);

        lowSpinner.setAdapter(lowerThreshAdapter);
        upperSpinner.setAdapter(upperThreshAdapter);

        lowSpinner.setSelection(50,false);
        upperSpinner.setSelection(100,false);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
       int id = parent.getId();

        switch (id){
            case R.id.lower_spinner: {
                lowStrength = parent.getSelectedItem().toString();
                Toast.makeText(this, "Upper is " + upperStrength + " Lower is " + lowStrength, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.upper_spinner: {
                upperStrength = parent.getSelectedItem().toString();
                Toast.makeText(this, "Upper is " + upperStrength + " Lower is " + lowStrength, Toast.LENGTH_LONG).show();
                //Intent upperIntent = intent;
                //upperIntent.putExtra("segStrength", segmentationStrength);
                //upperIntent.putExtra("segType", "Canny");
                //upperIntent.putExtra("filterStrength", filterStrength);
                //upperIntent.putExtra("cannyUpper", Integer.valueOf(upperStrength));
                //upperIntent.putExtra("cannyLower", Integer.valueOf(lowStrength));
                //upperIntent.putExtra("isCanny", true);
                //startActivity(upperIntent);
                break;
            }
        }
    }
    //Needs to be here but not needed
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}