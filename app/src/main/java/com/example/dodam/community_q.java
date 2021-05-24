package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class community_q extends AppCompatActivity {

    private Button btu_cback, btu_topo, btu_go, btu_f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        //이동 버튼
        btu_cback = findViewById(R.id.btu_cback);
        btu_f = findViewById(R.id.btu_f);
        btu_topo = findViewById(R.id.btu_topo);
        btu_go = findViewById(R.id.btu_go);

        //돌아가기
        btu_cback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, MainScreen.class);
                startActivity(intent);
                finish();
            }
        });
        //공지사항
        btu_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, Community.class);
                startActivity(intent);
                finish();
            }
        });

        //자유게시판
        btu_f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, community_f.class);
                startActivity(intent);
                finish();
            }
        });

        //글쓰기
        btu_topo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(community_q.this, Commu_po.class);
                startActivity(intent);
                finish();
            }
        });

    }
}