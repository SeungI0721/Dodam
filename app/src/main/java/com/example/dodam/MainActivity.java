// Firebase Authentication으로 로그인과 인증 세션을 관리하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button btn_login, btn_register;
    private EditText et_email, et_pass;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 로그인 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_main);

        // Firebase 인증 객체를 준비하고 기존 로그인 세션을 확인한다.
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            openDashboard();
            return;
        }

        // 로그인 화면의 입력칸과 버튼을 연결한다.
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);

        // 로그인 버튼을 누르면 이메일 기반 로그인을 실행한다.
        btn_login.setOnClickListener(v -> login());

        // 회원가입 버튼을 누르면 회원가입 화면으로 이동한다.
        btn_register.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Register.class)));
    }

    private void login() {
        String email = et_email.getText().toString().trim();
        String password = et_pass.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidEmail(email)) {
            Toast.makeText(this, "이메일 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        btn_login.setEnabled(false);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "로그인되었습니다.", Toast.LENGTH_SHORT).show();
                    openDashboard();
                })
                .addOnFailureListener(error ->
                        Toast.makeText(this, getLoginErrorMessage(error), Toast.LENGTH_LONG).show())
                .addOnCompleteListener(task -> btn_login.setEnabled(true));
    }

    private void openDashboard() {
        Intent intent = new Intent(MainActivity.this, MainScreen.class);
        startActivity(intent);
        finish();
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String getLoginErrorMessage(Exception error) {
        if (error instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) error).getErrorCode();
            if ("ERROR_CONFIGURATION_NOT_FOUND".equals(code)) {
                return "로그인 실패: Firebase Authentication 설정을 확인해주세요.";
            }
            if ("ERROR_INVALID_EMAIL".equals(code)) {
                return "로그인 실패: 이메일 형식이 올바르지 않습니다.";
            }
            if ("ERROR_INVALID_CREDENTIAL".equals(code) || "ERROR_WRONG_PASSWORD".equals(code)) {
                return "로그인 실패: 이메일 또는 비밀번호를 확인해주세요.";
            }
            if ("ERROR_USER_NOT_FOUND".equals(code)) {
                return "로그인 실패: 가입되지 않은 이메일입니다.";
            }
        }
        return "로그인 실패: " + error.getMessage();
    }
}
