package com.simonliebers.spritrechner.General;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.simonliebers.spritrechner.General.Database.GasDatabaseHelper;
import com.simonliebers.spritrechner.R;
import com.simonliebers.spritrechner.Webservice.OpeningTime;
import com.simonliebers.spritrechner.Webservice.Station;

import java.util.Locale;

import androidx.annotation.NonNull;

public class TankUpDialog extends Dialog {

    Station station;

    EditText priceNum;
    EditText litersNum;
    Button submitButton;

    ImageButton close;
    GasDatabaseHelper databaseHelper;

    double priceNumDouble = 0;
    double litersNumDouble = 0;

    public TankUpDialog(@NonNull Context context, final Station station, View view, Constants.Type type, double price) {
        super(context);

        this.station = station;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.tankup_popup_dialog);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        databaseHelper = new GasDatabaseHelper(getContext());

        priceNum = this.findViewById(R.id.priceNum);
        litersNum = this.findViewById(R.id.litersNum);
        submitButton = this.findViewById(R.id.submit);
        close = this.findViewById(R.id.closePopup);

        if(price == -1){
            priceNum.setText(0);
        } else {
            priceNum.setText(price + "");
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        submitButton.setActivated(false);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(station == null){
                    databaseHelper.addData("null", Double.toString(litersNumDouble), Double.toString(priceNumDouble));
                } else {
                    databaseHelper.addData(station.getID(), Double.toString(litersNumDouble), Double.toString(priceNumDouble));
                }


                dismiss();
            }
        });

        priceNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateAll();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        litersNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateAll();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        Window window = this.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, size.y - 200);
        window.setGravity(Gravity.BOTTOM);
    }

    void updateAll(){
        try{
            priceNumDouble = Double.parseDouble(priceNum.getText().toString());
            litersNumDouble = Double.parseDouble(litersNum.getText().toString());
        } catch (Exception e){

        }

        if(priceNumDouble != 0 && litersNumDouble != 0){
            submitButton.setActivated(true);
            submitButton.setText("Submit for " + Math.round((priceNumDouble * litersNumDouble)*1000d)/1000d + " €");
            submitButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorGreen)));
        } else {
            submitButton.setActivated(false);
            submitButton.setText("Change values");
            submitButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorDarkGrey)));
        }


    }

}
