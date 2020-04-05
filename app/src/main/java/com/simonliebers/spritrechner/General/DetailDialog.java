package com.simonliebers.spritrechner.General;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.simonliebers.spritrechner.R;
import com.simonliebers.spritrechner.Webservice.OpeningTime;
import com.simonliebers.spritrechner.Webservice.Station;

import androidx.annotation.NonNull;

public class DetailDialog extends Dialog {

    Station station;

    public DetailDialog(@NonNull Context context, Station station, View view) {
        super(context);

        this.station = station;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.detail_popup_dialog);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        ImageButton close = this.findViewById(R.id.closePopup);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        setBrandImage(station.getBrand().toLowerCase());
        setDetailsText();

        TextView brandText = this.findViewById(R.id.brandText);
        if(station.getBrand().equals("") || station.getBrand() == null){
            brandText.setText("FREIE TANKSTELLE");
        } else {
            brandText.setText(station.getBrand().toUpperCase());
        }

        TextView nameText = this.findViewById(R.id.nameText);
        nameText.setText(station.getName());

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

    private void setDetailsText(){
        TextView detailsText = this.findViewById(R.id.detailsText);
        String text = "Adress: \n" + station.getPostCode() + " " + station.getPlace()
                + "\n" + station.getStreet() + " " + station.getHouseNumber();
        if(station.getOpeningTimes() != null){
            text += "\n " + "Opening Times: \n";
            for(OpeningTime time : station.getOpeningTimes()){
                text += "\n" + time.getText() + ": " + time.getStart() + " - " + time.getEnd();
            }
        }

        text += "\nIsOpened: " + station.getIsOpen();
        text += "\nDistance: " + station.getDist() + " km";
        detailsText.setText(text);
    }
}
