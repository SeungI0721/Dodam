package com.example.dodam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private EditText et_id, et_pass, et_pass2, et_name, et_mail;
    private Button btn_register, btn_check;
    private CheckBox cb_check;

    private AlertDialog dialog;
    private boolean validate = false;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        et_pass2 = findViewById(R.id.et_pass2);
        et_name = findViewById(R.id.et_name);
        et_mail = findViewById(R.id.et_mail);

        btn_register = findViewById(R.id.btn_register);
        btn_check = findViewById(R.id.btn_check);

        cb_check = findViewById(R.id.cb_check);
        cb_check.setChecked(false);


        //체크 박스 변수 확인
        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check) {
                    return;
                }
                if (isChecked){
                    check = true;
                } else {
                    check = false;
                }
            }
        });
        //중복체크
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = et_id.getText().toString();

                if (validate) {
                    return; //검증 완료
                }

                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("아이디를 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                                et_id.setEnabled(false);
                                validate = true; //검증 완료
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("이미 존재하는 아이디입니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                UserTestRequest usertestRequest = new UserTestRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(usertestRequest);
            }
        });

        //회원가입
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditText에 입력된 값을 가져온다
                String userID = et_id.getText().toString();
                String userPassword = et_pass.getText().toString();
                String userName = et_name.getText().toString();
                String userEmail = et_mail.getText().toString();
                String PassCk = et_pass2.getText().toString();
                String point = "";
                String userPhoto = "";

                //아이디 중복체크 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("중복된 아이디가 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //정보 제공 동의 확인
                if (!check) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("개인 정보 제공에 동의하지 않을 시 사용이 불가능합니다.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                //한 칸이라도 입력 안했을 경우
                if (userID.equals("") || userPassword.equals("") || userName.equals("") || userEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                    dialog = builder.setMessage("모두 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (userPassword.equals(PassCk)) {
                                if (success) { //회원가입 성공
                                    Toast.makeText(getApplicationContext(), String.format("%s님 가입을 환영합니다.", userName), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else { //회원가입 실패
                                    Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
                                dialog = builder.setMessage("비밀번호가 동일하지 않습니다.").setNegativeButton("확인", null).create();
                                dialog.show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //서버로 Volley를 이용해 요청함.

                RegisterRequest registerRequest = new RegisterRequest(userID, point, userEmail, userPassword, userName, userPhoto, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Register.this);
                queue.add(registerRequest);
            }
        });
    }
}
