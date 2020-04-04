package com.simonliebers.spritrechner.General;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simonliebers.spritrechner.R;
import com.simonliebers.spritrechner.Webservice.Station;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class StationListAdapter extends ArrayAdapter<Station> {

    private static final String TAG = "StationListAdapter";

    SharedPreferences pref;
    String gasType;

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds variables in a View
     */
    private static class ViewHolder {
        TextView name;
        ImageView image;
        TextView money;
        TextView adress;
        TextView place;
        TextView distance;
    }

    /**
     * Default constructor for the PersonListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public StationListAdapter(Context context, int resource, ArrayList<Station> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        pref = mContext.getSharedPreferences(Constants.DataPrefs, 0);
        gasType = pref.getString(Constants.GasKey, null);

        Station station = getItem(position);

        final View result;

        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.stationName);
            holder.image = (ImageView) convertView.findViewById(R.id.stationImage);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.place = (TextView) convertView.findViewById(R.id.placeText);
            holder.adress = (TextView) convertView.findViewById(R.id.adressText);
            holder.distance = (TextView) convertView.findViewById(R.id.distanceText);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


//        Animation animation = AnimationUtils.loadAnimation(mContext,
//                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
//        result.startAnimation(animation);
        lastPosition = position;

        if(station != null){
            if(station.getPrice() == 0.0){
                holder.money.setText("-");
            } else {
                holder.money.setText(Double.toString(station.getPrice()).replace(".", ","));
            }

            holder.name.setText(station.getName().toUpperCase());
            holder.distance.setText(String.format("%s km", Double.toString(station.getDist())));
            holder.place.setText(station.getPostCode() + " " + station.getPlace());
            holder.adress.setText(station.getStreet() + " " + station.getHouseNumber());

            String brand = station.getBrand().toLowerCase();

            if(brand.contains("agip")){
                holder.image.setImageResource(R.drawable.brand_agip);
            } else if(brand.contains("aral")){
                holder.image.setImageResource(R.drawable.brand_aral);
            } else if(brand.contains("avia")){
                holder.image.setImageResource(R.drawable.brand_avia);
            } else if(brand.contains("baywa")){
                holder.image.setImageResource(R.drawable.brand_baywa);
            } else if(brand.contains("bft")){
                holder.image.setImageResource(R.drawable.brand_bft);
            } else if(brand.contains("classic")){
                holder.image.setImageResource(R.drawable.brand_classic);
            } else if(brand.equals("ed")){
                holder.image.setImageResource(R.drawable.brand_ed);
            } else if(brand.contains("elan")){
                holder.image.setImageResource(R.drawable.brand_elan);
            } else if(brand.contains("esso")){
                holder.image.setImageResource(R.drawable.brand_esso);
            } else if(brand.equals("go")){
                holder.image.setImageResource(R.drawable.brand_go);
            } else if(brand.contains("hem")){
                holder.image.setImageResource(R.drawable.brand_hem);
            } else if(brand.contains("hoyer")){
                holder.image.setImageResource(R.drawable.brand_hoyer);
            } else if(brand.contains("jet")){
                holder.image.setImageResource(R.drawable.brand_jet);
            } else if(brand.contains("markant")){
                holder.image.setImageResource(R.drawable.brand_markant);
            } else if(brand.contains("oil")){
                holder.image.setImageResource(R.drawable.brand_oil);
            } else if(brand.contains("omv")){
                holder.image.setImageResource(R.drawable.brand_omv);
            } else if(brand.contains("q1")){
                holder.image.setImageResource(R.drawable.brand_q1);
            } else if(brand.contains("raiffeisen")){
                holder.image.setImageResource(R.drawable.brand_raiffeisen);
            } else if(brand.contains("shell")){
                holder.image.setImageResource(R.drawable.brand_shell);
            } else if(brand.contains("sprint")){
                holder.image.setImageResource(R.drawable.brand_sprint);
            } else if(brand.contains("star")){
                holder.image.setImageResource(R.drawable.brand_star);
            } else if(brand.contains("supermarkt")){
                holder.image.setImageResource(R.drawable.brand_supermarkt);
            } else if(brand.contains("team")){
                holder.image.setImageResource(R.drawable.brand_team);
            } else if(brand.contains("total")) {
                holder.image.setImageResource(R.drawable.brand_total);
            } else if(brand.contains("westfalen")) {
                holder.image.setImageResource(R.drawable.brand_westfalen);
            } else if(brand.contains("zieglmeier")) {
                holder.image.setImageResource(R.drawable.brand_ziegelmeier);
            } else {
                holder.image.setImageResource(R.drawable.brand_frei);
            }
        }



        return convertView;
    }
}

