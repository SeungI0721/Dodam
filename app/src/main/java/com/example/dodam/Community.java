package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Community extends AppCompatActivity {

    Button btu_home;
    Button btu_setting;
    Button btu_chatbot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        //이동 버튼
        btu_home = findViewById(R.id.btu_home);
        btu_chatbot = findViewById(R.id.btu_chatbot);
        btu_setting = findViewById(R.id.btu_setting);

        btu_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Community.this, MainScreen.class);
                startActivity(intent);
            }
        });
        btu_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Community.this, Chatbot.class);
                startActivity(intent);
            }
        });
        btu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Community.this, Setting.class);
                startActivity(intent);
            }
        });
    }
}