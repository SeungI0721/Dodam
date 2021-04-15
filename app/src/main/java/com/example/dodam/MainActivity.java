package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btn_login, btn_guest, btn_register;
    private EditText et_id, et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //이 java파일이 어느 xml파일과 연결되어 있는지 나타내는 코드
        setContentView(R.layout.activity_main);

        //xml에서 버튼에게 동일 '모션 id'를 준 것이 아닌 '자체 id'를 부여 했을 때, 앞으로의 코딩에서
        // 해당 id를 입력하면 동일한 id를 가지고 있는 버튼을 찾아 실행하게 설정하는 코드
        btn_login = findViewById(R.id.btn_login);
        btn_guest = findViewById(R.id.btn_guest);
        btn_register = findViewById(R.id.btn_register);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);

        //설정 버튼이 클릭되면 어떤 것을 실행하라는 명령어
        //로그인
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String userID = et_id.getText().toString();
            String userPassword = et_pass.getText().toString();

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) { //로그인 성공
                            String userID = jsonObject.getString("userID");
                            String userPassword = jsonObject.getString("userPassword");
                            String userName = jsonObject.getString( "userName" );

                            Toast.makeText(getApplicationContext(), String.format("%s님 환영합니다.", userName), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainScreen.class);

                            intent.putExtra("userID", userID);
                            intent.putExtra("userPass", userPassword);
                            intent.putExtra( "UserName", userName );
                            startActivity(intent);

                        } else { //로그인 실패
                            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
         });

        //회원가입
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
            }
        });

        //로그인 없이 계속
        btn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainScreen.class);
                startActivity(intent);
            }
        });
    }
}