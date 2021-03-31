package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btu_login;
    Button btu_guest;
    Animation anim_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btu_login = findViewById(R.id.btu_login);
        btu_guest = findViewById(R.id.btu_guest);

        anim_test = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_test);

        btu_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
         });
        btu_login.startAnimation(anim_test);

        btu_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                startActivity(intent);

                btu_guest.startAnimation(anim_test);
            }
        });
    }
}