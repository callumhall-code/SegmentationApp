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

public class ThresholdChoice extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Boolean isCamera;
    String filterStrength;
    String threshVal = "122"; // default value. Half of max binary
    String maxBinary = "255";
    String threshType = "Binary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold_choice);

        Spinner thresholdVal = (Spinner) findViewById(R.id.thresh_spinner);
        Spinner binaryVal = (Spinner) findViewById(R.id.binary_spinner);
        Spinner thresholdType = (Spinner) findViewById(R.id.threshType_spinner);
        Button goBtn = findViewById(R.id.beginThresh);

        isCamera = getIntent().getBooleanExtra("isCamera", false);
        filterStrength = getIntent().getStringExtra("filterStrength");

        //Values for threshold value and binary value. Between 0 and 255
        List<String> valuesList = new ArrayList<String>();
        for(int i = 0; i<=255; i++) {
            valuesList.add(String.valueOf(i));
        }
        ArrayAdapter<String> valuesAdapter = new ArrayAdapter<String>(ThresholdChoice.this, android.R.layout.simple_list_item_1, valuesList);

        //Values for threshold type
        List<String> typesList = new ArrayList<String>();
        typesList.add("Binary");
        typesList.add("Binary Inverted");
        typesList.add("Truncate");
        typesList.add("To Zero");
        typesList.add("To Zero Inverted");
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<String>(ThresholdChoice.this, android.R.layout.simple_list_item_1, typesList);

        thresholdVal.setAdapter(valuesAdapter);
        binaryVal.setAdapter(valuesAdapter);
        thresholdType.setAdapter(typesAdapter);

        thresholdVal.setSelection(0,false);
        binaryVal.setSelection(255,false);
        thresholdType.setSelection(0,false);

        thresholdVal.setOnItemSelectedListener(this);
        binaryVal.setOnItemSelectedListener(this);
        thresholdType.setOnItemSelectedListener(this);

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (isCamera){
                    intent = new Intent(ThresholdChoice.this, CameraView.class);
                }
                else{
                    intent = new Intent(ThresholdChoice.this, ImageSelect.class);
                }
                intent.putExtra("filterStrength", filterStrength);
                intent.putExtra("threshVal", threshVal);
                intent.putExtra("threshType", threshType);
                intent.putExtra("maxBinary", maxBinary);
                intent.putExtra("segType", "Threshold");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3) {
        int id = parent.getId();

        switch (id) {
            case R.id.thresh_spinner: {
                threshVal = parent.getSelectedItem().toString();
                Toast.makeText(getBaseContext(), "Thresh val is " + threshVal, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.binary_spinner: {
                maxBinary = parent.getSelectedItem().toString();
                Toast.makeText(getBaseContext(), "Max binary is " + maxBinary, Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.threshType_spinner: {
                threshType = parent.getSelectedItem().toString();
                Toast.makeText(getBaseContext(), "Thresh type is " + threshType, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}