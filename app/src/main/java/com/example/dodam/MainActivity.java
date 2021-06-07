package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

    private Button btn_login, btn_register;
    private EditText et_id, et_pass;

    private boolean saveLoginData;
    private String id;
    private String pwd;

    private SharedPreferences appData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //이 java파일이 어느 xml파일과 연결되어 있는지 나타내는 코드
        setContentView(R.layout.activity_main);

        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        // 이전에 로그인 정보를 저장시킨 기록이 있다면
        if (saveLoginData) {
            et_id.setText(id);
            et_pass.setText(pwd);
        }


        //xml에서 버튼에게 동일 '모션 id'를 준 것이 아닌 '자체 id'를 부여 했을 때, 앞으로의 코딩에서
        // 해당 id를 입력하면 동일한 id를 가지고 있는 버튼을 찾아 실행하게 설정하는 코드
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);

        /*SharedPreferences pref = getSharedPreferences("my_user",MODE_PRIVATE);
        String prefData = pref.getString();*/

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
                            String point = jsonObject.getString("point");
                            String userEmail = jsonObject.getString("userEmail");
                            String userPhoto = jsonObject.getString("userPhoto");


                            Toast.makeText(getApplicationContext(), String.format("%s님 환영합니다.", userName), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MainScreen.class);

                            intent.putExtra( "userName", userName );
                            intent.putExtra("point", point);
                            intent.putExtra("userPhoto", userPhoto);

                            startActivity(intent);
                            save();

                            finish();

                        } else { //로그인 실패
                            Toast.makeText(getApplicationContext(), "로그인에 실패했습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
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
    }

    // 설정값을 저장하는 함수
    private void save() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putString("ID", et_id.getText().toString().trim());
        editor.putString("PWD", et_pass.getText().toString().trim());

        saveLoginData = true;
    }

    // 설정값을 불러오는 함수
    private void load() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        id = appData.getString("ID", "");
        pwd = appData.getString("PWD", "");
    }
}