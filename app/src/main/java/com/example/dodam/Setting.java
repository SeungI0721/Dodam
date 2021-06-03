package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    private Button btu_heater;
    private Button btu_wq_m;
    private Button btu_wq_t;
    private Button btu_wl_m;
    private Button btu_wl_t;
    private TextView tv_suon;

    boolean he = true;
    boolean wq_m = true;
    boolean wq_t = true;
    boolean wl_m = true;
    boolean wl_t = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button btu_bac = findViewById(R.id.btu_bac);
        Button btu_sa = findViewById(R.id.btu_sa);
        btu_heater = findViewById(R.id.btu_heater);
        btu_wq_m = findViewById(R.id.btu_wq_m);
        btu_wq_t = findViewById(R.id.btu_wq_t);
        btu_wl_m = findViewById(R.id.btu_wl_m);
        btu_wl_t = findViewById(R.id.btu_wl_t);

        tv_suon = findViewById(R.id.tv_suon);
        /*li_on = findViewById(R.id.li_on);
        li_off = findViewById(R.id.li_off);*/

        //돌아가기
        btu_bac.setOnClickListener(v -> {
            Intent setIntent = new Intent(Setting.this, MainScreen.class);
            setIntent.putExtra("he",he);
            setIntent.putExtra("wq_t", wq_t);
            setIntent.putExtra("wq_m", wq_m);
            setIntent.putExtra("wl_t", wl_t);
            setIntent.putExtra("wl_m", wl_m);
            setIntent.putExtra("suon", (Parcelable) tv_suon);

            startActivity(setIntent);
            finish();
        });

        //적용하기
        btu_sa.setOnClickListener(v -> {
            String suon = tv_suon.getText().toString();

        });

        //히터
        btu_heater.setOnClickListener(v -> {
            if (he) {
                //(보내는 값)
                he = false;
                btu_heater.setBackgroundResource(R.drawable.bt_on);
                return;
            }
            he = true;
            btu_heater.setBackgroundResource(R.drawable.bt_off);
        });

        //수질
        //상단바 경고
        btu_wq_t.setOnClickListener(v -> {
            if (wq_t) {
                wq_t = false;
                btu_wq_t.setBackgroundResource(R.drawable.bt_on);
                return;

            }
            wq_t = true;
            btu_wq_t.setBackgroundResource(R.drawable.bt_off);
        });


        //메인 경고
        btu_wq_m.setOnClickListener(v -> {
            if (wq_m) {
                wq_m = false;
                btu_wq_m.setBackgroundResource(R.drawable.bt_on);
                return;

            } if (!wq_m) {
                wq_m = true;
                btu_wq_m.setBackgroundResource(R.drawable.bt_off);
                return;
            }
        });

        //수위
        //상단 경고
        btu_wl_t.setOnClickListener(v -> {
            if (wl_t) {
                wl_t = false;
                btu_wl_t.setBackgroundResource(R.drawable.bt_on);
                return;

            } if (!wl_t) {
                wl_t = true;
                btu_wl_t.setBackgroundResource(R.drawable.bt_off);
                return;
            }
        });
        //메인 경고
        btu_wl_m.setOnClickListener(v -> {
            if (wl_m) {
                wl_m = false;
                btu_wl_m.setBackgroundResource(R.drawable.bt_on);
                return;

            } if (!wl_m) {
                wl_m = true;
                btu_wl_m.setBackgroundResource(R.drawable.bt_off);
                return;
            }
        });

        //조명 (?)

    }
}