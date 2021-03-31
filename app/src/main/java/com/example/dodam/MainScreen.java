package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainScreen extends AppCompatActivity {

    Button btu_community;
    Button btu_setting;
    Button btu_chatbot;

    ImageView member_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        btu_community = findViewById(R.id.btu_community);
        btu_chatbot = findViewById(R.id.btu_chatbot);
        btu_setting = findViewById(R.id.btu_setting);

        member_photo = (ImageView) findViewById(R.id.member_photo);

        member_photo.setImageResource(R.drawable.sample);

        btu_community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Community.class);
                startActivity(intent);
            }
        });
        btu_chatbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Chatbot.class);
                startActivity(intent);
            }
        });
        btu_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen.this, Setting.class);
                startActivity(intent);
            }
        });
    }
}