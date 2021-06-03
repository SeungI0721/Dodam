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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Commu_po extends AppCompatActivity {

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

        //날짜 및 시간
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA);

        Date date = new Date();
        String time = simpleDateFormat.format(date);

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

                String noticeTitle = tv_main.getText().toString();
                String noticeContent = tv_text.getText().toString();
                String time = date.toString();

                Intent intent = getIntent();
                String userName = intent.getStringExtra("userName");

                if (noticeTitle.equals("") || noticeContent.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Commu_po.this);
                    Dialog dialog = builder.setMessage("제목과 내용을 모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success) { //글작성 성공
                                Toast.makeText(getApplicationContext(), "작성되었습니다.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Commu_po.this, community_f.class);
                                startActivity(intent);
                                finish();
                            } else { //글작성 실패
                                Toast.makeText(getApplicationContext(), "작성에 실패하였습니다.", Toast.LENGTH_LONG).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //서버로 Volley를 이용해 요청함.

                PostingRequest postingRequest = new PostingRequest(noticeTitle, noticeContent, userName, time, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Commu_po.this);
                queue.add(postingRequest);
            }
        });
    }
}