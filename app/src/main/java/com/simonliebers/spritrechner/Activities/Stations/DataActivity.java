package com.simonliebers.spritrechner.Activities.Stations;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.simonliebers.spritrechner.General.Constants;
import com.simonliebers.spritrechner.R;

public class DataActivity extends AppCompatActivity {

    Button chooseSort;
    private Dialog dialog;

    CheckBox checkSuper;
    CheckBox checkE10;
    CheckBox checkDiesel;

    Constants.Type type;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        chooseSort = findViewById(R.id.chooseButton);

        //TankstellenAPI api = new TankstellenAPI();
        //api.getTankstellen(new Position(52.52099975265203, 13.43803882598877), Constants.Type.e5, Constants.Sort.dist);
        //api.getTankstellenDetails("");


        chooseSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowPopup(view);
            }
        });
    }

    public void onShowPopup(View v) {
        dialog = new Dialog(DataActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choosetype_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        ImageButton close = dialog.findViewById(R.id.closePopup);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button cancel = dialog.findViewById(R.id.popupCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button submit = dialog.findViewById(R.id.popupSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                if (checkDiesel.isChecked() || checkSuper.isChecked() || checkE10.isChecked()) {
                    dialog.dismiss();

                    pref = getApplicationContext().getSharedPreferences(Constants.DataPrefs, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Constants.GasKey, type.toString());
                    editor.apply();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(DataActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 1000);
                }
            }
        });

        checkSuper = dialog.findViewById(R.id.checkSuper);
        checkE10 = dialog.findViewById(R.id.checkE10);
        checkDiesel = dialog.findViewById(R.id.checkDiesel);

        checkSuper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkE10.setChecked(false);
                checkDiesel.setChecked(false);
                type = Constants.Type.e5;
            }
        });

        checkDiesel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkE10.setChecked(false);
                checkSuper.setChecked(false);
                type = Constants.Type.diesel;
            }
        });

        checkE10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSuper.setChecked(false);
                checkDiesel.setChecked(false);
                type = Constants.Type.e10;
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, size.y / 2);
        window.setGravity(Gravity.BOTTOM);

        dialog.show();
    }
}
