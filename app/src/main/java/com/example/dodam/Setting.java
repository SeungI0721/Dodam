package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    private Button btu_bac, btu_sa, btu_heater, btu_wq_m, btu_wq_t, btu_wl_m, btu_wl_t;
    private TextView tv_suon, li_on, li_off;

    boolean he = true;
    boolean wq_m = true;
    boolean wq_t = true;
    boolean wl_m = true;
    boolean wl_t = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btu_bac = findViewById(R.id.btu_bac);
        btu_sa = findViewById(R.id.btu_sa);
        btu_heater = findViewById(R.id.btu_heater);
        btu_wq_m = findViewById(R.id.btu_wq_m);
        btu_wq_t = findViewById(R.id.btu_wq_t);
        btu_wl_m = findViewById(R.id.btu_wl_m);
        btu_wl_t = findViewById(R.id.btu_wl_t);

        tv_suon = findViewById(R.id.tv_suon);
        li_on = findViewById(R.id.li_on);
        li_off = findViewById(R.id.li_off);

        //돌아가기
        btu_bac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setIntent = new Intent(Setting.this, MainScreen.class);
                setIntent.putExtra("he",he);
                setIntent.putExtra("wq_t", wq_t);
                setIntent.putExtra("wq_m", wq_m);
                setIntent.putExtra("wl_t", wl_t);
                setIntent.putExtra("wl_m", wl_m);

                startActivity(setIntent);
                finish();
            }
        });

        //적용하기
        btu_sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suon = tv_suon.getText().toString();
            }
        });

        //히터
        btu_heater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (he==true) {
                    //(보내는 값)
                    he = false;
                    btu_heater.setBackgroundResource(R.drawable.bt_on);
                } if (he==false) {
                    he = true;
                    btu_heater.setBackgroundResource(R.drawable.bt_off);
                }
            }
        });

        //수질
        //상단바 경고
        btu_wq_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wq_t == true) {
                    wq_t = false;
                    btu_wq_t.setBackgroundResource(R.drawable.bt_on);
                } if (wq_t == false) {
                    wq_t = true;
                    btu_wq_t.setBackgroundResource(R.drawable.bt_off);
                }
            }
        });
        //메인 경고
        btu_wq_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wq_m == true) {
                    wq_m = false;
                    btu_wq_m.setBackgroundResource(R.drawable.bt_on);
                } if (wq_m == false) {
                    wq_m = true;
                    btu_wq_m.setBackgroundResource(R.drawable.bt_off);
                }
            }
        });

        //수위
        //상단 경고
        btu_wl_t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wl_t == true) {
                    wl_t = false;
                    btu_wl_t.setBackgroundResource(R.drawable.bt_on);
                } if (wl_t == false) {
                    wl_t = true;
                    btu_wl_t.setBackgroundResource(R.drawable.bt_off);
                }
            }
        });
        //메인 경고
        btu_wl_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wl_m == true) {
                    wl_m = false;
                    btu_wl_m.setBackgroundResource(R.drawable.bt_on);
                } if (wl_m == false) {
                    wl_m = true;
                    btu_wl_m.setBackgroundResource(R.drawable.bt_off);
                }
            }
        });

        //조명 (?)

    }
}