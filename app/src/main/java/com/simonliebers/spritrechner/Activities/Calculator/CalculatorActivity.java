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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        //textView = findViewById(R.id.textView);

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

        setThisMonthAmount();

    }

    private void setThisMonthAmount(){

        int completeAmount = 0;

        Cursor data = gasDatabaseHelper.getData();
        while(data.moveToNext()){
            String stationUID = data.getString(1);
            String timestamp = data.getString(2);
            int amount = data.getInt(3);
            int price = data.getInt(4);

            Date date;

            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
            } catch (ParseException e) {
                date = new Date();
            }

            if(date.getMonth() == new Date().getMonth()){
                completeAmount += amount;
            }
        }

        textView.setText(completeAmount + "€");
    }
}
