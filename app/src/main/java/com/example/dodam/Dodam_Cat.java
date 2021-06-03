package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class Dodam_Cat extends AppCompatActivity {

    private Button but_bak, but_dodamt;
    private TextView tv_dodam, tv_name, tv_point;

    /*Random random = new Random();
    Random chat = random;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodam_cat);

        but_bak = findViewById(R.id.but_bak);
        but_dodamt = findViewById(R.id.but_dodamt);

        tv_dodam = findViewById(R.id.tv_dodam);
        tv_name = findViewById(R.id.tv_name);
        tv_point = findViewById(R.id.tv_point);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        String point = intent.getStringExtra("point");

        tv_name.setText(userName);
        tv_point.setText(point);

        /*try {
            tv_dodam.setText((CharSequence) chat);

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        but_dodamt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dodam_Cat.this, Chatbot.class);
                startActivity(intent);
                finish();
            }
        });

        but_bak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dodam_Cat.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }
}