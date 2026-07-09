// Firebase Authentication 이메일 회원가입을 처리하는 Activity 파일이다.
package com.example.dodam;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dodam.data.firebase.DodamFirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText et_id, et_pass, et_pass2, et_name, et_mail;
    private Button btn_register, btn_check;
    private CheckBox cb_check;
    private AlertDialog dialog;
    private boolean idChecked = false;
    private String checkedUserId = "";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userIdsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 회원가입 화면 XML과 Activity를 연결한다.
        setContentView(R.layout.activity_register);

        // Firebase 인증 객체를 준비한다.
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = DodamFirebaseDatabase.getInstance();
        userIdsReference = database.getReference("UserIds");

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

        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력 변경 전에는 처리할 내용이 없다.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                idChecked = false;
                checkedUserId = "";
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력 변경 후에는 처리할 내용이 없다.
            }
        });

        // 아이디 중복확인 버튼을 누르면 DB에서 동일 아이디가 있는지 확인한다.
        btn_check.setOnClickListener(v -> {
            String userId = normalizeUserId(et_id.getText().toString());
            if (!isValidUserId(userId)) {
                showDialog("아이디는 영문, 숫자, 밑줄만 사용해 4~20자로 입력해주세요.");
                idChecked = false;
                return;
            }
            btn_check.setEnabled(false);
            userIdsReference.child(userId).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            idChecked = false;
                            checkedUserId = "";
                            showDialog("이미 사용 중인 아이디입니다.");
                        } else {
                            idChecked = true;
                            checkedUserId = userId;
                            showDialog("사용 가능한 아이디입니다.");
                        }
                    })
                    .addOnFailureListener(error -> {
                        idChecked = false;
                        checkedUserId = "";
                        showDialog(getUserIdCheckErrorMessage(error));
                    })
                    .addOnCompleteListener(task -> btn_check.setEnabled(true));
        });

        // 회원가입 버튼을 누르면 Firebase 이메일 회원가입을 실행한다.
        btn_register.setOnClickListener(v -> register());
    }

    private void register() {
        String userId = normalizeUserId(et_id.getText().toString());
        String nickname = et_name.getText().toString().trim();
        String email = et_mail.getText().toString().trim();
        String password = et_pass.getText().toString();
        String passwordConfirm = et_pass2.getText().toString();

        if (userId.isEmpty() || nickname.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            showDialog("모든 항목을 입력해주세요.");
            return;
        }
        if (!isValidUserId(userId)) {
            idChecked = false;
            showDialog("아이디는 영문, 숫자, 밑줄만 사용해 4~20자로 입력해주세요.");
            return;
        }
        if (!isValidEmail(email)) {
            showDialog("이메일 형식이 올바르지 않습니다.");
            return;
        }
        if (!cb_check.isChecked()) {
            showDialog("개인정보 제공에 동의해야 가입할 수 있습니다.");
            return;
        }
        if (!idChecked) {
            showDialog("아이디 중복 확인을 먼저 진행해주세요.");
            return;
        }
        if (!userId.equals(checkedUserId)) {
            idChecked = false;
            showDialog("아이디가 변경되었습니다. 중복 확인을 다시 진행해주세요.");
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
        userIdsReference.child(userId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        idChecked = false;
                        checkedUserId = "";
                        showDialog("이미 사용 중인 아이디입니다.");
                        btn_register.setEnabled(true);
                        return;
                    }
                    createFirebaseUser(userId, nickname, email, password);
                })
                .addOnFailureListener(error -> {
                    btn_register.setEnabled(true);
                    showDialog(getUserIdCheckErrorMessage(error));
                });
    }

    private void createFirebaseUser(String userId, String nickname, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        btn_register.setEnabled(true);
                        showDialog("회원가입 실패: 사용자 정보를 확인할 수 없습니다.");
                        return;
                    }
                    String uid = user.getUid();
                    UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nickname)
                            .build();
                    user.updateProfile(request)
                            .addOnSuccessListener(unused -> requestFreshToken(user, uid, userId, nickname, email))
                            .addOnFailureListener(error -> {
                                btn_register.setEnabled(true);
                                rollbackCreatedUser(user);
                                showDialog("회원가입 실패: 닉네임을 저장할 수 없습니다. " + error.getMessage());
                            });
                })
                .addOnFailureListener(error -> {
                    btn_register.setEnabled(true);
                    Toast.makeText(this, getRegisterErrorMessage(error), Toast.LENGTH_LONG).show();
                });
    }

    private void requestFreshToken(FirebaseUser user, String uid, String userId, String nickname, String email) {
        user.getIdToken(true)
                .addOnSuccessListener(token -> saveUserProfile(user, uid, userId, nickname, email))
                .addOnFailureListener(error -> {
                    btn_register.setEnabled(true);
                    rollbackCreatedUser(user);
                    showDialog("회원가입 실패: Firebase 인증 토큰을 확인할 수 없습니다. " + error.getMessage());
                });
    }

    private void saveUserProfile(FirebaseUser createdUser, String uid, String userId, String nickname, String email) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("uid", uid);
        userProfile.put("userId", userId);
        userProfile.put("nickname", nickname);
        userProfile.put("email", email);
        userProfile.put("createdAt", System.currentTimeMillis());

        Map<String, Object> updates = new HashMap<>();
        updates.put("/UserIds/" + userId, uid);
        updates.put("/Users/" + uid, userProfile);

        DodamFirebaseDatabase.getInstance().getReference().updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, nickname + "님 가입을 환영합니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, MainScreen.class);
                    intent.putExtra("userName", nickname);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(error -> {
                    btn_register.setEnabled(true);
                    rollbackCreatedUser(createdUser);
                    showDialog(getDatabaseSaveErrorMessage(error));
                });
    }

    private void rollbackCreatedUser(FirebaseUser createdUser) {
        if (createdUser != null) {
            createdUser.delete();
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String normalizeUserId(String userId) {
        return userId == null ? "" : userId.trim().toLowerCase();
    }

    private boolean isValidUserId(String userId) {
        return userId != null && userId.matches("^[a-z0-9_]{4,20}$");
    }

    private String getUserIdCheckErrorMessage(Exception error) {
        String message = error.getMessage() == null ? "" : error.getMessage();
        if (message.toLowerCase().contains("permission denied")) {
            return "아이디 중복 확인 실패: Firebase Database Rules에서 UserIds 읽기를 허용해야 합니다.";
        }
        return "아이디 중복 확인 실패: " + message;
    }

    private String getRegisterErrorMessage(Exception error) {
        if (error instanceof FirebaseAuthException) {
            String code = ((FirebaseAuthException) error).getErrorCode();
            if ("ERROR_CONFIGURATION_NOT_FOUND".equals(code)) {
                return "회원가입 실패: Firebase Console에서 Authentication을 시작하고 Email/Password 로그인을 활성화해야 합니다. (CONFIGURATION_NOT_FOUND)";
            }
            if ("ERROR_EMAIL_ALREADY_IN_USE".equals(code)) {
                return "회원가입 실패: 이미 사용 중인 이메일입니다.";
            }
            if ("ERROR_INVALID_EMAIL".equals(code)) {
                return "회원가입 실패: 이메일 형식이 올바르지 않습니다.";
            }
            if ("ERROR_WEAK_PASSWORD".equals(code)) {
                return "회원가입 실패: 비밀번호가 너무 약합니다.";
            }
        }
        return "회원가입 실패: " + error.getMessage();
    }

    private String getDatabaseSaveErrorMessage(Exception error) {
        String message = error.getMessage() == null ? "" : error.getMessage();
        if (message.toLowerCase().contains("permission denied")) {
            return "사용자 정보 저장 실패: Firebase Database Rules에서 UserIds 쓰기와 Users 쓰기를 허용해야 합니다. database.rules.json을 다시 적용해주세요.";
        }
        return "사용자 정보 저장 실패: " + message;
    }

    private void showDialog(String message) {
        dialog = new AlertDialog.Builder(Register.this)
                .setMessage(message)
                .setPositiveButton("확인", null)
                .create();
        dialog.show();
    }
}
