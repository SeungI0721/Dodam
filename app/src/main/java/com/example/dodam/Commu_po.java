package com.example.dodam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Commu_po extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private Button btu_back, btu_pos;
    private TextView tv_main, tv_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_po);

        btu_back = findViewById(R.id.btu_back);
        btu_pos = findViewById(R.id.btu_pos);

        tv_main = findViewById(R.id.tv_main);
        tv_text = findViewById(R.id.tv_text);

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

                Intent intent = getIntent();
                String userName = intent.getStringExtra("userName");



                if (tv_main.equals("") || tv_text.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Commu_po.this);
                    Dialog dialog = builder.setMessage("제목과 내용을 모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

            }
        });
    }

}