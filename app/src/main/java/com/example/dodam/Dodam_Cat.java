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
    private TextView  tv_name, tv_point, tv_dodam;

    int ran = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodam_cat);

        but_bak = findViewById(R.id.but_bak);
        but_dodamt = findViewById(R.id.but_dodamt);

        tv_dodam = findViewById(R.id.tv_dodam);
        tv_name = findViewById(R.id.tv_name);
        tv_point = findViewById(R.id.tv_point);

            try {
                ran = (int)(Math.random() * 10);

                if (ran == 0) {
                    tv_dodam.setText("안녕하세요?");
                } else if (ran == 1) {
                    tv_dodam.setText("오늘 하루는 어땠어요?");
                } else if (ran == 2) {
                    tv_dodam.setText("오늘 하루도 파이팅!");
                } else if (ran == 3) {
                    tv_dodam.setText("오늘 만날 수 있어서 너무 기뻐요!");
                } else if (ran == 4) {
                    tv_dodam.setText("오늘 식사는 어땠나요?");
                } else if (ran == 5) {
                    tv_dodam.setText("매일이 행복했으면 좋겠어요!");
                } else if (ran == 6) {
                    tv_dodam.setText("저와 대화해 보세요!");
                } else if (ran == 7) {
                    tv_dodam.setText("오늘도 열심히 배우는 중이에요!");
                } else if (ran == 8) {
                    tv_dodam.setText("저는 어떤 종류의 물고기 같나요?");
                } else if (ran == 9) {
                    tv_dodam.setText("어떤 색을 좋아하세요?");
                }

            } catch (Exception e) {
            e.printStackTrace();
            }

        but_dodamt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reintent = new Intent(Dodam_Cat.this, Chatbot.class);
                startActivity(reintent);
                finish();
            }
        });

        but_bak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reintent = new Intent(Dodam_Cat.this, MainScreen.class);
                startActivity(reintent);
                finish();
            }
        });
    }
}