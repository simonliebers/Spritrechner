package com.simonliebers.spritrechner.Activities.Calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simonliebers.spritrechner.Activities.Stations.AppActivity;
import com.simonliebers.spritrechner.General.Database.GasDatabaseHelper;
import com.simonliebers.spritrechner.R;

public class CalculatorActivity extends AppCompatActivity {

    ImageButton stationsButton;
    String fromId;
    Button tankButton;
    TextView textView;

    GasDatabaseHelper gasDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        gasDatabaseHelper = new GasDatabaseHelper(this);

        Intent intent = getIntent();
        fromId = intent.getStringExtra("uid");

        stationsButton = findViewById(R.id.stationsButton);
        tankButton = findViewById(R.id.tankButton);
        textView = findViewById(R.id.textView);

        stationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalculatorActivity.this, AppActivity.class);
                startActivity(intent);
            }
        });

        tankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start Tanking manual
                gasDatabaseHelper.addData("Test", 20, 50);
                setThisMonthAmount();
            }
        });

        if(fromId != null){
            //Start tanking

        }


    }

    private void setThisMonthAmount(){
        int amount = 0;
        Cursor data = gasDatabaseHelper.getData();
        while(data.moveToNext()){
            text += data.getString(1) + " " + data.getString(2) + " " + data.getInt(3) + " " + data.getInt(4) + "\n";

        }

        textView.setText(text);
    }
}
