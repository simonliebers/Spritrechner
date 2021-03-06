package com.simonliebers.spritrechner.General;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.simonliebers.spritrechner.Activities.Stations.AppActivity;
import com.simonliebers.spritrechner.R;
import com.simonliebers.spritrechner.Webservice.DetailsConverter;
import com.simonliebers.spritrechner.Webservice.OpeningTime;
import com.simonliebers.spritrechner.Webservice.Station;

import java.util.Locale;

import androidx.annotation.NonNull;

public class DetailDialog extends Dialog {

    Station station;
    Dialog dialog;
    Constants.Type type;
    View view;

    public DetailDialog(@NonNull Context context, Station station, View view, Constants.Type type) {
        super(context);

        this.station = station;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.detail_popup_dialog);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        this.type = type;
        this.view = view;

        ImageButton close = this.findViewById(R.id.closePopup);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button tankButton = this.findViewById(R.id.tankButton);

        if(!station.getIsOpen()){
            tankButton.setVisibility(View.INVISIBLE);
        }

        tankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTankUp();
            }
        });

        setBrandImage(station.getBrand().toLowerCase());
        setAdressText();
        updateOpenIndicator();
        setBrandText();
        setOpeningTimes();
        setPrices(type);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        Window window = this.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, size.y - 100);
        window.setGravity(Gravity.BOTTOM);
    }

    private void setBrandImage(String brand){
        ImageView brandImage = this.findViewById(R.id.brandImage);
        brandImage.setImageResource(R.drawable.brand_aral);

        if(brand.contains("agip")){
            brandImage.setImageResource(R.drawable.brand_agip);
        } else if(brand.contains("aral")){
            brandImage.setImageResource(R.drawable.brand_aral);
        } else if(brand.contains("avia")){
            brandImage.setImageResource(R.drawable.brand_avia);
        } else if(brand.contains("baywa")){
            brandImage.setImageResource(R.drawable.brand_baywa);
        } else if(brand.contains("bft")){
            brandImage.setImageResource(R.drawable.brand_bft);
        } else if(brand.contains("classic")){
            brandImage.setImageResource(R.drawable.brand_classic);
        } else if(brand.equals("ed")){
            brandImage.setImageResource(R.drawable.brand_ed);
        } else if(brand.contains("elan")){
            brandImage.setImageResource(R.drawable.brand_elan);
        } else if(brand.contains("esso")){
            brandImage.setImageResource(R.drawable.brand_esso);
        } else if(brand.equals("go")){
            brandImage.setImageResource(R.drawable.brand_go);
        } else if(brand.contains("hem")){
            brandImage.setImageResource(R.drawable.brand_hem);
        } else if(brand.contains("hoyer")){
            brandImage.setImageResource(R.drawable.brand_hoyer);
        } else if(brand.contains("jet")){
            brandImage.setImageResource(R.drawable.brand_jet);
        } else if(brand.contains("markant")){
            brandImage.setImageResource(R.drawable.brand_markant);
        } else if(brand.contains("oil")){
            brandImage.setImageResource(R.drawable.brand_oil);
        } else if(brand.contains("omv")){
            brandImage.setImageResource(R.drawable.brand_omv);
        } else if(brand.contains("q1")){
            brandImage.setImageResource(R.drawable.brand_q1);
        } else if(brand.contains("raiffeisen")){
            brandImage.setImageResource(R.drawable.brand_raiffeisen);
        } else if(brand.contains("shell")){
            brandImage.setImageResource(R.drawable.brand_shell);
        } else if(brand.contains("sprint")){
            brandImage.setImageResource(R.drawable.brand_sprint);
        } else if(brand.contains("star")){
            brandImage.setImageResource(R.drawable.brand_star);
        } else if(brand.contains("supermarkt")){
            brandImage.setImageResource(R.drawable.brand_supermarkt);
        } else if(brand.contains("team")){
            brandImage.setImageResource(R.drawable.brand_team);
        } else if(brand.contains("total")) {
            brandImage.setImageResource(R.drawable.brand_total);
        } else if(brand.contains("westfalen")) {
            brandImage.setImageResource(R.drawable.brand_westfalen);
        } else if(brand.contains("zieglmeier")) {
            brandImage.setImageResource(R.drawable.brand_ziegelmeier);
        } else {
            brandImage.setImageResource(R.drawable.brand_frei);
        }
    }

    void setAdressText(){
        TextView placeText = this.findViewById(R.id.placeText);
        placeText.setText(station.getPostCode() + " " + station.getPlace());

        TextView streetText = this.findViewById(R.id.streetText);
        streetText.setText(station.getStreet() + " " + station.getHouseNumber());
    }

    void updateOpenIndicator(){
        Button openIndicator = this.findViewById(R.id.openIndicator);

        openIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOpenIndicatorClick();
            }
        });

        if(station.getIsOpen()){
            openIndicator.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorGreen)));
            openIndicator.setText("START NAVIGATION");
        } else {
            openIndicator.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.colorRed)));
            openIndicator.setText("THIS STATION IS CLOSED");
        }
    }

    void setBrandText(){
        TextView brandText = this.findViewById(R.id.brandText);
        if(station.getBrand().equals("") || station.getBrand() == null){
            brandText.setText("FREIE TANKSTELLE");
        } else {
            brandText.setText(station.getBrand().toUpperCase());
        }

        TextView nameText = this.findViewById(R.id.nameText);
        nameText.setText(station.getName());
    }

    void setOpeningTimes(){
        TextView timesText = this.findViewById(R.id.timesText);
        String times = "";

        if(station.getOpeningTimes() == null || station.getOpeningTimes().length == 0){
            timesText.setText("Not known");
        } else {
            for(OpeningTime time : station.getOpeningTimes()){
                times += time.getText() + " " + time.getStart() + " - " + time.getEnd() + "\n";
            }
            timesText.setText(times);
        }
    }

    void setPrices(Constants.Type type){

        TextView typeText = this.findViewById(R.id.typeText);
        TextView typePrice = this.findViewById(R.id.typeNum);

        if(type == Constants.Type.diesel){
            typePrice.setText(Double.toString(station.getPrice()).replace(".", ","));
            typeText.setText("Diesel");
        } else if(type == Constants.Type.e5){
            typePrice.setText(Double.toString(station.getPrice()).replace(".", ","));
            typeText.setText("E5");
        } else if(type == Constants.Type.e10){
            typePrice.setText(Double.toString(station.getPrice()).replace(".", ","));
            typeText.setText("E10");
        }


    }

    void handleOpenIndicatorClick(){
        if(station.getIsOpen()){
            String uri = String.format(Locale.ENGLISH, "geo:" + station.getLat() + "," + station.getLng() + "?q=" + station.getLat() +"," + station.getLng() + "(" + station.getBrand()+ ")");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            getContext().startActivity(intent);
        }
    }

    void handleTankUp(){
        dialog = new TankUpDialog(getContext(), station, view, type, station.getPrice());

        dialog.show();
    }

}
