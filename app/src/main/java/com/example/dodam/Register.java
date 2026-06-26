// Firebase Authentication 이메일 회원가입을 처리하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {

    private EditText et_id, et_pass, et_pass2, et_name, et_mail;
    private Button btn_register, btn_check;
    private CheckBox cb_check;
    private AlertDialog dialog;
    private boolean emailChecked = false;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 회원가입 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_register);

        // Firebase 인증 객체를 준비한다.
        firebaseAuth = FirebaseAuth.getInstance();

        // 회원가입 화면의 입력칸, 버튼, 체크박스를 연결한다.
        et_id = findViewById(R.id.et_id);
        et_pass = findViewById(R.id.et_pass);
        et_pass2 = findViewById(R.id.et_pass2);
        et_name = findViewById(R.id.et_name);
        et_mail = findViewById(R.id.et_mail);
        btn_register = findViewById(R.id.btn_register);
        btn_check = findViewById(R.id.btn_check);
        cb_check = findViewById(R.id.cb_check);
        cb_check.setChecked(false);

        // 이메일 확인 버튼을 누르면 이메일 형식을 검증한다.
        btn_check.setOnClickListener(v -> {
            String email = et_mail.getText().toString().trim();
            if (!isValidEmail(email)) {
                showDialog("이메일 형식이 올바르지 않습니다.");
                emailChecked = false;
                return;
            }
            emailChecked = true;
            showDialog("이메일 형식 확인이 완료되었습니다.");
        });

        // 회원가입 버튼을 누르면 Firebase 이메일 회원가입을 실행한다.
        btn_register.setOnClickListener(v -> register());
    }

    private void register() {
        String nickname = et_name.getText().toString().trim();
        String email = et_mail.getText().toString().trim();
        String password = et_pass.getText().toString();
        String passwordConfirm = et_pass2.getText().toString();

        if (!emailChecked) {
            showDialog("이메일 확인을 먼저 진행해주세요.");
            return;
        }
        if (!cb_check.isChecked()) {
            showDialog("개인정보 제공에 동의해야 가입할 수 있습니다.");
            return;
        }
        if (nickname.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            showDialog("모든 항목을 입력해주세요.");
            return;
        }
        if (!password.equals(passwordConfirm)) {
            showDialog("비밀번호가 동일하지 않습니다.");
            return;
        }
        if (password.length() < 6) {
            showDialog("비밀번호는 6자 이상이어야 합니다.");
            return;
        }

        btn_register.setEnabled(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nickname)
                                .build();
                        authResult.getUser().updateProfile(request);
                    }
                    Toast.makeText(this, nickname + "님 가입을 환영합니다.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(error ->
                        Toast.makeText(this, "회원가입 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> btn_register.setEnabled(true));
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private void showDialog(String message) {
        dialog = new AlertDialog.Builder(Register.this)
                .setMessage(message)
                .setPositiveButton("확인", null)
                .create();
        dialog.show();
    }
}
