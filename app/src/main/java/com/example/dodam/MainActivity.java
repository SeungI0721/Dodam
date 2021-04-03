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
    Button btu_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //이 java파일이 어느 xml파일과 연결되어 있는지 나타내는 코드
        setContentView(R.layout.activity_main);

        //xml에서 버튼에게 동일 '모션 id'를 준 것이 아닌 '자체 id'를 부여 했을 때, 앞으로의 코딩에서
        // 해당 id를 입력하면 동일한 id를 가지고 있는 버튼을 찾아 실행하게 설정하는 코드
        btu_login = findViewById(R.id.btu_login);
        btu_guest = findViewById(R.id.btu_guest);
        btu_register = findViewById(R.id.btu_register);

        //설정 버튼이 클릭되면 어떤 것을 실행하라는 명령어
        //로그인
        btu_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent 기능을 이용하여 엑티비티를 옮기는 명령어
                //(현재 엑티비티).this, (전환 할 엑티비티).class); 올바른 엑티비티라면 자동으로 연결 됨
                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                startActivity(intent);

                //애니메이션 실행 명령어 (현재 엑티비티 적용, 전환 엑티비티 적용)
                overridePendingTransition(R.anim.up_anim,R.anim.down_anim);

                //이 버튼을 클릭하면 이 엑티비티를 닫는다.
                finish();
            }
         });

        //회원가입
        btu_register.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        }));

        //로그인 없이 계속
        btu_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                startActivity(intent);
            }
        });

    }
}