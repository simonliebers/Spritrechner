package com.simonliebers.spritrechner.Activities.Calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.simonliebers.spritrechner.Activities.Stations.AppActivity;
import com.simonliebers.spritrechner.General.Database.GasDatabaseHelper;
import com.simonliebers.spritrechner.General.TankUpDialog;
import com.simonliebers.spritrechner.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalculatorActivity extends AppCompatActivity {

    ImageButton stationsButton;
    String fromId;
    Button tankButton;

    TextView averagePrice;
    TextView totalMoney;
    TextView totalAmount;

    Dialog dialog;
    GasDatabaseHelper gasDatabaseHelper;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        context = this;

        gasDatabaseHelper = new GasDatabaseHelper(this);

        Intent intent = getIntent();
        fromId = intent.getStringExtra("uid");

        stationsButton = findViewById(R.id.stationsButton);
        tankButton = findViewById(R.id.tankButton);

        totalMoney = findViewById(R.id.moneyText);
        totalAmount = findViewById(R.id.amountText);
        averagePrice = findViewById(R.id.averageCostText);

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
                setThisMonthAmount();
                setThisMonthAveragePrice();
                setThisMonthMoney();

                dialog = new TankUpDialog(context, null, view, null, 0);

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        setThisMonthAmount();
                        setThisMonthAveragePrice();
                        setThisMonthMoney();
                    }
                });

                dialog.show();
            }
        });

        if(fromId != null){
            //Start tanking

        }

        setThisMonthMoney();
        setThisMonthAmount();
        setThisMonthAveragePrice();

    }

    private void setThisMonthMoney(){

        double completeAmount = 0;

        Cursor data = gasDatabaseHelper.getData();
        while(data.moveToNext()){
            String stationUID = data.getString(1);
            String timestamp = data.getString(2);
            String amountString = data.getString(3);
            String priceString = data.getString(4);

            double amount = Double.parseDouble(amountString);
            double price = Double.parseDouble(priceString);

            Date date;

            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
            } catch (ParseException e) {
                date = new Date();
            }

            if(date.getMonth() == new Date().getMonth()){
                completeAmount += amount * price;
            }
        }

        totalMoney.setText(Math.round(completeAmount * 1000d)/1000d + "");
    }

    private void setThisMonthAveragePrice(){

        double completeAmount = 0;
        int counter = 0;

        Cursor data = gasDatabaseHelper.getData();
        while(data.moveToNext()){

            counter += 1;

            String timestamp = data.getString(2);
            String amountString = data.getString(3);
            String priceString = data.getString(4);

            double amount = Double.parseDouble(amountString);
            double price = Double.parseDouble(priceString);

            Date date;

            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
            } catch (ParseException e) {
                date = new Date();
            }

            if(date.getMonth() == new Date().getMonth()){
                completeAmount += price;
            }
        }

        averagePrice.setText(Math.round((completeAmount / counter) * 1000d)/1000d + "");
    }

    private void setThisMonthAmount(){

        double completeAmount = 0;

        Cursor data = gasDatabaseHelper.getData();
        while(data.moveToNext()){
            String timestamp = data.getString(2);
            String amountString = data.getString(3);

            double amount = Double.parseDouble(amountString);

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

        totalAmount.setText(Math.round(completeAmount * 1000d)/1000d + "");
    }
}
