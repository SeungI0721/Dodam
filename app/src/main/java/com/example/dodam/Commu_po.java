package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Commu_po extends AppCompatActivity {

    private Button btu_back, btu_pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_po);

        btu_back = findViewById(R.id.btu_back);
        btu_pos = findViewById(R.id.btu_pos);

        //돌아가기
        btu_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Commu_po.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });

        //작성하기
        btu_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Commu_po.this, community_f.class);
                startActivity(intent);
                finish();
            }
        });

    }
}